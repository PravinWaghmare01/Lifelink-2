package org.organdomation.repository;

import org.organdomation.model.OrganRequest;
import org.organdomation.model.OrganType;
import org.organdomation.model.Receiver;
import org.organdomation.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganRequestRepository extends JpaRepository<OrganRequest, Long> {
    List<OrganRequest> findByReceiver(Receiver receiver);
    List<OrganRequest> findByStatus(RequestStatus status);
    List<OrganRequest> findByOrganType(OrganType organType);
    List<OrganRequest> findByIsActive(boolean isActive);
    List<OrganRequest> findByReceiverIdAndIsActive(Long receiverId, boolean isActive);
    List<OrganRequest> findByOrganTypeAndStatus(OrganType organType, RequestStatus status);
}