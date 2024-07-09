package com.project.supershop.features.product.services.impl;

import com.project.supershop.features.product.domain.dto.requests.CategoryRequest;
import com.project.supershop.features.product.domain.dto.responses.CategoryImageResponse;
import com.project.supershop.features.product.domain.dto.responses.CategoryResponse;
import com.project.supershop.features.product.domain.dto.responses.ProductResponse;
import com.project.supershop.features.product.domain.entities.Category;
import com.project.supershop.features.product.domain.entities.CategoryImage;
import com.project.supershop.features.product.domain.entities.PreviewImage;
import com.project.supershop.features.product.repositories.CategoryRepository;
import com.project.supershop.features.product.services.CategoryService;
import com.project.supershop.handler.ConflictException;
import com.project.supershop.handler.NotFoundException;
import com.project.supershop.handler.UnprocessableException;
import com.project.supershop.services.FileUploadUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final FileUploadUtils fileUploadUtils;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, FileUploadUtils fileUploadUtils, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.fileUploadUtils = fileUploadUtils;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) throws IOException {
        Optional<Category> resultFindByName = categoryRepository.findByName(categoryRequest.getName());
        if(!resultFindByName.isEmpty()){
            new ConflictException("Duplicate category name: " + categoryRequest.getName());
        }

        if (categoryRequest.getParentId() != null && !categoryRequest.getParentId().isEmpty()) {
            Optional<Category> resultFindById = categoryRepository.findById(UUID.fromString(categoryRequest.getParentId()));
            if (resultFindById.isEmpty()) {
                throw new NotFoundException(categoryRequest.getParentId() + " does not exist");
            }
        }

        if(categoryRequest.getIsActive() != "true" || categoryRequest.getIsActive() != "false"){
            new UnprocessableException("The isActive field must be 'true' or 'false'");
        }

        if(categoryRequest.getIsChild() != "true" || categoryRequest.getIsChild() != "false"){
            new UnprocessableException("The isChild field must be 'true' or 'false'");
        }

        Category category = Category.createCategory(categoryRequest);
        Category resultCategory = categoryRepository.save(category);

        if(categoryRequest.getIsChild() == "false"){
            if (categoryRequest.getImageFiles().length < 6 || categoryRequest.getImageFiles().length > 6) {
                throw new UnprocessableException("The imageFiles field must contain exactly 6 files");
            }

            List<CategoryImage> categoryImages = new ArrayList<>();
            for (MultipartFile imageFile : categoryRequest.getImageFiles()) {
                String fileName = fileUploadUtils.uploadFile(imageFile, "categories");

                CategoryImage previewImage = CategoryImage.createCategoryImage(fileName, resultCategory);
                categoryImages.add(previewImage);
            }
            resultCategory.setCategoryImages(categoryImages);
        } else {
            if (categoryRequest.getParentId() != null && !categoryRequest.getParentId().isEmpty()) {
                Optional<Category> resultFindById = categoryRepository.findById(UUID.fromString(categoryRequest.getParentId()));
                if (resultFindById.isEmpty()) {
                    throw new NotFoundException(categoryRequest.getParentId() + " does not exist");
                }
            } else {
                throw new UnprocessableException("The isChild field is true, the parentId field cannot be null");
            }
        }

        CategoryResponse categoryResponse = modelMapper.map(resultCategory, CategoryResponse.class);
        return categoryResponse;
    }
}
