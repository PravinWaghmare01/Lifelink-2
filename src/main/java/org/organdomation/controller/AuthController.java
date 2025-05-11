package org.organdomation.controller;

import org.organdomation.payload.request.ChangePasswordRequest;
import org.organdomation.payload.request.LoginRequest;
import org.organdomation.payload.request.SignupRequest;
import org.organdomation.payload.response.JwtResponse;
import org.organdomation.payload.response.MessageResponse;
import org.organdomation.security.jwt.JwtUtils;
import org.organdomation.security.services.UserDetailsImpl;
import org.organdomation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        System.out.println("Test endpoint accessed");
        return ResponseEntity.ok(new MessageResponse("API is working!"));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("Login attempt for user: " + loginRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
    
            System.out.println("Authentication successful for: " + loginRequest.getUsername());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            
            System.out.println("User roles: " + roles);
    
            return ResponseEntity.ok(new JwtResponse(jwt,
                                                     userDetails.getId(),
                                                     userDetails.getUsername(),
                                                     userDetails.getEmail(),
                                                     userDetails.getFullName(),
                                                     roles));
        } catch (Exception e) {
            System.out.println("Authentication failed for user: " + loginRequest.getUsername());
            System.out.println("Error message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        userService.registerUser(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword(),
                signUpRequest.getFullName(),
                signUpRequest.getRoles(),
                signUpRequest.getUserType()
        );

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, 
                                           Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Try to authenticate with current password
            try {
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(), 
                        changePasswordRequest.getOldPassword()
                    )
                );
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Current password is incorrect"));
            }
            
            // Validate new password
            if (changePasswordRequest.getNewPassword().length() < 8) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("New password must be at least 8 characters long"));
            }
            
            // Change password
            userService.changePassword(
                userDetails.getUsername(),
                changePasswordRequest.getNewPassword()
            );
            
            return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error changing password: " + e.getMessage()));
        }
    }
}