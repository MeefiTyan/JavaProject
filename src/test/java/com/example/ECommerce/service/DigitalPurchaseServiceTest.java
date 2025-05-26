package com.example.ECommerce.service;

import com.example.ECommerce.dto.DigitalPurchaseDTO;
import com.example.ECommerce.entity.DigitalPurchase;
import com.example.ECommerce.entity.Book;
import com.example.ECommerce.entity.Order;
import com.example.ECommerce.entity.OrderStatus;
import com.example.ECommerce.mapper.DigitalPurchaseMapper;
import com.example.ECommerce.repository.DigitalPurchaseRepository;
import com.example.ECommerce.repository.BookRepository;
import com.example.ECommerce.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalPurchaseServiceTest {

    @Mock
    private DigitalPurchaseRepository digitalPurchaseRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DigitalPurchaseMapper digitalPurchaseMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DigitalPurchaseService digitalPurchaseService;

    private DigitalPurchase digitalPurchase;
    private DigitalPurchaseDTO digitalPurchaseDTO;
    private Book book;
    private Order order;
    private String testUserId;

    @BeforeEach
    void setUp() {
        testUserId = "test-user-id";

        // Setup security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(testUserId);

        // Setup book
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsDigital(true);

        // Setup order
        order = new Order();
        order.setId(1L);
        order.setKeycloakUserId(testUserId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setDeliveryType("DIGITAL");

        // Setup digital purchase
        digitalPurchase = new DigitalPurchase();
        digitalPurchase.setId(1L);
        digitalPurchase.setKeycloakUserId(testUserId);
        digitalPurchase.setBook(book);
        digitalPurchase.setPurchaseDate(LocalDateTime.now());
        digitalPurchase.setDownloadCount(0);

        // Setup digital purchase DTO
        digitalPurchaseDTO = new DigitalPurchaseDTO();
        digitalPurchaseDTO.setId(1L);
        digitalPurchaseDTO.setUserId(testUserId);
        digitalPurchaseDTO.setBookId(1L);
        digitalPurchaseDTO.setBookTitle("Test Book");
        digitalPurchaseDTO.setPurchaseDate(LocalDateTime.now());
        digitalPurchaseDTO.setDownloadCount(0);
    }

    @Test
    void createPurchase_ShouldCreateNewDigitalPurchase() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(digitalPurchaseRepository.findByKeycloakUserId(testUserId)).thenReturn(List.of());
        when(digitalPurchaseRepository.save(any(DigitalPurchase.class))).thenReturn(digitalPurchase);
        when(digitalPurchaseMapper.toDTO(digitalPurchase)).thenReturn(digitalPurchaseDTO);

        // Act
        DigitalPurchaseDTO result = digitalPurchaseService.createPurchase(testUserId, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(digitalPurchaseDTO.getBookId(), result.getBookId());
        verify(bookRepository).findById(1L);
        verify(digitalPurchaseRepository).findByKeycloakUserId(testUserId);
        verify(digitalPurchaseRepository).save(any(DigitalPurchase.class));
        verify(digitalPurchaseMapper).toDTO(digitalPurchase);
    }

    @Test
    void getPurchasesForUser_ShouldReturnUserPurchases() {
        // Arrange
        List<DigitalPurchase> purchases = Arrays.asList(digitalPurchase);
        when(digitalPurchaseRepository.findByKeycloakUserId(testUserId)).thenReturn(purchases);
        when(digitalPurchaseMapper.toDTO(digitalPurchase)).thenReturn(digitalPurchaseDTO);

        // Act
        List<DigitalPurchaseDTO> result = digitalPurchaseService.getPurchasesForUser(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(digitalPurchaseDTO.getBookId(), result.get(0).getBookId());
        verify(digitalPurchaseRepository).findByKeycloakUserId(testUserId);
        verify(digitalPurchaseMapper).toDTO(digitalPurchase);
    }

    @Test
    void verifyDigitalAccess_ShouldIncrementDownloadCount() {
        // Arrange
        when(digitalPurchaseRepository.findById(1L)).thenReturn(Optional.of(digitalPurchase));
        when(orderRepository.findByKeycloakUserIdAndBookId(testUserId, book.getId())).thenReturn(Optional.of(order));
        when(digitalPurchaseRepository.save(any(DigitalPurchase.class))).thenReturn(digitalPurchase);

        // Act
        digitalPurchaseService.verifyDigitalAccess(1L);

        // Assert
        verify(digitalPurchaseRepository).findById(1L);
        verify(orderRepository).findByKeycloakUserIdAndBookId(testUserId, book.getId());
        verify(digitalPurchaseRepository).save(any(DigitalPurchase.class));
        assertEquals(1, digitalPurchase.getDownloadCount());
    }

    @Test
    void verifyDigitalAccess_ShouldThrowExceptionForInvalidPurchase() {
        // Arrange
        when(digitalPurchaseRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> digitalPurchaseService.verifyDigitalAccess(1L));
        verify(digitalPurchaseRepository).findById(1L);
        verify(digitalPurchaseRepository, never()).save(any(DigitalPurchase.class));
    }
} 