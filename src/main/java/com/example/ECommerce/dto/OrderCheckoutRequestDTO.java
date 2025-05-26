package com.example.ECommerce.dto;

import lombok.Data;

@Data
public class OrderCheckoutRequestDTO {
    private String shippingAddress;
    private String deliveryType;
    private String deliveryMethod;
    private String paymentMethod;
} 