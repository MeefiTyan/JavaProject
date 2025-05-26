package com.example.ECommerce.service;

import com.example.ECommerce.dto.OrderDTO;
import com.example.ECommerce.entity.*;
import com.example.ECommerce.mapper.OrderMapper;
import com.example.ECommerce.repository.OrderRepository;
import com.example.ECommerce.repository.CartRepository;
import com.example.ECommerce.repository.DigitalPurchaseRepository;
import com.example.ECommerce.service.KeycloakUserService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private DigitalPurchaseRepository digitalPurchaseRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private KeycloakUserService keycloakUserService;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderDTO orderDTO;
    private Cart cart;
    private Book book;
    private CartItem cartItem;
    private Pageable pageable;
    private String testUserId;

    @BeforeEach
    void setUp() {
        testUserId = "test-user-id";

        // Setup cart
        cart = new Cart();
        cart.setId(1L);
        cart.setKeycloakUserId(testUserId);
        cart.setItems(new ArrayList<>());

        // Setup book
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setPrice(new BigDecimal("29.99"));

        // Setup cart item
        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        cart.getItems().add(cartItem);

        // Setup order
        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setKeycloakUserId(testUserId);
        order.setShippingAddress("123 Test St");
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(29.99);

        // Setup orderDTO
        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setStatus(OrderStatus.PENDING);
        orderDTO.setUserId(testUserId);
        orderDTO.setShippingAddress("123 Test St");
        orderDTO.setDeliveryType("PHYSICAL");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createOrder_ShouldCreateNewOrder() {
        // Arrange
        when(keycloakUserService.getCurrentUserId()).thenReturn(testUserId);
        when(cartRepository.findByKeycloakUserId(testUserId)).thenReturn(Optional.of(cart));
        when(orderMapper.toEntity(orderDTO)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        // Act
        OrderDTO result = orderService.createOrder(orderDTO);

        // Assert
        assertNotNull(result);
        assertEquals(orderDTO.getStatus(), result.getStatus());
        assertEquals(orderDTO.getShippingAddress(), result.getShippingAddress());
        verify(cartRepository).findByKeycloakUserId(testUserId);
        verify(orderMapper).toEntity(orderDTO);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toDTO(order);
    }

    @Test
    void getCurrentUserOrders_ShouldReturnUserOrders() {
        // Arrange
        when(keycloakUserService.getCurrentUserId()).thenReturn(testUserId);
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByKeycloakUserId(testUserId)).thenReturn(orders);
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        // Act
        List<OrderDTO> result = orderService.getCurrentUserOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderDTO.getStatus(), result.get(0).getStatus());
        verify(orderRepository).findByKeycloakUserId(testUserId);
        verify(orderMapper).toDTO(order);
    }

    @Test
    void findById_WhenOrderExists_ShouldReturnOrderDTO() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        // Act
        OrderDTO result = orderService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(orderDTO.getStatus(), result.getStatus());
        verify(orderRepository).findById(1L);
        verify(orderMapper).toDTO(order);
    }

    @Test
    void findById_WhenOrderDoesNotExist_ShouldThrowException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.findById(1L));
        verify(orderRepository).findById(1L);
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        // Act
        OrderDTO result = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toDTO(order);
    }

    @Test
    void findAll_ShouldReturnAllOrders() {
        // Arrange
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        // Act
        List<OrderDTO> result = orderService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderDTO.getStatus(), result.get(0).getStatus());
        verify(orderRepository).findAll();
        verify(orderMapper).toDTO(order);
    }

    @Test
    void updateTrackingInfo_ShouldUpdateShippingAddress() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        // Act
        OrderDTO result = orderService.updateTrackingInfo(1L, "TRACK123");

        // Assert
        assertNotNull(result);
        assertEquals("TRACK123", order.getShippingAddress());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toDTO(order);
    }
} 