package com.example.ECommerce.controller;

import com.example.ECommerce.dto.CartDTO;
import com.example.ECommerce.dto.CartItemDTO;
import com.example.ECommerce.dto.CartItemRequestDTO;
import com.example.ECommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CartController extends BaseController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCurrentCart() {
        return ResponseEntity.ok(cartService.getCurrentCart(getCurrentUser().getId()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(@Valid @RequestBody CartItemRequestDTO cartItemRequestDTO) {
        return ResponseEntity.ok(cartService.addItem(getCurrentUser().getId(), cartItemRequestDTO));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemRequestDTO cartItemRequestDTO) {
        return ResponseEntity.ok(cartService.updateItem(getCurrentUser().getId(), itemId, cartItemRequestDTO));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> removeItemFromCart(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(getCurrentUser().getId(), itemId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart(getCurrentUser().getId());
        return ResponseEntity.noContent().build();
    }
} 