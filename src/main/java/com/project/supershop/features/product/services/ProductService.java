package com.project.supershop.features.product.services;

import com.project.supershop.features.product.domain.dto.requests.ProductRequest;
import com.project.supershop.features.product.domain.dto.responses.ProductResponse;
import com.project.supershop.features.voucher.domain.dto.responses.VoucherResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest, String jwtToken);
    ProductResponse updateProduct(ProductRequest productRequest, String jwtToken);
    Page<ProductResponse> getListProduct(Pageable pageable);
    ProductResponse getProductByIdForUser(String id, String shopId);
    ProductResponse getProductByIdOfShop(String id, String shopId);
    ProductResponse getProductById(String id);
}
