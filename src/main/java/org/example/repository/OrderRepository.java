package org.example.repository;
import org.example.model.*;
import java.util.List;
import java.util.Optional;
public interface OrderRepository {
    Optional<Order> findById(Long id);
    Order save(Order order);
    List<Order> findByUser(User user);
}
