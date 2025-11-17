package com.example.vag.controller.mobile;

import com.example.vag.dto.AuthResponse;
import com.example.vag.dto.UserDTO;
import com.example.vag.mapper.ArtworkMapper;
import com.example.vag.model.User;
import com.example.vag.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mobile/auth")
public class MobileAuthController {

    private final UserService userService;
    private final ArtworkMapper artworkMapper;

    public MobileAuthController(UserService userService, ArtworkMapper artworkMapper) {
        this.userService = userService;
        this.artworkMapper = artworkMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            User user = userService.authenticate(username, password);
            UserDTO userDTO = artworkMapper.toUserDTO(user);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setSuccess(true);
            authResponse.setMessage("Login successful");
            authResponse.setId(userDTO.getId());
            authResponse.setUsername(userDTO.getUsername());
            authResponse.setEmail(userDTO.getEmail());
            authResponse.setRole(userDTO.getRole());

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setSuccess(false);
            authResponse.setMessage("Invalid credentials: " + e.getMessage());
            return ResponseEntity.badRequest().body(authResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                AuthResponse authResponse = new AuthResponse();
                authResponse.setSuccess(false);
                authResponse.setMessage("Username already exists");
                return ResponseEntity.badRequest().body(authResponse);
            }

            if (userService.findByEmail(user.getEmail()).isPresent()) {
                AuthResponse authResponse = new AuthResponse();
                authResponse.setSuccess(false);
                authResponse.setMessage("Email already exists");
                return ResponseEntity.badRequest().body(authResponse);
            }

            User registeredUser = userService.register(user);
            UserDTO userDTO = artworkMapper.toUserDTO(registeredUser);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setSuccess(true);
            authResponse.setMessage("Registration successful");
            authResponse.setId(userDTO.getId());
            authResponse.setUsername(userDTO.getUsername());
            authResponse.setEmail(userDTO.getEmail());
            authResponse.setRole(userDTO.getRole());

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setSuccess(false);
            authResponse.setMessage("Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(authResponse);
        }
    }
}