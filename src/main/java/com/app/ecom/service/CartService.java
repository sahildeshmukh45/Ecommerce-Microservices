package com.app.ecom.service;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.dto.productDtos.ProductResponse;
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

@Service
@RequiredArgsConstructor
public class CartService {


    private final UserRepo userRepo;
    private final CartItemRepository cartItemRepository;
    private final ProductRepo productRepo;
    private final ProductMapper  productMapper;

    public boolean addToCart(Long userId, CartItemRequest cartItemRequest) {

        User user = userRepo.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found with the id"+ userId));

        Optional<Product> productOptional = productRepo.findById(cartItemRequest.getProductId());

        if(productOptional.isEmpty()){
            return false;
        }

        Product product = productOptional.get();

        if(product.getStockQuantity() < cartItemRequest.getQuantity()){
            return false;
        }

        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user,product);
        if(existingCartItem != null){
            // update the quantity if exists
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemRequest.getQuantity());
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
        }else{
            // if did not exist then we will create it here
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItemRequest.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItemRequest.getQuantity())));
            cartItemRepository.save(cartItem);
        }

        return true;
    }

    @Transactional
    public boolean deleteFromCart(Long userId, Long productId) {

        Optional<User> userOpt = userRepo.findById(userId);

        Optional<Product> productOptional = productRepo.findById(productId);

        if(userOpt.isPresent() &&  productOptional.isPresent()){
            cartItemRepository.deleteByUserAndProduct(userOpt.get(),productOptional.get());
            return true;
        }
        return false;
    }


    public List<CartItem> fetchCartByUserId(Long userId) {

       return userRepo.findById(userId)
               .map(cartItemRepository::findByUser)
               .orElseGet(List::of);
    }

}
