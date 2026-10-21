package com.shopsphere.auth;

import com.shopsphere.security.CustomUserDetailsService;
import com.shopsphere.security.JwtService;
import com.shopsphere.user.User;
import com.shopsphere.user.UserRepository;
import com.shopsphere.user.UserRole;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository; private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService detailsService; private final JwtService jwtService;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, CustomUserDetailsService detailsService, JwtService jwtService) {
        this.userRepository = userRepository; this.passwordEncoder = passwordEncoder; this.detailsService = detailsService; this.jwtService = jwtService;
    }
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("Email is already registered.");
        User user = userRepository.save(new User(request.name().trim(), email, passwordEncoder.encode(request.password()), UserRole.CUSTOMER));
        return response(user);
    }
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email().trim()).orElseThrow(() -> new BadCredentialsException("Invalid email or password."));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) throw new BadCredentialsException("Invalid email or password.");
        return response(user);
    }
    public AuthDtos.UserResponse me(String email) {
        return userRepository.findByEmailIgnoreCase(email).map(this::userResponse).orElseThrow(() -> new BadCredentialsException("User not found."));
    }
    private AuthDtos.AuthResponse response(User user) {
        var details = detailsService.loadUserByUsername(user.getEmail());
        return new AuthDtos.AuthResponse(jwtService.generateToken(details, user.getRole().name()), userResponse(user));
    }
    public AuthDtos.UserResponse userResponse(User user) { return new AuthDtos.UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name()); }
}
