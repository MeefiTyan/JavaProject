package com.example.ECommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "digital_purchases", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"book_id", "keycloak_user_id"})
})
public class DigitalPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "keycloak_user_id", nullable = false)
    private String keycloakUserId;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "download_count", nullable = false)
    private Integer downloadCount = 0;

    @PrePersist
    protected void onCreate() {
        purchaseDate = LocalDateTime.now();
    }
} 