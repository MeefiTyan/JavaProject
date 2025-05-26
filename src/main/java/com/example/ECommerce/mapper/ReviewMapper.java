package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.ReviewDTO;
import com.example.ECommerce.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    Review toEntity(ReviewDTO reviewDTO);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "userId", source = "keycloakUserId")
    ReviewDTO toDTO(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "approved", ignore = true)
    @Mapping(target = "moderatorComment", ignore = true)
    void updateEntityFromDTO(ReviewDTO reviewDTO, @MappingTarget Review review);
} 