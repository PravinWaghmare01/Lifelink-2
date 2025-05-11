package org.organdomation.repository;

import org.organdomation.model.Donor;
import org.organdomation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {
    Optional<Donor> findByUser(User user);
    
    Optional<Donor> findByUserId(Long userId);
    
    List<Donor> findByIsActiveDonor(boolean isActive);
}