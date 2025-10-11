package com.example.carpark.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.IncompatibleParkingSpaceException;
import com.example.carpark.customexception.OccupiedParkingSpaceException;
import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.example.carpark.infrastructure.entity.ParkingSpace;
import com.example.carpark.infrastructure.entity.ParkingSpaceRental;
import com.example.carpark.infrastructure.entity.VehicleOwnership;
import com.example.carpark.infrastructure.repository.ParkingSpaceRentalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingSpaceRentalService {
	
	private final ParkingSpaceRentalRepository repo;
	private final ParkingSpaceService parkingSpaceService;
	private final VehicleOwnershipService vehicleOwnershipService;
	
	public ParkingSpaceRental createParkingSpaceRental(ParkingSpaceRental body) {
		ParkingSpace parkingSpace = parkingSpaceService
			.getParkingSpaceById(body.getParkingSpace().getId());
		boolean isParkingSpaceOccupied = parkingSpace.getOccupied().booleanValue();
		if(isParkingSpaceOccupied) throw new OccupiedParkingSpaceException("Occupied parking space");
		VehicleOwnership vehicleOwnership = vehicleOwnershipService
			.getVehicleOwnershipById(body.getVehicleOwnership().getId());
		boolean isParkingSpaceCompatible = parkingSpace.getType() == 
			vehicleOwnership.getVehicle().getType();
		if(!isParkingSpaceCompatible)
			throw new IncompatibleParkingSpaceException("Incompatible parking space");
		List<ParkingSpaceRental> parkingSpaceRentalList = repo
			.findAllByVehicleOwnership(vehicleOwnership);
		String incomingVehiclePlaque = vehicleOwnership.getVehicle().getPlaque();
		parkingSpaceRentalList.forEach(parkingSpaceRental -> {
			boolean isOccupied = parkingSpaceRental.getParkingSpace().getOccupied().booleanValue();
			if(isOccupied) {
				String existingVehiclePlaque = parkingSpaceRental
					.getVehicleOwnership().getVehicle().getPlaque();
				boolean samePlaque = Objects.equals(existingVehiclePlaque, incomingVehiclePlaque);
				if(samePlaque) 
					throw new ResourceAlreadyExistsException("The vehicle with plaque "+incomingVehiclePlaque+" is already in the parking space");
			}
		});
		parkingSpace.setOccupied(true);
		body.setStartRenting(LocalDateTime.now());
		body.setParkingSpace(parkingSpace);
		body.setVehicleOwnership(vehicleOwnership);
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
		ParkingSpaceRental parkingSpaceRental = getParkingSpaceRentalById(id);
		Integer definedParkingSpace = parkingSpaceRental.getParkingSpace().getId();
		Integer newParkingSpace = body.getParkingSpace().getId();
		boolean isSameParkingSpace = Objects.equals(definedParkingSpace, newParkingSpace);
		boolean containsVehicleOwnership = body.getVehicleOwnership() != null;
		boolean containsStartRenting = body.getStartRenting() != null;
		boolean containsEndRenting = body.getEndRenting() != null;
		boolean containsTotalRent = body.getTotalRent() != null;
		VehicleOwnership vehicleOwner = vehicleOwnershipService
			.getVehicleOwnershipById(containsVehicleOwnership ? 
				body.getVehicleOwnership().getId() : parkingSpaceRental.getVehicleOwnership().getId());
		body.setId(parkingSpaceRental.getId());
		body.setStartRenting(containsStartRenting ? body.getStartRenting() :
			parkingSpaceRental.getStartRenting());
		body.setEndRenting(containsEndRenting ? body.getEndRenting() :
			parkingSpaceRental.getEndRenting());
		body.setTotalRent(containsTotalRent ? body.getTotalRent() : 
			parkingSpaceRental.getTotalRent());
		body.setVehicleOwnership(vehicleOwner);
		if(isSameParkingSpace) {
			ParkingSpace parkingSpace = parkingSpaceService
				.getParkingSpaceById(parkingSpaceRental.getParkingSpace().getId());
			body.setParkingSpace(parkingSpace);
		}
		if(!isSameParkingSpace){
			ParkingSpace parkingSpace = parkingSpaceService
				.getParkingSpaceById(body.getParkingSpace().getId());
			boolean isOccupied = parkingSpace.getOccupied();
			boolean isSameType = parkingSpace.getType() == vehicleOwner.getVehicle().getType();
			if(isOccupied) throw new OccupiedParkingSpaceException("Occupied parking space");
			if(!isSameType) throw new IncompatibleParkingSpaceException("Incompatible parking space");
			parkingSpaceRental.getParkingSpace().setOccupied(false);
			parkingSpace.setOccupied(true);
			body.setParkingSpace(parkingSpace);
		}
		return repo.saveAndFlush(body);
	}
	
	public boolean deleteParkingSpaceRental(Long id) {
		boolean existingParkingSpaceRental = repo.existsById(id);
		if(!existingParkingSpaceRental) throw new ResourceNotFoundException("No resource found with id: "+id);
		repo.deleteById(id);
		return true;
	}
	
	public ParkingSpaceRental endParkingSpaceRental(Long id) {
		ParkingSpaceRental parkingSpaceRental = getParkingSpaceRentalById(id);
		parkingSpaceRental.setEndRenting(LocalDateTime.now());
		parkingSpaceRental.getParkingSpace().setOccupied(false);
		LocalDateTime startRenting = parkingSpaceRental.getStartRenting();
		LocalDateTime endRenting = parkingSpaceRental.getEndRenting();
		BigDecimal durationInHours = new BigDecimal(Duration
			.between(startRenting, endRenting).toHours());
		BigDecimal hourPrice = parkingSpaceRental.getParkingSpace().getPrice();
		parkingSpaceRental.setTotalRent(durationInHours.multiply(hourPrice).add(hourPrice));
		return repo.saveAndFlush(parkingSpaceRental);
	}
}
