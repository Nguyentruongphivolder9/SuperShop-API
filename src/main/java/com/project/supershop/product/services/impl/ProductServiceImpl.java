package com.project.supershop.product.services.impl;

import com.project.supershop.common.ResultResponse;
import com.project.supershop.product.domain.dto.requests.ProductRequest;
import com.project.supershop.product.domain.dto.requests.ProductVariantRequest;
import com.project.supershop.product.domain.dto.requests.VariantGroupRequest;
import com.project.supershop.product.domain.dto.requests.VariantRequest;
import com.project.supershop.product.domain.dto.responses.ProductResponse;
import com.project.supershop.product.domain.entities.Product;
import com.project.supershop.product.domain.entities.ProductVariant;
import com.project.supershop.product.domain.entities.Variant;
import com.project.supershop.product.domain.entities.VariantGroup;
import com.project.supershop.product.repositories.ProductRepository;
import com.project.supershop.product.repositories.ProductVariantRepository;
import com.project.supershop.product.repositories.VariantGroupRepository;
import com.project.supershop.product.repositories.VariantRepository;
import com.project.supershop.product.services.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private ModelMapper modelMapper;
    private ProductRepository productRepository;
    private VariantGroupRepository variantGroupRepository;
    private VariantRepository variantRepository;
    private ProductVariantRepository productVariantRepository;

    public ProductServiceImpl(ModelMapper modelMapper, ProductVariantRepository productVariantRepository, VariantGroupRepository variantGroupRepository, ProductRepository productRepository, VariantRepository variantRepository) {
        this.modelMapper = modelMapper;
        this.variantGroupRepository = variantGroupRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public ResultResponse<ProductResponse> createProduct(ProductRequest productRequest) {
        List<VariantGroup> variantGroups = new ArrayList<>();
        List<Variant> variants = new ArrayList<>();
        List<ProductVariant> productVariants = new ArrayList<>();
        Product product = Product.createProduct(productRequest);
        Product productResult = productRepository.save(product);
        if(!productRequest.getIsVariant()) {
            if(productRequest.getPrice() == null || productRequest.getPrice() == 0){
                throw new RuntimeException("error");
            }

            if(productRequest.getStockQuantity() == null || productRequest.getStockQuantity() <= 0){
                throw new RuntimeException("error");
            }
        } else {
            if(productRequest.getStockQuantity() == null
                    && productRequest.getPrice() == null
                    && !productRequest.getVariantsGroup().isEmpty()
                    && !productRequest.getProductVariants().isEmpty()){

                Set<String> variantGroupNames = new HashSet<>();
                Map<String, Set<String>> variantsGroupMap = new HashMap<>();
                // kiểm tra xem field name của variantsGroup không được trùng nhau
                for (VariantGroupRequest groupRequest : productRequest.getVariantsGroup()) {
                    if (!variantGroupNames.add(groupRequest.getName())) {
                        throw new RuntimeException("error");
                    }

                    VariantGroup variantGroupBuild = VariantGroup.createVariantGroup(groupRequest, productResult);
                    VariantGroup variantGroupResult = variantGroupRepository.save(variantGroupBuild);
                    variantGroups.add(variantGroupResult);

                    Set<String> variantNames = new HashSet<>();
                    // kiểm tra xem các field name của variant không được trùng nhau
                    for (VariantRequest variantRequest : groupRequest.getVariants()) {
                        if (!variantNames.add(variantRequest.getName())) {
                            throw new RuntimeException("error");
                        }

                        // kiểm tra isPrimary là true thì variant mới được chứa hình ảnh hoặc null
                        if (!groupRequest.getIsPrimary() && (variantRequest.getImageUrl() != null && !variantRequest.getImageUrl().isEmpty())) {
                            throw new RuntimeException("error");
                        }

                        Variant variant = Variant.createVariant(variantRequest, variantGroupResult);
                        Variant variantResult = variantRepository.save(variant);
                        variants.add(variantResult);
                    }
                    variantsGroupMap.put(groupRequest.getName(), variantNames);
                }

                if(variantGroups.toArray().length == 1
                        && variants.toArray().length != productRequest.getProductVariants().toArray().length){
                    throw new RuntimeException("error");
                }

                Set<String> variantPairs = new HashSet<>();
                Set<String> variantExistsOneVariantGroup = new HashSet<>();
                for (ProductVariantRequest variantRequest : productRequest.getProductVariants()) {
                    String group1 = variantRequest.getVariantsGroup1();
                    String group2 = variantRequest.getVariantsGroup2();

                    if(variantsGroupMap.get(group1) == null){
                        throw new RuntimeException("error");
                    }
                    // Kiểm tra variant1 phải tồn tại trong variantsGroup1
                    if (!variantsGroupMap.get(group1).contains(variantRequest.getVariant1())) {
                        throw new RuntimeException("error");
                    }

                    if(variantRequest.getPrice() == null || variantRequest.getPrice() == 0){
                        throw new RuntimeException("error");
                    }

                    // trả về lối nếu StockQuantity là null hoặc <= 0
                    if(variantRequest.getStockQuantity() == null || variantRequest.getStockQuantity() <= 0){
                        throw new RuntimeException("error");
                    }

                    if(variantGroups.toArray().length == 1
                            && productRequest.getProductVariants().toArray().length == variants.toArray().length){
                        ProductVariant productVariantBuild = ProductVariant.createVariant(variantRequest, productResult);
                        // trả về lối nếu VariantsGroup2 tôn tại dữ liệu
                        if(variantRequest.getVariantsGroup2() != null || variantRequest.getVariant2() != null){
                            throw new RuntimeException("error");
                        }

                        if(!variantExistsOneVariantGroup.add(variantRequest.getVariant1())){
                            throw new RuntimeException("error");
                        }
                        for(Variant variant : variants){

                            if(variant.getName().equals(variantRequest.getVariant1())){
                                productVariantBuild.setVariant1(variant);
                            }
                        }
                        ProductVariant productVariant = productVariantRepository.save(productVariantBuild);
                        productVariants.add(productVariant);
                    } else {
                        // Kiểm tra variantsGroup1 và variantsGroup2 không được trùng nhau
                        if (group1.equals(group2)) {
                            throw new RuntimeException("error");
                        }

                        // Kiểm tra group1 và group2 phải tồn tại trong variantsGroup
                        if (!variantsGroupMap.containsKey(group1) || !variantsGroupMap.containsKey(group2)) {
                            throw new RuntimeException("error");
                        }

                        // Kiểm tra variant2 phải tồn tại trong variantsGroup2
                        if (!variantsGroupMap.get(group2).contains(variantRequest.getVariant2())) {
                            throw new RuntimeException("error");
                        }

                        // Kiểm tra cặp variant1 và variant2 không được giống nha
                        String variantPair = variantRequest.getVariant1() + "-" + variantRequest.getVariant2();
                        if (!variantPairs.add(variantPair)) {
                            throw new RuntimeException("error");
                        }

                        // save ProductVariant
                        ProductVariant productVariantBuild = ProductVariant.createVariant(variantRequest, productResult);
                        for(Variant variant : variants){

                            if(variant.getName().equals(variantRequest.getVariant1())){
                                productVariantBuild.setVariant1(variant);
                            }

                            if(variant.getName().equals(variantRequest.getVariant2())){
                                productVariantBuild.setVariant2(variant);
                            }
                        }
                        ProductVariant productVariant = productVariantRepository.save(productVariantBuild);
                        productVariants.add(productVariant);
                    }
                }

            } else {
                throw new RuntimeException("error");
            }
        }

        for (VariantGroup variantGroup : variantGroups){
            List<Variant> variantList = new ArrayList<>();
            for (Variant variant : variants){
                if(variantGroup.getId() == variant.getVariantGroup().getId()){
                    variantList.add(variant);
                }
            }
            variantGroup.setVariants(variantList);
        }

        productResult.setProductVariants(productVariants);
        productResult.setVariantsGroup(variantGroups);

        ProductResponse productResponse = modelMapper.map(productResult, ProductResponse.class);
        return ResultResponse.<ProductResponse>builder()
                .body(productResponse)
                .message("Create product successfully")
                .statusCode(HttpStatus.CREATED.value())
                .build();
    }
}