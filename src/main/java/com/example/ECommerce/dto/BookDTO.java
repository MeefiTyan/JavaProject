package com.example.ECommerce.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private String isbn;
    private BigDecimal price;
    private String description;
    private Integer stockQuantity;
    private Set<Long> authorIds;
    private Set<Long> categoryIds;
    private String coverImageUrl;
    private String publisher;
    private Integer publicationYear;
    private String language;
    private Integer pageCount;
    private Boolean isDigital;
    
    // Author information
    private String authorName;
    private String authorBiography;
    
    // Category information
    private String categoryName;
    private String categoryDescription;
} 