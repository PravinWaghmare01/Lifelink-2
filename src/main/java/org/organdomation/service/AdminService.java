package org.organdomation.service;

import org.organdomation.model.*;
import org.organdomation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DonorRepository donorRepository;
    
    @Autowired
    private ReceiverRepository receiverRepository;
    
    @Autowired
    private OrganDonationRepository donationRepository;
    
    @Autowired
    private OrganRequestRepository requestRepository;
    
    @Autowired
    private OrganMatchRepository matchRepository;
    
    @Autowired
    private MatchingService matchingService;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }
    
    public List<Receiver> getAllReceivers() {
        return receiverRepository.findAll();
    }
    
    public List<OrganDonation> getAllDonations() {
        return donationRepository.findAll();
    }
    
    public List<OrganRequest> getAllRequests() {
        return requestRepository.findAll();
    }
    
    public List<OrganMatch> getAllMatches() {
        return matchRepository.findAll();
    }
    
    public List<MatchSuggestion> getMatchSuggestions() {
        return matchingService.generateMatchSuggestions();
    }
    
    public Map<String, Long> getDashboardStatistics() {
        Map<String, Long> stats = new HashMap<>();
        
        // User statistics
        stats.put("totalUsers", (long) userRepository.findAll().size());
        stats.put("totalDonors", (long) donorRepository.findAll().size());
        stats.put("totalReceivers", (long) receiverRepository.findAll().size());
        
        // Donation statistics
        stats.put("totalDonations", (long) donationRepository.findAll().size());
        stats.put("availableDonations", (long) donationRepository.findByStatus(DonationStatus.AVAILABLE).size());
        stats.put("matchedDonations", (long) donationRepository.findByStatus(DonationStatus.MATCHED).size());
        stats.put("transplantedDonations", (long) donationRepository.findByStatus(DonationStatus.TRANSPLANTED).size());
        
        // Request statistics
        stats.put("totalRequests", (long) requestRepository.findAll().size());
        stats.put("pendingRequests", (long) requestRepository.findByStatus(RequestStatus.PENDING).size());
        stats.put("approvedRequests", (long) requestRepository.findByStatus(RequestStatus.APPROVED).size());
        stats.put("matchedRequests", (long) requestRepository.findByStatus(RequestStatus.MATCHED).size());
        stats.put("completedRequests", (long) requestRepository.findByStatus(RequestStatus.COMPLETED).size());
        
        // Match statistics
        stats.put("totalMatches", (long) matchRepository.findAll().size());
        stats.put("pendingMatches", (long) matchRepository.findByStatus(MatchStatus.PENDING).size());
        stats.put("scheduledMatches", (long) matchRepository.findByStatus(MatchStatus.SCHEDULED).size());
        stats.put("completedMatches", (long) matchRepository.findByStatus(MatchStatus.COMPLETED).size());
        
        // Instead of iterating over enum values, use individual counts for each organ type
        // This approach avoids issues with different enum versions
        
        // Count donations by organ type
        stats.put("totalHeartDonations", countDonationsByOrganType(OrganType.HEART));
        stats.put("totalKidneyDonations", countDonationsByOrganType(OrganType.KIDNEY));
        stats.put("totalLiverDonations", countDonationsByOrganType(OrganType.LIVER));
        stats.put("totalLungDonations", countDonationsByOrganType(OrganType.LUNGS));
        stats.put("totalPancreasDonations", countDonationsByOrganType(OrganType.PANCREAS));
        stats.put("totalIntestineDonations", countDonationsByOrganType(OrganType.SMALL_INTESTINE));
        stats.put("totalCorneaDonations", countDonationsByOrganType(OrganType.CORNEA));
        stats.put("totalBoneMarrowDonations", countDonationsByOrganType(OrganType.BONE_MARROW));
        stats.put("totalSkinDonations", countDonationsByOrganType(OrganType.SKIN));
        
        // Count requests by organ type
        stats.put("totalHeartRequests", countRequestsByOrganType(OrganType.HEART));
        stats.put("totalKidneyRequests", countRequestsByOrganType(OrganType.KIDNEY));
        stats.put("totalLiverRequests", countRequestsByOrganType(OrganType.LIVER));
        stats.put("totalLungRequests", countRequestsByOrganType(OrganType.LUNGS));
        stats.put("totalPancreasRequests", countRequestsByOrganType(OrganType.PANCREAS));
        stats.put("totalIntestineRequests", countRequestsByOrganType(OrganType.SMALL_INTESTINE));
        stats.put("totalCorneaRequests", countRequestsByOrganType(OrganType.CORNEA));
        stats.put("totalBoneMarrowRequests", countRequestsByOrganType(OrganType.BONE_MARROW));
        stats.put("totalSkinRequests", countRequestsByOrganType(OrganType.SKIN));
        
        // Recently registered users (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long recentUsers = userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt() != null && 
                        user.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();
        stats.put("recentUsers", recentUsers);
        
        return stats;
    }
    
    public OrganMatch createMatch(Long donationId, Long requestId) {
        return matchingService.createMatch(donationId, requestId);
    }
    
    public OrganMatch scheduleMatch(Long matchId, LocalDateTime scheduledDate) {
        return matchingService.scheduleMatch(matchId, scheduledDate);
    }
    
    public OrganMatch completeMatch(Long matchId) {
        return matchingService.completeMatch(matchId);
    }
    
    /**
     * Helper method to safely count donations by organ type
     * @param organType the organ type to count
     * @return the count of donations with the specified organ type
     */
    private long countDonationsByOrganType(OrganType organType) {
        try {
            return donationRepository.findByOrganType(organType).size();
        } catch (Exception e) {
            return 0L;
        }
    }
    
    /**
     * Helper method to safely count requests by organ type
     * @param organType the organ type to count
     * @return the count of requests with the specified organ type
     */
    private long countRequestsByOrganType(OrganType organType) {
        try {
            return requestRepository.findByOrganType(organType).size();
        } catch (Exception e) {
            return 0L;
        }
    }
}