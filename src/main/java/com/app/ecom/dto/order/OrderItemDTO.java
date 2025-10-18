package com.app.ecom.dto.order;

import jdk.jfr.DataAmount;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {

    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}
