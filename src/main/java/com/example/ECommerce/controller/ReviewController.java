package com.example.ECommerce.controller;

import com.example.ECommerce.dto.ReviewDTO;
import com.example.ECommerce.dto.CreateReviewDTO;
import com.example.ECommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/books/{bookId}")
    public ResponseEntity<ReviewDTO> createReview(
            @PathVariable Long bookId,
            @Valid @RequestBody CreateReviewDTO createReviewDTO) {
        return ResponseEntity.ok(reviewService.createReview(bookId, createReviewDTO));
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<List<ReviewDTO>> getBookReviews(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getBookReviews(bookId));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getPendingReviews() {
        return ResponseEntity.ok(reviewService.getPendingReviews());
    }

    @PutMapping("/{id}/moderate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewDTO> moderateReview(
            @PathVariable Long id,
            @RequestParam boolean approved,
            @RequestParam(required = false) String moderatorComment) {
        return ResponseEntity.ok(reviewService.moderateReview(id, approved, moderatorComment));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.updateReview(id, reviewDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
} 