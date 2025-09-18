package org.example.service.impl;

import org.example.model.*;
import org.example.model.enums.UserRole;
import org.example.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class OrderServiceImplTest {

    private OrderRepository orderRepository;
    private CartRepository cartRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private OrderServiceImpl orderService;

    private User user;
    private Book book;
    private CartItem cartItem;

    @BeforeEach
    void setup() {
        // Dùng Mockito-inline để tránh lỗi final class / Lombok
        orderRepository = mock(OrderRepository.class, withSettings().lenient());
        cartRepository = mock(CartRepository.class, withSettings().lenient());
        bookRepository = mock(BookRepository.class, withSettings().lenient());
        userRepository = mock(UserRepository.class, withSettings().lenient());

        orderService = new OrderServiceImpl(orderRepository, cartRepository, bookRepository, userRepository);

        // Tạo user và book thực
        user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setRole(UserRole.USER);

        book = new Book();
        book.setId(10L);
        book.setTitle("Test Book");
        book.setPrice(100);
        book.setStockQuantity(5);

        cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(3);
    }

    @Test
    void testCheckoutSuccess() {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(List.of(cartItem));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order order = orderService.checkout(1L);

        assertThat(order, is(notNullValue()));
        assertThat(order.getBook().getId(), is(10L));
        assertThat(order.getQuantity(), is(3));
        assertThat(book.getStockQuantity(), is(2)); // stock giảm đúng

        verify(cartRepository, times(1)).delete(cart.getId());
        verify(bookRepository, times(1)).save(book);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testCheckout_UserNotLoggedIn() {
        // Giả lập user chưa login / session hết hạn
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> orderService.checkout(1L)
        );

        assertThat(ex.getMessage(), is("User must login"));
        verifyNoInteractions(cartRepository, bookRepository, orderRepository);
    }

    @Test
    void testCheckout_NotEnoughStock() {
        CartItem item = new CartItem();
        item.setBook(book);
        item.setQuantity(10); // vượt stock

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(List.of(item));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        IllegalArgumentException ex = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> orderService.checkout(1L)
        );

        assertThat(ex.getMessage(), containsString("Not enough stock"));
        verify(bookRepository, never()).save(any(Book.class));
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartRepository, never()).delete(anyLong());
    }

    @Test
    void testCancelOrder_Success() {
        Order order = new Order();
        order.setBook(book);
        order.setQuantity(2);
        order.setCanceled(false);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        orderService.cancelOrder(1L);

        assertThat(order.isCanceled(), is(true));
        assertThat(book.getStockQuantity(), is(7)); // stock được trả lại

        verify(bookRepository, times(1)).save(book);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testCancelOrder_AlreadyCanceled() {
        Order order = new Order();
        order.setBook(book);
        order.setQuantity(1);
        order.setCanceled(true);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        IllegalArgumentException ex = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> orderService.cancelOrder(1L)
        );

        assertThat(ex.getMessage(), is("Order already canceled"));
        verify(bookRepository, never()).save(any(Book.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testListOrders() {
        Order order1 = new Order();
        order1.setBook(book);
        order1.setQuantity(1);
        order1.setUser(user);

        Order order2 = new Order();
        order2.setBook(book);
        order2.setQuantity(2);
        order2.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(List.of(order1, order2));

        List<Order> orders = orderService.listOrders(1L);

        assertThat(orders, hasSize(2));
        assertThat(orders, contains(order1, order2));
    }
}
