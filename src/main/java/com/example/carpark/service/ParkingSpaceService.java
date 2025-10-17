package com.example.carpark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.ForbiddenFieldModificationException;
import com.example.carpark.customexception.IncompatibleParkingSpaceException;
import com.example.carpark.customexception.OccupiedParkingSpaceException;
import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.example.carpark.dto.parkingspace.ParkingSpaceRequestDTO;
import com.example.carpark.dto.parkingspace.ParkingSpaceUpdateDTO;
import com.example.carpark.infrastructure.entity.ParkingSpace;
import com.example.carpark.infrastructure.repository.ParkingSpaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingSpaceService {

	private final ParkingSpaceRepository repo;
	
	public ParkingSpace createParkingSpace(ParkingSpaceRequestDTO body) {
		if(repo.existsByPlaceId(body.getPlaceId())) throw new ResourceAlreadyExistsException("This parking space already exists");
		ParkingSpace newParkingSpace = ParkingSpace.builder()
			.type(body.getType())
			.placeId(body.getPlaceId())
			.price(body.getType().getPrice())
			.occupied(false)
			.build();
		return repo.saveAndFlush(newParkingSpace);
	}
	
	public List<ParkingSpace> getAllParkingSpaces(){
		return repo.findAll();
	}
	
	public ParkingSpace getParkingSpaceById(Integer id) {
		return repo.findById(id).orElseThrow(()-> new ResourceNotFoundException("No resource found with id: "+id));
	}
	
	public ParkingSpace getParkingSpaceByPlaceId(String placeId) {
		return repo.findByPlaceId(placeId)
			.orElseThrow(() -> new ResourceNotFoundException("No parking space found with placeId: " + placeId));
	}
	
	public ParkingSpace updateParkingSpaceByPlaceId(String currentPlaceId, ParkingSpaceUpdateDTO body) {
        ParkingSpace existingParkingSpace = getParkingSpaceByPlaceId(currentPlaceId);
        boolean containsOccupied = body.getOccupied() != null;
        boolean containsType = body.getType() != null;
        boolean containsPlaceId = body.getPlaceId() != null;
        if(containsPlaceId) {
        	boolean isSamePlaceId = existingParkingSpace.getPlaceId().equals(body.getPlaceId());
        	if(!isSamePlaceId) {
        		boolean existisNewParkingSpace = repo.existsByPlaceId(body.getPlaceId());
        		if(existisNewParkingSpace ) {
        			ParkingSpace existingNewParkingSpace = getParkingSpaceByPlaceId(body.getPlaceId());
        			boolean isExistingNewParkingSpaceOccupied = existingNewParkingSpace.getOccupied() == true;
        			if(isExistingNewParkingSpaceOccupied) throw new OccupiedParkingSpaceException("This parking space is already occupied");
        			boolean isSameType = existingNewParkingSpace.getType() == 
        				existingParkingSpace.getType();
        			if(!isSameType) throw new IncompatibleParkingSpaceException("Incompatible types of parking space");
        			existingParkingSpace.setPlaceId(body.getPlaceId());
        		}
            	existingParkingSpace.setPlaceId(body.getPlaceId());
        	}
        }
        if(containsType) {
        	boolean isSameType = existingParkingSpace.getType() == body.getType();
        	if(!isSameType) {
        		boolean isExistingParkingSpaceOccupied = existingParkingSpace.getOccupied() == true;
        		if(isExistingParkingSpaceOccupied) throw new OccupiedParkingSpaceException("Cannot change the type of an occupied parking space");
        		existingParkingSpace.setType(null);
        	}
        }
        if(containsOccupied) existingParkingSpace.setOccupied(body.getOccupied());
        return repo.saveAndFlush(existingParkingSpace);
	}
	
	public boolean deleteParkingSpaceByPlaceId(String placeId) {
		if(!repo.existsByPlaceId(placeId)) throw new ResourceNotFoundException("Cannot find parking space with placeId: "+placeId);
		repo.deleteByPlaceId(placeId);
		return true;
	}
}