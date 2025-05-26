package com.example.ECommerce.service;

import com.example.ECommerce.dto.CategoryDTO;
import com.example.ECommerce.entity.Category;
import com.example.ECommerce.mapper.CategoryMapper;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        // Setup category
        category = new Category();
        category.setId(1L);
        category.setName("Fiction");
        category.setDescription("Fiction books");

        // Setup categoryDTO
        categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Fiction");
        categoryDTO.setDescription("Fiction books");
    }

    @Test
    void create_ShouldCreateNewCategory() {
        // Arrange
        when(categoryMapper.toEntity(categoryDTO)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        // Act
        CategoryDTO result = categoryService.create(categoryDTO);

        // Assert
        assertNotNull(result);
        assertEquals(categoryDTO.getName(), result.getName());
        verify(categoryMapper).toEntity(categoryDTO);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDTO(category);
    }

    @Test
    void findById_WhenCategoryExists_ShouldReturnCategoryDTO() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        // Act
        CategoryDTO result = categoryService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(categoryDTO.getName(), result.getName());
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toDTO(category);
    }

    @Test
    void findById_WhenCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> categoryService.findById(1L));
        verify(categoryRepository).findById(1L);
    }

    @Test
    void update_WhenCategoryExists_ShouldUpdateCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        // Act
        CategoryDTO result = categoryService.update(1L, categoryDTO);

        // Assert
        assertNotNull(result);
        assertEquals(categoryDTO.getName(), result.getName());
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).updateEntityFromDTO(categoryDTO, category);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDTO(category);
    }

    @Test
    void update_WhenCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> categoryService.update(1L, categoryDTO));
        verify(categoryRepository).findById(1L);
        verify(categoryMapper, never()).updateEntityFromDTO(any(), any());
    }

    @Test
    void delete_WhenCategoryExists_ShouldDeleteCategory() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // Act
        categoryService.delete(1L);

        // Assert
        verify(categoryRepository).existsById(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void delete_WhenCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> categoryService.delete(1L));
        verify(categoryRepository).existsById(1L);
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void findAll_ShouldReturnAllCategories() {
        // Arrange
        List<Category> categories = Arrays.asList(category);
        Page<Category> categoryPage = new PageImpl<>(categories);
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        // Act
        Page<CategoryDTO> result = categoryService.findAll(Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(categoryDTO.getName(), result.getContent().get(0).getName());
        verify(categoryRepository).findAll(any(Pageable.class));
        verify(categoryMapper).toDTO(category);
    }
} 