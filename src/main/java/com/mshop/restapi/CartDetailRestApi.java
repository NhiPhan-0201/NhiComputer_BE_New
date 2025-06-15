package com.mshop.restapi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mshop.entity.CartDetail;
import com.mshop.entity.Product;
import com.mshop.repository.CartDetailRepository;
import com.mshop.repository.CartRepository;
import com.mshop.repository.ProductResository;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/cart-detail")
public class CartDetailRestApi {
	@Autowired
	CartDetailRepository repo;

	@Autowired
	CartRepository Crepo;

	
	@Autowired
	ProductResository Prepo;

	@GetMapping("cart/{id}")
	public ResponseEntity<List<CartDetail>> getByCartId(@PathVariable("id") Long id) {
		if (!Crepo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(repo.getByCartId(id));
	}
	
	@RequestMapping(value="{id}", method = RequestMethod.GET)
	public ResponseEntity<CartDetail> getOne(@PathVariable("id") Long id) {
		if(!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(repo.findById(id).get());
	}

	@PostMapping()
	public ResponseEntity<CartDetail> post(@RequestBody CartDetail detail) {
		if (!Crepo.existsById(detail.getCart().getId())) {
			return ResponseEntity.notFound().build();
		}
		boolean check = false;
		List<Product> listP = Prepo.findAllStatusTrue();
		Product product = Prepo.findByIdAndStatusTrue(detail.getProduct().getProductId());
		for(Product p : listP){
			if(p.getProductId() == product.getProductId()) {
				check = true;
			}
		};
		if(!check) {
			return ResponseEntity.notFound().build();			
		}
		List<CartDetail> listD = repo.getByCartId(detail.getCart().getId());
		for (CartDetail item : listD) {
			if (item.getProduct().getProductId() == detail.getProduct().getProductId()) {
				item.setQuantity(item.getQuantity() + 1);
				item.setPrice(item.getPrice() + detail.getPrice());
				return ResponseEntity.ok(repo.save(item));
			}
		}
		return ResponseEntity.ok(repo.save(detail));
	}

	@PutMapping()
	public ResponseEntity<CartDetail> put(@RequestBody CartDetail detail) {
		if (!Crepo.existsById(detail.getCart().getId())) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(repo.save(detail));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		repo.deleteById(id);
		return ResponseEntity.ok().build();
	}
	@PutMapping("/select/{id}")
	public ResponseEntity<CartDetail> updateSelected(
			@PathVariable("id") Long id,
			@RequestParam("selected") boolean selected) {
		return repo.findById(id)
				.map(detail -> {
					detail.setSelected(selected);
					return ResponseEntity.ok(repo.save(detail));
				})
				.orElse(ResponseEntity.notFound().build());
	}
	@GetMapping("/cart/{cartId}/selected")
	public ResponseEntity<List<CartDetail>> getSelectedItems(@PathVariable("cartId") Long cartId) {
		return ResponseEntity.ok(repo.findSelectedItemsByCartId(cartId));
	}
	// Tính tổng tiền các sản phẩm đã chọn trong giỏ hàng
	@GetMapping("/cart/{cartId}/selected/total")
	public ResponseEntity<Double> getSelectedTotal(@PathVariable("cartId") Long cartId) {
		List<CartDetail> selectedItems = repo.findSelectedItemsByCartId(cartId);
		double total = selectedItems.stream()
				.mapToDouble(CartDetail::getPrice)
				.sum();
		return ResponseEntity.ok(total);
	}


}
