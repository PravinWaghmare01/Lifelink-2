package org.organdomation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organ_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Receiver receiver;
    
    @Column(name = "organ_type")
    @Enumerated(EnumType.STRING)
    private OrganType organType;
    
    @Column(name = "request_status")
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;
    
    @Column(name = "medical_notes", columnDefinition = "TEXT")
    private String medicalNotes;
    
    @Column(name = "doctor_approval")
    private boolean doctorApproval = false;
    
    @Column(name = "urgency_level")
    @Enumerated(EnumType.STRING)
    private UrgencyLevel urgencyLevel;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @OneToOne(mappedBy = "organRequest", cascade = CascadeType.ALL)
    private OrganMatch match;
}