package com.example.carpark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.example.carpark.dto.owner.OwnerRequestDTO;
import com.example.carpark.dto.owner.OwnerUpdateDTO;
import com.example.carpark.infrastructure.entity.Owner;
import com.example.carpark.infrastructure.repository.OwnerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerService {

	private final OwnerRepository repo;
	
	public Owner createOwner(OwnerRequestDTO body) {
		boolean existingDriversLisence = repo.existsByDriversLicense(body.getDriversLicense());
		if(existingDriversLisence) {
			String driversLicense = body.getDriversLicense();
			throw new ResourceAlreadyExistsException("The owner with drivers license "+driversLicense+" already exists");
		}
		Owner newOwner = Owner.builder()
			.fullName(body.getFullName())
			.driversLicense(body.getDriversLicense())
			.build();
		return repo.saveAndFlush(newOwner);
	}
	
	public List<Owner> getAllOwners(){
		return repo.findAll();
	}
	
	public Owner getOwnerById(Long id) {
		return repo.findById(id).orElseThrow(()->
			new ResourceNotFoundException("No resource found with id: "+id));
	}
	
	public Owner updateOwner(Long id, OwnerUpdateDTO body) {
		Owner existingOwner = getOwnerById(id);
		if (body.getFullName() != null)  existingOwner.setFullName(body.getFullName());
		if (body.getDriversLicense() != null) {
			boolean driversLicenseAlreadyExists = !existingOwner.getDriversLicense()
			.equals(body.getDriversLicense()) && 
			repo.existsByDriversLicense(body.getDriversLicense());
				if (driversLicenseAlreadyExists) {
				String driversLicense = body.getDriversLicense();
				throw new ResourceAlreadyExistsException("The owner with drivers license "+driversLicense+" already exists");
			}
			existingOwner.setDriversLicense(body.getDriversLicense());
		}
		return repo.saveAndFlush(existingOwner);
	}
	
	public boolean deleteOwner(Long id) {
		boolean existingOwner = repo.existsById(id);
		if(!existingOwner) throw new ResourceNotFoundException("No resource found with id: "+id);
		repo.deleteById(id);
		return true;
	}
}
