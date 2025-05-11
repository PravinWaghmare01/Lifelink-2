package org.organdomation.model;

import lombok.Data;

/**
 * This class represents a suggested match between a donation and a request.
 * It's used for the matching algorithm to suggest potential matches to administrators.
 */
@Data
public class MatchSuggestion {
    private Long donationId;
    private Long requestId;
    private OrganType organType;
    private BloodType donorBloodType;
    private BloodType receiverBloodType;
    private UrgencyLevel urgencyLevel;
    private Double compatibilityScore;
}