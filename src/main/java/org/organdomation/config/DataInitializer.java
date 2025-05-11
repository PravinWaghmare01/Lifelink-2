package org.organdomation.config;

import org.organdomation.model.ERole;
import org.organdomation.model.Role;
import org.organdomation.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Data initializer to populate initial essential data when the application starts.
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    public void run(String... args) {
        // Initialize roles if they don't exist
        initRoles();
    }
    
    private void initRoles() {
        Arrays.stream(ERole.values()).forEach(eRole -> {
            if (!roleRepository.existsByName(eRole)) {
                Role role = new Role();
                role.setName(eRole);
                roleRepository.save(role);
                System.out.println("Created role: " + eRole.name());
            }
        });
    }
}