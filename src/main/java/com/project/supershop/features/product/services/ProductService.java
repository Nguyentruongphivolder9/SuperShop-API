package com.project.supershop.features.product.services;

import com.project.supershop.features.product.domain.dto.requests.ProductRequest;
import com.project.supershop.features.product.domain.dto.responses.ProductResponse;
import com.project.supershop.features.voucher.domain.dto.responses.VoucherResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);
    Page<ProductResponse> getListProduct(Pageable pageable);
    ProductResponse getProductById(String id);
}
