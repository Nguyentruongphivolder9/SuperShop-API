package com.project.supershop.product.services;

import com.project.supershop.common.ResultResponse;
import com.project.supershop.product.domain.dto.requests.ProductRequest;
import com.project.supershop.product.domain.dto.responses.ProductResponse;
import org.springframework.http.ResponseEntity;

public interface ProductService {

    ResultResponse<ProductResponse> createProduct(ProductRequest productRequest);
}
