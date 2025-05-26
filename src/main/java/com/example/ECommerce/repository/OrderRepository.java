package com.example.ECommerce.repository;
import com.example.ECommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByKeycloakUserId(String keycloakUserId);
    
    @Query("SELECT o FROM Order o JOIN o.items i WHERE o.keycloakUserId = :userId AND i.book.id = :bookId")
    Optional<Order> findByKeycloakUserIdAndBookId(@Param("userId") String userId, @Param("bookId") Long bookId);
}