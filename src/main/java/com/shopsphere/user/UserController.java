package com.shopsphere.user;

import com.shopsphere.auth.AuthDtos;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository repository;
    public UserController(UserRepository repository) { this.repository=repository; }
    @GetMapping public List<AuthDtos.UserResponse> all() { return repository.findAll().stream().map(u -> new AuthDtos.UserResponse(u.getId(),u.getName(),u.getEmail(),u.getRole().name())).toList(); }
}
