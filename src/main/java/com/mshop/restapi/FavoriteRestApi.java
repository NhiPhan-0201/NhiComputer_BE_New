package com.mshop.restapi;

import com.mshop.entity.Favorite;
import com.mshop.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("api/favorites")
public class FavoriteRestApi {

    @Autowired
    FavoriteService favoriteService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> getFavoritesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavoritesByUserId(userId));
    }
    @GetMapping("/is-favorite")
    public ResponseEntity<Boolean> isFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        boolean isFav = favoriteService.isFavorite(userId, productId);
        return ResponseEntity.ok(isFav);
    }

    @PostMapping
    public ResponseEntity<Favorite> addFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        return ResponseEntity.ok(favoriteService.addFavorite(userId, productId));
    }

    @DeleteMapping
    public ResponseEntity<?> removeFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        boolean removed = favoriteService.removeFavorite(userId, productId);
        if (removed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(404).body("Favorite not found or invalid user/product ID");
        }
    }

}