package com.example.ECommerce.repository;

import com.example.ECommerce.entity.Book;
import com.example.ECommerce.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookAndApprovedTrue(Book book);
    List<Review> findByApprovedFalse();
} 