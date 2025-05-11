package org.organdomation.controller;

import org.organdomation.model.*;
import org.organdomation.payload.response.MessageResponse;
import org.organdomation.service.AdminService;
import org.organdomation.service.DonorService;
import org.organdomation.service.MatchingService;
import org.organdomation.service.ReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins="http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private DonorService donorService;
    
    @Autowired
    private ReceiverService receiverService;
    
    @Autowired
    private MatchingService matchingService;

    @GetMapping("/donors")
    public ResponseEntity<?> getAllDonors() {
        List<Donor> donors = adminService.getAllDonors();
        return ResponseEntity.ok(donors);
    }

    @GetMapping("/receivers")
    public ResponseEntity<?> getAllReceivers() {
        List<Receiver> receivers = adminService.getAllReceivers();
        return ResponseEntity.ok(receivers);
    }

    @GetMapping("/donations")
    public ResponseEntity<?> getAllDonations() {
        List<OrganDonation> donations = adminService.getAllDonations();
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getAllRequests() {
        List<OrganRequest> requests = adminService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/matches")
    public ResponseEntity<?> getAllMatches() {
        List<OrganMatch> matches = adminService.getAllMatches();
        return ResponseEntity.ok(matches);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<?> getDashboardStatistics() {
        Map<String, Long> statistics = adminService.getDashboardStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/match-suggestions")
    public ResponseEntity<?> getMatchSuggestions() {
        List<MatchSuggestion> suggestions = adminService.getMatchSuggestions();
        return ResponseEntity.ok(suggestions);
    }

    @PutMapping("/donations/{id}/approve")
    public ResponseEntity<?> approveDonation(@PathVariable Long id) {
        boolean approved = donorService.approveDonation(id);
        
        if (!approved) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Failed to approve donation."));
        }
        
        return ResponseEntity.ok(new MessageResponse("Donation approved successfully!"));
    }

    @PutMapping("/donations/{id}/reject")
    public ResponseEntity<?> rejectDonation(@PathVariable Long id) {
        boolean rejected = donorService.rejectDonation(id);
        
        if (!rejected) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Failed to reject donation."));
        }
        
        return ResponseEntity.ok(new MessageResponse("Donation rejected successfully!"));
    }

    @PutMapping("/requests/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        boolean approved = receiverService.approveRequest(id);
        
        if (!approved) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Failed to approve request."));
        }
        
        return ResponseEntity.ok(new MessageResponse("Request approved successfully!"));
    }

    @PutMapping("/requests/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        boolean rejected = receiverService.rejectRequest(id);
        
        if (!rejected) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Failed to reject request."));
        }
        
        return ResponseEntity.ok(new MessageResponse("Request rejected successfully!"));
    }

    @PostMapping("/match")
    public ResponseEntity<?> createMatch(@RequestBody Map<String, Long> matchRequest) {
        try {
            Long donationId = matchRequest.get("donationId");
            Long requestId = matchRequest.get("requestId");
            
            if (donationId == null || requestId == null) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Missing donation or request ID."));
            }
            
            OrganMatch match = adminService.createMatch(donationId, requestId);
            return ResponseEntity.ok(match);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/matches/{id}/schedule")
    public ResponseEntity<?> scheduleMatch(@PathVariable Long id, @RequestBody java.time.LocalDateTime scheduledDate) {
        try {
            adminService.scheduleMatch(id, scheduledDate);
            return ResponseEntity.ok(new MessageResponse("Match scheduled successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/matches/{id}/complete")
    public ResponseEntity<?> completeMatch(@PathVariable Long id) {
        try {
            adminService.completeMatch(id);
            return ResponseEntity.ok(new MessageResponse("Match completed successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/potential-matches/{donationId}")
    public ResponseEntity<?> getPotentialMatches(@PathVariable Long donationId) {
        try {
            List<OrganRequest> potentialMatches = matchingService.findPotentialMatches(donationId);
            return ResponseEntity.ok(potentialMatches);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/potential-donations/{requestId}")
    public ResponseEntity<?> getPotentialDonations(@PathVariable Long requestId) {
        try {
            List<OrganDonation> potentialDonations = matchingService.findPotentialDonations(requestId);
            return ResponseEntity.ok(potentialDonations);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}