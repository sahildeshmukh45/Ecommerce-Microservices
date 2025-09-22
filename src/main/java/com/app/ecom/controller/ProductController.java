package com.app.ecom.controller;

import com.app.ecom.dto.productDtos.ProductRequest;
import com.app.ecom.dto.productDtos.ProductResponse;
import com.app.ecom.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/product/")
@RestController
public class ProductController {

    private ProductService productService;

    @PostMapping("createProduct")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest){
        return new ResponseEntity<ProductResponse>
                (productService.createProduct(productRequest), HttpStatus.CREATED);
    }
}
