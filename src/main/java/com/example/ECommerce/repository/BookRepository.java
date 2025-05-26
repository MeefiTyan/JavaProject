package com.example.ECommerce.repository;
import com.example.ECommerce.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE " +
            "(:title IS NULL OR b.title LIKE %:title%) AND " +
            "(:authorName IS NULL OR b.author.name LIKE %:authorName%) AND " +
            "(:categoryName IS NULL OR b.category.name = :categoryName)")
    List<Book> findByTitleContainingOrAuthor_NameContainingOrCategory_NameContaining(
            @Param("title") String title,
            @Param("authorName") String authorName,
            @Param("categoryName") String categoryName
    );
}