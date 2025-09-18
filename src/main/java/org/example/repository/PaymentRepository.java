package org.example.repository;
import org.example.model.*;
import java.util.List;
import java.util.Optional;
public interface PaymentRepository {
    Optional<Payment> findById(Long id);
    Payment save(Payment payment);
    List<Payment> findByOrder(Order order);
}
