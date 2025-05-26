package com.example.ECommerce.service;

import com.example.ECommerce.dto.ReviewDTO;
import com.example.ECommerce.dto.CreateReviewDTO;
import com.example.ECommerce.entity.Review;
import com.example.ECommerce.entity.Book;
import com.example.ECommerce.mapper.ReviewMapper;
import com.example.ECommerce.repository.ReviewRepository;
import com.example.ECommerce.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final ReviewMapper reviewMapper;

    public ReviewDTO createReview(Long bookId, CreateReviewDTO createReviewDTO) {
        String keycloakId = getKeycloakId();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        Review review = new Review();
        review.setBook(book);
        review.setKeycloakUserId(keycloakId);
        review.setRating(createReviewDTO.getRating());
        review.setComment(createReviewDTO.getComment());
        review.setApproved(false);
        
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(savedReview);
    }

    public List<ReviewDTO> getBookReviews(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        return reviewRepository.findByBookAndApprovedTrue(book).stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getPendingReviews() {
        return reviewRepository.findByApprovedFalse().stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ReviewDTO updateReview(Long id, ReviewDTO reviewDTO) {
        String keycloakId = getKeycloakId();
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        if (!review.getKeycloakUserId().equals(keycloakId)) {
            throw new RuntimeException("You can only update your own reviews");
        }

        reviewMapper.updateEntityFromDTO(reviewDTO, review);
        review.setApproved(false); // Reset approval status when review is updated
        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(updatedReview);
    }

    public void deleteReview(Long id) {
        String keycloakId = getKeycloakId();
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        if (!review.getKeycloakUserId().equals(keycloakId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    public ReviewDTO moderateReview(Long id, boolean approved, String moderatorComment) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        review.setApproved(approved);
        review.setModeratorComment(moderatorComment);
        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(updatedReview);
    }

    private String getKeycloakId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getSubject();
    }
} 