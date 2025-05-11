package org.organdomation.service;

import org.organdomation.model.*;
import org.organdomation.repository.OrganRequestRepository;
import org.organdomation.repository.ReceiverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReceiverService {
    
    @Autowired
    private ReceiverRepository receiverRepository;
    
    @Autowired
    private OrganRequestRepository organRequestRepository;
    
    public Optional<Receiver> findByUserId(Long userId) {
        return receiverRepository.findByUserId(userId);
    }
    
    public List<Receiver> findAllReceivers() {
        return receiverRepository.findAll();
    }
    
    public Receiver saveReceiver(Receiver receiver) {
        return receiverRepository.save(receiver);
    }
    
    public boolean updateUrgencyLevel(Long receiverId, UrgencyLevel urgencyLevel) {
        Optional<Receiver> receiverOpt = receiverRepository.findById(receiverId);
        if (!receiverOpt.isPresent()) {
            return false;
        }
        
        Receiver receiver = receiverOpt.get();
        receiver.setUrgencyLevel(urgencyLevel);
        receiverRepository.save(receiver);
        
        // Also update urgency level in all active requests
        List<OrganRequest> activeRequests = organRequestRepository.findByReceiverIdAndIsActive(receiver.getId(), true);
        for (OrganRequest request : activeRequests) {
            request.setUrgencyLevel(urgencyLevel);
            organRequestRepository.save(request);
        }
        
        return true;
    }
    
    public OrganRequest createRequest(OrganRequest request, Long receiverId) {
        Receiver receiver = receiverRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Error: Receiver not found."));
        
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);
        request.setUrgencyLevel(receiver.getUrgencyLevel());
        request.setActive(true);
        
        return organRequestRepository.save(request);
    }
    
    public List<OrganRequest> getRequestsByReceiver(Receiver receiver) {
        return organRequestRepository.findByReceiver(receiver);
    }
    
    public List<OrganRequest> getActiveRequests() {
        return organRequestRepository.findByIsActive(true);
    }
    
    public List<OrganRequest> getRequestsByStatus(RequestStatus status) {
        return organRequestRepository.findByStatus(status);
    }
    
    public Optional<OrganRequest> getRequestById(Long requestId) {
        return organRequestRepository.findById(requestId);
    }
    
    public boolean cancelRequest(Long requestId) {
        Optional<OrganRequest> requestOpt = organRequestRepository.findById(requestId);
        if (!requestOpt.isPresent()) {
            return false;
        }
        
        OrganRequest request = requestOpt.get();
        
        // Only cancel if not already matched or completed
        if (request.getStatus() == RequestStatus.MATCHED || request.getStatus() == RequestStatus.COMPLETED) {
            return false;
        }
        
        request.setStatus(RequestStatus.CANCELLED);
        request.setActive(false);
        organRequestRepository.save(request);
        return true;
    }
    
    public boolean approveRequest(Long requestId) {
        Optional<OrganRequest> requestOpt = organRequestRepository.findById(requestId);
        if (!requestOpt.isPresent()) {
            return false;
        }
        
        OrganRequest request = requestOpt.get();
        request.setStatus(RequestStatus.APPROVED);
        request.setDoctorApproval(true);
        organRequestRepository.save(request);
        return true;
    }
    
    public boolean rejectRequest(Long requestId) {
        Optional<OrganRequest> requestOpt = organRequestRepository.findById(requestId);
        if (!requestOpt.isPresent()) {
            return false;
        }
        
        OrganRequest request = requestOpt.get();
        request.setStatus(RequestStatus.REJECTED);
        request.setActive(false);
        organRequestRepository.save(request);
        return true;
    }
    
    public List<OrganRequest> getRequestsByOrganType(OrganType organType) {
        return organRequestRepository.findByOrganTypeAndStatus(organType, RequestStatus.APPROVED);
    }
}