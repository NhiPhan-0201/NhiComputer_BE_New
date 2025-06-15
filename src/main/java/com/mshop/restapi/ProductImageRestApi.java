package com.mshop.restapi;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mshop.entity.Product;
import com.mshop.entity.ProductImage;
import com.mshop.repository.ProductImageRepository;
import com.mshop.repository.ProductResository;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin("*")
@RestController
@RequestMapping("api/product-images")
public class ProductImageRestApi {

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    ProductResository productRepository;

    // Lấy tất cả hình ảnh
    @GetMapping
    public ResponseEntity<List<ProductImage>> getAll() {
        return ResponseEntity.ok(productImageRepository.findAll());
    }

    // Lấy tất cả hình ảnh theo ID sản phẩm
    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<ProductImage>> getAllByProductId(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(productImageRepository.findByProduct_ProductId(productId));
    }

    // Lấy một hình ảnh theo ID
    @GetMapping("{id}")
    public ResponseEntity<ProductImage> getOne(@PathVariable("id") Long id) {
        Optional<ProductImage> imageOpt = productImageRepository.findById(id);
        if (!imageOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(imageOpt.get());
    }

    // Thêm hình ảnh mới
    @PostMapping
    public ResponseEntity<ProductImage> post(
            @RequestParam("productId") Long productId,
            @RequestParam("image") MultipartFile imageFile
    ) {
        try {
            // Kiểm tra kích thước file (ví dụ: 5MB)
            if (imageFile.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(null);
            }

            // Xử lý file...
            byte[] bytes = imageFile.getBytes();
            String base64 = "data:" + imageFile.getContentType() + ";base64," + Base64.getEncoder().encodeToString(bytes);

            ProductImage productImage = new ProductImage();
            productImage.setImageUrl(base64);
            productRepository.findById(productId).ifPresent(productImage::setProduct);

            return ResponseEntity.ok(productImageRepository.save(productImage));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
        // Cập nhật hình ảnh
    @PutMapping("{id}")
    public ResponseEntity<ProductImage> put(@PathVariable("id") Long id, @RequestBody ProductImage productImage) {
        if (!id.equals(productImage.getImageId())) {
            return ResponseEntity.badRequest().build();
        }

        Optional<ProductImage> existingImageOpt = productImageRepository.findById(id);
        if (!existingImageOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ProductImage existingImage = existingImageOpt.get();

        // Preserve existing values for fields not included in the update
        if (productImage.getImageUrl() == null) {
            productImage.setImageUrl(existingImage.getImageUrl());
        }
        if (productImage.getProduct() == null) {
            productImage.setProduct(existingImage.getProduct());
        }

        return ResponseEntity.ok(productImageRepository.save(productImage));
    }

    // Xóa hình ảnh
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        Optional<ProductImage> imageOpt = productImageRepository.findById(id);
        if (!imageOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        productImageRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}