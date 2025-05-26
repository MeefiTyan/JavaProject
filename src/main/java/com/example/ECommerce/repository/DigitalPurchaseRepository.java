package com.example.ECommerce.repository;

import com.example.ECommerce.entity.DigitalPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DigitalPurchaseRepository extends JpaRepository<DigitalPurchase, Long> {
    List<DigitalPurchase> findByKeycloakUserId(String keycloakUserId);
    List<DigitalPurchase> findByBookId(Long bookId);
} 