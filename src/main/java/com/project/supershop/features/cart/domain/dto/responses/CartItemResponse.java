package com.project.supershop.features.cart.domain.dto.responses;

import com.project.supershop.features.product.domain.dto.responses.ProductResponse;
import com.project.supershop.features.product.domain.dto.responses.ProductVariantResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemResponse {
    private Integer quantity;
    private String shopId;
    private ProductVariantResponse productVariant;
    private ProductResponse product;
}
