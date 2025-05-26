package com.example.ECommerce.dto;

import com.example.ECommerce.entity.OrderStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String userId;
    private List<OrderItemDTO> items;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String deliveryType;
    private String deliveryMethod;
    private String paymentMethod;
} 