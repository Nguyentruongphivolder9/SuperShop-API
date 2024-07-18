package com.project.supershop.features.product.services.impl;

import com.project.supershop.features.product.domain.dto.requests.CategoryRequest;
import com.project.supershop.features.product.domain.dto.responses.CategoryResponse;
import com.project.supershop.features.product.domain.entities.Category;
import com.project.supershop.features.product.domain.entities.CategoryImage;
import com.project.supershop.features.product.repositories.CategoryImageRepository;
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
    private final CategoryImageRepository categoryImageRepository;
    private final FileUploadUtils fileUploadUtils;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryImageRepository categoryImageRepository, FileUploadUtils fileUploadUtils, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryImageRepository = categoryImageRepository;
        this.fileUploadUtils = fileUploadUtils;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) throws IOException {
        Optional<Category> resultFindByName = categoryRepository.findByName(categoryRequest.getName());
        if(resultFindByName.isPresent()){
            throw new ConflictException("Duplicate category name: " + categoryRequest.getName());
        }

        if (categoryRequest.getParentId() != null && !categoryRequest.getParentId().isEmpty()) {
            Optional<Category> resultFindById = categoryRepository.findById(UUID.fromString(categoryRequest.getParentId()));
            if (resultFindById.isEmpty()) {
                throw new NotFoundException(categoryRequest.getParentId() + " does not exist");
            }
        }

        if(!categoryRequest.getIsActive().equals("true") && !categoryRequest.getIsActive().equals("false")){
            throw new UnprocessableException("The isActive field must be 'true' or 'false'");
        }

        if(!categoryRequest.getIsChild().equals("true") && !categoryRequest.getIsChild().equals("false")){
            throw new UnprocessableException("The isChild field must be 'true' or 'false'");
        }

        if (categoryRequest.getParentId() != null && !categoryRequest.getParentId().isEmpty()) {
            Optional<Category> resultFindById = categoryRepository.findById(UUID.fromString(categoryRequest.getParentId()));
            if (resultFindById.isEmpty()) {
                throw new NotFoundException(categoryRequest.getParentId() + " does not exist");
            } else {
                if(!resultFindById.get().getIsChild()) {
                    throw new NotFoundException(categoryRequest.getParentId() + " is not allowed to contain child elements");
                }
            }
        }

        Category category = Category.createCategory(categoryRequest);
        Category resultCategory = categoryRepository.save(category);

        if(categoryRequest.getIsChild().equals("false")){
            if (categoryRequest.getImageFiles().length != 6) {
                throw new UnprocessableException("The imageFiles field must contain exactly 6 files");
            }

            List<CategoryImage> categoryImage = saveImage(categoryRequest.getImageFiles(), resultCategory);
            resultCategory.setCategoryImages(categoryImage);
        } else {
            if(categoryRequest.getParentId() == null || categoryRequest.getParentId().isEmpty()){
                if (categoryRequest.getImageFiles().length != 1) {
                    throw new UnprocessableException("The imageFiles field must contain exactly 1 files");
                }

                List<CategoryImage> categoryImage = saveImage(categoryRequest.getImageFiles(), resultCategory);
                resultCategory.setCategoryImages(categoryImage);
            }
        }

        return modelMapper.map(resultCategory, CategoryResponse.class);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categoriesLevel1 = categoryRepository.findAllCategoriesWithImagesByParentIdAndIsChild("", true);
        return categoriesLevel1.stream()
                .map(category -> mapCategoryToResponse(category))
                .collect(Collectors.toList());
    }

    private List<CategoryImage> saveImage(MultipartFile[] imageFiles, Category category) throws IOException {
        List<CategoryImage> categoryImages = new ArrayList<>();
        for (MultipartFile imageFile : imageFiles) {
            String fileName = fileUploadUtils.uploadFile(imageFile, "categories");

            CategoryImage previewImage = CategoryImage.createCategoryImage(fileName, category);
            categoryImages.add(previewImage);
        }
        List<CategoryImage> categoryImage = categoryImageRepository.saveAll(categoryImages);
        return categoryImage;
    }

    private CategoryResponse mapCategoryToResponse(Category category) {
        CategoryResponse response = modelMapper.map(category, CategoryResponse.class);
        List<Category> childCategories = categoryRepository.findAllCategoriesWithImagesByParentId(category.getId().toString());

        if (!childCategories.isEmpty()) {
            List<CategoryResponse> childResponses = childCategories.stream()
                    .map(this::mapCategoryToResponse)
                    .collect(Collectors.toList());
            response.setCategoriesChild(childResponses);
        }

        return response;
    }

    @Override
    public CategoryResponse getCategoryById(String id) {
        return null;
    }

    @Override
    public void deleteCategoryById(String id) {
        Optional<Category> category = categoryRepository.findById(UUID.fromString(id));
        if (category.isEmpty()) {
            throw new NotFoundException(id + " does not exist");
        }
        // code kiểm tra category tồn tại product chưa
        categoryRepository.delete(category.get());
    }
}
