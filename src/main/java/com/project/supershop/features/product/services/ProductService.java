package com.project.supershop.features.product.services;

import com.project.supershop.features.product.domain.dto.requests.ProductRequest;
import com.project.supershop.features.product.domain.dto.responses.ProductResponse;

import java.io.IOException;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);
}
