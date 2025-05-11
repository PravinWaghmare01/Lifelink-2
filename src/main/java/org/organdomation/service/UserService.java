package org.organdomation.service;

import org.organdomation.model.*;
import org.organdomation.repository.DonorRepository;
import org.organdomation.repository.ReceiverRepository;
import org.organdomation.repository.RoleRepository;
import org.organdomation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private DonorRepository donorRepository;
    
    @Autowired
    private ReceiverRepository receiverRepository;
    
    @Autowired
    private PasswordEncoder encoder;
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User registerUser(String username, String email, String password, String fullName, Set<String> strRoles, UserType userType) {
        User user = new User(username, email, encoder.encode(password), fullName);
        
        user.setUserType(userType);
        
        Set<Role> roles = new HashSet<>();
        
        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
            
            // Also add a specific role based on user type
            if (userType != null) {
                switch (userType) {
                    case DONOR:
                        Role donorRole = roleRepository.findByName(ERole.ROLE_DONOR)
                                .orElseThrow(() -> new RuntimeException("Error: Donor Role is not found."));
                        roles.add(donorRole);
                        break;
                    case RECEIVER:
                        Role receiverRole = roleRepository.findByName(ERole.ROLE_RECEIVER)
                                .orElseThrow(() -> new RuntimeException("Error: Receiver Role is not found."));
                        roles.add(receiverRole);
                        break;
                    case ADMIN:
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        break;
                }
            }
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "donor":
                        Role donorRole = roleRepository.findByName(ERole.ROLE_DONOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(donorRole);
                        break;
                    case "receiver":
                        Role receiverRole = roleRepository.findByName(ERole.ROLE_RECEIVER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(receiverRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        
        user.setRoles(roles);
        return userRepository.save(user);
    }
    
    public Donor createDonorProfile(Donor donor, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        donor.setUser(user);
        donor.setActiveDonor(true);
        
        return donorRepository.save(donor);
    }
    
    public Receiver createReceiverProfile(Receiver receiver, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        receiver.setUser(user);
        
        return receiverRepository.save(receiver);
    }
    
    /**
     * Changes the password for a user
     * @param username the username of the user
     * @param newPassword the new password
     * @return the updated user object
     */
    public User changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        user.setPassword(encoder.encode(newPassword));
        return userRepository.save(user);
    }
}