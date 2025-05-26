package com.example.ECommerce.service;

import com.example.ECommerce.dto.ReviewDTO;
import com.example.ECommerce.entity.Review;
import com.example.ECommerce.entity.Book;
import com.example.ECommerce.mapper.ReviewMapper;
import com.example.ECommerce.repository.ReviewRepository;
import com.example.ECommerce.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private ReviewService reviewService;

    private Review review;
    private ReviewDTO reviewDTO;
    private Book book;
    private String testUserId;

    @BeforeEach
    void setUp() {
        testUserId = "test-user-id";

        // Setup security context with JWT
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn(testUserId);

        // Setup book
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        // Setup review
        review = new Review();
        review.setId(1L);
        review.setKeycloakUserId(testUserId);
        review.setBook(book);
        review.setRating(5);
        review.setComment("Great book!");
        review.setCreatedAt(LocalDateTime.now());
        review.setApproved(false);

        // Setup review DTO
        reviewDTO = new ReviewDTO();
        reviewDTO.setId(1L);
        reviewDTO.setUserId(testUserId);
        reviewDTO.setBookId(1L);
        reviewDTO.setRating(5);
        reviewDTO.setComment("Great book!");
        reviewDTO.setApproved(false);
    }

    @Test
    void getBookReviews_ShouldReturnBookReviews() {
        // Arrange
        List<Review> reviews = Arrays.asList(review);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reviewRepository.findByBookAndApprovedTrue(book)).thenReturn(reviews);
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);

        // Act
        List<ReviewDTO> result = reviewService.getBookReviews(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reviewDTO.getComment(), result.get(0).getComment());
        verify(bookRepository).findById(1L);
        verify(reviewRepository).findByBookAndApprovedTrue(book);
        verify(reviewMapper).toDTO(review);
    }

    @Test
    void getPendingReviews_ShouldReturnPendingReviews() {
        // Arrange
        List<Review> reviews = Arrays.asList(review);
        when(reviewRepository.findByApprovedFalse()).thenReturn(reviews);
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);

        // Act
        List<ReviewDTO> result = reviewService.getPendingReviews();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reviewDTO.getComment(), result.get(0).getComment());
        verify(reviewRepository).findByApprovedFalse();
        verify(reviewMapper).toDTO(review);
    }

    @Test
    void moderateReview_ShouldUpdateReviewStatus() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        
        // Create a new DTO with the expected state after moderation
        ReviewDTO moderatedDTO = new ReviewDTO();
        moderatedDTO.setId(1L);
        moderatedDTO.setUserId(testUserId);
        moderatedDTO.setBookId(1L);
        moderatedDTO.setRating(5);
        moderatedDTO.setComment("Great book!");
        moderatedDTO.setApproved(true);
        moderatedDTO.setModeratorComment("Approved");
        
        when(reviewMapper.toDTO(review)).thenReturn(moderatedDTO);

        // Act
        ReviewDTO result = reviewService.moderateReview(1L, true, "Approved");

        // Assert
        assertNotNull(result);
        assertTrue(result.isApproved());
        assertEquals("Approved", result.getModeratorComment());
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).save(review);
        verify(reviewMapper).toDTO(review);
    }

    @Test
    void updateReview_WhenReviewExists_ShouldUpdateReview() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);

        // Act
        ReviewDTO result = reviewService.updateReview(1L, reviewDTO);

        // Assert
        assertNotNull(result);
        assertEquals(reviewDTO.getComment(), result.getComment());
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).save(review);
        verify(reviewMapper).toDTO(review);
    }

    @Test
    void updateReview_WhenReviewDoesNotExist_ShouldThrowException() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reviewService.updateReview(1L, reviewDTO));
        verify(reviewRepository).findById(1L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void deleteReview_WhenReviewExists_ShouldDeleteReview() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // Act
        reviewService.deleteReview(1L);

        // Assert
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_WhenReviewDoesNotExist_ShouldThrowException() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reviewService.deleteReview(1L));
        verify(reviewRepository).findById(1L);
        verify(reviewRepository, never()).delete(any());
    }
} 