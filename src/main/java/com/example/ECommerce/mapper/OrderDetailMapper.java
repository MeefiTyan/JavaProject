package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.OrderDetailDTO;
import com.example.ECommerce.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {
    
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "book", ignore = true)
    OrderDetail toEntity(OrderDetailDTO orderDetailDTO);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "subtotal", expression = "java(orderDetail.getUnitPrice().multiply(java.math.BigDecimal.valueOf(orderDetail.getQuantity())))")
    OrderDetailDTO toDTO(OrderDetail orderDetail);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "book", ignore = true)
    void updateEntityFromDTO(OrderDetailDTO orderDetailDTO, @MappingTarget OrderDetail orderDetail);
} 