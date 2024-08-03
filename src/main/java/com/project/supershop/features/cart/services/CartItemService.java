package com.project.supershop.features.cart.services;

import com.project.supershop.features.cart.domain.dto.requests.CartItemRequest;
import com.project.supershop.features.cart.domain.dto.responses.CartItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartItemService {
    CartItemResponse addCartItem(CartItemRequest cartItemRequest, String jwtToken);
    Page<CartItemResponse> getListCartItem(Pageable pageable, String jwtToken);
}
