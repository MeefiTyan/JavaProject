package com.example.ECommerce.controller;

import com.example.ECommerce.dto.OrderDTO;
import com.example.ECommerce.dto.OrderCheckoutRequestDTO;
import com.example.ECommerce.entity.OrderStatus;
import com.example.ECommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDTO> checkout(@RequestBody @Valid OrderCheckoutRequestDTO orderCheckoutRequestDTO) {
        return ResponseEntity.ok(orderService.checkout(orderCheckoutRequestDTO));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDTO>> getCurrentCustomerOrders() {
        return ResponseEntity.ok(orderService.getCurrentUserOrders());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @PutMapping("/{id}/tracking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateTrackingInfo(
            @PathVariable Long id,
            @RequestParam String trackingNumber) {
        return ResponseEntity.ok(orderService.updateTrackingInfo(id, trackingNumber));
    }
} 