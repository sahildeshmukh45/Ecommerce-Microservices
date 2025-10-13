package com.app.ecom.dto.productDtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductImageResponse {

    private Long id;
    private String imageUrl;
    private String cloudinaryPublicId;
    private Integer displayOrder;
    private Boolean isPrimary;
    private LocalDateTime createdDate;
}