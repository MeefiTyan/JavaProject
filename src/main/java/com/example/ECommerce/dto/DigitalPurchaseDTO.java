package com.example.ECommerce.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DigitalPurchaseDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String userId;
    private LocalDateTime purchaseDate;
    private Integer downloadCount;
} 