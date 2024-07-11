package com.project.supershop.features.product.services;

import com.project.supershop.features.product.domain.dto.requests.CategoryRequest;
import com.project.supershop.features.product.domain.dto.responses.CategoryResponse;

import java.io.IOException;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest) throws IOException;
}
