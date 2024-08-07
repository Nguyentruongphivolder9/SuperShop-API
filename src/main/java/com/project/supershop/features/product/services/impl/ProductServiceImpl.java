package com.project.supershop.features.product.services.impl;

import com.project.supershop.features.account.domain.entities.Account;
<<<<<<< HEAD
import com.project.supershop.features.auth.services.AccessTokenService;
=======
>>>>>>> e1c9ffec31b7323c9e55728c935b37448b60bd0a
import com.project.supershop.features.auth.services.JwtTokenService;
import com.project.supershop.features.product.domain.dto.requests.*;
import com.project.supershop.features.product.domain.dto.responses.ProductResponse;
import com.project.supershop.features.product.domain.entities.*;
import com.project.supershop.features.product.repositories.*;
import com.project.supershop.features.product.services.ProductService;
import com.project.supershop.handler.NotFoundException;
import com.project.supershop.handler.UnprocessableException;
import com.project.supershop.utils.ArrayUtils;
import com.project.supershop.utils.CheckTypeUUID;
import io.github.dengliming.redismodule.redisjson.RedisJSON;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final VariantGroupRepository variantGroupRepository;
    private final VariantRepository variantRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final PreviewImageRepository previewImageRepository;
    private final CategoryRepository categoryRepository;
