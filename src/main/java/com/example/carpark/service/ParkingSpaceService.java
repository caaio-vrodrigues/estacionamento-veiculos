package com.example.carpark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.ForbiddenFieldModificationException;
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
	
	public ParkingSpace updateParkingSpace(String currentPlaceId, ParkingSpaceUpdateDTO updateDTO) {
		if(updateDTO.getOccupied() != null) throw new ForbiddenFieldModificationException("The 'occupied' status cannot be manually changed");
        ParkingSpace existingParkingSpace = getParkingSpaceByPlaceId(currentPlaceId);
        boolean occupiedParkingSpace = existingParkingSpace.getOccupied().booleanValue();
        if(occupiedParkingSpace) throw new OccupiedParkingSpaceException("Cannot change an occupied parking space");
        boolean containsPlaceId = updateDTO.getPlaceId() != null;
        if(containsPlaceId) {
            boolean isSamePlaceId = existingParkingSpace.getPlaceId().equals(updateDTO.getPlaceId());
            if(!isSamePlaceId) {
                boolean placeIdAlreadyExists = repo.existsByPlaceId(updateDTO.getPlaceId());
                if (placeIdAlreadyExists) throw new ResourceAlreadyExistsException("This 'placeId' is already in use: "+updateDTO.getPlaceId());
                existingParkingSpace.setPlaceId(updateDTO.getPlaceId());
            }
        }
        boolean containsType = updateDTO.getType() != null;
        if(containsType) {
        	boolean isSameType = existingParkingSpace.getType().equals(updateDTO.getType());
            if(!isSameType) {
                existingParkingSpace.setType(updateDTO.getType());
                existingParkingSpace.setPrice(updateDTO.getType().getPrice());
            }
        }
        return repo.saveAndFlush(existingParkingSpace);
    }
	
	public boolean deleteParkingSpace(String placeId) {
		boolean existingParkingSpace = repo.existsByPlaceId(placeId);
		if(!existingParkingSpace) throw new ResourceNotFoundException("No resource found with placeId: "+placeId);
		return repo.deleteByPlaceId(placeId);
	}
}