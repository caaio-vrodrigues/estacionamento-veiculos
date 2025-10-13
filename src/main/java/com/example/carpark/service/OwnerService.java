package com.example.carpark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.MissingRequiredFieldException;
import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.example.carpark.infrastructure.entity.Owner;
import com.example.carpark.infrastructure.repository.OwnerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerService {

	private final OwnerRepository repo;
	
	public Owner createOwner(Owner body) {
		boolean missingField = body.getDriversLicense() == null || body.getFullName() == null;
		if(missingField) throw new MissingRequiredFieldException("Incomplete fields in the request");
		boolean existingDriversLisence = repo.existsByDriversLicense(body.getDriversLicense());
		if(existingDriversLisence) {
			String driversLicense = body.getDriversLicense();
			throw new ResourceAlreadyExistsException("The owner with drivers license "+driversLicense+" already exists");
		}
		return repo.saveAndFlush(body);
	}
	
	public List<Owner> getAllOwners(){
		return repo.findAll();
	}
	
	public Owner getOwnerById(Long id) {
		return repo.findById(id).orElseThrow(()->
			new ResourceNotFoundException("No resource found with id: "+id));
	}
	
	public Owner updateOwner(Long id, Owner body) {
		Owner existingOwner = getOwnerById(id);
		boolean containsFullName = body.getFullName() != null;
		boolean containsDriversLicense = body.getDriversLicense() != null;
		boolean duplicatedDriversLicense = containsDriversLicense && 
			repo.existsByDriversLicense(body.getDriversLicense()) && 
			!existingOwner.getDriversLicense().equals(body.getDriversLicense());
		if(duplicatedDriversLicense) {
			String driversLicense = body.getDriversLicense();
			throw new ResourceAlreadyExistsException("The owner with drivers license "+driversLicense+" already exists");
		}
		body.setId(existingOwner.getId());
		body.setFullName(containsFullName ? body.getFullName() : existingOwner.getFullName());
		body.setDriversLicense(containsDriversLicense ? 
			body.getDriversLicense() : existingOwner.getDriversLicense());
		return repo.saveAndFlush(body);
	}
	
	public boolean deleteOwner(Long id) {
		boolean existingOwner = repo.existsById(id);
		if(!existingOwner) throw new ResourceNotFoundException("No resource found with id: "+id);
		repo.deleteById(id);
		return true;
	}
}
