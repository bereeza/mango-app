package com.mango.mangogatewayservice.service;

import com.mango.mangogatewayservice.dto.AuthenticationRequest;
import com.mango.mangogatewayservice.dto.user.UserInfoDto;
import com.mango.mangogatewayservice.dto.user.UserSaveDto;
import com.mango.mangogatewayservice.exception.InvalidUserDataException;
import com.mango.mangogatewayservice.exception.UserAlreadyExistsException;
import com.mango.mangogatewayservice.exception.UserNotFoundException;
import com.mango.mangogatewayservice.repository.UserRepository;
import com.mango.mangogatewayservice.user.User;
import com.mango.mangogatewayservice.utils.BCryptEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    public UserSaveDto save(AuthenticationRequest request) {
        validateRequest(request);
        User user = getUserFromRequest(request);

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "User with email" + user.getEmail() + " already exists"
            );
        }
        User savedUser = userRepository.save(user);

        return getDtoFrom(savedUser);
    }

    public UserInfoDto getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with this email does not exist")
        );
        return getUserDtoFrom(user);
    }

    public void deleteUser(long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException("User with this email does not exist");
        }
        userRepository.deleteById(id);
    }

    private void validateRequest(AuthenticationRequest request) {
        if (request.getEmail().isEmpty() &&
                request.getPassword().isEmpty()) {
            throw new InvalidUserDataException("Request is null.");
        } else if (request.getEmail().isEmpty() ||
                request.getEmail().matches(EMAIL_REGEX)) {
            throw new InvalidUserDataException("Email is required or invalid.");
        } else if (request.getPassword() == null ||
                request.getPassword().isEmpty()) {
            throw new InvalidUserDataException("Password is required.");
        }
    }

    private UserSaveDto getDtoFrom(User user) {
        return UserSaveDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }

    private UserInfoDto getUserDtoFrom(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    private User getUserFromRequest(AuthenticationRequest request) {
        return User.builder()
                .email(request.getEmail())
                .password(BCryptEncoder.encode(request.getPassword()))
                .nickname(request.getEmail())
                .build();
    }
}
