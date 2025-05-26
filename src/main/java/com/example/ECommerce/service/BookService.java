package com.example.ECommerce.service;

import com.example.ECommerce.dto.BookDTO;
import com.example.ECommerce.entity.Book;
import com.example.ECommerce.mapper.BookMapper;
import com.example.ECommerce.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public List<BookDTO> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public BookDTO findById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    public List<BookDTO> searchBooks(String title, String author, String category) {
        // Convert empty strings to null
        title = (title != null && title.trim().isEmpty()) ? null : title;
        author = (author != null && author.trim().isEmpty()) ? null : author;
        category = (category != null && category.trim().isEmpty()) ? null : category;

        // If all parameters are null, return all books
        if (title == null && author == null && category == null) {
            return findAll();
        }

        return bookRepository.findByTitleContainingOrAuthor_NameContainingOrCategory_NameContaining(
                title,
                author,
                category
        ).stream()
        .map(bookMapper::toDTO)
        .collect(Collectors.toList());
    }

    public BookDTO create(BookDTO bookDTO) {
        Book book = bookMapper.toEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDTO(savedBook);
    }

    public BookDTO update(Long id, BookDTO bookDTO) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        bookMapper.updateEntityFromDTO(bookDTO, existingBook);
        Book updatedBook = bookRepository.save(existingBook);
        return bookMapper.toDTO(updatedBook);
    }

    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
} 