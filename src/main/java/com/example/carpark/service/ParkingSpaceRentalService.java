package com.example.carpark.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

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
		if(parkingSpace.getOccupied()) 
			throw new RuntimeException("Vaga ocupada");
		VehicleOwnership vehicleOwner = vehicleOwnershipService
			.getVehicleOwnershipById(body.getVehicleOwnership().getId());
		if(parkingSpace.getType() != vehicleOwner.getVehicle().getType())
			throw new RuntimeException("Vaga incompatível");
		parkingSpace.setOccupied(true);
		body.setStartRenting(LocalDateTime.now());
		body.setParkingSpace(parkingSpace);
		body.setVehicleOwnership(vehicleOwner);
		return repo.saveAndFlush(body);
	}
	
	public List<ParkingSpaceRental> getAllParkingSpaceRentals(){
		return repo.findAll();
	}
	
	public ParkingSpaceRental getParkingSpaceRentalById(Long id) {
		return repo.findById(id).orElseThrow(()->
			new NullPointerException("No ressource found witha id : "+id));
	}
	
	public ParkingSpaceRental updateParkingSpaceRental(
		Long id, 
		ParkingSpaceRental body
	){
		ParkingSpaceRental parkingSpaceRental = getParkingSpaceRentalById(id);
		if(parkingSpaceRental.getParkingSpace()
			.getId() != body.getParkingSpace().getId()
		){
			if(body.getParkingSpace().getOccupied()) 
				throw new RuntimeException("Vaga ocupada");
			body.getParkingSpace().setOccupied(true);
			parkingSpaceRental.getParkingSpace().setOccupied(false);
		}
		ParkingSpace parkingSpace = parkingSpaceService
			.getParkingSpaceById(body.getParkingSpace() != null ?
				body.getParkingSpace().getId() :
				parkingSpaceRental.getParkingSpace().getId());
		VehicleOwnership vehicleOwner = vehicleOwnershipService
			.getVehicleOwnershipById(body.getVehicleOwnership() != null ?
				body.getVehicleOwnership().getId() :
				parkingSpaceRental.getVehicleOwnership().getId());
		body.setId(parkingSpaceRental.getId());
		body.setStartRenting(body.getStartRenting() != null ?
			body.getStartRenting() : parkingSpaceRental.getStartRenting());
		body.setEndRenting(body.getEndRenting() != null ?
			body.getEndRenting() : parkingSpaceRental.getEndRenting());
		body.setTotalRent(body.getTotalRent() != null ?
			body.getTotalRent() : parkingSpaceRental.getTotalRent());
		body.setVehicleOwnership(vehicleOwner);
		body.setParkingSpace(parkingSpace);
		return repo.saveAndFlush(body);
	}
	
	public boolean deleteParkingSpaceRental(Long id) {
		if(!repo.existsById(id)) throw
			new NullPointerException("No ressource found witha id : "+id);
		repo.deleteById(id);
		return true;
	}
	
	public ParkingSpaceRental endParkingSpaceRental(Long id) {
		ParkingSpaceRental parkingSpaceRental = getParkingSpaceRentalById(id);
		parkingSpaceRental.setEndRenting(LocalDateTime.now());
		parkingSpaceRental.getParkingSpace().setOccupied(false);
		Duration duration = Duration.between(
			parkingSpaceRental.getStartRenting(), 
			parkingSpaceRental.getEndRenting());
		BigDecimal durationInHours = new BigDecimal(duration.toHours());
		BigDecimal hourPrice = parkingSpaceRental.getParkingSpace().getPrice();
		parkingSpaceRental.setTotalRent(durationInHours.multiply(hourPrice).add(hourPrice));
		return repo.saveAndFlush(parkingSpaceRental);
	}
}
