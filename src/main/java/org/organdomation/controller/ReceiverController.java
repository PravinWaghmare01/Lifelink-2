package org.organdomation.controller;

import org.organdomation.model.*;
import org.organdomation.payload.response.MessageResponse;
import org.organdomation.security.services.UserDetailsImpl;
import org.organdomation.service.ReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/receiver")
public class ReceiverController {
    @Autowired
    private ReceiverService receiverService;

    @PostMapping("/request")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<?> createRequest(@Valid @RequestBody OrganRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Check if receiver profile exists
        Optional<Receiver> receiverOpt = receiverService.findByUserId(userDetails.getId());
        if (receiverOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("You need to create a receiver profile before submitting an organ request. Please create your profile first."));
        }
        
        Receiver receiver = receiverOpt.get();
        
        // Use the service to create the request
        receiverService.createRequest(request, receiver.getId());
        
        return ResponseEntity.ok(new MessageResponse("Organ request submitted successfully!"));
    }
    
    @GetMapping("/requests")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<?> getRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Check if receiver profile exists
        Optional<Receiver> receiverOpt = receiverService.findByUserId(userDetails.getId());
        if (receiverOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Receiver profile not found. Please create your profile first."));
        }
        
        Receiver receiver = receiverOpt.get();
        List<OrganRequest> requests = receiverService.getRequestsByReceiver(receiver);
        
        return ResponseEntity.ok(requests);
    }
    
    @PutMapping("/requests/{id}/cancel")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<?> cancelRequest(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Check if receiver profile exists
        Optional<Receiver> receiverOpt = receiverService.findByUserId(userDetails.getId());
        if (receiverOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Receiver profile not found. Please create your profile first."));
        }
        
        Receiver receiver = receiverOpt.get();
        
        Optional<OrganRequest> requestOpt = receiverService.getRequestById(id);
        if (!requestOpt.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Request not found."));
        }
        
        OrganRequest request = requestOpt.get();
        
        // Check if request belongs to the authenticated receiver
        if (!request.getReceiver().getId().equals(receiver.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You don't have permission to cancel this request."));
        }
        
        // Check if request can be cancelled (not already matched or completed)
        if (request.getStatus() == RequestStatus.MATCHED || request.getStatus() == RequestStatus.COMPLETED) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Cannot cancel a request that is already matched or completed."));
        }
        
        // Use the service to cancel the request
        boolean cancelled = receiverService.cancelRequest(id);
        
        if (!cancelled) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Failed to cancel request."));
        }
        
        return ResponseEntity.ok(new MessageResponse("Request cancelled successfully!"));
    }
    
    @PutMapping("/urgency")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<?> updateUrgencyLevel(@RequestBody UrgencyLevel urgencyLevel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Check if receiver profile exists
        Optional<Receiver> receiverOpt = receiverService.findByUserId(userDetails.getId());
        if (receiverOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Receiver profile not found. Please create your profile first."));
        }
        
        Receiver receiver = receiverOpt.get();
        
        // Use the service to update urgency level
        boolean updated = receiverService.updateUrgencyLevel(receiver.getId(), urgencyLevel);
        
        if (!updated) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Failed to update urgency level."));
        }
        
        return ResponseEntity.ok(new MessageResponse("Urgency level updated successfully!"));
    }
}