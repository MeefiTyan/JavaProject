package com.example.ECommerce.service;

import com.example.ECommerce.dto.DigitalPurchaseDTO;
import com.example.ECommerce.entity.Book;
import com.example.ECommerce.entity.DigitalPurchase;
import com.example.ECommerce.entity.Order;
import com.example.ECommerce.entity.OrderStatus;
import com.example.ECommerce.mapper.DigitalPurchaseMapper;
import com.example.ECommerce.repository.BookRepository;
import com.example.ECommerce.repository.DigitalPurchaseRepository;
import com.example.ECommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class DigitalPurchaseService {
    private final DigitalPurchaseRepository digitalPurchaseRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final DigitalPurchaseMapper digitalPurchaseMapper;

    public List<DigitalPurchaseDTO> getPurchasesForUser(String userId) {
        return digitalPurchaseRepository.findByKeycloakUserId(userId).stream()
                .map(digitalPurchaseMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<DigitalPurchaseDTO> getCurrentUserPurchases() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserPurchases(userId);
    }

    public ResponseEntity<byte[]> downloadEBook(Long purchaseId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        DigitalPurchase purchase = digitalPurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        if (!purchase.getKeycloakUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have access to this purchase");
        }

        // Get the book
        Book book = purchase.getBook();
        if (!book.getIsDigital()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "This book is not available in digital format");
        }

        // Find the order that contains this book
        Order order = orderRepository.findByKeycloakUserIdAndBookId(userId, book.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "No order found for this book. Please ensure you have purchased this book."));

        // Verify order status
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Order is not confirmed yet. Current status: " + order.getStatus());
        }

        // Verify delivery type
        if (!"DIGITAL".equals(order.getDeliveryType())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "This order was not placed for digital delivery");
        }

        // Check if digital content is available
        if (book.getIsDigital() == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Digital content is not available for this book. Please contact support.");
        }

        // Increment download count
        purchase.setDownloadCount(purchase.getDownloadCount() + 1);
        digitalPurchaseRepository.save(purchase);

        // Set up response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", book.getTitle() + ".pdf");
        
        return new ResponseEntity<>(book.getDigitalContent(), headers, HttpStatus.OK);
    }

    @Transactional
    public void verifyDigitalAccess(Long purchaseId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        DigitalPurchase purchase = digitalPurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        if (!purchase.getKeycloakUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have access to this purchase");
        }

        // Get the book
        Book book = purchase.getBook();
        if (!book.getIsDigital()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "This book is not available in digital format");
        }

        // Find the order that contains this book
        Order order = orderRepository.findByKeycloakUserIdAndBookId(userId, book.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "No order found for this book. Please ensure you have purchased this book."));

        // Verify order status
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Order is not confirmed yet. Current status: " + order.getStatus());
        }

        // Verify delivery type
        if (!"DIGITAL".equals(order.getDeliveryType())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "This order was not placed for digital delivery");
        }

        // Increment access count
        purchase.setDownloadCount(purchase.getDownloadCount() + 1);
        digitalPurchaseRepository.save(purchase);
    }

    @Transactional(readOnly = true)
    public List<DigitalPurchaseDTO> getUserPurchases(String userId) {
        return digitalPurchaseRepository.findByKeycloakUserId(userId)
                .stream()
                .map(digitalPurchaseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DigitalPurchaseDTO createPurchase(String userId, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if (!book.getIsDigital()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "This book is not available in digital format");
        }

        // Check if user already purchased this book
        if (digitalPurchaseRepository.findByKeycloakUserId(userId).stream()
                .anyMatch(purchase -> purchase.getBook().getId().equals(bookId))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book already purchased");
        }

        DigitalPurchase purchase = new DigitalPurchase();
        purchase.setBook(book);
        purchase.setKeycloakUserId(userId);
        purchase.setDownloadCount(0);

        DigitalPurchase savedPurchase = digitalPurchaseRepository.save(purchase);
        return digitalPurchaseMapper.toDTO(savedPurchase);
    }

    @Transactional
    public void incrementDownloadCount(Long purchaseId) {
        DigitalPurchase purchase = digitalPurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        purchase.setDownloadCount(purchase.getDownloadCount() + 1);
        digitalPurchaseRepository.save(purchase);
    }

    @Transactional
    public DigitalPurchaseDTO createPurchaseFromOrder(String userId, Long bookId, Long orderId) {
        // Verify the order exists and belongs to the user
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.getKeycloakUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have access to this order");
        }

        // Verify order status
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Order is not confirmed yet. Current status: " + order.getStatus());
        }

        // Verify delivery type
        if (!"DIGITAL".equals(order.getDeliveryType())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "This order was not placed for digital delivery");
        }

        // Get the book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        // Verify the book is digital
        if (!book.getIsDigital()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "This book is not available in digital format");
        }

        // Check if user already has a digital purchase for this book
        if (digitalPurchaseRepository.findByKeycloakUserId(userId).stream()
                .anyMatch(purchase -> purchase.getBook().getId().equals(bookId))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have access to this digital book");
        }

        // Create the digital purchase
        DigitalPurchase purchase = new DigitalPurchase();
        purchase.setBook(book);
        purchase.setKeycloakUserId(userId);
        purchase.setDownloadCount(0);

        DigitalPurchase savedPurchase = digitalPurchaseRepository.save(purchase);
        return digitalPurchaseMapper.toDTO(savedPurchase);
    }
} 