package org.organdomation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "donors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Donor extends Patient {
    
    @Column(name = "is_active_donor")
    private boolean isActiveDonor = true;
    
    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrganDonation> organDonations = new HashSet<>();
    
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_number")
    private String emergencyContactNumber;
    
    @Column(name = "organ_donor_card_id")
    private String organDonorCardId;
    
    @Column(name = "preferred_hospital")
    private String preferredHospital;
}