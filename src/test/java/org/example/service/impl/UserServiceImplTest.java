package org.example.service.impl;

import org.example.model.User;
import org.example.model.enums.UserRole;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;


class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(UserRole.USER);
    }

    @Test
    void testRegister_NewUser() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("12345");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registered = userService.register(newUser);

        assertThat(registered, is(notNullValue()));
        assertThat(registered.getUsername(), is("newuser"));
        assertThat(registered.getRole(), is(UserRole.USER));
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> loggedIn = userService.login("testuser", "password");

        assertThat(loggedIn.isPresent(), is(true));
        assertThat(loggedIn.get().getUsername(), is("testuser"));
    }

    @Test
    void testLogin_WrongPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> loggedIn = userService.login("testuser", "wrongpassword");

        assertThat(loggedIn.isPresent(), is(false));
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> loggedIn = userService.login("unknown", "password");

        assertThat(loggedIn.isPresent(), is(false));
    }

    @Test
    void testFindById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> found = userService.findById(1L);

        assertThat(found.isPresent(), is(true));
        assertThat(found.get().getUsername(), is("testuser"));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<User> found = userService.findById(2L);

        assertThat(found.isPresent(), is(false));
        verify(userRepository, times(1)).findById(2L);
    }
}
