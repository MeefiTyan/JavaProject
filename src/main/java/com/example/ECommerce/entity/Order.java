package com.example.ECommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "keycloak_user_id", nullable = false)
    private String keycloakUserId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @Column(name = "delivery_type", nullable = false)
    private String deliveryType;

    @Column(name = "delivery_method", nullable = false)
    private String deliveryMethod;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
} 