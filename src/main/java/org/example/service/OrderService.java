package org.example.service;

import org.example.model.Order;
import java.util.List;

public interface OrderService {
    Order checkout(Long userId);
    void cancelOrder(Long orderId);
    List<Order> listOrders(Long userId);
}
