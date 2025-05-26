package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.CartItemDTO;
import com.example.ECommerce.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "book", ignore = true)
    CartItem toEntity(CartItemDTO cartItemDTO);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "unitPrice", expression = "java(cartItem.getBook() != null ? cartItem.getBook().getPrice() : null)")
    @Mapping(target = "subtotal", expression = "java(cartItem.getBook() != null ? cartItem.getBook().getPrice().multiply(java.math.BigDecimal.valueOf(cartItem.getQuantity())) : null)")
    CartItemDTO toDTO(CartItem cartItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "book", ignore = true)
    void updateEntityFromDTO(CartItemDTO cartItemDTO, @MappingTarget CartItem cartItem);
} 