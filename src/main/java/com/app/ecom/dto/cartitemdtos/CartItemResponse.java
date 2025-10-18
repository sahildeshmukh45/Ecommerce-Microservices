package com.app.ecom.dto.cartitemdtos;


import com.app.ecom.dto.productDtos.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long id;
    private ProductResponse product;
    private int quantity;
    private BigDecimal price;
}
