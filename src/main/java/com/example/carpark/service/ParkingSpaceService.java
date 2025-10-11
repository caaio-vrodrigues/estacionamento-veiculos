package com.example.carpark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.ResourceNotFoundException;
import com.example.carpark.infrastructure.entity.ParkingSpace;
import com.example.carpark.infrastructure.repository.ParkingSpaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingSpaceService {

	private final ParkingSpaceRepository repo;
	
	public ParkingSpace createParkingSpace(ParkingSpace body) {
		body.setPrice(body.getType().getPrice());
		body.setOccupied(false);
		return repo.saveAndFlush(body);
	}
	
	public List<ParkingSpace> getAllParkingSpaces(){
		return repo.findAll();
	}
	
	public ParkingSpace getParkingSpaceById(Integer id) {
		return repo.findById(id).orElseThrow(()->
			new ResourceNotFoundException("No resource found with id: "+id));
	}
	
	public ParkingSpace updateParkingSpace(Integer id, ParkingSpace body) {
		ParkingSpace parkingSpace = getParkingSpaceById(id);
		boolean containsType = body.getType() != null;
		boolean containsOccupied = body.getOccupied() != null;
		body.setId(parkingSpace.getId());
		body.setType(containsType ? body.getType() : parkingSpace.getType());
		body.setPrice(containsType ? body.getType().getPrice() : parkingSpace.getType().getPrice());
		body.setOccupied(containsOccupied ? body.getOccupied() : parkingSpace.getOccupied());
		return repo.saveAndFlush(body);
	}
	
	public boolean deleteParkingSpace(Integer id) {
		boolean existingParkingSpace = repo.existsById(id);
		if(!existingParkingSpace) throw new ResourceNotFoundException("No resource found with id: "+id);
		repo.deleteById(id);
		return true;
	}
}