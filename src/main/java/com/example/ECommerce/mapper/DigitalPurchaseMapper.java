package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.DigitalPurchaseDTO;
import com.example.ECommerce.entity.DigitalPurchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DigitalPurchaseMapper {
    
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    DigitalPurchase toEntity(DigitalPurchaseDTO digitalPurchaseDTO);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "userId", source = "keycloakUserId")
    DigitalPurchaseDTO toDTO(DigitalPurchase digitalPurchase);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "purchaseDate", ignore = true)
    void updateEntityFromDTO(DigitalPurchaseDTO digitalPurchaseDTO, @MappingTarget DigitalPurchase digitalPurchase);
} 