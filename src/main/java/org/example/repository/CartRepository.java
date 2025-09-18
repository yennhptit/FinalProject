package org.example.repository;
import org.example.model.*;
import java.util.List;
import java.util.Optional;
public interface CartRepository {
    Optional<Cart> findById(Long id);
    Cart save(Cart cart);
    void delete(Long id);
    Optional<Cart> findByUser(User user);
}
