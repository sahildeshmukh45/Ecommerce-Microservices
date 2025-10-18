package com.app.ecom.service;

import com.app.ecom.dto.cartitemdtos.CartItemRequest;
import com.app.ecom.dto.cartitemdtos.CartItemResponse;
import com.app.ecom.entity.CartItem;
import com.app.ecom.entity.Product;
import com.app.ecom.entity.User;
import com.app.ecom.exception.UserNotFoundException;
import com.app.ecom.mapper.ProductMapper;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.ProductRepo;
import com.app.ecom.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepo userRepo;
    private final CartItemRepository cartItemRepository;
    private final ProductRepo productRepo;
    private final ProductMapper productMapper;

    public boolean addToCart(Long userId, CartItemRequest cartItemRequest) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with the id " + userId));

        Optional<Product> productOptional = productRepo.findById(cartItemRequest.getProductId());

        if (productOptional.isEmpty()) {
            return false;
        }

        Product product = productOptional.get();

        if (product.getStockQuantity() < cartItemRequest.getQuantity()) {
            return false;
        }

        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user, product);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemRequest.getQuantity());
            existingCartItem.setPrice(product.getPrice()
                    .multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItemRequest.getQuantity());
            cartItem.setPrice(product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItemRequest.getQuantity())));
            cartItemRepository.save(cartItem);
        }

        return true;
    }

    @Transactional
    public boolean deleteFromCart(Long userId, Long productId) {
        Optional<User> userOpt = userRepo.findById(userId);
        Optional<Product> productOptional = productRepo.findById(productId);

        if (userOpt.isPresent() && productOptional.isPresent()) {
            cartItemRepository.deleteByUserAndProduct(userOpt.get(), productOptional.get());
            return true;
        }
        return false;
    }

    public List<CartItemResponse> fetchCartByUserId(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        return cartItems.stream()
                .map(cartItem -> CartItemResponse.builder()
                        .id(cartItem.getId())
                        .product(productMapper.toResponse(cartItem.getProduct()))
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CartItem> getCart(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public void clearCart(Long userId) {
        userRepo.findById(userId).ifPresent(cartItemRepository::deleteByUser);
    }
}
