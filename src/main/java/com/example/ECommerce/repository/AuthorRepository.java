package com.example.ECommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ECommerce.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    // Custom queries if needed
} 