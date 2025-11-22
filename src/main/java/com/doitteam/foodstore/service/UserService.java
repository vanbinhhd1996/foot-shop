package com.doitteam.foodstore.service;


import com.doitteam.foodstore.dto.request.ChangePasswordRequest;
import com.doitteam.foodstore.dto.request.UpdateUserRequest;
import com.doitteam.foodstore.dto.response.UserResponse;
import com.doitteam.foodstore.exception.BadRequestException;
import com.doitteam.foodstore.exception.ResourceNotFoundException;
import com.doitteam.foodstore.exception.UnauthorizedException;
import com.doitteam.foodstore.model.User;
import com.doitteam.foodstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        User updatedUser = userRepository.save(user);
        log.info("User profile updated: {}", updatedUser.getUsername());

        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!user.getPassword().equals(request.getOldPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        log.info("Password changed for user: {}", user.getUsername());
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAddress(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }
}