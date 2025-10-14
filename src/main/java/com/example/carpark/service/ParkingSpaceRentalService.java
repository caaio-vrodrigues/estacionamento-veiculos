package com.example.carpark.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.IncompatibleParkingSpaceException;
import com.example.carpark.customexception.MissingRequiredFieldException;
import com.example.carpark.customexception.OccupiedParkingSpaceException;
import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.example.carpark.infrastructure.entity.ParkingSpace;
import com.example.carpark.infrastructure.entity.ParkingSpaceRental;
import com.example.carpark.infrastructure.entity.VehicleOwnership;
import com.example.carpark.infrastructure.repository.ParkingSpaceRentalRepository;
import com.example.carpark.infrastructure.repository.VehicleOwnershipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingSpaceRentalService {
	
	private final ParkingSpaceRentalRepository repo;
	private final ParkingSpaceService parkingSpaceService;
	private final VehicleOwnershipService vehicleOwnershipService;
	
	public ParkingSpaceRental createParkingSpaceRental(ParkingSpaceRental body) {
		boolean missingField = body.getVehicleOwnership() == null || body.getParkingSpace() == null;
		if(missingField) throw new MissingRequiredFieldException("Incomplete fields in the request");
		ParkingSpace newParkingSpace = parkingSpaceService
			.getParkingSpaceById(body.getParkingSpace().getId());
		boolean isNewParkingSpaceOccupied = newParkingSpace.getOccupied().booleanValue();
		if(isNewParkingSpaceOccupied) throw new OccupiedParkingSpaceException("Occupied parking space");
		VehicleOwnership newVehicleOwnership = vehicleOwnershipService
			.getVehicleOwnershipById(body.getVehicleOwnership().getId());
		boolean isNewParkingSpaceAndNewVehicleCompatible = newParkingSpace.getType() ==
			newVehicleOwnership.getVehicle().getType();
		if(!isNewParkingSpaceAndNewVehicleCompatible) throw new IncompatibleParkingSpaceException("Incompatible parking space");
		List<ParkingSpaceRental> existingParkingSpaceRentalListByVehicleOwnership = repo
			.findAllByVehicleOwnership(newVehicleOwnership);
		String incomingVehiclePlaque = newVehicleOwnership.getVehicle().getPlaque();
		existingParkingSpaceRentalListByVehicleOwnership.forEach(parkingSpaceRental -> {
			boolean isOpenRent = parkingSpaceRental.getEndRenting() == null;
			if(isOpenRent) throw new ResourceAlreadyExistsException("The vehicle with plaque "+incomingVehiclePlaque+" is already in the parking space");
		});
		newParkingSpace.setOccupied(true);
		parkingSpaceService.updateParkingSpace(newParkingSpace.getId(), newParkingSpace);
		body.setStartRenting(LocalDateTime.now());
		body.setParkingSpace(newParkingSpace);
		body.setVehicleOwnership(newVehicleOwnership);
		return repo.saveAndFlush(body);
	}
	
	public List<ParkingSpaceRental> getAllParkingSpaceRentals(){
		return repo.findAll();
	}
	
	public ParkingSpaceRental getParkingSpaceRentalById(Long id) {
		return repo.findById(id).orElseThrow(()->
			new ResourceNotFoundException("No resource found with id: "+id));
	}
	
	public ParkingSpaceRental updateParkingSpaceRental(Long id, ParkingSpaceRental body){
		boolean containsParkingSpace = body.getParkingSpace() != null;
		boolean containsVehicleOwnership = body.getVehicleOwnership() != null;
		boolean containsStartRenting = body.getStartRenting() != null;
		boolean containsEndRenting = body.getEndRenting() != null;
		boolean containsTotalRent = body.getTotalRent() != null;
		ParkingSpaceRental existingParkingSpaceRental = getParkingSpaceRentalById(id);
		ParkingSpace parkingSpace = parkingSpaceService.getParkingSpaceById(containsParkingSpace ?
			body.getParkingSpace().getId() : existingParkingSpaceRental.getParkingSpace().getId());
		boolean occupiedParkingSpace = !existingParkingSpaceRental.getParkingSpace().getId().equals(
			parkingSpace.getId()) && parkingSpace.getOccupied().booleanValue();
		if(occupiedParkingSpace) throw new OccupiedParkingSpaceException("The parking space is already occupied");
		VehicleOwnership vehicleOwnership = vehicleOwnershipService
			.getVehicleOwnershipById(containsVehicleOwnership ? body.getVehicleOwnership().getId() :
				existingParkingSpaceRental.getVehicleOwnership().getId());
		boolean isSameExistingParkingSpaceAndNewParkingSpaceType = existingParkingSpaceRental
			.getParkingSpace().getType() == parkingSpace.getType();
		boolean isSameExistingParkingSpaceAndNewVehicleType = existingParkingSpaceRental
			.getParkingSpace().getType() == vehicleOwnership.getVehicle().getType();
		boolean incompatibleTypeOfParkinSpace = !isSameExistingParkingSpaceAndNewParkingSpaceType ||
			!isSameExistingParkingSpaceAndNewVehicleType;
		if(incompatibleTypeOfParkinSpace) throw new IncompatibleParkingSpaceException("Incompatible parking space type");
		body.setId(existingParkingSpaceRental.getId());
		body.setStartRenting(containsStartRenting ? 
			body.getStartRenting() : existingParkingSpaceRental.getStartRenting());
		body.setEndRenting(containsEndRenting ? 
			body.getEndRenting() : existingParkingSpaceRental.getEndRenting());
		body.setTotalRent(containsTotalRent ?
			body.getTotalRent() : existingParkingSpaceRental.getTotalRent());
		body.setParkingSpace(parkingSpace);
		body.setVehicleOwnership(vehicleOwnership);
		return repo.saveAndFlush(body);
	}
	
	public boolean deleteParkingSpaceRental(Long id) {
		boolean existingParkingSpaceRental = repo.existsById(id);
		if(!existingParkingSpaceRental) throw new ResourceNotFoundException("No resource found with id: "+id);
		ParkingSpaceRental parkingSpaceRental = getParkingSpaceRentalById(id);
		parkingSpaceRental.getParkingSpace().setOccupied(false);
		parkingSpaceService.updateParkingSpace(
			parkingSpaceRental.getParkingSpace().getId(), 
			parkingSpaceRental.getParkingSpace());
		repo.deleteById(id);
		return true;
	}
	
	public ParkingSpaceRental endParkingSpaceRental(Long id) {
		ParkingSpaceRental parkingSpaceRental = getParkingSpaceRentalById(id);
		parkingSpaceRental.setEndRenting(LocalDateTime.now());
		parkingSpaceRental.getParkingSpace().setOccupied(false);
		parkingSpaceService.updateParkingSpace(
			parkingSpaceRental.getParkingSpace().getId(), 
			parkingSpaceRental.getParkingSpace());
		LocalDateTime startRenting = parkingSpaceRental.getStartRenting();
		LocalDateTime endRenting = parkingSpaceRental.getEndRenting();
		BigDecimal durationInHours = new BigDecimal(Duration
			.between(startRenting, endRenting).toHours());
		BigDecimal hourPrice = parkingSpaceRental.getParkingSpace().getPrice();
		parkingSpaceRental.setTotalRent(durationInHours.multiply(hourPrice).add(hourPrice));
		return repo.saveAndFlush(parkingSpaceRental);
	}
}
