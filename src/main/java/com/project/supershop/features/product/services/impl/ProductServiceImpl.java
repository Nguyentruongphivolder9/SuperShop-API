package com.project.supershop.features.product.services.impl;

import com.project.supershop.features.product.domain.dto.requests.*;
import com.project.supershop.features.product.domain.dto.responses.ProductResponse;
import com.project.supershop.features.product.domain.entities.*;
import com.project.supershop.features.product.repositories.*;
import com.project.supershop.features.product.services.ProductService;
import com.project.supershop.handler.NotFoundException;
import com.project.supershop.handler.UnprocessableException;
import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import io.github.dengliming.redismodule.redisjson.utils.GsonUtils;
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
    private final RedisJSON redisJSON;

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository, VariantGroupRepository variantGroupRepository, VariantRepository variantRepository, ProductVariantRepository productVariantRepository, ProductImageRepository productImageRepository, PreviewImageRepository previewImageRepository, CategoryRepository categoryRepository, RedisJSON redisJSON) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.variantGroupRepository = variantGroupRepository;
        this.variantRepository = variantRepository;
        this.productVariantRepository = productVariantRepository;
        this.productImageRepository = productImageRepository;
        this.previewImageRepository = previewImageRepository;
        this.categoryRepository = categoryRepository;
        this.redisJSON = redisJSON;
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        List<VariantGroup> variantGroups = new ArrayList<>();
        List<Variant> variants = new ArrayList<>();
        List<ProductVariant> productVariants = new ArrayList<>();
        List<ProductImage> productImages = new ArrayList<>();

        if(productRequest.getCategoryId().isEmpty()){
            throw new UnprocessableException("The categoryId field can't be empty.");
        }

        Optional<Category> resultCateFindById = categoryRepository.findById(Integer.parseInt(productRequest.getCategoryId()));
        if(resultCateFindById.isEmpty()) {
            throw new NotFoundException(productRequest.getCategoryId() + " does not exist");
        }


        Product product = Product.createProduct(productRequest, resultCateFindById.get());
        Product  productResult = productRepository.save(product);

        if(productRequest.getProductImages().isEmpty() || productRequest.getProductImages().size() < 3){
            throw new UnprocessableException("The product image field can't be empty and contain 3 or more images.");
        }

        if(!productRequest.getIsVariant()) {
            if(productRequest.getPrice() == null || productRequest.getPrice() <= 0){
                throw new UnprocessableException("The product price field can't be empty or less than 0.");
            }

            if(productRequest.getStockQuantity() == null || productRequest.getStockQuantity() <= 0){
                throw new UnprocessableException("The product stock field can't be empty or less than 0.");
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
            Set<String> variantGroupNames = new HashSet<>();
            Map<String, Set<String>> variantsGroupMap = new HashMap<>();

            for (VariantGroupRequest groupRequest : productRequest.getVariantsGroup()) { // kiểm tra xem field name của variantsGroup không được trùng nhau
                if (
                        !variantGroupNames.add(groupRequest.getName())
                ) {
                    throw new UnprocessableException(groupRequest.getName() + " is the same as the variation name of another variation");
                }

                VariantGroup variantGroupBuild = VariantGroup.createVariantGroup(groupRequest, productResult);
                VariantGroup variantGroupResult = variantGroupRepository.save(variantGroupBuild);
                variantGroups.add(variantGroupResult);

                Set<String> variantNames = new HashSet<>();
                Set<String> variantIds = new HashSet<>();
                for (VariantRequest variantRequest : groupRequest.getVariants()) {
                    if (
                            !variantNames.add(variantRequest.getName())
                    ) { // kiểm tra xem các field name của variant không được trùng nhau
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

                // Kiểm tra giá sản phẩm không được null hoặc bằng 0
                if (variantRequest.getPrice() == null || variantRequest.getPrice() == 0) {
                    throw new UnprocessableException("The product price field can't be empty or less than 0.");
                }

                // Kiểm tra StockQuantity phải lớn hơn 0
                if (variantRequest.getStockQuantity() == null || variantRequest.getStockQuantity() <= 0) {
                    throw new UnprocessableException("The product stock field can't be empty or less than 0.");
                }

                // Kiểm tra khi chỉ có một variantsGroup thì sẽ không có variantsGroup2 và variant2Id trong productVariant
                if (variantGroups.size() == 1 && variantRequest.getVariantsGroup2Id() == null && variantRequest.getVariant2Id() == null) {

                    // Kiểm tra chỉ có một variant1Id trong mỗi variantsGroup1
                    if (!variantExistsOneVariantGroup.add(variantRequest.getVariant1Id())) {
                        throw new UnprocessableException("Only one variant1Id is allowed in variantsGroup1");
                    }

                    // Tìm variant1 và thiết lập vào productVariantBuild
                    Variant variant1 = null;
                    Variant variant2 = null;
                    for (Variant variant : variants) {
                        if (variant.getId().equals(variantRequest.getVariant1Id())) {
                            variant1 = variant;
                            break;
                        }
                    }

                    // Lưu ProductVariant và thêm vào danh sách productVariants
                    ProductVariant productVariantBuild = ProductVariant.createVariant(variantRequest, productResult, variant1, variant2);
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
                    ProductVariant productVariantBuild = ProductVariant.createVariant(variantRequest, productResult, variant1, variant2);
                    ProductVariant productVariant = productVariantRepository.save(productVariantBuild);
                    productVariants.add(productVariant);
                }
            }


        }

        for (VariantGroup variantGroup : variantGroups){
            List<Variant> variantList = new ArrayList<>();
            for (Variant variant : variants){
                if(variantGroup.getId().equals(variant.getVariantGroup().getId())){
                    variantList.add(variant);
                }
            }
            variantGroup.setVariants(variantList);
        }

        productResult.setProductVariants(productVariants);
        productResult.setVariantsGroup(variantGroups);
        productResult.setProductImages(productImages);

        ProductResponse productResponse = modelMapper.map(productResult, ProductResponse.class);
        String key = "product:" + productResponse.getId();
        redisJSON.set(key, SetArgs.Builder.create(".", GsonUtils.toJson(productResponse)));
        return productResponse;
    }

    @Override
    public Page<ProductResponse> getListProduct(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(product -> {
            modelMapper.typeMap(Product.class, ProductResponse.class);
            return modelMapper.map(product, ProductResponse.class);
        });
    }

    @Override
    public ProductResponse getProductById(String id) {
        return productRepository.findByProductId(UUID.fromString(id))
                .map(product -> {
                    modelMapper.typeMap(Product.class, ProductResponse.class);
                    return modelMapper.map(product, ProductResponse.class);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
}
