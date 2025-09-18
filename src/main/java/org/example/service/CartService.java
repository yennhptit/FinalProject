package org.example.service;

import org.example.model.Cart;

public interface CartService {
    Cart addProductToCart(Long userId, Long bookId, int quantity);
    Cart getCartByUser(Long userId);
    void clearCart(Long userId);
}
