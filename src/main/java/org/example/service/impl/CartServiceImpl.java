package org.example.service.impl;

import org.example.model.*;
import org.example.repository.*;
import org.example.service.CartService;

import java.util.ArrayList;
import java.util.Optional;

public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           BookRepository bookRepository,
                           UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Cart addProductToCart(Long userId, Long bookId, int quantity) {
        User user = getLoggedInUser(userId);
        Book book = getBook(bookId);

        int allowedQuantity = validateBookQuantity(quantity, book);

        Cart cart = getOrCreateCart(user);

        addOrUpdateItem(cart, book, allowedQuantity);

        return cartRepository.save(cart);
    }

    @Override
    public Cart getCartByUser(Long userId) {
        User user = getLoggedInUser(userId);
        return getCart(user);
    }

    @Override
    public void clearCart(Long userId) {
        User user = getLoggedInUser(userId);
        Cart cart = getCart(user);
        cartRepository.delete(cart.getId());
    }

    // -------------------- Private helper methods --------------------

    private User getLoggedInUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User must login"));
    }

    private Book getBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    private int validateBookQuantity(int quantity, Book book) {
        return Math.min(quantity, book.getStockQuantity());
    }

    private Cart getCart(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setItems(new ArrayList<>());
            return cartRepository.save(newCart);
        });
    }

    private void addOrUpdateItem(Cart cart, Book book, int quantity) {
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(book.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setBook(book);
            item.setQuantity(quantity);
            cart.getItems().add(item);
        }
    }
}
