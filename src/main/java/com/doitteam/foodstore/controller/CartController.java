package com.doitteam.foodstore.controller;

import com.doitteam.foodstore.dto.request.*;
import com.doitteam.foodstore.dto.response.*;
import com.doitteam.foodstore.dto.response.ApiResponse;
import com.doitteam.foodstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@RequestParam Long userId) {
        CartResponse cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @RequestParam Long userId,
            @Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addToCart(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Product added to cart", cart));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @RequestParam Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse cart = cartService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Cart item updated", cart));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(
            @RequestParam Long userId,
            @PathVariable Long itemId) {
        CartResponse cart = cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }
}