package com.example.ECommerce.mapper;

import com.example.ECommerce.dto.BookDTO;
import com.example.ECommerce.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface BookMapper {
    
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "orderDetails", ignore = true)
    @Mapping(target = "stock", source = "stockQuantity")
    @Mapping(target = "publicationDate", expression = "java(java.time.LocalDate.of(bookDTO.getPublicationYear(), 1, 1))")
    @Mapping(target = "isDigital", source = "isDigital")
    Book toEntity(BookDTO bookDTO);

    @Mapping(target = "authorIds", expression = "java(book.getAuthor() != null ? java.util.Collections.singleton(book.getAuthor().getId()) : java.util.Collections.emptySet())")
    @Mapping(target = "categoryIds", expression = "java(book.getCategory() != null ? java.util.Collections.singleton(book.getCategory().getId()) : java.util.Collections.emptySet())")
    @Mapping(target = "stockQuantity", source = "stock")
    @Mapping(target = "publicationYear", expression = "java(book.getPublicationDate() != null ? book.getPublicationDate().getYear() : null)")
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "coverImageUrl", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "language", ignore = true)
    @Mapping(target = "pageCount", ignore = true)
    @Mapping(target = "authorName", expression = "java(book.getAuthor() != null ? book.getAuthor().getName() : null)")
    @Mapping(target = "authorBiography", expression = "java(book.getAuthor() != null ? book.getAuthor().getBiography() : null)")
    @Mapping(target = "categoryName", expression = "java(book.getCategory() != null ? book.getCategory().getName() : null)")
    @Mapping(target = "categoryDescription", expression = "java(book.getCategory() != null ? book.getCategory().getDescription() : null)")
    @Mapping(target = "isDigital", source = "isDigital")
    BookDTO toDTO(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "orderDetails", ignore = true)
    @Mapping(target = "stock", source = "stockQuantity")
    @Mapping(target = "publicationDate", expression = "java(java.time.LocalDate.of(bookDTO.getPublicationYear(), 1, 1))")
    @Mapping(target = "isDigital", source = "isDigital")
    void updateEntityFromDTO(BookDTO bookDTO, @MappingTarget Book book);
} 