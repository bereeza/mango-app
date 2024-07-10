package com.mango.mangoauthservice.service;

import com.mango.mangoauthservice.dto.AuthenticationRequest;
import com.mango.mangoauthservice.dto.AuthenticationResponse;
import com.mango.mangoauthservice.exception.InvalidUserDataException;
import com.mango.mangoauthservice.exception.UserExistsException;
import com.mango.mangoauthservice.user.Role;
import com.mango.mangoauthservice.user.User;
import com.mango.mangoauthservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(AuthenticationRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserExistsException(
                    String.format("User with email %s already exists", request.getEmail())
            );
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getEmail())
                .role(Role.USER)
                .build();

        repository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new InvalidUserDataException("The data is not correct.");
        }

        User user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
