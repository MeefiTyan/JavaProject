package com.example.ECommerce.service;

import com.example.ECommerce.dto.CartDTO;
import com.example.ECommerce.dto.CartItemDTO;
import com.example.ECommerce.dto.CartItemRequestDTO;
import com.example.ECommerce.entity.Book;
import com.example.ECommerce.entity.Cart;
import com.example.ECommerce.entity.CartItem;
import com.example.ECommerce.mapper.CartItemMapper;
import com.example.ECommerce.mapper.CartMapper;
import com.example.ECommerce.repository.BookRepository;
import com.example.ECommerce.repository.CartRepository;
import com.example.ECommerce.service.KeycloakUserService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private KeycloakUserService keycloakUserService;

    @InjectMocks
    private CartService cartService;

    private Cart testCart;
    private Book testBook;
    private CartItem testCartItem;
    private CartItemDTO testCartItemDTO;
    private CartDTO testCartDTO;
    private String testUserId;
    private CartItemRequestDTO testCartItemRequestDTO;

    @BeforeEach
    void setUp() {
        testUserId = "test-user-id";

        // Setup test cart
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setKeycloakUserId(testUserId);
        testCart.setItems(new ArrayList<>());

        // Setup test book
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setPrice(new BigDecimal("29.99"));

        // Setup test cart item
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setCart(testCart);
        testCartItem.setBook(testBook);
        testCartItem.setQuantity(1);
        testCart.getItems().add(testCartItem);  // Add the cart item to the cart

        // Setup test cart item DTO
        testCartItemDTO = new CartItemDTO();
        testCartItemDTO.setBookId(1L);
        testCartItemDTO.setQuantity(1);

        // Setup test cart DTO
        testCartDTO = new CartDTO();
        testCartDTO.setId(1L);
        testCartDTO.setUserId(testUserId);

        // Setup test cart item request DTO
        testCartItemRequestDTO = new CartItemRequestDTO();
        testCartItemRequestDTO.setBookId(1L);
        testCartItemRequestDTO.setQuantity(1);

        // Setup security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    void getCurrentCart_ShouldReturnCart() {
        // Arrange
        when(keycloakUserService.getCurrentUserId()).thenReturn(testUserId);
        when(cartRepository.findByKeycloakUserId(testUserId)).thenReturn(Optional.of(testCart));
        when(cartMapper.toDTO(testCart)).thenReturn(testCartDTO);

        // Act
        CartDTO result = cartService.getCurrentCart(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(testCartDTO.getId(), result.getId());
        verify(cartRepository).findByKeycloakUserId(testUserId);
        verify(cartMapper).toDTO(testCart);
    }

    @Test
    void addItem_ShouldAddItem() {
        // Arrange
        when(keycloakUserService.getCurrentUserId()).thenReturn(testUserId);
        when(cartRepository.findByKeycloakUserId(testUserId)).thenReturn(Optional.of(testCart));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toDTO(testCart)).thenReturn(testCartDTO);

        // Act
        CartDTO result = cartService.addItem(testUserId, testCartItemRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testCartDTO.getId(), result.getId());
        verify(cartRepository).findByKeycloakUserId(testUserId);
        verify(bookRepository).findById(1L);
        verify(cartRepository).save(any(Cart.class));
        verify(cartMapper).toDTO(testCart);
    }

    @Test
    void updateItem_ShouldUpdateQuantity() {
        // Arrange
        when(keycloakUserService.getCurrentUserId()).thenReturn(testUserId);
        when(cartRepository.findByKeycloakUserId(testUserId)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toDTO(testCart)).thenReturn(testCartDTO);

        // Act
        CartDTO result = cartService.updateItem(testUserId, testCartItem.getId(), testCartItemRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testCartDTO.getId(), result.getId());
        verify(cartRepository).findByKeycloakUserId(testUserId);
        verify(cartRepository).save(any(Cart.class));
        verify(cartMapper).toDTO(testCart);
    }

    @Test
    void removeItem_ShouldRemoveItem() {
        // Arrange
        when(keycloakUserService.getCurrentUserId()).thenReturn(testUserId);
        when(cartRepository.findByKeycloakUserId(testUserId)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toDTO(testCart)).thenReturn(testCartDTO);

        // Act
        CartDTO result = cartService.removeItem(testUserId, testCartItem.getId());

        // Assert
        assertNotNull(result);
        assertEquals(testCartDTO.getId(), result.getId());
        verify(cartRepository).findByKeycloakUserId(testUserId);
        verify(cartRepository).save(any(Cart.class));
        verify(cartMapper).toDTO(testCart);
    }

    @Test
    void clearCart_ShouldClearItems() {
        // Arrange
        when(keycloakUserService.getCurrentUserId()).thenReturn(testUserId);
        when(cartRepository.findByKeycloakUserId(testUserId)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        cartService.clearCart(testUserId);

        // Assert
        assertTrue(testCart.getItems().isEmpty());
        verify(cartRepository).findByKeycloakUserId(testUserId);
        verify(cartRepository).save(any(Cart.class));
    }
} 