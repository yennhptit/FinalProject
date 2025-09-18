package org.example.service.impl;
import static org.mockito.ArgumentMatchers.any;
import org.example.model.*;
import org.example.model.enums.UserRole;
import org.example.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    private CartRepository cartRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private CartServiceImpl cartService;

    private User user;
    private Book book;

    @BeforeEach
    void setup() {
        // Dùng Mockito-inline để tránh lỗi với final class / Lombok
        cartRepository = Mockito.mock(CartRepository.class, withSettings().lenient());
        bookRepository = Mockito.mock(BookRepository.class, withSettings().lenient());
        userRepository = Mockito.mock(UserRepository.class, withSettings().lenient());

        cartService = new CartServiceImpl(cartRepository, bookRepository, userRepository);

        // Tạo user và book thực, không mock
        user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setRole(UserRole.USER);

        book = new Book();
        book.setId(10L);
        book.setTitle("Test Book");
        book.setStockQuantity(5);
    }

    @Test
    void testAddProductToCart_NewCart() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

        Cart savedCart = new Cart();
        savedCart.setUser(user);
        savedCart.setItems(new ArrayList<>());
        when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);

        Cart cart = cartService.addProductToCart(1L, 10L, 3);

        assertThat(cart, is(notNullValue()));
        assertThat(cart.getItems(), hasSize(1));
        assertThat(cart.getItems().get(0).getBook().getId(), is(10L));
        assertThat(cart.getItems().get(0).getQuantity(), is(3));
    }

    @Test
    void testAddProductToCart_ExceedStock() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        Cart existingCart = new Cart();
        existingCart.setUser(user);
        existingCart.setItems(new ArrayList<>());
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart cart = cartService.addProductToCart(1L, 10L, 10);

        assertThat(cart.getItems(), hasSize(1));
        assertThat(cart.getItems().get(0).getQuantity(), is(5)); // max stock
    }

    @Test
    void testAddProductToCart_UpdateExistingItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        CartItem existingItem = new CartItem();
        existingItem.setBook(book);
        existingItem.setQuantity(2);

        Cart existingCart = new Cart();
        existingCart.setUser(user);
        existingCart.setItems(new ArrayList<>());
        existingCart.getItems().add(existingItem);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart cart = cartService.addProductToCart(1L, 10L, 3);

        assertThat(cart.getItems(), hasSize(1));
        assertThat(cart.getItems().get(0).getQuantity(), is(5)); // 2 + 3
    }

    @Test
    void testClearCart() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Cart cart = new Cart();
        cart.setId(100L);
        cart.setUser(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        cartService.clearCart(1L);

        verify(cartRepository, times(1)).delete(100L);
    }

    @Test
    void testGetCartByUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        Cart result = cartService.getCartByUser(1L);

        assertThat(result, is(notNullValue()));
        assertThat(result.getUser().getId(), is(1L));
    }

    @Test
    void testAddProductToCart_UserNotLoggedIn() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addProductToCart(1L, 10L, 3)
        );

        assertThat(exception.getMessage(), is("User must login"));

        verify(bookRepository, never()).findById(anyLong());
        verify(cartRepository, never()).findByUser(any(User.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

}
