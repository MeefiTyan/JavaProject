package com.example.ECommerce.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
} 