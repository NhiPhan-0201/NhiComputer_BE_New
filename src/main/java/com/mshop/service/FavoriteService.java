package com.mshop.service;

import com.mshop.entity.Favorite;
import com.mshop.entity.Product;
import com.mshop.entity.User;
import com.mshop.repository.FavoriteRepository;
import com.mshop.repository.ProductResository;
import com.mshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {

    @Autowired
    FavoriteRepository favoriteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductResository productRepository;

    public List<Favorite> getFavoritesByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return favoriteRepository.findByUser(user);
    }

    public Favorite addFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        // Check if exists
        Optional<Favorite> existing = favoriteRepository.findByUserAndProduct(user, product);
        if (existing.isPresent()) return existing.get();
        Favorite fav = new Favorite();
        fav.setUser(user);
        fav.setProduct(product);
        return favoriteRepository.save(fav);
    }

    public boolean removeFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (user == null) {
            System.out.println("User not found with ID: " + userId);
            return false;
        }

        if (product == null) {
            System.out.println("Product not found with ID: " + productId);
            return false;
        }

        Optional<Favorite> fav = favoriteRepository.findByUserAndProduct(user, product);
        if (fav.isPresent()) {
            favoriteRepository.delete(fav.get());
            return true;
        }

        System.out.println("Favorite not found for user " + userId + " and product " + productId);
        return false;
    }
    public boolean isFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (user != null && product != null) {
            return favoriteRepository.existsByUserAndProduct(user, product);
        }
        return false;
    }
}