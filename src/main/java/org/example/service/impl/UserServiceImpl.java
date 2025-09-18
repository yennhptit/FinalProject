package org.example.service.impl;

import org.example.model.User;
import org.example.model.enums.UserRole;
import org.example.repository.UserRepository;
import org.example.service.UserService;

import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(User user) {
        // Mặc định role là USER nếu chưa set
        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(String username, String password) {
        // Tìm user theo username và kiểm tra password
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password));
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
