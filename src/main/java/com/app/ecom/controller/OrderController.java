package com.app.ecom.controller;

import com.app.ecom.dto.order.OrderResponse;
import com.app.ecom.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("createOrder/{userId}")
    public ResponseEntity<Optional<OrderResponse>> createOrder(@PathVariable Long userId){
        Optional<OrderResponse> order = orderService.createOrder(userId);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
