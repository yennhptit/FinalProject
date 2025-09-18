package org.example.repository;

import org.example.model.*;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    User save(User user);
    List<User> findAll();
}
