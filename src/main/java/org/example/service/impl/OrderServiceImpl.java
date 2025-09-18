//package org.example.service.impl;
//
//import org.example.model.*;
//import org.example.repository.*;
//import org.example.service.OrderService;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class OrderServiceImpl implements OrderService {
//    private final OrderRepository orderRepository;
//    private final CartRepository cartRepository;
//    private final BookRepository bookRepository;
//    private final UserRepository userRepository;
//
//    public OrderServiceImpl(OrderRepository orderRepository,
//                            CartRepository cartRepository,
//                            BookRepository bookRepository,
//                            UserRepository userRepository) {
//        this.orderRepository = orderRepository;
//        this.cartRepository = cartRepository;
//        this.bookRepository = bookRepository;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public Order checkout(Long userId) {
//        User user = getLoggedInUser(userId);
//        Cart cart = getCart(user);
//
//        validateCartNotEmpty(cart);
//
//        List<Order> createdOrders = new ArrayList<>();
//        for (CartItem item : cart.getItems()) {
//            processCartItem(user, item, createdOrders);
//        }
//
//        // Xóa giỏ hàng sau checkout
//        cartRepository.delete(cart.getId());
//
//        return createdOrders.get(0); // trả về đơn hàng đầu tiên
//    }
//
//    private User getLoggedInUser(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User must login"));
//    }
//
//    private Cart getCart(User user) {
//        return cartRepository.findByUser(user)
//                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));
//    }
//
//    private void validateCartNotEmpty(Cart cart) {
//        if (cart.getItems().isEmpty()) {
//            throw new IllegalArgumentException("Cart is empty");
//        }
//    }
//
//    private void processCartItem(User user, CartItem item, List<Order> createdOrders) {
//        Book book = item.getBook();
//        validateStock(item, book);
//
//        updateStock(book, item.getQuantity());
//
//        Order order = createOrder(user, item);
//        createdOrders.add(order);
//    }
//
//    private void validateStock(CartItem item, Book book) {
//        if (item.getQuantity() > book.getStockQuantity()) {
//            throw new IllegalArgumentException("Not enough stock for book: " + book.getTitle());
//        }
//    }
//
//    private void updateStock(Book book, int quantity) {
//        book.setStockQuantity(book.getStockQuantity() - quantity);
//        bookRepository.save(book);
//    }
//
//    private Order createOrder(User user, CartItem item) {
//        Order order = new Order();
//        order.setBook(item.getBook());
//        order.setQuantity(item.getQuantity());
//        order.setUser(user);
//        order.setCanceled(false);
//        return orderRepository.save(order);
//    }
//
//    @Override
//    public void cancelOrder(Long orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
//
//        if (order.isCanceled()) {
//            throw new IllegalArgumentException("Order already canceled");
//        }
//
//        restoreStock(order);
//        markOrderCanceled(order);
//    }
//
//    private void restoreStock(Order order) {
//        Book book = order.getBook();
//        book.setStockQuantity(book.getStockQuantity() + order.getQuantity());
//        bookRepository.save(book);
//    }
//
//    private void markOrderCanceled(Order order) {
//        order.setCanceled(true);
//        orderRepository.save(order);
//    }
//
//    @Override
//    public List<Order> listOrders(Long userId) {
//        User user = getLoggedInUser(userId);
//        return orderRepository.findByUser(user);
//    }
//}
package org.example.service.impl;

import org.example.model.*;
import org.example.repository.*;
import org.example.service.OrderService;

import java.util.ArrayList;
import java.util.List;

public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            BookRepository bookRepository,
                            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Order checkout(Long userId) {
        User user = getUserOrThrow(userId);
        Cart cart = getCartOrThrow(user);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        List<Order> createdOrders = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            createdOrders.add(handleCartItem(user, item));
        }

        cartRepository.delete(cart.getId());
        return createdOrders.get(0); // giả sử trả về order đầu tiên
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.isCanceled()) {
            throw new IllegalArgumentException("Order already canceled");
        }

        restoreStock(order);
        order.setCanceled(true);
        orderRepository.save(order);
    }

    @Override
    public List<Order> listOrders(Long userId) {
        User user = getUserOrThrow(userId);
        return orderRepository.findByUser(user);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User must login"));
    }

    private Cart getCartOrThrow(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));
    }

    private Order handleCartItem(User user, CartItem item) {
        Book book = item.getBook();

        if (item.getQuantity() > book.getStockQuantity()) {
            throw new IllegalArgumentException("Not enough stock for book: " + book.getTitle());
        }

        book.setStockQuantity(book.getStockQuantity() - item.getQuantity());
        bookRepository.save(book);

        Order order = new Order();
        order.setBook(book);
        order.setQuantity(item.getQuantity());
        order.setUser(user);
        order.setCanceled(false);

        return orderRepository.save(order);
    }

    private void restoreStock(Order order) {
        Book book = order.getBook();
        book.setStockQuantity(book.getStockQuantity() + order.getQuantity());
        bookRepository.save(book);
    }
}
