package com.example.carpark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.ForbiddenActionException;
import com.example.carpark.customexception.ForbiddenFieldModificationException;
import com.example.carpark.customexception.ParkingSpaceAlreadyExistsException;
import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.example.carpark.dto.parkingspace.ParkingSpaceRequestDTO;
import com.example.carpark.dto.parkingspace.ParkingSpaceUpdateDTO;
import com.example.carpark.infrastructure.entity.ParkingSpace;
import com.example.carpark.infrastructure.repository.ParkingSpaceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingSpaceService {

	private final ParkingSpaceRepository repo;
	
	public ParkingSpace createParkingSpace(ParkingSpaceRequestDTO body) {
		if(repo.existsByPlaceId(body.getPlaceId())) throw new ResourceAlreadyExistsException("The parking space with placeId: "+body.getPlaceId()+" already exists");
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
	
	public ParkingSpace updateParkingSpaceByPlaceId(
		String placeId, 
		ParkingSpaceUpdateDTO body
	) {
		ParkingSpace existingParkingSpace = getParkingSpaceByPlaceId(placeId);
		boolean containsOccupied = body.getOccupied() != null;
		boolean containsPlaceId = body.getPlaceId() != null;
		boolean containsType = body.getType() != null;
		boolean isOccupiedAndNoChanges = 
			existingParkingSpace.getOccupied() == true && !containsOccupied;
		boolean isOccupiedAndModifyToSameState = existingParkingSpace.getOccupied() == true &&
			containsOccupied && body.getOccupied() == true;
		boolean isOccupied = isOccupiedAndNoChanges || isOccupiedAndModifyToSameState;
		if(containsOccupied) {
			boolean occupied = body.getOccupied();
			if(occupied == true) {
				boolean containsTypeOrPlaceIdAndIsDifferent = containsType && body.getType() != 
					existingParkingSpace.getType() || containsPlaceId && !body.getPlaceId().equals(
						existingParkingSpace.getPlaceId());
				if(containsTypeOrPlaceIdAndIsDifferent) throw new ForbiddenFieldModificationException("Cannot set property occupied to true and modify the same parking space at the same time");
				existingParkingSpace.setOccupied(body.getOccupied());
			}
			if(occupied == false) existingParkingSpace.setOccupied(body.getOccupied());
		}
		if(containsPlaceId) {
			boolean isSamePlaceId = existingParkingSpace.getPlaceId().equals(body.getPlaceId());
			if(!isSamePlaceId) {
				if(isOccupied) throw new ForbiddenActionException("Cannot modify an occupied parking space");
				boolean placeIdIsAlreadyInUse = repo.existsByPlaceId(body.getPlaceId());
				if(placeIdIsAlreadyInUse) throw new ParkingSpaceAlreadyExistsException("The placeId: "+body.getPlaceId()+" already exists");
				existingParkingSpace.setPlaceId(body.getPlaceId());
			}
		}
		if(containsType) {
			boolean isSameType = existingParkingSpace.getType() == body.getType();
			if(!isSameType) {
				if(isOccupied) throw new ForbiddenActionException("Cannot modify an occupied parking space");
				existingParkingSpace.setType(body.getType());
			}
		}
		return repo.saveAndFlush(existingParkingSpace);
	}
	
	@Transactional
	public boolean deleteParkingSpaceByPlaceId(String placeId) {
		if(!repo.existsByPlaceId(placeId)) throw new ResourceNotFoundException("Cannot find parking space with placeId: "+placeId);
		repo.deleteByPlaceId(placeId);
		return true;
	}
}