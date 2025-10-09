package com.example.carpark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.carpark.infrastructure.entity.Owner;
import com.example.carpark.infrastructure.repository.OwnerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerService {

	private final OwnerRepository repo;
	
	public Owner createOwner(Owner body) {
		return repo.saveAndFlush(body);
	}
	
	public List<Owner> getAllOwners(){
		return repo.findAll();
	}
	
	public Owner getOwnerById(Long id) {
		return repo.findById(id).orElseThrow(()->
			new NullPointerException("No ressource found witha id : "+id));
	}
	
	public Owner updateOwner(Long id, Owner body) {
		Owner owner = getOwnerById(id);
		body.setId(owner.getId());
		body.setFullName(body.getFullName() != null ? 
			body.getFullName() : owner.getFullName());
		body.setDriversLicense(body.getDriversLicense() != null ? 
			body.getDriversLicense() : owner.getDriversLicense());
		return repo.saveAndFlush(body);
	}
	
	public boolean deleteOwner(Long id) {
		if(!repo.existsById(id)) throw
			new NullPointerException("No ressource found witha id : "+id);
		repo.deleteById(id);
		return !repo.existsById(id);
	}
}
