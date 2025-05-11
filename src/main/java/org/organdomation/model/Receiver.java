package org.organdomation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "receivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Receiver extends Patient {
    
    @Column(name = "urgency_level")
    @Enumerated(EnumType.STRING)
    private UrgencyLevel urgencyLevel;
    
    @Column(name = "waiting_since")
    private java.time.LocalDate waitingSince;
    
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrganRequest> organRequests = new HashSet<>();
    
    @Column(name = "primary_physician")
    private String primaryPhysician;
    
    @Column(name = "physician_contact")
    private String physicianContact;
    
    @Column(name = "insurance_information")
    private String insuranceInformation;
}