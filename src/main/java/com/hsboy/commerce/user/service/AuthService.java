package com.hsboy.commerce.user.service;

import com.hsboy.commerce.common.security.JwtProvider;
import com.hsboy.commerce.user.User;
import com.hsboy.commerce.user.dto.LoginRequest;
import com.hsboy.commerce.user.dto.LoginResponse;
import com.hsboy.commerce.user.dto.SignupRequest;
import com.hsboy.commerce.user.dto.SignupResponse;
import com.hsboy.commerce.user.exception.DuplicateEmailException;
import com.hsboy.commerce.user.exception.InvalidPasswordException;
import com.hsboy.commerce.user.exception.NotExistedUserException;
import com.hsboy.commerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = User.create(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName()
        );

        return SignupResponse.from(userRepository.save(user));

    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotExistedUserException(request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        String accessToken = jwtProvider.generateAccessToken(request.email());
        String refreshToken = jwtProvider.generateRefreshToken(request.email());

        return new LoginResponse(accessToken, refreshToken);
    }


}