<<<<<<< HEAD
    private final JwtTokenService accessTokenService;
    private final RedisJSON redisJSON;

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository, VariantGroupRepository variantGroupRepository, VariantRepository variantRepository, ProductVariantRepository productVariantRepository, ProductImageRepository productImageRepository, PreviewImageRepository previewImageRepository, CategoryRepository categoryRepository, JwtTokenService accessTokenService, RedisJSON redisJSON) {
=======
    private final JwtTokenService jwtTokenService;
    private final RedisJSON redisJSON;

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository, VariantGroupRepository variantGroupRepository, VariantRepository variantRepository, ProductVariantRepository productVariantRepository, ProductImageRepository productImageRepository, PreviewImageRepository previewImageRepository, CategoryRepository categoryRepository, JwtTokenService jwtTokenService, RedisJSON redisJSON) {
>>>>>>> e1c9ffec31b7323c9e55728c935b37448b60bd0a
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.variantGroupRepository = variantGroupRepository;
        this.variantRepository = variantRepository;
        this.productVariantRepository = productVariantRepository;
        this.productImageRepository = productImageRepository;
        this.previewImageRepository = previewImageRepository;
        this.categoryRepository = categoryRepository;
        this.jwtTokenService = jwtTokenService;
        this.redisJSON = redisJSON;
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest, String jwtToken) {
        List<VariantGroup> variantGroups = new ArrayList<>();
        List<Variant> variants = new ArrayList<>();
        List<ProductVariant> productVariants = new ArrayList<>();
        List<ProductImage> productImages = new ArrayList<>();

        Account parseJwtToAccount = jwtTokenService.parseJwtTokenToAccount(jwtToken);
        Optional<Category> resultCateFindById = categoryRepository.findById(Integer.parseInt(productRequest.getCategoryId()));

        if(productRequest.getCategoryId().isEmpty()){
            throw new UnprocessableException("The categoryId field can't be empty.");
        }

        if(resultCateFindById.isEmpty()) {
            throw new NotFoundException(productRequest.getCategoryId() + " does not exist");
        }

        Product product = Product.createProduct(productRequest, resultCateFindById.get(), parseJwtToAccount);
        Product  productResult = productRepository.save(product);

        if(productRequest.getProductImages().isEmpty() || productRequest.getProductImages().size() < 3 || productRequest.getProductImages().size() > 9){
            throw new UnprocessableException("The product image field must not be blank and contain 3 or more images and a maximum of 9 images.");
        }

        if(!productRequest.getIsVariant()) {
            if(productRequest.getPrice() == null || productRequest.getPrice() < 1000 || productRequest.getPrice() > 120000000){
                throw new UnprocessableException("The product price field cannot be empty and must be between 1,000 and 120,000,000.");
            }

            if(productRequest.getStockQuantity() == null || productRequest.getStockQuantity() <= 0 || productRequest.getStockQuantity() > 10000000){
                    throw new UnprocessableException("The product stock field must be greater than 0 and less than 10,000,000.");
            }
        }

        if(productRequest.getStockQuantity() == null
                && productRequest.getPrice() == null
                && productRequest.getVariantsGroup().isEmpty()
                && productRequest.getProductVariants().isEmpty()
        ){
            throw new UnprocessableException("The product stock field can't be empty or less than 0.");
        }

        // thêm hình ảnh của product
        boolean isFirstImage = true;
        for (ProductImagesRequest imageRequest : productRequest.getProductImages()) {
            Optional<PreviewImage> productImage = previewImageRepository.findById(UUID.fromString(imageRequest.getId()));
            if(productImage.isEmpty()){
                throw new UnprocessableException("The product image field can't be empty.");
            }
            if (imageRequest.getImageUrl() == null) {
                throw new UnprocessableException("The product image field can't be empty.");
            }

            ProductImage resultProductImage = ProductImage.createProductImage(imageRequest.getImageUrl(), isFirstImage, productResult);

            isFirstImage = false;
            productImages.add(resultProductImage);
            previewImageRepository.deleteById(UUID.fromString(imageRequest.getId()));
        }
        productImageRepository.saveAll(productImages);

        if(productRequest.getIsVariant()
                && productRequest.getStockQuantity() == null
                && productRequest.getPrice() == null
                && !productRequest.getVariantsGroup().isEmpty()
                && !productRequest.getProductVariants().isEmpty()
        ){
            if(productRequest.getVariantsGroup().size() > 2) {
                throw new UnprocessableException("Product variants contain up to 2 groups of variants.");
            }

            Set<String> variantGroupNames = new HashSet<>();
            Map<String, Set<String>> variantsGroupMap = new HashMap<>();
            for (VariantGroupRequest groupRequest : productRequest.getVariantsGroup()) { // kiểm tra xem field name của variantsGroup không được trùng nhau
                if (!variantGroupNames.add(groupRequest.getName())) {
                    throw new UnprocessableException(groupRequest.getName() + " is the same as the variation name of another variation");
                }

                if(groupRequest.getVariants().isEmpty() || groupRequest.getVariants().size() > 50) {
                    throw new UnprocessableException("In a group of variants containing only 1 to 50 variants.");
                }

                VariantGroup variantGroupBuild = VariantGroup.createVariantGroup(groupRequest, productResult);
                VariantGroup variantGroupResult = variantGroupRepository.save(variantGroupBuild);
                variantGroups.add(variantGroupResult);

                Set<String> variantNames = new HashSet<>();
                Set<String> variantIds = new HashSet<>();
                for (VariantRequest variantRequest : groupRequest.getVariants()) {
                    if (!variantNames.add(variantRequest.getName())) { // kiểm tra xem các field name của variant không được trùng nhau
                        throw new UnprocessableException(variantRequest.getName() + " is the same as the variant name of another variant");
                    }

                    if (!groupRequest.getIsPrimary() && (variantRequest.getVariantImage().getImageUrl() != null)) { //kiểm tra isPrimary là true thì variant mới được chứa hình ảnh hoặc null
                        throw new UnprocessableException("IsPrimary field is true, then the new variant will contain an image or null");
                    }

                    if(variantRequest.getVariantImage().getId() != null) {
                        previewImageRepository.deleteById(UUID.fromString(variantRequest.getVariantImage().getId()));
                    }


                    Variant variant = Variant.createVariant(variantRequest.getName(), variantRequest.getVariantImage().getImageUrl(), variantGroupResult);
                    Variant variantResult = variantRepository.save(variant);
                    variantIds.add(variantResult.getId().toString());

                    for(ProductVariantRequest productVariantRequest : productRequest.getProductVariants()){
                        if(productVariantRequest.getVariantsGroup1Id().equals(groupRequest.getId())
                                && productVariantRequest.getVariant1Id().equals(variantRequest.getId())){
                            productVariantRequest.setVariant1Id(variantResult.getId().toString());
                            productVariantRequest.setVariantsGroup1Id(variantGroupResult.getId().toString());
                        }
                        if(productVariantRequest.getVariantsGroup2Id().equals(groupRequest.getId())
                                && productVariantRequest.getVariant2Id().equals(variantRequest.getId())){
                            productVariantRequest.setVariant2Id(variantResult.getId().toString());
                            productVariantRequest.setVariantsGroup2Id(variantGroupResult.getId().toString());
                        }
                    }
                    variants.add(variantResult);
                }
                variantsGroupMap.put(variantGroupResult.getId().toString(), variantIds);
            }

            Set<String> variantPairs = new HashSet<>();
            Set<String> variantExistsOneVariantGroup = new HashSet<>();

            for (ProductVariantRequest variantRequest : productRequest.getProductVariants()) {
                String group1 = variantRequest.getVariantsGroup1Id();
                String group2 = variantRequest.getVariantsGroup2Id();

                // Kiểm tra variantsGroup1 phải tồn tại trong variantsGroupMap
                if (!variantsGroupMap.containsKey(group1)) {
                    throw new UnprocessableException("The variantsGroup1Id field named " + group1 + " must exist in variations.");
                }

                // Kiểm tra variant1Id phải tồn tại trong variantsGroup1
                if (!variantsGroupMap.get(group1).contains(variantRequest.getVariant1Id())) {
                    throw new UnprocessableException("The variant1Id field named " + variantRequest.getVariant1Id() + " must exist in variations " + group1);
                }

                // Kiểm tra giá sản phẩm không được null hoặc bé hơn 1000 và lớn hơn 120,000,000
                if (variantRequest.getPrice() == null || variantRequest.getPrice() < 1000 || variantRequest.getPrice() > 120000000) {
                    throw new UnprocessableException("The product price field cannot be empty and must be between 1,000 and 120,000,000.");
                }

                // Kiểm tra StockQuantity phải lớn hơn 0 và bé hơn 10,000,000
                if (variantRequest.getStockQuantity() == null || variantRequest.getStockQuantity() <= 0 || variantRequest.getStockQuantity() > 10000000) {
                    throw new UnprocessableException("The product stock field must be greater than 0 and less than 10,000,000.");
                }

                // Kiểm tra khi chỉ có một variantsGroup thì sẽ không có variantsGroup2 và variant2Id trong productVariant
                if (variantGroups.size() == 1 && variantRequest.getVariantsGroup2Id() == null && variantRequest.getVariant2Id() == null) {

                    // Kiểm tra chỉ có một variant1Id trong mỗi variantsGroup1
                    if (!variantExistsOneVariantGroup.add(variantRequest.getVariant1Id())) {
                        throw new UnprocessableException("Only one variant1Id is allowed in variantsGroup1");
                    }

                    // Tìm variant1 và thiết lập vào productVariantBuild
                    Variant variant1 = null;
                    for (Variant variant : variants) {
                        if (variant.getId().toString().equals(variantRequest.getVariant1Id())) {
                            variant1 = variant;
                            break;
                        }
                    }

                    // Lưu ProductVariant và thêm vào danh sách productVariants
                    ProductVariant productVariantBuild = ProductVariant.createProductVariant(variantRequest, productResult, variant1, null);
                    ProductVariant productVariant = productVariantRepository.save(productVariantBuild);
                    productVariants.add(productVariant);
                } else {
                    // Kiểm tra variantsGroup1 và variantsGroup2 không được giống nhau
                    if (group1.equals(group2)) {

                        throw new RuntimeException("variantsGroup1 and variantsGroup2 must be different");
                    }

                    // Kiểm tra variantsGroup1 và variantsGroup2 phải tồn tại trong variantsGroupMap
                    if (!variantsGroupMap.containsKey(group1) || !variantsGroupMap.containsKey(group2)) {
                        throw new UnprocessableException("variantsGroup1 " + group1 + " or variantsGroup2 " + group2 + " not found");
                    }

                    // Kiểm tra variant2Id phải tồn tại trong variantsGroup2
                    if (!variantsGroupMap.get(group2).contains(variantRequest.getVariant2Id())) {
                        throw new UnprocessableException("The variant1Id field named " + variantRequest.getVariant2Id() + " must exist in variations " + group2);
                    }

                    // Kiểm tra cặp variant1Id và variant2Id không được trùng nhau
                    String variantPair = variantRequest.getVariant1Id() + "-" + variantRequest.getVariant2Id();
                    if (!variantPairs.add(variantPair)) {
                        throw new UnprocessableException("Variant pair " + variantPair + " already exists");
                    }

                    // Tạo ProductVariant và thiết lập variant1 và variant2 vào đó
                    Variant variant1 = null;
                    Variant variant2 = null;
                    for (Variant variant : variants) {
                        if (variant.getId().toString().equals(variantRequest.getVariant1Id())) {
                            variant1 = variant;
                        }
                        if (variant.getId().toString().equals(variantRequest.getVariant2Id())) {
                            variant2 = variant;
                        }
                    }

                    // Lưu ProductVariant và thêm vào danh sách productVariants
                    ProductVariant productVariantBuild = ProductVariant.createProductVariant(variantRequest, productResult, variant1, variant2);
                    ProductVariant productVariant = productVariantRepository.save(productVariantBuild);
                    productVariants.add(productVariant);
                }
            }
        }

        if(!variantGroups.isEmpty()){
            for (VariantGroup variantGroup : variantGroups){
                List<Variant> variantList = new ArrayList<>();
                for (Variant variant : variants){
                    if(variantGroup.getId().equals(variant.getVariantGroup().getId())){
                        variantList.add(variant);
                    }
                }
                variantGroup.setVariants(variantList);
            }
        }

        productResult.setProductVariants(productVariants);
        productResult.setVariantsGroup(variantGroups);
        productResult.setProductImages(productImages);

        mapProductToProductResponse(productResult);
        ProductResponse productResponse = modelMapper.map(productResult, ProductResponse.class);
//        String key = "product:" + productResponse.getId();
//        redisJSON.set(key, SetArgs.Builder.create(".", GsonUtils.toJson(productResponse)));
        return productResponse;
    }

    @Override
    public ProductResponse updateProduct(ProductRequest productRequest, String jwtToken) {
        List<VariantGroup> variantGroups = new ArrayList<>();
        List<Variant> variants = new ArrayList<>();
        List<ProductVariant> productVariants = new ArrayList<>();
        List<ProductImage> productImages = new ArrayList<>();

        Account parseJwtToAccount = jwtTokenService.parseJwtTokenToAccount(jwtToken);
        Optional<Category> resultCateFindById = categoryRepository.findById(Integer.parseInt(productRequest.getCategoryId()));

        if(productRequest.getCategoryId().isEmpty()){
            throw new UnprocessableException("The categoryId field can't be empty.");
        }

        if(resultCateFindById.isEmpty()) {
            throw new NotFoundException(productRequest.getCategoryId() + " does not exist");
        }

        Optional<Product> productOptional = productRepository.findByProductIdOfProductOfShop(UUID.fromString(productRequest.getId()), UUID.fromString(productRequest.getShopId()));
        if(productOptional.isEmpty()){
            throw new NotFoundException("Product does not exists.");
        }

        productOptional.get().setShop(parseJwtToAccount);
        productOptional.get().setName(productRequest.getName());
        productOptional.get().setPrice(productRequest.getPrice());
        productOptional.get().setStockQuantity(productRequest.getStockQuantity());
        productOptional.get().setConditionProduct(productRequest.getConditionProduct());
        productOptional.get().setDescription(productRequest.getDescription());
        productOptional.get().setCategory(resultCateFindById.get());
        productOptional.get().setIsVariant(productRequest.getIsVariant());
        productOptional.get().setIsActive(productRequest.getIsActive());
        Product  productResult = productRepository.save(productOptional.get());

        if(productRequest.getProductImages().isEmpty() || productRequest.getProductImages().size() < 3 || productRequest.getProductImages().size() > 9){
            throw new UnprocessableException("The product image field must not be blank and contain 3 or more images and a maximum of 9 images.");
        }

        if(!productRequest.getIsVariant()) {
            if(productRequest.getPrice() == null || productRequest.getPrice() < 1000 || productRequest.getPrice() > 120000000){
                throw new UnprocessableException("The product price field cannot be empty and must be between 1,000 and 120,000,000.");
            }

            if(productRequest.getStockQuantity() == null || productRequest.getStockQuantity() < 0 || productRequest.getStockQuantity() > 10000000){
                throw new UnprocessableException("The product stock field must be greater than 0 and less than 10,000,000.");
            }
        }

        if(productRequest.getStockQuantity() == null
                && productRequest.getPrice() == null
                && productRequest.getVariantsGroup().isEmpty()
                && productRequest.getProductVariants().isEmpty()
        ){
            throw new UnprocessableException("The product stock field can't be empty or less than 0.");
        }

        // thêm hình ảnh của product
        boolean isFirstImage = true;
        for (ProductImagesRequest imageRequest : productRequest.getProductImages()) {
            Optional<ProductImage> productImageOptional = productImageRepository.findById(UUID.fromString(imageRequest.getId()));
            ProductImage productImage;

            if (productImageOptional.isEmpty()) {
                Optional<PreviewImage> productPreviewImage = previewImageRepository.findById(UUID.fromString(imageRequest.getId()));
                if (productPreviewImage.isEmpty() || imageRequest.getImageUrl() == null) {
                    throw new UnprocessableException("The product image field can't be empty.");
                }

                productImage = ProductImage.createProductImage(imageRequest.getImageUrl(), isFirstImage, productResult);
                previewImageRepository.deleteById(UUID.fromString(imageRequest.getId()));
            } else {
                productImage = productImageOptional.get();
                productImage.setImageUrl(imageRequest.getImageUrl());
                productImage.setIsPrimary(isFirstImage);
                productImage.setProduct(productResult);
            }

            productImages.add(productImage);
            isFirstImage = false;
        }
        productImageRepository.saveAll(productImages);

        if(productRequest.getIsVariant()
                && productRequest.getStockQuantity() == null
                && productRequest.getPrice() == null
                && !productRequest.getVariantsGroup().isEmpty()
                && !productRequest.getProductVariants().isEmpty()
        ){
            if(productRequest.getVariantsGroup().size() > 2) {
                throw new UnprocessableException("Product variants contain up to 2 groups of variants.");
            }

            Set<String> variantGroupNames = new HashSet<>();
            Map<String, Set<String>> variantsGroupMap = new HashMap<>();
            for (VariantGroupRequest groupRequest : productRequest.getVariantsGroup()) { // kiểm tra xem field name của variantsGroup không được trùng nhau
                if (!variantGroupNames.add(groupRequest.getName())) {
                    throw new UnprocessableException(groupRequest.getName() + " is the same as the variation name of another variation");
                }

                if(groupRequest.getVariants().isEmpty() || groupRequest.getVariants().size() > 50) {
                    throw new UnprocessableException("In a group of variants containing only 1 to 50 variants.");
                }

                VariantGroup variantGroupBuild;
                boolean checkTypeUUIDVariantGroup = CheckTypeUUID.isValidUUID(groupRequest.getId());
                // nếu checkTypeUUID là true thì update dựa trên id đó luôn
                // nếu như id đó không tồn tại trong database thì nó tự tạo mới
                if(checkTypeUUIDVariantGroup) {
                    Optional<VariantGroup> variantGroupOptional = variantGroupRepository.findById(UUID.fromString(groupRequest.getId()));
                    if(variantGroupOptional.isPresent()){
                        variantGroupBuild = variantGroupOptional.get();
                        variantGroupBuild.setName(groupRequest.getName());
                        variantGroupBuild.setIsPrimary(groupRequest.getIsPrimary());
                        variantGroupBuild.setProduct(productResult);
                    } else {
                        variantGroupBuild = VariantGroup.createVariantGroup(groupRequest, productResult);
                    }
                } else {
                    variantGroupBuild = VariantGroup.createVariantGroup(groupRequest, productResult);
                }

                VariantGroup variantGroupResult = variantGroupRepository.save(variantGroupBuild);
                variantGroups.add(variantGroupResult);

                Set<String> variantNames = new HashSet<>();
                Set<String> variantIds = new HashSet<>();
                for (VariantRequest variantRequest : groupRequest.getVariants()) {
                    if (!variantNames.add(variantRequest.getName())) { // kiểm tra xem các field name của variant không được trùng nhau
                        throw new UnprocessableException(variantRequest.getName() + " is the same as the variant name of another variant");
                    }

                    if (!groupRequest.getIsPrimary() && (variantRequest.getVariantImage().getImageUrl() != null)) { //kiểm tra isPrimary là true thì variant mới được chứa hình ảnh hoặc null
                        throw new UnprocessableException("IsPrimary field is true, then the new variant will contain an image or null");
                    }

                    if(variantRequest.getVariantImage().getId() != null) {
                        boolean checkTypeUUIDPreviewImage = CheckTypeUUID.isValidUUID(variantRequest.getVariantImage().getId());
                        if(checkTypeUUIDPreviewImage) previewImageRepository.deleteById(UUID.fromString(variantRequest.getVariantImage().getId()));
                    }

                    Variant variantBuild;
                    boolean checkTypeUUIDVariant = CheckTypeUUID.isValidUUID(variantRequest.getId());
                    // nếu checkTypeUUID là true thì update dựa trên id đó luôn
                    // nếu như id đó không tồn tại trong database thì nó tự tạo mới
                    if(checkTypeUUIDVariant) {
                        Optional<Variant> variantOptional = variantRepository.findById(UUID.fromString(variantRequest.getId()));
                        if(variantOptional.isPresent()){
                            variantBuild = variantOptional.get();
                            variantBuild.setName(variantRequest.getName());
                            variantBuild.setImageUrl(variantRequest.getVariantImage().getImageUrl());
                            variantBuild.setVariantGroup(variantGroupResult);
                        } else {
                            variantBuild = Variant.createVariant(variantRequest.getName(), variantRequest.getVariantImage().getImageUrl(), variantGroupResult);
                        }
                    } else {
                        variantBuild = Variant.createVariant(variantRequest.getName(), variantRequest.getVariantImage().getImageUrl(), variantGroupResult);
                    }

                    Variant variantResult = variantRepository.save(variantBuild);
                    variantIds.add(variantResult.getId().toString());

                    for(ProductVariantRequest productVariantRequest : productRequest.getProductVariants()){
                        if(productVariantRequest.getVariantsGroup1Id().equals(groupRequest.getId())
                                && productVariantRequest.getVariant1Id().equals(variantRequest.getId())){
                            productVariantRequest.setVariant1Id(variantResult.getId().toString());
                            productVariantRequest.setVariantsGroup1Id(variantGroupResult.getId().toString());
                        }
                        if(productVariantRequest.getVariantsGroup2Id().equals(groupRequest.getId())
                                && productVariantRequest.getVariant2Id().equals(variantRequest.getId())){
                            productVariantRequest.setVariant2Id(variantResult.getId().toString());
                            productVariantRequest.setVariantsGroup2Id(variantGroupResult.getId().toString());
                        }
                    }
                    variants.add(variantResult);
                }
                variantsGroupMap.put(variantGroupResult.getId().toString(), variantIds);
            }

            Set<String> variantPairs = new HashSet<>();
            Set<String> variantExistsOneVariantGroup = new HashSet<>();

            for (ProductVariantRequest productVariantRequest : productRequest.getProductVariants()) {
                String group1 = productVariantRequest.getVariantsGroup1Id();
                String group2 = productVariantRequest.getVariantsGroup2Id();

                // Kiểm tra variantsGroup1 phải tồn tại trong variantsGroupMap
                if (!variantsGroupMap.containsKey(group1)) {
                    throw new UnprocessableException("The variantsGroup1Id field named " + group1 + " must exist in variations.");
                }

                // Kiểm tra variant1Id phải tồn tại trong variantsGroup1
                if (!variantsGroupMap.get(group1).contains(productVariantRequest.getVariant1Id())) {
                    throw new UnprocessableException("The variant1Id field named " + productVariantRequest.getVariant1Id() + " must exist in variations " + group1);
                }

                // Kiểm tra giá sản phẩm không được null hoặc bé hơn 1000 và lớn hơn 120,000,000
                if (productVariantRequest.getPrice() == null || productVariantRequest.getPrice() <= 1000 || productVariantRequest.getPrice() > 120000000) {
                    throw new UnprocessableException("The product price field cannot be empty and must be between 1,000 and 120,000,000.");
                }

                // Kiểm tra StockQuantity phải lớn hơn 0 và bé hơn 10,000,000
                if (productVariantRequest.getStockQuantity() == null || productVariantRequest.getStockQuantity() < 0 || productVariantRequest.getStockQuantity() > 10000000) {
                    throw new UnprocessableException("The product stock field must be greater than 0 and less than 10,000,000.");
                }

                // Kiểm tra khi chỉ có một variantsGroup thì sẽ không có variantsGroup2 và variant2Id trong productVariant
                if (variantGroups.size() == 1 && productVariantRequest.getVariantsGroup2Id() == null && productVariantRequest.getVariant2Id() == null) {

                    // Kiểm tra chỉ có một variant1Id trong mỗi variantsGroup1
                    if (!variantExistsOneVariantGroup.add(productVariantRequest.getVariant1Id())) {
                        throw new UnprocessableException("Only one variant1Id is allowed in variantsGroup1");
                    }

                    // Tìm variant1 và thiết lập vào productVariantBuild
                    Variant variant1 = null;
                    for (Variant variant : variants) {
                        if (variant.getId().toString().equals(productVariantRequest.getVariant1Id())) {
                            variant1 = variant;
                            break;
                        }
                    }

                    ProductVariant productVariantBuild;
                    boolean checkTypeUUIDProductVariant = CheckTypeUUID.isValidUUID(productVariantRequest.getId());
                    // nếu checkTypeUUID là true thì update dựa trên id đó luôn
                    // nếu như id đó không tồn tại trong database thì nó tự tạo mới
                    if(checkTypeUUIDProductVariant) {
                        Optional<ProductVariant> productVariantOptional = productVariantRepository.findById(UUID.fromString(productVariantRequest.getId()));
                        if (productVariantOptional.isPresent()){
                            productVariantBuild = productVariantOptional.get();
                            productVariantBuild.setPrice(productVariantRequest.getPrice());
                            productVariantBuild.setStockQuantity(productVariantRequest.getStockQuantity());
                            productVariantBuild.setProduct(productResult);
                            productVariantBuild.setVariant1(variant1);
                            productVariantBuild.setVariant2(null);
                        } else {
                            productVariantBuild = ProductVariant.createProductVariant(productVariantRequest, productResult, variant1, null);
                        }
                    } else {
                        productVariantBuild = ProductVariant.createProductVariant(productVariantRequest, productResult, variant1, null);
                    }

                    ProductVariant productVariant = productVariantRepository.save(productVariantBuild);
                    productVariants.add(productVariant);
                } else {
                    // Kiểm tra variantsGroup1 và variantsGroup2 không được giống nhau
                    if (group1.equals(group2)) {

                        throw new RuntimeException("variantsGroup1 and variantsGroup2 must be different");
                    }

                    // Kiểm tra variantsGroup1 và variantsGroup2 phải tồn tại trong variantsGroupMap
                    if (!variantsGroupMap.containsKey(group1) || !variantsGroupMap.containsKey(group2)) {
                        throw new UnprocessableException("variantsGroup1 " + group1 + " or variantsGroup2 " + group2 + " not found");
                    }

                    // Kiểm tra variant2Id phải tồn tại trong variantsGroup2
                    if (!variantsGroupMap.get(group2).contains(productVariantRequest.getVariant2Id())) {
                        throw new UnprocessableException("The variant1Id field named " + productVariantRequest.getVariant2Id() + " must exist in variations " + group2);
                    }

                    // Kiểm tra cặp variant1Id và variant2Id không được trùng nhau
                    String variantPair = productVariantRequest.getVariant1Id() + "-" + productVariantRequest.getVariant2Id();
                    if (!variantPairs.add(variantPair)) {
                        throw new UnprocessableException("Variant pair " + variantPair + " already exists");
                    }

                    // Tạo ProductVariant và thiết lập variant1 và variant2 vào đó
                    Variant variant1 = null;
                    Variant variant2 = null;
                    for (Variant variant : variants) {
                        if (variant.getId().toString().equals(productVariantRequest.getVariant1Id())) {
                            variant1 = variant;
                        }
                        if (variant.getId().toString().equals(productVariantRequest.getVariant2Id())) {
                            variant2 = variant;
                        }
                    }

                    ProductVariant productVariantBuild;
                    boolean checkTypeUUIDProductVariant = CheckTypeUUID.isValidUUID(productVariantRequest.getId());
                    // nếu checkTypeUUID là true thì update dựa trên id đó luôn
                    // nếu như id đó không tồn tại trong database thì nó tự tạo mới
                    if(checkTypeUUIDProductVariant) {
                        Optional<ProductVariant> productVariantOptional = productVariantRepository.findById(UUID.fromString(productVariantRequest.getId()));
                        if (productVariantOptional.isPresent()){
                            productVariantBuild = productVariantOptional.get();
                            productVariantBuild.setPrice(productVariantRequest.getPrice());
                            productVariantBuild.setStockQuantity(productVariantRequest.getStockQuantity());
                            productVariantBuild.setProduct(productResult);
                            productVariantBuild.setVariant1(variant1);
                            productVariantBuild.setVariant2(variant2);
                        } else {
                            productVariantBuild = ProductVariant.createProductVariant(productVariantRequest, productResult, variant1, variant2);
                        }
                    } else {
                        productVariantBuild = ProductVariant.createProductVariant(productVariantRequest, productResult, variant1, variant2);
                    }
                    ProductVariant productVariant = productVariantRepository.save(productVariantBuild);
                    productVariants.add(productVariant);
                }
            }
        }

        // xóa dữ liệu cũ
        List<VariantGroup> findAllVariantGroup = variantGroupRepository.findAllVariantGroupByProductId(UUID.fromString(productRequest.getId()));
        List<ProductVariant> findAllProductVariant = productVariantRepository.findAllProductVariantByProductId(UUID.fromString(productRequest.getId()));
        if(!findAllVariantGroup.isEmpty()) {
            for (VariantGroup variantGroup : findAllVariantGroup){
                VariantGroup foundVariantGroup = ArrayUtils.findById(variantGroups, variantGroupUpdate -> variantGroupUpdate.getId().equals(variantGroup.getId()));
                List<Variant> findAllVariants = variantRepository.findAllVariantsByVariantGroupId(variantGroup.getId());
                // kiểm tra xem variant group có trong dữ liệu cập nhật không
                // nếu không thì xóa variant group đó đi
                // trước khi xóa variant group phải xoá hết các collection của variant có foreignKey là primaryKey của variant group
                if (foundVariantGroup == null) {
                    for (Variant variantToDelete : findAllVariants) {
                        // trước khi xóa variant thì phải xóa hết các collection của product variant có foreignKey là primaryKey của variant
                        for (ProductVariant productVariantToDelete : findAllProductVariant) {
                            if(variantGroup.getIsPrimary()) {
                                if(productVariantToDelete.getVariant1().getId() == variantToDelete.getId()){
                                    productVariantRepository.delete(productVariantToDelete);
                                }
                            } else {
                                if(productVariantToDelete.getVariant2().getId() == variantToDelete.getId()){
                                    productVariantRepository.delete(productVariantToDelete);
                                }
                            }
                        }
                        variantRepository.delete(variantToDelete);
                    }
                    variantGroupRepository.delete(variantGroup);
                } else {
                    if(!findAllVariants.isEmpty()){
                        for (Variant variantToDelete : findAllVariants){
                            // nếu variant là variant cũ mà người dùng đã xóa nó thì xử lý xóa variant đó
                            // bởi vì khi người dùng xóa nó thì không call api ma chỉ xóa trong yup
                            // khi đẩy qua cho bên BE xử lý phải loại bỏ nó khỏi db
                            Variant foundVariant = ArrayUtils.findById(variants, variantUpdate -> variantUpdate.getId() == variantToDelete.getId());
                            if (foundVariant == null) {
                                // trước khi xóa variant thì phải xóa hết các collection của product variant có foreignKey là primaryKey của variant
                                for (ProductVariant productVariantToDelete : findAllProductVariant) {
                                    if(variantGroup.getIsPrimary()) {
                                        if(productVariantToDelete.getVariant1().getId() == variantToDelete.getId()){
                                            productVariantRepository.delete(productVariantToDelete);
                                        }
                                    } else {
                                        if(productVariantToDelete.getVariant2().getId() == variantToDelete.getId()){
                                            productVariantRepository.delete(productVariantToDelete);
                                        }
                                    }
                                }
                                variantRepository.delete(variantToDelete);
                            }
                        }
                    }
                }
            }
        }

        if(!variantGroups.isEmpty()){
            for (VariantGroup variantGroup : variantGroups){
                List<Variant> variantList = new ArrayList<>();
                for (Variant variant : variants){
                    if(variantGroup.getId().equals(variant.getVariantGroup().getId())){
                        variantList.add(variant);
                    }
                }
                variantGroup.setVariants(variantList);
            }
        }

        productResult.setProductVariants(productVariants);
        productResult.setVariantsGroup(variantGroups);
        productResult.setProductImages(productImages);

        mapProductToProductResponse(productResult);
        return modelMapper.map(productResult, ProductResponse.class);
    }

    @Override
    public Page<ProductResponse> getListProduct(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        if(products.isEmpty()){
            throw new NotFoundException("Product no data.");
        }

        return products.map(this::mapProductToProductResponse);
    }

    @Override
    public ProductResponse getProductByIdForUser(String id, String shopId) {
        Optional<Product> productResponse = productRepository.findByProductIdAndIsActiveOfProductOfShop(UUID.fromString(id), UUID.fromString(shopId), true);
        return productResponse.map(this::mapProductToProductResponse)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    @Override
    public ProductResponse getProductById(String id) {
        Optional<Product> productResponse = productRepository.findByProductIdOfProduct(UUID.fromString(id));
        if (productResponse.isEmpty()) {
           return null;
        }
        return productResponse.map(this::mapProductToProductResponse).get();
    }

    @Override
    public ProductResponse getProductByIdOfShop(String id, String shopId) {
        Optional<Product> productResponse = productRepository.findByProductIdOfProductOfShop(UUID.fromString(id), UUID.fromString(shopId));
        return productResponse.map(this::mapProductToProductResponse)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    private ProductResponse mapProductToProductResponse(Product product) {
        modelMapper.typeMap(Product.class, ProductResponse.class).addMappings(mapper -> {
            mapper.map(src -> src.getShop().getId(), ProductResponse::setShopId);
            mapper.map(src -> src.getCategory().getId(), ProductResponse::setCategoryId);
        });

        return modelMapper.map(product, ProductResponse.class);
    }
}
