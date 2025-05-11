package org.organdomation.repository;

import org.organdomation.model.Receiver;
import org.organdomation.model.UrgencyLevel;
import org.organdomation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiverRepository extends JpaRepository<Receiver, Long> {
    Optional<Receiver> findByUser(User user);
    
    Optional<Receiver> findByUserId(Long userId);
    
    List<Receiver> findByUrgencyLevel(UrgencyLevel urgencyLevel);
    
    List<Receiver> findByUrgencyLevelOrderByWaitingSinceAsc(UrgencyLevel urgencyLevel);
}