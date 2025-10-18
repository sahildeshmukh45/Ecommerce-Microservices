package com.app.ecom.service;

import com.app.ecom.dto.cartitemdtos.CartItemResponse;
import com.app.ecom.dto.order.OrderResponse;
import com.app.ecom.entity.*;
import com.app.ecom.repository.OrderRepository;
import com.app.ecom.repository.ProductRepo;
import com.app.ecom.repository.UserRepo;
import com.app.ecom.utility.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final UserRepo userRepo;
    private final OrderRepository orderRepository;

    public Optional<OrderResponse> createOrder(Long userId){

        // validate for cart item
        List<CartItem> cartItems= cartService.getCart(userId);
        if(cartItems.isEmpty()){
            return Optional.empty();
        }

        // validate for user
        Optional<User> userOptional=userRepo.findById(userId);
        if(userOptional.isEmpty()){
            return Optional.empty();
        }
        User user=userOptional.get();

        // calculate total price
        BigDecimal totalPrice= cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // create order
        Order order=new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);

        List<OrderItem> orderItems = cartItems.stream()
                .map(item->new OrderItem(
                        null,
                        item.getProduct(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                )).toList();

        order.setItems(orderItems);

        // minimize the stock of that produc

        Order savedOrder=orderRepository.save(order);

        // clear the cart
        cartService.clearCart(userId);

    }
}
