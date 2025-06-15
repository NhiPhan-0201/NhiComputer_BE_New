package com.mshop.service;

import com.mshop.entity.ProductImage;
import com.mshop.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    public List<ProductImage> getImagesByProductId(Long productId) {
        return productImageRepository.findByProduct_ProductId(productId);
    }

    public ProductImage saveProductImage(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }
    public void deleteProductImage(Long imageId) {
        productImageRepository.deleteById(imageId);
    }

    public boolean existsProductById(Long productId) {
        return false;
    }
}