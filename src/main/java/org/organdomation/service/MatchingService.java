package org.organdomation.service;

import org.organdomation.model.*;
import org.organdomation.repository.OrganDonationRepository;
import org.organdomation.repository.OrganMatchRepository;
import org.organdomation.repository.OrganRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchingService {
    
    @Autowired
    private OrganDonationRepository donationRepository;
    
    @Autowired
    private OrganRequestRepository requestRepository;
    
    @Autowired
    private OrganMatchRepository matchRepository;
    
    public List<OrganMatch> getAllMatches() {
        return matchRepository.findAll();
    }
    
    public Optional<OrganMatch> getMatchById(Long matchId) {
        return matchRepository.findById(matchId);
    }
    
    public OrganMatch createMatch(Long donationId, Long requestId) {
        OrganDonation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Error: Donation not found."));
        
        OrganRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Error: Request not found."));
        
        // Check if donation and request are already matched
        Optional<OrganMatch> existingDonationMatch = matchRepository.findByOrganDonationId(donation.getId());
        if (existingDonationMatch.isPresent()) {
            throw new RuntimeException("Error: Donation is already matched.");
        }
        
        Optional<OrganMatch> existingRequestMatch = matchRepository.findByOrganRequestId(request.getId());
        if (existingRequestMatch.isPresent()) {
            throw new RuntimeException("Error: Request is already matched.");
        }
        
        // Check if organs match
        if (donation.getOrganType() != request.getOrganType()) {
            throw new RuntimeException("Error: Organ types do not match.");
        }
        
        // Check blood type compatibility
        if (!isBloodTypeCompatible(donation.getDonor().getBloodType(), request.getReceiver().getBloodType())) {
            throw new RuntimeException("Error: Blood types are not compatible.");
        }
        
        // Create match
        OrganMatch match = new OrganMatch();
        match.setOrganDonation(donation);
        match.setOrganRequest(request);
        match.setStatus(MatchStatus.PENDING);
        match.setAdminApproval(true);
        
        // Update donation and request status
        donation.setStatus(DonationStatus.MATCHED);
        request.setStatus(RequestStatus.MATCHED);
        
        // Save all changes
        matchRepository.save(match);
        donationRepository.save(donation);
        requestRepository.save(request);
        
        return match;
    }
    
    public OrganMatch scheduleMatch(Long matchId, LocalDateTime scheduledDate) {
        OrganMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Error: Match not found."));
        
        match.setStatus(MatchStatus.SCHEDULED);
        match.setScheduledDate(scheduledDate);
        
        return matchRepository.save(match);
    }
    
    public OrganMatch completeMatch(Long matchId) {
        OrganMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Error: Match not found."));
        
        match.setStatus(MatchStatus.COMPLETED);
        match.setCompletionDate(LocalDateTime.now());
        
        OrganDonation donation = match.getOrganDonation();
        donation.setStatus(DonationStatus.TRANSPLANTED);
        donation.setActive(false);
        
        OrganRequest request = match.getOrganRequest();
        request.setStatus(RequestStatus.COMPLETED);
        request.setActive(false);
        
        matchRepository.save(match);
        donationRepository.save(donation);
        requestRepository.save(request);
        
        return match;
    }
    
    public List<OrganRequest> findPotentialMatches(Long donationId) {
        OrganDonation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Error: Donation not found."));
        
        // Get all active requests for the same organ type with APPROVED status
        List<OrganRequest> potentialMatches = requestRepository.findByStatus(RequestStatus.APPROVED)
                .stream()
                .filter(request -> request.getOrganType() == donation.getOrganType() && request.isActive())
                .collect(Collectors.toList());
        
        // Filter by blood type compatibility
        List<OrganRequest> compatibleMatches = potentialMatches.stream()
                .filter(request -> isBloodTypeCompatible(
                        donation.getDonor().getBloodType(), 
                        request.getReceiver().getBloodType()))
                .collect(Collectors.toList());
        
        // Sort by urgency level, waiting time, etc.
        compatibleMatches.sort(
                Comparator.comparing(OrganRequest::getUrgencyLevel).reversed()
                        .thenComparing(request -> request.getReceiver().getWaitingSince())
        );
        
        return compatibleMatches;
    }
    
    public List<OrganDonation> findPotentialDonations(Long requestId) {
        OrganRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Error: Request not found."));
        
        // Get all available donations for the same organ type
        List<OrganDonation> potentialDonations = donationRepository.findByStatus(DonationStatus.AVAILABLE)
                .stream()
                .filter(donation -> donation.getOrganType() == request.getOrganType() && donation.isActive())
                .collect(Collectors.toList());
        
        // Filter by blood type compatibility
        return potentialDonations.stream()
                .filter(donation -> isBloodTypeCompatible(
                        donation.getDonor().getBloodType(), 
                        request.getReceiver().getBloodType()))
                .collect(Collectors.toList());
    }
    
    public List<MatchSuggestion> generateMatchSuggestions() {
        List<MatchSuggestion> suggestions = new ArrayList<>();
        
        // Get all available donations
        List<OrganDonation> availableDonations = donationRepository.findByStatus(DonationStatus.AVAILABLE);
        
        for (OrganDonation donation : availableDonations) {
            List<OrganRequest> potentialMatches = findPotentialMatches(donation.getId());
            
            if (!potentialMatches.isEmpty()) {
                // Take the top match (most urgent, waiting longest)
                OrganRequest bestMatch = potentialMatches.get(0);
                
                MatchSuggestion suggestion = new MatchSuggestion();
                suggestion.setDonationId(donation.getId());
                suggestion.setRequestId(bestMatch.getId());
                suggestion.setOrganType(donation.getOrganType());
                suggestion.setDonorBloodType(donation.getDonor().getBloodType());
                suggestion.setReceiverBloodType(bestMatch.getReceiver().getBloodType());
                suggestion.setUrgencyLevel(bestMatch.getUrgencyLevel());
                
                suggestions.add(suggestion);
            }
        }
        
        return suggestions;
    }
    
    // Helper method to check blood type compatibility
    private boolean isBloodTypeCompatible(BloodType donorBloodType, BloodType receiverBloodType) {
        switch (donorBloodType) {
            case O_NEGATIVE:
                // O- can donate to anyone
                return true;
            case O_POSITIVE:
                // O+ can donate to O+, A+, B+, AB+
                return receiverBloodType == BloodType.O_POSITIVE ||
                        receiverBloodType == BloodType.A_POSITIVE ||
                        receiverBloodType == BloodType.B_POSITIVE ||
                        receiverBloodType == BloodType.AB_POSITIVE;
            case A_NEGATIVE:
                // A- can donate to A-, A+, AB-, AB+
                return receiverBloodType == BloodType.A_NEGATIVE ||
                        receiverBloodType == BloodType.A_POSITIVE ||
                        receiverBloodType == BloodType.AB_NEGATIVE ||
                        receiverBloodType == BloodType.AB_POSITIVE;
            case A_POSITIVE:
                // A+ can donate to A+, AB+
                return receiverBloodType == BloodType.A_POSITIVE ||
                        receiverBloodType == BloodType.AB_POSITIVE;
            case B_NEGATIVE:
                // B- can donate to B-, B+, AB-, AB+
                return receiverBloodType == BloodType.B_NEGATIVE ||
                        receiverBloodType == BloodType.B_POSITIVE ||
                        receiverBloodType == BloodType.AB_NEGATIVE ||
                        receiverBloodType == BloodType.AB_POSITIVE;
            case B_POSITIVE:
                // B+ can donate to B+, AB+
                return receiverBloodType == BloodType.B_POSITIVE ||
                        receiverBloodType == BloodType.AB_POSITIVE;
            case AB_NEGATIVE:
                // AB- can donate to AB-, AB+
                return receiverBloodType == BloodType.AB_NEGATIVE ||
                        receiverBloodType == BloodType.AB_POSITIVE;
            case AB_POSITIVE:
                // AB+ can donate only to AB+
                return receiverBloodType == BloodType.AB_POSITIVE;
            default:
                return false;
        }
    }
}