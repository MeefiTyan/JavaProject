package com.example.ECommerce.service;

import com.example.ECommerce.dto.CartDTO;
import com.example.ECommerce.dto.CartItemDTO;
import com.example.ECommerce.dto.CartItemRequestDTO;
import com.example.ECommerce.entity.Cart;
import com.example.ECommerce.entity.CartItem;
import com.example.ECommerce.entity.Book;
import com.example.ECommerce.mapper.CartMapper;
import com.example.ECommerce.mapper.CartItemMapper;
import com.example.ECommerce.repository.CartRepository;
import com.example.ECommerce.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import jakarta.persistence.OptimisticLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Transactional(readOnly = true)
    public CartDTO getCurrentCart(String userId) {
        Cart cart = cartRepository.findByKeycloakUserId(userId)
            .orElseGet(() -> createNewCart(userId));
        return cartMapper.toDTO(cart);
    }

    @Transactional
    @Retryable(
        value = { ObjectOptimisticLockingFailureException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 100)
    )
    public CartDTO addItem(String userId, CartItemRequestDTO cartItemRequestDTO) {
        Cart cart = getCurrentCartEntity(userId);
        Book book = bookRepository.findById(cartItemRequestDTO.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        
        // Check if item already exists in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + cartItemRequestDTO.getQuantity());
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setBook(book);
            cartItem.setQuantity(cartItemRequestDTO.getQuantity());
            cart.getItems().add(cartItem);
        }

        try {
            Cart updatedCart = cartRepository.save(cart);
            return cartMapper.toDTO(updatedCart);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cart was modified by another operation. Please try again.");
        }
    }

    @Transactional
    @Retryable(
        value = { ObjectOptimisticLockingFailureException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 100)
    )
    public CartDTO updateItem(String userId, Long itemId, CartItemRequestDTO cartItemRequestDTO) {
        Cart cart = getCurrentCartEntity(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));
        
        // Only update quantity
        item.setQuantity(cartItemRequestDTO.getQuantity());
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toDTO(updatedCart);
    }

    @Transactional
    @Retryable(
        value = { ObjectOptimisticLockingFailureException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 100)
    )
    public CartDTO removeItem(String userId, Long itemId) {
        Cart cart = getCurrentCartEntity(userId);
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found");
        }
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toDTO(updatedCart);
    }

    @Transactional
    @Retryable(
        value = { ObjectOptimisticLockingFailureException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 100)
    )
    public void clearCart(String userId) {
        Cart cart = getCurrentCartEntity(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    protected Cart getCurrentCartEntity(String userId) {
        return cartRepository.findByKeycloakUserId(userId)
            .orElseGet(() -> createNewCart(userId));
    }

    @Transactional
    protected Cart createNewCart(String userId) {
        Cart cart = new Cart();
        cart.setKeycloakUserId(userId);
        return cartRepository.save(cart);
    }
} 