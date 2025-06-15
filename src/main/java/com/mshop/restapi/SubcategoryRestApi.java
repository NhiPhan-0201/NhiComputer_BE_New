package com.mshop.restapi;

import com.mshop.entity.Product;
import com.mshop.entity.Subcategory;
import com.mshop.repository.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("api/subcategories")
public class SubcategoryRestApi {
    @Autowired
    SubcategoryRepository repo;

    @GetMapping
    public ResponseEntity<List<Subcategory>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Subcategory> getOne(@PathVariable("id") Long id) {
        Optional<Subcategory> opt = repo.findById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("{id}/products")
    public ResponseEntity<List<Product>> getProductsBySubcategory(@PathVariable("id") Long id) {
        List<Product> products = repo.findAllProductBySubcategoryId(id);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Subcategory> post(@RequestBody Subcategory subcategory) {
        if (subcategory.getSubcategoryId() != null && repo.existsById(subcategory.getSubcategoryId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.save(subcategory));
    }

    @PutMapping("{id}")
    public ResponseEntity<Subcategory> put(@PathVariable("id") Long id, @RequestBody Subcategory subcategory) {
        if (!id.equals(subcategory.getSubcategoryId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repo.save(subcategory));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}