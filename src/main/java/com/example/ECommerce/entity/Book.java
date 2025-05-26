package com.example.ECommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String isbn;
    private BigDecimal price;
    private LocalDate publicationDate;
    private int stock;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "book")
    private List<Review> reviews;

    @OneToMany(mappedBy = "book")
    private List<OrderDetail> orderDetails;

    @Lob
    @Column(name = "digital_content")
    private byte[] digitalContent;

    @Column(name = "is_digital", nullable = false)
    private Boolean isDigital = false;
} 