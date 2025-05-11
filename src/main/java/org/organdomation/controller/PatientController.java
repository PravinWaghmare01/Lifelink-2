package org.organdomation.controller;

import org.organdomation.model.*;
import org.organdomation.payload.response.MessageResponse;
import org.organdomation.payload.response.ProfileResponse;
import org.organdomation.security.services.UserDetailsImpl;
import org.organdomation.service.DonorService;
import org.organdomation.service.ReceiverService;
import org.organdomation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/patient")
public class PatientController {
    @Autowired
    private UserService userService;

    @Autowired
    private DonorService donorService;

    @Autowired
    private ReceiverService receiverService;

    @PostMapping("/profile/donor")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<?> createDonorProfile(@Valid @RequestBody Donor donorProfile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Check if profile already exists
        Optional<Donor> existingDonor = donorService.findByUserId(userDetails.getId());
        if (existingDonor.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Profile already exists!"));
        }
        
        userService.createDonorProfile(donorProfile, userDetails.getId());
        
        return ResponseEntity.ok(new MessageResponse("Donor profile created successfully!"));
    }
    
    @PostMapping("/profile/receiver")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<?> createReceiverProfile(@Valid @RequestBody Receiver receiverProfile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Check if profile already exists
        Optional<Receiver> existingReceiver = receiverService.findByUserId(userDetails.getId());
        if (existingReceiver.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Profile already exists!"));
        }
        
        userService.createReceiverProfile(receiverProfile, userDetails.getId());
        
        return ResponseEntity.ok(new MessageResponse("Receiver profile created successfully!"));
    }
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('DONOR') or hasRole('RECEIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Check if user has DONOR role
        boolean isDonor = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DONOR"));
                
        // Check if user has RECEIVER role
        boolean isReceiver = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RECEIVER"));
        
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setUserId(userDetails.getId());
        profileResponse.setUsername(userDetails.getUsername());
        profileResponse.setEmail(userDetails.getEmail());
        profileResponse.setFullName(userDetails.getFullName());
        profileResponse.setUserType(isDonor ? "DONOR" : (isReceiver ? "RECEIVER" : "USER"));
        
        if (isDonor) {
            // Check donor profile
            Optional<Donor> donor = donorService.findByUserId(userDetails.getId());
            if (donor.isPresent()) {
                profileResponse.setProfileExists(true);
                profileResponse.setProfileData(donor.get());
            } else {
                profileResponse.setProfileExists(false);
                profileResponse.setMessage("Donor profile not created yet");
            }
        } else if (isReceiver) {
            // Check receiver profile
            Optional<Receiver> receiver = receiverService.findByUserId(userDetails.getId());
            if (receiver.isPresent()) {
                profileResponse.setProfileExists(true);
                profileResponse.setProfileData(receiver.get());
            } else {
                profileResponse.setProfileExists(false);
                profileResponse.setMessage("Receiver profile not created yet");
            }
        } else {
            profileResponse.setProfileExists(false);
            profileResponse.setMessage("You are not registered as a donor or receiver");
        }
        
        return ResponseEntity.ok(profileResponse);
    }
    
    @PutMapping("/profile/donor")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<?> updateDonorProfile(@Valid @RequestBody Donor updatedDonor) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Donor donor = donorService.findByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: Donor profile not found."));
        
        // Update fields
        donor.setBloodType(updatedDonor.getBloodType());
        donor.setDateOfBirth(updatedDonor.getDateOfBirth());
        donor.setMedicalHistory(updatedDonor.getMedicalHistory());
        donor.setContactNumber(updatedDonor.getContactNumber());
        donor.setAddress(updatedDonor.getAddress());
        donor.setEmergencyContactName(updatedDonor.getEmergencyContactName());
        donor.setEmergencyContactNumber(updatedDonor.getEmergencyContactNumber());
        donor.setPreferredHospital(updatedDonor.getPreferredHospital());
        
        donorService.saveDonor(donor);
        
        return ResponseEntity.ok(new MessageResponse("Donor profile updated successfully!"));
    }
    
    @PutMapping("/profile/receiver")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<?> updateReceiverProfile(@Valid @RequestBody Receiver updatedReceiver) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Receiver receiver = receiverService.findByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: Receiver profile not found."));
        
        // Update fields
        receiver.setBloodType(updatedReceiver.getBloodType());
        receiver.setDateOfBirth(updatedReceiver.getDateOfBirth());
        receiver.setMedicalHistory(updatedReceiver.getMedicalHistory());
        receiver.setContactNumber(updatedReceiver.getContactNumber());
        receiver.setAddress(updatedReceiver.getAddress());
        receiver.setPrimaryPhysician(updatedReceiver.getPrimaryPhysician());
        receiver.setPhysicianContact(updatedReceiver.getPhysicianContact());
        receiver.setInsuranceInformation(updatedReceiver.getInsuranceInformation());
        
        receiverService.saveReceiver(receiver);
        
        return ResponseEntity.ok(new MessageResponse("Receiver profile updated successfully!"));
    }
}