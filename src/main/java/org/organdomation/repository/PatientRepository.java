package org.organdomation.repository;

import org.organdomation.model.Patient;
import org.organdomation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUser(User user);
    
    Optional<Patient> findByUserId(Long userId);
}