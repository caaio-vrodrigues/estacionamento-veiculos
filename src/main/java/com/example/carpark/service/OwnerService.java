package com.example.carpark.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
		Owner owner = getOwnerById(id);
		boolean containsFullName = body.getFullName() != null;
		boolean containsDriversLicense = body.getDriversLicense() != null;
		body.setId(owner.getId());
		body.setFullName(containsFullName ? body.getFullName() : owner.getFullName());
		body.setDriversLicense(containsDriversLicense ? 
			body.getDriversLicense() : owner.getDriversLicense());
		return repo.saveAndFlush(body);
	}
	
	public boolean deleteOwner(Long id) {
		boolean existingOwner = repo.existsById(id);
		if(!existingOwner) throw new ResourceNotFoundException("No resource found with id: "+id);
		repo.deleteById(id);
		return true;
	}
}
