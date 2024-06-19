package com.project.supershop.features.product.controllers;

import com.project.supershop.common.ResultResponse;
import com.project.supershop.features.product.domain.dto.requests.ProductRequest;
import com.project.supershop.features.product.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultResponse> createAuthor(@Valid @ModelAttribute ProductRequest productRequest) {
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
