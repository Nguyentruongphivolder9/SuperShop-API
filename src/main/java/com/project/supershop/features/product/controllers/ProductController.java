package com.project.supershop.features.product.controllers;

import com.project.supershop.common.ResultResponse;
import com.project.supershop.features.product.domain.dto.requests.ProductRequest;
import com.project.supershop.features.product.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ResultResponse> createAuthor(@Valid @RequestBody ProductRequest productRequest) {
        return ResponseEntity.created(URI.create("")).body(
                ResultResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("product", productService.createProduct(productRequest)))
                        .message("Create product successfully")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build());
    }
}
