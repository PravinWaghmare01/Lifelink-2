package org.organdomation.repository;

import org.organdomation.model.DonationStatus;
import org.organdomation.model.Donor;
import org.organdomation.model.OrganDonation;
import org.organdomation.model.OrganType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganDonationRepository extends JpaRepository<OrganDonation, Long> {
    List<OrganDonation> findByDonor(Donor donor);
    List<OrganDonation> findByStatus(DonationStatus status);
    List<OrganDonation> findByOrganType(OrganType organType);
    List<OrganDonation> findByIsActive(boolean isActive);
}