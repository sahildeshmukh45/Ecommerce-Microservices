package com.app.ecom.service;

import com.app.ecom.dto.userDtos.UserRequest;
import com.app.ecom.dto.userDtos.UserResponse;
import com.app.ecom.exception.ConflictException;
import com.app.ecom.exception.UserNotFoundException;
import com.app.ecom.mapper.UserMapper;
import com.app.ecom.repository.UserRepo;
import com.app.ecom.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepo.findAll(pageable);
        return userPage.map(userMapper::toResponse);
    }


    public UserResponse createUser(UserRequest userRequest) {
        try {
            boolean isPhoneNumberExists = userRepo.existsByPhoneNumber(userRequest.getPhoneNumber());

            if (isPhoneNumberExists) {
                throw new ConflictException("Phone number already in use");
            }

            User newUser = userMapper.toEntity(userRequest);
            User savedUser = userRepo.save(newUser);

            log.info("User created with id: {}", savedUser.getId());

            return userMapper.toResponse(savedUser);

        } catch (DataAccessException e) {
            log.error("Database error while saving user: {}", userRequest, e);
            throw new RuntimeException("Unable to create user at the moment");
        }
    }


    public UserResponse getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with id " + id));

        log.info("User found with id: {}", id);

        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));

        boolean existsByPhoneNumber = userRepo.existsByPhoneNumberAndIdNot(userRequest.getPhoneNumber(), id);
        if (existsByPhoneNumber) {
            throw new ConflictException("Phone number already in use by another user");
        }

        // Use MapStruct for partial updates
        userMapper.updateUserFromRequest(userRequest, existingUser);

        User updatedUser = userRepo.save(existingUser);

        return userMapper.toResponse(updatedUser);
    }



    @Transactional
    public void deleteUser(Long id) {
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));

        userRepo.deleteById(id);
    }

}
