package org.example.service;

import org.example.model.User;
import java.util.Optional;

public interface UserService {
    User register(User user);
    Optional<User> login(String username, String password); // login trả về Optional<User>
    Optional<User> findById(Long id);
}
