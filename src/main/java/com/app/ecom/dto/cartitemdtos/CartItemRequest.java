package com.app.ecom.dto.cartitemdtos;

import lombok.Data;

@Data
public class CartItemRequest {

    private Long productId;

    private Integer quantity;
}
