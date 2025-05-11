package org.organdomation.repository;

import org.organdomation.model.MatchStatus;
import org.organdomation.model.OrganMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganMatchRepository extends JpaRepository<OrganMatch, Long> {
    Optional<OrganMatch> findByOrganDonationId(Long donationId);
    Optional<OrganMatch> findByOrganRequestId(Long requestId);
    List<OrganMatch> findByStatus(MatchStatus status);
}