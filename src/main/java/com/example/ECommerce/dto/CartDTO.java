package com.example.ECommerce.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private String userId;
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;
} 