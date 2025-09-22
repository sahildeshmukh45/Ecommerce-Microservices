package com.app.ecom.service;

import com.app.ecom.dto.productDtos.ProductRequest;
import com.app.ecom.dto.productDtos.ProductResponse;
import com.app.ecom.repository.ProductRepo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;


    public ProductResponse createProduct(ProductRequest productRequest) {

    }
}
