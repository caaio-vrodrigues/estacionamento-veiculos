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
import com.example.carpark.infrastructure.repository.VehicleOwnershipRepository;

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
		boolean containsVehicleOwnership = body.getVehicleOwnership() != null;
		boolean containsStartRenting = body.getStartRenting() != null;
		boolean containsEndRenting = body.getEndRenting() != null;
		boolean containsTotalRent = body.getTotalRent() != null;
		ParkingSpaceRental existingparkingSpaceRental = getParkingSpaceRentalById(id);
		ParkingSpace usingParkingSpace = parkingSpaceService
				.getParkingSpaceById(existingparkingSpaceRental.getParkingSpace().getId());
		if(containsVehicleOwnership) {
			VehicleOwnership vehicleOwnership = vehicleOwnershipService
				.getVehicleOwnershipById(body.getVehicleOwnership().getId());
			List<ParkingSpaceRental> vehicleOwnershipList = repo
				.findAllByVehicleOwnership(vehicleOwnership);
			vehicleOwnershipList.forEach(vehicleOwner -> {
				if(vehicleOwner.getEndRenting() == null) {
					String vehiclePlaque = vehicleOwner
						.getVehicleOwnership().getVehicle().getPlaque();
					throw new ResourceAlreadyExistsException("The vehicle with plaque "+vehiclePlaque+" is already in the parking space");
				}
			});
			body.setVehicleOwnership(vehicleOwnership);
		}
		if(!containsVehicleOwnership) {
			VehicleOwnership vehicleOwnership = vehicleOwnershipService
				.getVehicleOwnershipById(existingparkingSpaceRental.getVehicleOwnership().getId());
			body.setVehicleOwnership(vehicleOwnership);
		}
		if(body.getParkingSpace() != null) {
			Integer newParkingSpaceId = body.getParkingSpace().getId();
			boolean isSameParkingSpace = Objects.equals(usingParkingSpace.getId(), newParkingSpaceId);
			if(isSameParkingSpace) body.setParkingSpace(usingParkingSpace);
			if(!isSameParkingSpace){
				ParkingSpace newParkingSpace = parkingSpaceService
					.getParkingSpaceById(body.getParkingSpace().getId());
				boolean isOccupied = newParkingSpace.getOccupied() == true;
				boolean isSameType = usingParkingSpace.getType() == newParkingSpace.getType();
				if(isOccupied) throw new OccupiedParkingSpaceException("Occupied parking space");
				if(!isSameType) throw new IncompatibleParkingSpaceException("Incompatible parking space");
				existingparkingSpaceRental.getParkingSpace().setOccupied(false);
				usingParkingSpace.setOccupied(true);
				body.setParkingSpace(newParkingSpace);
			}
		}
		if(body.getParkingSpace() == null) body.setParkingSpace(usingParkingSpace);
		body.setId(existingparkingSpaceRental.getId());
		body.setStartRenting(containsStartRenting ? body.getStartRenting() :
			existingparkingSpaceRental.getStartRenting());
		body.setEndRenting(containsEndRenting ? body.getEndRenting() :
			existingparkingSpaceRental.getEndRenting());
		body.setTotalRent(containsTotalRent ? body.getTotalRent() : 
			existingparkingSpaceRental.getTotalRent());
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
