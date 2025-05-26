package com.example.ECommerce.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDetailDTO {
    private Long id;
    private Long orderId;
    private Long bookId;
    private String bookTitle;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
} 