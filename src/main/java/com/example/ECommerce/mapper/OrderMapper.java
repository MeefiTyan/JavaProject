package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.OrderDTO;
import com.example.ECommerce.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    Order toEntity(OrderDTO orderDTO);

    @Mapping(target = "userId", source = "keycloakUserId")
    @Mapping(target = "items", source = "items")
    OrderDTO toDTO(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    void updateEntityFromDTO(OrderDTO orderDTO, @MappingTarget Order order);
} 