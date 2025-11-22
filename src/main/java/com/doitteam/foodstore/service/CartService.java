package com.doitteam.foodstore.service;


import com.doitteam.foodstore.dto.request.AddToCartRequest;
import com.doitteam.foodstore.dto.request.UpdateCartItemRequest;
import com.doitteam.foodstore.dto.response.CartItemResponse;
import com.doitteam.foodstore.dto.response.CartResponse;
import com.doitteam.foodstore.exception.BadRequestException;
import com.doitteam.foodstore.exception.InsufficientStockException;
import com.doitteam.foodstore.exception.ResourceNotFoundException;
import com.doitteam.foodstore.model.Cart;
import com.doitteam.foodstore.model.CartItem;
import com.doitteam.foodstore.model.Product;
import com.doitteam.foodstore.repository.CartRepository;
import com.doitteam.foodstore.repository.CartItemRepository;
import com.doitteam.foodstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Product " + product.getName() + " has insufficient stock");
        }

        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException("Product " + product.getName() + " has insufficient stock");
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
            log.info("Updated cart item quantity for user: {}", userId);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice());
            cartItemRepository.save(cartItem);
            log.info("Added product to cart for user: {}", userId);
        }

        return getCartByUserId(userId);
    }

    @Transactional
    public CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new BadRequestException("This cart item does not belong to you");
        }

        if (cartItem.getProduct().getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Product has insufficient stock");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        log.info("Updated cart item for user: {}", userId);
        return getCartByUserId(userId);
    }

    @Transactional
    public CartResponse removeCartItem(Long userId, Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new BadRequestException("This cart item does not belong to you");
        }

        cartItemRepository.delete(cartItem);
        log.info("Removed cart item for user: {}", userId);

        return getCartByUserId(userId);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        cartItemRepository.deleteByCartId(cart.getId());
        log.info("Cleared cart for user: {}", userId);
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        return new CartResponse(
                cart.getId(),
                items,
                cart.getTotalAmount(),
                cart.getTotalItems()
        );
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getImageUrl(),
                item.getPrice(),
                item.getQuantity(),
                item.getSubtotal(),
                item.getProduct().getStockQuantity()
        );
    }
}