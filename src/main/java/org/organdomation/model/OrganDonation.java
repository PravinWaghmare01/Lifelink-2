package org.organdomation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organ_donations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;
    
    @Column(name = "organ_type")
    @Enumerated(EnumType.STRING)
    private OrganType organType;
    
    @Column(name = "donation_status")
    @Enumerated(EnumType.STRING)
    private DonationStatus status = DonationStatus.AVAILABLE;
    
    @Column(name = "medical_notes", columnDefinition = "TEXT")
    private String medicalNotes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @OneToOne(mappedBy = "organDonation", cascade = CascadeType.ALL)
    private OrganMatch match;
}