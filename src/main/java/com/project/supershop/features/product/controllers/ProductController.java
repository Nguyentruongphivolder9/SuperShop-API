package com.project.supershop.features.product.controllers;

import com.project.supershop.common.ResultResponse;
import com.project.supershop.features.product.domain.dto.requests.ProductRequest;
import com.project.supershop.features.product.domain.dto.responses.ProductResponse;
import com.project.supershop.features.product.domain.entities.Product;
import com.project.supershop.features.product.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResultResponse> createAuthor(@Valid @RequestBody ProductRequest productRequest) {
        ProductResponse result = productService.createProduct(productRequest);
        return ResponseEntity.created(URI.create("")).body(
                ResultResponse.builder()
                        .body(result)
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Create product successfully")
                        .statusCode(HttpStatus.CREATED.value())
                        .build());
    }
}
