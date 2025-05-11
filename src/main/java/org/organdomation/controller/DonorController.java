package org.organdomation.controller;

import org.organdomation.model.*;
import org.organdomation.payload.response.MessageResponse;
import org.organdomation.security.services.UserDetailsImpl;
import org.organdomation.service.DonorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/donor")
public class DonorController {
    @Autowired
    private DonorService donorService;

    @PostMapping("/donate")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<?> createDonation(@Valid @RequestBody OrganDonation donation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Donor donor = donorService.findByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: Donor profile not found."));
        
        if (!donor.isActiveDonor()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Your donor status is inactive. Contact admin for help."));
        }
        
        // Use the service to create the donation
        donorService.createDonation(donation, donor.getId());
        
        return ResponseEntity.ok(new MessageResponse("Organ donation registered successfully!"));
    }
    
    @GetMapping("/donations")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<?> getDonations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Donor donor = donorService.findByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: Donor profile not found."));
        
        List<OrganDonation> donations = donorService.getDonationsByDonor(donor);
        
        return ResponseEntity.ok(donations);
    }
    
    @PutMapping("/donations/{id}/cancel")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<?> cancelDonation(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Donor donor = donorService.findByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: Donor profile not found."));
        
        Optional<OrganDonation> donationOpt = donorService.getDonationById(id);
        if (!donationOpt.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Donation not found."));
        }
        
        OrganDonation donation = donationOpt.get();
        
        // Check if donation belongs to the authenticated donor
        if (!donation.getDonor().getId().equals(donor.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You don't have permission to cancel this donation."));
        }
        
        // Check if donation can be cancelled (not already matched or transplanted)
        if (donation.getStatus() == DonationStatus.MATCHED || donation.getStatus() == DonationStatus.TRANSPLANTED) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Cannot cancel a donation that is already matched or transplanted."));
        }
        
        // Use the service to cancel the donation
        boolean cancelled = donorService.cancelDonation(id);
        
        if (!cancelled) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Failed to cancel donation."));
        }
        
        return ResponseEntity.ok(new MessageResponse("Donation cancelled successfully!"));
    }
    
    @PutMapping("/status/toggle")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<?> toggleDonorStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Donor donor = donorService.findByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: Donor profile not found."));
        
        // Toggle the donor status
        boolean newStatus = !donor.isActiveDonor();
        donorService.updateDonorStatus(donor.getId(), newStatus);
        
        String status = newStatus ? "active" : "inactive";
        return ResponseEntity.ok(new MessageResponse("Donor status changed to " + status + " successfully!"));
    }
}