package org.organdomation.service;

import org.organdomation.model.*;
import org.organdomation.repository.DonorRepository;
import org.organdomation.repository.OrganDonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonorService {
    
    @Autowired
    private DonorRepository donorRepository;
    
    @Autowired
    private OrganDonationRepository organDonationRepository;
    
    public Optional<Donor> findByUserId(Long userId) {
        return donorRepository.findByUserId(userId);
    }
    
    public List<Donor> findAllDonors() {
        return donorRepository.findAll();
    }
    
    public List<Donor> findActiveDonors() {
        return donorRepository.findByIsActiveDonor(true);
    }
    
    public Donor saveDonor(Donor donor) {
        return donorRepository.save(donor);
    }
    
    public boolean updateDonorStatus(Long donorId, boolean isActive) {
        Optional<Donor> donorOpt = donorRepository.findById(donorId);
        if (!donorOpt.isPresent()) {
            return false;
        }
        
        Donor donor = donorOpt.get();
        donor.setActiveDonor(isActive);
        donorRepository.save(donor);
        return true;
    }
    
    public OrganDonation createDonation(OrganDonation donation, Long donorId) {
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Error: Donor not found."));
        
        donation.setDonor(donor);
        donation.setStatus(DonationStatus.AVAILABLE);
        donation.setActive(true);
        
        return organDonationRepository.save(donation);
    }
    
    public List<OrganDonation> getDonationsByDonor(Donor donor) {
        return organDonationRepository.findByDonor(donor);
    }
    
    public List<OrganDonation> getActiveDonations() {
        return organDonationRepository.findByIsActive(true);
    }
    
    public List<OrganDonation> getDonationsByStatus(DonationStatus status) {
        return organDonationRepository.findByStatus(status);
    }
    
    public Optional<OrganDonation> getDonationById(Long donationId) {
        return organDonationRepository.findById(donationId);
    }
    
    public boolean cancelDonation(Long donationId) {
        Optional<OrganDonation> donationOpt = organDonationRepository.findById(donationId);
        if (!donationOpt.isPresent()) {
            return false;
        }
        
        OrganDonation donation = donationOpt.get();
        
        // Only cancel if not already matched or transplanted
        if (donation.getStatus() == DonationStatus.MATCHED || donation.getStatus() == DonationStatus.TRANSPLANTED) {
            return false;
        }
        
        donation.setStatus(DonationStatus.EXPIRED);
        donation.setActive(false);
        organDonationRepository.save(donation);
        return true;
    }
    
    public boolean approveDonation(Long donationId) {
        Optional<OrganDonation> donationOpt = organDonationRepository.findById(donationId);
        if (!donationOpt.isPresent()) {
            return false;
        }
        
        OrganDonation donation = donationOpt.get();
        donation.setStatus(DonationStatus.AVAILABLE);
        organDonationRepository.save(donation);
        return true;
    }
    
    public boolean rejectDonation(Long donationId) {
        Optional<OrganDonation> donationOpt = organDonationRepository.findById(donationId);
        if (!donationOpt.isPresent()) {
            return false;
        }
        
        OrganDonation donation = donationOpt.get();
        donation.setStatus(DonationStatus.EXPIRED);
        donation.setActive(false);
        organDonationRepository.save(donation);
        return true;
    }
}