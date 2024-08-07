package com.project.supershop.features.product.controllers;

import com.project.supershop.common.ResultResponse;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.auth.services.AccessTokenService;
import com.project.supershop.features.auth.services.JwtTokenService;
import com.project.supershop.features.product.domain.dto.requests.ProductRequest;
import com.project.supershop.features.product.domain.dto.responses.ProductResponse;
import com.project.supershop.features.product.services.ProductService;
import com.project.supershop.features.voucher.domain.dto.responses.VoucherResponse;
import com.project.supershop.handler.ForBiddenException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
    private final JwtTokenService accessTokenService;

    public ProductController(ProductService productService, JwtTokenService accessTokenService) {
        this.productService = productService;
        this.accessTokenService = accessTokenService;
    }

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResultResponse> createProduct(@Valid @RequestBody ProductRequest productRequest,
         @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String jwtToken) {
        ProductResponse result = productService.createProduct(productRequest, jwtToken);
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
    @GetMapping("/{id}/shop/{shopId}")
    public ResponseEntity<ResultResponse<ProductResponse>> getById(@PathVariable("id") String id, @PathVariable("shopId") String shopId) {
        ProductResponse result = productService.getProductByIdForUser(id, shopId);
        ResultResponse<ProductResponse> response = ResultResponse.<ProductResponse>builder()
                .body(result)
                .timeStamp(LocalDateTime.now().toString())
                .message("Get List successfully")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/shop/edit")
    public ResponseEntity<ResultResponse<ProductResponse>> getByIdForEdit(
            @PathVariable("id") String id,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String jwtToken) {
        // chưa kiểm tra role
        Account parseJwtToAccount = accessTokenService.parseJwtTokenToAccount(jwtToken);
        ProductResponse result = productService.getProductByIdOfShop(id, parseJwtToAccount.getId().toString());
        if(!parseJwtToAccount.getId().toString().equals(result.getShopId())){
            throw new ForBiddenException("Your account doesn't have permission to edit products from other shop");
        }
        ResultResponse<ProductResponse> response = ResultResponse.<ProductResponse>builder()
                .body(result)
                .timeStamp(LocalDateTime.now().toString())
                .message("Get List successfully")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping(
            value = "/{id}/shop/edit",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResultResponse> updateProduct(
            @Valid @RequestBody ProductRequest productRequest,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String jwtToken) {
        Account parseJwtToAccount = accessTokenService.parseJwtTokenToAccount(jwtToken);
        ProductResponse result = productService.updateProduct(productRequest, jwtToken);
        return ResponseEntity.ok().body(
                ResultResponse.builder()
                        .body(result)
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Update product successfully")
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
}
