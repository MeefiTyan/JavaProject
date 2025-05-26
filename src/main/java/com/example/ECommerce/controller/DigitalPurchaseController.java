package com.example.ECommerce.controller;

import com.example.ECommerce.dto.DigitalPurchaseDTO;
import com.example.ECommerce.service.DigitalPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/digital-purchases")
@RequiredArgsConstructor
public class DigitalPurchaseController {

    private final DigitalPurchaseService digitalPurchaseService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DigitalPurchaseDTO>> getCurrentUserPurchases() {
        return ResponseEntity.ok(digitalPurchaseService.getCurrentUserPurchases());
    }

    @GetMapping("/download/{purchaseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadEBook(@PathVariable Long purchaseId) {
        return digitalPurchaseService.downloadEBook(purchaseId);
    }
} 