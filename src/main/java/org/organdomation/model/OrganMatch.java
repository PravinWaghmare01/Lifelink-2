package org.organdomation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organ_matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", unique = true)
    private OrganDonation organDonation;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", unique = true)
    private OrganRequest organRequest;
    
    @Column(name = "match_status")
    @Enumerated(EnumType.STRING)
    private MatchStatus status = MatchStatus.PENDING;
    
    @Column(name = "compatibility_score")
    private Integer compatibilityScore;
    
    @Column(name = "match_notes", columnDefinition = "TEXT")
    private String matchNotes;
    
    @Column(name = "admin_approval")
    private boolean adminApproval = false;
    
    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;
    
    @Column(name = "completion_date")
    private LocalDateTime completionDate;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}