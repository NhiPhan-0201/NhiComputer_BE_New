package com.mshop.restapi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mshop.entity.Product;
import com.mshop.repository.ProductResository;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class BestSellingAPI {

    @Autowired
    ProductResository productRepository;

    @GetMapping("/best-selling")
    public ResponseEntity<List<Product>> getBestSellingProducts(
            @RequestParam(defaultValue = "4") int limit) {
        List<Product> products = productRepository.findBestSellingProducts(limit);
        return ResponseEntity.ok(products);
    }
}