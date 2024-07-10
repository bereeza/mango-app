package com.mango.mangoauthservice.service;

import com.mango.mangoauthservice.dto.UserInfoDto;
import com.mango.mangoauthservice.repository.UserRepository;
import com.mango.mangoauthservice.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfoDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return getBuild(userDetails.getUsername());
        }
        return null;
    }

    private UserInfoDto getBuild(String username) {
        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return UserInfoDto.builder()
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .role(user.getRole())
                    .build();
        }
        return null;
    }
}
