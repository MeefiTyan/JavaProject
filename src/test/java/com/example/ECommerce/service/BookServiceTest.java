package com.example.ECommerce.service;

import com.example.ECommerce.dto.BookDTO;
import com.example.ECommerce.entity.Book;
import com.example.ECommerce.entity.Author;
import com.example.ECommerce.entity.Category;
import com.example.ECommerce.mapper.BookMapper;
import com.example.ECommerce.repository.BookRepository;
import com.example.ECommerce.repository.AuthorRepository;
import com.example.ECommerce.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private BookDTO bookDTO;
    private Author author;
    private Category category;

    @BeforeEach
    void setUp() {
        // Setup author
        author = new Author();
        author.setId(1L);
        author.setName("Test Author");

        // Setup category
        category = new Category();
        category.setId(1L);
        category.setName("Fiction");

        // Setup book
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book.setPrice(new BigDecimal("19.99"));
        book.setStock(10);
        book.setAuthor(author);
        book.setCategory(category);

        // Setup bookDTO
        bookDTO = new BookDTO();
        bookDTO.setId(1L);
        bookDTO.setTitle("Test Book");
        bookDTO.setIsbn("1234567890");
        bookDTO.setPrice(new BigDecimal("19.99"));
        bookDTO.setStockQuantity(10);
        bookDTO.setAuthorIds(Set.of(1L));
        bookDTO.setCategoryIds(Set.of(1L));
    }

    @Test
    void create_ShouldCreateNewBook() {
        // Arrange
        when(bookMapper.toEntity(bookDTO)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDTO(book)).thenReturn(bookDTO);

        // Act
        BookDTO result = bookService.create(bookDTO);

        // Assert
        assertNotNull(result);
        assertEquals(bookDTO.getTitle(), result.getTitle());
        verify(bookMapper).toEntity(bookDTO);
        verify(bookRepository).save(book);
        verify(bookMapper).toDTO(book);
    }

    @Test
    void findById_WhenBookExists_ShouldReturnBookDTO() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDTO(book)).thenReturn(bookDTO);

        // Act
        BookDTO result = bookService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(bookDTO.getTitle(), result.getTitle());
        verify(bookRepository).findById(1L);
        verify(bookMapper).toDTO(book);
    }

    @Test
    void findById_WhenBookDoesNotExist_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookService.findById(1L));
        verify(bookRepository).findById(1L);
    }

    @Test
    void update_WhenBookExists_ShouldUpdateBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDTO(book)).thenReturn(bookDTO);

        // Act
        BookDTO result = bookService.update(1L, bookDTO);

        // Assert
        assertNotNull(result);
        assertEquals(bookDTO.getTitle(), result.getTitle());
        verify(bookRepository).findById(1L);
        verify(bookMapper).updateEntityFromDTO(bookDTO, book);
        verify(bookRepository).save(book);
        verify(bookMapper).toDTO(book);
    }

    @Test
    void update_WhenBookDoesNotExist_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookService.update(1L, bookDTO));
        verify(bookRepository).findById(1L);
        verify(bookMapper, never()).updateEntityFromDTO(any(), any());
    }

    @Test
    void delete_WhenBookExists_ShouldDeleteBook() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(true);

        // Act
        bookService.delete(1L);

        // Assert
        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void delete_WhenBookDoesNotExist_ShouldThrowException() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookService.delete(1L));
        verify(bookRepository).existsById(1L);
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void findAll_ShouldReturnAllBooks() {
        // Arrange
        List<Book> books = Arrays.asList(book);
        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.toDTO(book)).thenReturn(bookDTO);

        // Act
        List<BookDTO> result = bookService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository).findAll();
        verify(bookMapper).toDTO(book);
    }

    @Test
    void searchBooks_ShouldReturnMatchingBooks() {
        // Arrange
        List<Book> books = Arrays.asList(book);
        when(bookRepository.findByTitleContainingOrAuthor_NameContainingOrCategory_NameContaining(
                anyString(), anyString(), anyString())).thenReturn(books);
        when(bookMapper.toDTO(book)).thenReturn(bookDTO);

        // Act
        List<BookDTO> result = bookService.searchBooks("Test", "Author", "Category");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository).findByTitleContainingOrAuthor_NameContainingOrCategory_NameContaining(
                "Test", "Author", "Category");
        verify(bookMapper).toDTO(book);
    }
} 