package com.project.supershop.features.cart.services.impl;

import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.auth.services.JwtTokenService;
import com.project.supershop.features.cart.domain.dto.requests.CartItemRequest;
import com.project.supershop.features.cart.domain.dto.responses.CartItemResponse;
import com.project.supershop.features.cart.domain.entities.CartItem;
import com.project.supershop.features.cart.repositories.CartItemRepository;
import com.project.supershop.features.cart.services.CartItemService;
import com.project.supershop.features.product.domain.dto.responses.ProductResponse;
import com.project.supershop.features.product.domain.entities.Product;
import com.project.supershop.features.product.domain.entities.ProductVariant;
import com.project.supershop.features.product.repositories.ProductRepository;
import com.project.supershop.features.product.repositories.ProductVariantRepository;
import com.project.supershop.features.product.services.ProductService;
import com.project.supershop.handler.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CartItemServiceImpl implements CartItemService {
    private final ModelMapper modelMapper;
    private final JwtTokenService jwtTokenService;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public CartItemServiceImpl(ModelMapper modelMapper, JwtTokenService jwtTokenService, CartItemRepository cartItemRepository, ProductRepository productRepository, ProductVariantRepository productVariantRepository) {
        this.modelMapper = modelMapper;
        this.jwtTokenService = jwtTokenService;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public CartItemResponse addCartItem(CartItemRequest cartItemRequest, String jwtToken) {
        Account parseJwtToAccount = jwtTokenService.parseJwtTokenToAccount(jwtToken);
        Optional<Product> productOptional = productRepository.findByProductIdOfProductOfShop(UUID.fromString(cartItemRequest.getProductId()), UUID.fromString(cartItemRequest.getShopId()));

        if (productOptional.isEmpty() || !productOptional.get().getIsActive()) {
            throw new NotFoundException("Product does not exists or store owners who have not yet posted for sale.");
        }

        int quantityLimit =  Optional.ofNullable(productOptional.get().getStockQuantity()).orElse(0);
        if (cartItemRequest.getProductVariantId() != null && !cartItemRequest.getProductVariantId().isEmpty()) {
            Optional<ProductVariant> productVariantOptional = productVariantRepository.findProductVariantByIdAndProductId(UUID.fromString(cartItemRequest.getProductVariantId()), productOptional.get().getId());
            if (productVariantOptional.isEmpty()) {
                throw new NotFoundException("Product variant does not exists.");
            }
            quantityLimit = productVariantOptional.get().getStockQuantity();
        }

        Optional<CartItem> cartItemOptional = cartItemRepository.findCartItemByAccountIdAndProductId(UUID.fromString(cartItemRequest.getProductId()), parseJwtToAccount.getId());
        CartItem cartItemResult;
        CartItem cartItem;
        int quantity;

        if (cartItemOptional.isEmpty()) {
            quantity = cartItemRequest.getQuantity();
            if (quantity > quantityLimit) {
                cartItemRequest.setQuantity(quantityLimit);
            }
            cartItem = CartItem.createCartItem(cartItemRequest, productOptional.get(), parseJwtToAccount);
        } else {
            cartItem = cartItemOptional.get();
            quantity = cartItem.getQuantity() + cartItemRequest.getQuantity();
            if (quantity > quantityLimit) {
                quantity = quantityLimit;
            }
            cartItem.setQuantity(quantity);
            cartItem.setProductVariantId(cartItemRequest.getProductVariantId());
        }

        cartItemResult = cartItemRepository.save(cartItem);
        CartItemResponse cartItemResponse = modelMapper.map(cartItemResult, CartItemResponse.class);
        cartItemResponse.setProduct(mapProductToProductResponse(productOptional.get()));
        return cartItemResponse;
    }

    @Override
    public Page<CartItemResponse> getListCartItem(Pageable pageable, String jwtToken) {
        Account parseJwtToAccount = jwtTokenService.parseJwtTokenToAccount(jwtToken);
        Page<CartItem> cartItems = cartItemRepository.findListCartItemByAccountId(pageable, parseJwtToAccount.getId());
        if (cartItems.isEmpty()){
            throw new NotFoundException("Empty cart data list.");
        }

        modelMapper.typeMap(CartItem.class, CartItemResponse.class).addMappings(mapper -> {
            mapper.map(src -> src.getAccount().getId(), CartItemResponse::setShopId);
            mapper.map(src -> mapProductToProductResponse(src.getProduct()), CartItemResponse::setProduct);
        });

        return cartItems.map(cartItem -> modelMapper.map(cartItem, CartItemResponse.class));
    }

    private ProductResponse mapProductToProductResponse(Product product) {
        modelMapper.typeMap(Product.class, ProductResponse.class).addMappings(mapper -> {
            mapper.map(src -> src.getShop().getId(), ProductResponse::setShopId);
            mapper.map(src -> src.getCategory().getId(), ProductResponse::setCategoryId);
        });

        return modelMapper.map(product, ProductResponse.class);
    }
}
