package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.OrderItemDTO;
import com.example.ECommerce.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "book", ignore = true)
    OrderItem toEntity(OrderItemDTO orderItemDTO);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    OrderItemDTO toDTO(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "book", ignore = true)
    void updateEntityFromDTO(OrderItemDTO orderItemDTO, @MappingTarget OrderItem orderItem);
} 