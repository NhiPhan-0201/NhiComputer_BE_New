package com.mshop.restapi;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.mshop.entity.ProductImage;
import com.mshop.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mshop.entity.Product;
import com.mshop.repository.ProductResository;
import com.mshop.repository.SubcategoryRepository;


@CrossOrigin("*")
@RestController
@RequestMapping("api/products")
public class ProductRestApi {
	@Autowired
	ProductResository repo;

	@Autowired
	SubcategoryRepository subcategoryRepository;
	@Autowired
	ProductImageRepository productImageRepository;

	@GetMapping
	public ResponseEntity<List<Product>> getAll() {
		return ResponseEntity.ok(repo.findAllStatusTrue());
	}

	@GetMapping("{id}")
	public ResponseEntity<Product> getOne(@PathVariable("id") Long id) {
		Optional<Product> productOpt = repo.findById(id);
		if (!productOpt.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Product product = productOpt.get();
		if (!product.getStatus()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(product);
	}

	@PostMapping
	public ResponseEntity<Product> post(@RequestBody Product product) {
		if (product.getProductId() != null && repo.existsById(product.getProductId())) {
			return ResponseEntity.badRequest().build();
		}

		// Set default values
		product.setStatus(true);
		product.setEnteredDate(new Date());
		if (product.getDiscount() == null) {
			product.setDiscount(0.0);
		}

		// Xử lý subcategory nếu chỉ truyền id
		if (product.getSubcategory() != null && product.getSubcategory().getSubcategoryId() != null) {
			Long subId = product.getSubcategory().getSubcategoryId();
			subcategoryRepository.findById(subId).ifPresent(product::setSubcategory);
		}

		return ResponseEntity.ok(repo.save(product));
	}

	@PutMapping("{id}")
	public ResponseEntity<Product> put(@PathVariable("id") Long id, @RequestBody Product product) {
		if (!id.equals(product.getProductId())) {
			return ResponseEntity.badRequest().build();
		}

		Optional<Product> existingProductOpt = repo.findById(id);
		if (!existingProductOpt.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Product existingProduct = existingProductOpt.get();
		// Preserve existing values for fields not included in the update
		if (product.getStatus() == null) {
			product.setStatus(existingProduct.getStatus());
		}
		if (product.getEnteredDate() == null) {
			product.setEnteredDate(existingProduct.getEnteredDate());
		}

		// Xử lý subcategory nếu chỉ truyền id
		if (product.getSubcategory() != null && product.getSubcategory().getSubcategoryId() != null) {
			Long subId = product.getSubcategory().getSubcategoryId();
			subcategoryRepository.findById(subId).ifPresent(product::setSubcategory);
		}

		return ResponseEntity.ok(repo.save(product));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		repo.deleteById(id);  // Xóa vĩnh viễn
		return ResponseEntity.ok().build();
	}

	@GetMapping("/by-category/{id}")
	public ResponseEntity<List<Product>> getAllByCategory(@PathVariable("id") Long id) {
		return ResponseEntity.ok(repo.findAllProductByCategoryId(id));
	}
	@GetMapping("/images/{productId}")
	public ResponseEntity<List<ProductImage>> getImagesByProductId(@PathVariable("productId") Long productId) {
		List<ProductImage> images = productImageRepository.findByProduct_ProductId(productId);
		if (images.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(images);
	}
	@GetMapping("/filter")
	public ResponseEntity<List<Product>> filterProducts(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String sort,
			@RequestParam(required = false) String priceRange,
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) Long subcategoryId
	) {
		List<Product> products = repo.findAllStatusTrue();

		// Lọc theo tên
		if (name != null && !name.isEmpty()) {
			String lowerName = name.toLowerCase();
			products = products.stream()
					.filter(p -> p.getName().toLowerCase().contains(lowerName))
					.toList();
		}

		// Lọc theo khoảng giá (giá đã giảm)
		if (priceRange != null && !priceRange.isEmpty()) {
			if (priceRange.contains("+")) {
				int min = Integer.parseInt(priceRange.replace("+", ""));
				products = products.stream()
						.filter(p -> p.getPrice() * (1 - p.getDiscount() / 100.0) >= min * 1000000)
						.toList();
			} else if (priceRange.contains("-")) {
				String[] parts = priceRange.split("-");
				int min = Integer.parseInt(parts[0]);
				int max = Integer.parseInt(parts[1]);
				products = products.stream()
						.filter(p -> {
							double price = p.getPrice() * (1 - p.getDiscount() / 100.0);
							return price >= min * 1000000 && price <= max * 1000000;
						})
						.toList();
			}
		}

		// Sắp xếp
		if (sort != null && !sort.isEmpty()) {
			switch (sort) {
				case "enteredDate":
					products = products.stream()
							.sorted((a, b) -> b.getEnteredDate().compareTo(a.getEnteredDate()))
							.toList();
					break;
				case "priceAsc":
					products = products.stream()
							.sorted((a, b) -> Double.compare(
									a.getPrice() * (1 - a.getDiscount() / 100.0),
									b.getPrice() * (1 - b.getDiscount() / 100.0)))
							.toList();
					break;
				case "priceDesc":
					products = products.stream()
							.sorted((a, b) -> Double.compare(
									b.getPrice() * (1 - b.getDiscount() / 100.0),
									a.getPrice() * (1 - a.getDiscount() / 100.0)))
							.toList();
					break;
				default:
					break;
			}
		}
		if (categoryId != null) {
			products = products.stream()
					.filter(p -> p.getCategory() != null && p.getCategory().getCategoryId().equals(categoryId))
					.toList();
		}
		if (subcategoryId != null) {
			products = products.stream()
					.filter(p -> p.getSubcategory() != null && p.getSubcategory().getSubcategoryId().equals(subcategoryId))
					.toList();
		}
		return ResponseEntity.ok(products);
	}

}
