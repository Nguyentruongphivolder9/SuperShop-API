package com.project.supershop.features.product.controllers;

import com.project.supershop.common.ResultResponse;
import com.project.supershop.features.product.domain.dto.requests.ProductRequest;
import com.project.supershop.features.product.domain.dto.responses.ProductResponse;
import com.project.supershop.features.product.services.ProductService;
import com.project.supershop.features.voucher.domain.dto.responses.VoucherResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

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

    @GetMapping
    public ResponseEntity<ResultResponse<Page<ProductResponse>>> getList(Pageable pageable) {
        Page<ProductResponse> result = productService.getListProduct(pageable);
        ResultResponse<Page<ProductResponse>> response = ResultResponse.<Page<ProductResponse>>builder()
                .body(result)
                .timeStamp(LocalDateTime.now().toString())
                .message("Get List successfully")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse<ProductResponse>> getById(@PathVariable("id") String id) {
        ProductResponse result = productService.getProductById(id);
        ResultResponse<ProductResponse> response = ResultResponse.<ProductResponse>builder()
                .body(result)
                .timeStamp(LocalDateTime.now().toString())
                .message("Get List successfully")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }
}
