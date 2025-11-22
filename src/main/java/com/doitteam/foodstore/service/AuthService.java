package com.doitteam.foodstore.service;

import com.doitteam.foodstore.dto.response.*;
import com.doitteam.foodstore.dto.request.*;
import com.doitteam.foodstore.exception.BadRequestException;
import com.doitteam.foodstore.model.User;
import com.doitteam.foodstore.model.Cart;
import com.doitteam.foodstore.model.enums.Role;
import com.doitteam.foodstore.repository.UserRepository;
import com.doitteam.foodstore.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        log.info("User registered successfully: {}", savedUser.getUsername());

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().name(),
                "Registration successful"
        );
    }
}