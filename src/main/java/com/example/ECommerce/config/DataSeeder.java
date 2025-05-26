package com.example.ECommerce.config;

import com.example.ECommerce.entity.*;
import com.example.ECommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Only seed data if the database is empty
        if (bookRepository.count() == 0) {
            seedData();
        }
    }

    private void seedData() {
        // Create Authors
        Author author1 = new Author();
        author1.setName("J.K. Rowling");
        author1.setBiography("British author best known for the Harry Potter series");
        authorRepository.save(author1);

        Author author2 = new Author();
        author2.setName("George R.R. Martin");
        author2.setBiography("American novelist and short story writer");
        authorRepository.save(author2);

        // Create Categories
        Category category1 = new Category();
        category1.setName("Fantasy");
        category1.setDescription("Fantasy books and novels");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Science Fiction");
        category2.setDescription("Science fiction books and novels");
        categoryRepository.save(category2);

        // Create Books
        Book book1 = new Book();
        book1.setTitle("Harry Potter and the Philosopher's Stone");
        book1.setIsbn("978-0747532743");
        book1.setPrice(new BigDecimal("19.99"));
        book1.setPublicationDate(LocalDate.of(1997, 6, 26));
        book1.setStock(100);
        book1.setAuthor(author1);
        book1.setCategory(category1);
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("A Game of Thrones");
        book2.setIsbn("978-0553103540");
        book2.setPrice(new BigDecimal("24.99"));
        book2.setPublicationDate(LocalDate.of(1996, 8, 1));
        book2.setStock(75);
        book2.setAuthor(author2);
        book2.setCategory(category1);
        bookRepository.save(book2);

        // Create sample books
        Author author3 = new Author();
        author3.setName("F. Scott Fitzgerald");
        author3.setBiography("American novelist and short story writer");
        authorRepository.save(author3);

        Author author4 = new Author();
        author4.setName("Harper Lee");
        author4.setBiography("American novelist best known for To Kill a Mockingbird");
        authorRepository.save(author4);

        Book sampleBook1 = new Book();
        sampleBook1.setTitle("The Great Gatsby");
        sampleBook1.setAuthor(author3);
        sampleBook1.setPrice(new BigDecimal("19.99"));
        sampleBook1.setStock(100);
        sampleBook1.setCategory(category2);
        sampleBook1.setIsbn("978-0743273565");
        sampleBook1.setPublicationDate(LocalDate.of(1925, 4, 10));
        sampleBook1.setIsDigital(true);

        Book sampleBook2 = new Book();
        sampleBook2.setTitle("To Kill a Mockingbird");
        sampleBook2.setAuthor(author4);
        sampleBook2.setPrice(new BigDecimal("15.99"));
        sampleBook2.setStock(75);
        sampleBook2.setCategory(category2);
        sampleBook2.setIsbn("978-0446310789");
        sampleBook2.setPublicationDate(LocalDate.of(1960, 7, 11));
        sampleBook2.setIsDigital(true);

        bookRepository.saveAll(Arrays.asList(sampleBook1, sampleBook2));

        // Create a sample cart for testing
        Cart cart = new Cart();
        cart.setKeycloakUserId("test-user-id");
        cartRepository.save(cart);
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner initData() {
        return args -> {
            // Create sample books
            Book book1 = new Book();
            book1.setTitle("The Great Gatsby");
            book1.setPrice(new BigDecimal("19.99"));
            book1.setStock(100);

            Book book2 = new Book();
            book2.setTitle("To Kill a Mockingbird");
            book2.setPrice(new BigDecimal("15.99"));
            book2.setStock(75);

            bookRepository.saveAll(Arrays.asList(book1, book2));

            // Create a sample cart for testing
            Cart cart = new Cart();
            cart.setKeycloakUserId("test-user-id");
            cartRepository.save(cart);
        };
    }
} 