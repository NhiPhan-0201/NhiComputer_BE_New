package com.mshop.service;

import com.mshop.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> searchProducts(String name);
    List<Product> findAllProductByCategoryId(Long categoryId);
    List<Product> findAllStatusTrue();
    Product findByIdAndStatusTrue(Long id);
}