package com.app.ecom.controller;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.dto.productDtos.ProductResponse;
import com.app.ecom.entity.CartItem;
import com.app.ecom.entity.Product;
import com.app.ecom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("addToCart/{userId}")
    public ResponseEntity<String> addToCart(@PathVariable Long userId, @RequestBody CartItemRequest cartItemRequest){
       if(!cartService.addToCart(userId,cartItemRequest)){
           return ResponseEntity.badRequest().body("Product out of stock or user not found or  product not found");
       };
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("removeFromCart/{userId}/{productId}")
    public ResponseEntity<String> removeFromCart
            (@PathVariable Long userId,@PathVariable Long productId){

        boolean deleted = cartService.deleteFromCart(userId,productId);

        return deleted  ? ResponseEntity.ok("Product removed successfully")
                : ResponseEntity.badRequest().body("Product not found");
    }

    @GetMapping("fetchCart/{userId}")
    public ResponseEntity<List<?>> fetchCart(@PathVariable Long userId){

        return ResponseEntity.ok(cartService.fetchCartByUserId(userId));
    }
}
