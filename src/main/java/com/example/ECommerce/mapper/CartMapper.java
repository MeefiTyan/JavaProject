package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.CartDTO;
import com.example.ECommerce.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {
    
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "items", source = "items")
    Cart toEntity(CartDTO cartDTO);

    @Mapping(target = "userId", source = "keycloakUserId")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalAmount", expression = "java(cart.getItems().stream().map(item -> item.getBook().getPrice().multiply(new java.math.BigDecimal(item.getQuantity()))).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add))")
    CartDTO toDTO(Cart cart);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "items", ignore = true)
    void updateEntityFromDTO(CartDTO cartDTO, @MappingTarget Cart cart);
} 