package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.CategoryDTO;
import com.example.ECommerce.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    @Mapping(target = "books", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);

    CategoryDTO toDTO(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    void updateEntityFromDTO(CategoryDTO categoryDTO, @MappingTarget Category category);
} 