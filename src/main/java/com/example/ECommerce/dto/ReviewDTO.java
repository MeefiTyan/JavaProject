package com.example.ECommerce.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String userId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean approved;
    private String moderatorComment;
} 