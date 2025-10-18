package com.app.ecom.dto.order;

import com.app.ecom.entity.OrderItem;
import com.app.ecom.utility.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderResponse {

    private Long id;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private List<OrderItemDTO> orderItems;
    private LocalDate createdAt;
}
