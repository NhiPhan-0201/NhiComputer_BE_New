package com.mshop.service.impl;

import com.mshop.entity.Product;
import com.mshop.repository.ProductResository;
import com.mshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImplement implements ProductService {

    @Autowired
    private ProductResository productRepository;

    @Override
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> findAllProductByCategoryId(Long categoryId) {
        return productRepository.findAllProductByCategoryId(categoryId);
    }

    @Override
    public List<Product> findAllStatusTrue() {
        return productRepository.findAllStatusTrue();
    }

    @Override
    public Product findByIdAndStatusTrue(Long id) {
        return productRepository.findByIdAndStatusTrue(id);
    }
}