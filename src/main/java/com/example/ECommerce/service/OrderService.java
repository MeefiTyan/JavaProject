package com.example.ECommerce.service;

import com.example.ECommerce.dto.OrderDTO;
import com.example.ECommerce.dto.OrderCheckoutRequestDTO;
import com.example.ECommerce.entity.*;
import com.example.ECommerce.mapper.OrderMapper;
import com.example.ECommerce.repository.OrderRepository;
import com.example.ECommerce.repository.CartRepository;
import com.example.ECommerce.repository.DigitalPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final DigitalPurchaseRepository digitalPurchaseRepository;
    private final OrderMapper orderMapper;
    private final KeycloakUserService keycloakUserService;

    @Transactional
    @Retryable(
        value = { ObjectOptimisticLockingFailureException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 100)
    )
    public OrderDTO createOrder(OrderDTO orderDTO) {
        String keycloakUserId = keycloakUserService.getCurrentUserId();
        Cart cart = cartRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Order order = orderMapper.toEntity(orderDTO);
        order.setKeycloakUserId(keycloakUserId);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        // Convert cart items to order items and calculate total amount
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    
                    // Calculate prices
                    double unitPrice = cartItem.getBook().getPrice().doubleValue();
                    double totalPrice = unitPrice * cartItem.getQuantity();
                    
                    orderItem.setUnitPrice(unitPrice);
                    orderItem.setTotalPrice(totalPrice);
                    return orderItem;
                })
                .collect(Collectors.toList());

        // Calculate total amount from order items
        double totalAmount = orderItems.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
                
        order.setTotalAmount(totalAmount);
        order.getItems().addAll(orderItems);

        try {
            Order savedOrder = orderRepository.save(order);

            // Create digital purchases for digital delivery
            if ("DIGITAL".equals(orderDTO.getDeliveryType())) {
                orderItems.forEach(orderItem -> {
                    DigitalPurchase digitalPurchase = new DigitalPurchase();
                    digitalPurchase.setKeycloakUserId(keycloakUserId);
                    digitalPurchase.setBook(orderItem.getBook());
                    digitalPurchase.setPurchaseDate(LocalDateTime.now());
                    digitalPurchase.setDownloadCount(0);
                    digitalPurchaseRepository.save(digitalPurchase);
                });
            }

            // Clear the cart after successful order
            cart.getItems().clear();
            cartRepository.save(cart);

            return orderMapper.toDTO(savedOrder);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order was modified by another operation. Please try again.");
        }
    }

    public List<OrderDTO> getCurrentUserOrders() {
        String keycloakUserId = keycloakUserService.getCurrentUserId();
        return orderRepository.findByKeycloakUserId(keycloakUserId).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO findById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }

    public Page<OrderDTO> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDTO);
    }

    public OrderDTO updateTrackingInfo(Long id, String trackingNumber) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        order.setShippingAddress(trackingNumber);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }

    public List<OrderDTO> findAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Retryable(
        value = { ObjectOptimisticLockingFailureException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 100)
    )
    public OrderDTO checkout(OrderCheckoutRequestDTO orderCheckoutRequestDTO) {
        String keycloakUserId = keycloakUserService.getCurrentUserId();
        Cart cart = cartRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Order order = new Order();
        order.setKeycloakUserId(keycloakUserId);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(orderCheckoutRequestDTO.getShippingAddress());
        order.setDeliveryType(orderCheckoutRequestDTO.getDeliveryType());
        order.setDeliveryMethod(orderCheckoutRequestDTO.getDeliveryMethod());
        order.setPaymentMethod(orderCheckoutRequestDTO.getPaymentMethod());

        // Convert cart items to order items and calculate total amount
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    // Calculate prices
                    double unitPrice = cartItem.getBook().getPrice().doubleValue();
                    double totalPrice = unitPrice * cartItem.getQuantity();
                    orderItem.setUnitPrice(unitPrice);
                    orderItem.setTotalPrice(totalPrice);
                    return orderItem;
                })
                .collect(Collectors.toList());

        // Calculate total amount from order items
        double totalAmount = orderItems.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
        order.setTotalAmount(totalAmount);
        order.getItems().addAll(orderItems);

        try {
            Order savedOrder = orderRepository.save(order);

            // Create digital purchases for digital delivery
            if ("DIGITAL".equals(order.getDeliveryType())) {
                orderItems.forEach(orderItem -> {
                    DigitalPurchase digitalPurchase = new DigitalPurchase();
                    digitalPurchase.setKeycloakUserId(keycloakUserId);
                    digitalPurchase.setBook(orderItem.getBook());
                    digitalPurchase.setPurchaseDate(LocalDateTime.now());
                    digitalPurchase.setDownloadCount(0);
                    digitalPurchaseRepository.save(digitalPurchase);
                });
            }

            // Clear the cart after successful order
            cart.getItems().clear();
            cartRepository.save(cart);

            return orderMapper.toDTO(savedOrder);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order was modified by another operation. Please try again.");
        }
    }
} 