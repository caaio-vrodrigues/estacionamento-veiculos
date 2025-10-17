package com.example.carpark.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.MissingRequiredFieldException;
import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.example.carpark.dto.vehicleownership.VehicleOwnershipRequestDTO;
import com.example.carpark.dto.vehicleownership.VehicleOwnershipUpdateDTO;
import com.example.carpark.infrastructure.entity.Owner;
import com.example.carpark.infrastructure.entity.Vehicle;
import com.example.carpark.infrastructure.entity.VehicleOwnership;
import com.example.carpark.infrastructure.repository.VehicleOwnershipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleOwnershipService {

	private final VehicleOwnershipRepository repo;
	private final OwnerService ownerService;
	private final VehicleService vehicleService;
	
	public VehicleOwnership createVehicleOwnership(VehicleOwnershipRequestDTO body) {
		Owner owner = ownerService.getOwnerById(body.getOwner().getId());
		Vehicle vehicle = vehicleService.getVehicleById(body.getVehicle().getId());
		List<VehicleOwnership> vehicleOwnershipList = repo.findAllByOwner(owner);
		vehicleOwnershipList.forEach(vehicleOwnership -> {
			String existingVehiclePlaque = vehicleOwnership.getVehicle().getPlaque();
			String newVehiclePlaque = vehicle.getPlaque();
			boolean samePlaque = Objects.equals(existingVehiclePlaque, newVehiclePlaque);
			if(samePlaque) throw new ResourceAlreadyExistsException("The owner already possesses this vehicle with plaque: "+vehicle.getPlaque());
		});
		VehicleOwnership newVehicleOwnership = VehicleOwnership.builder()
			.vehicle(vehicle)
			.owner(owner)
			.build();
		return repo.saveAndFlush(newVehicleOwnership);
	}
	
	public List<VehicleOwnership> getAllVehicleOwnerships(){
		return repo.findAll();
	}
	
	public List<VehicleOwnership> getAllVehicleOwnershipByVehicle(Vehicle vehicle){
		return repo.findAllByVehicle(vehicle);
	}
	
	public VehicleOwnership getVehicleOwnershipById(Long id) {
		return repo.findById(id).orElseThrow(() -> 
			new ResourceNotFoundException("No ressource found with id: "+id));
	}
	
	public VehicleOwnership updateVehicleOwnership(Long id, VehicleOwnershipUpdateDTO body) {
		VehicleOwnership existingVehicleOwnership = getVehicleOwnershipById(id);
		boolean containsVehicle = body.getVehicle() != null;
		boolean containsOwner = body.getOwner() != null;
		Vehicle potentialVehicle = existingVehicleOwnership.getVehicle();
		if(containsVehicle) potentialVehicle = vehicleService.getVehicleById(body.getVehicle().getId());
		Owner potentialOwner = existingVehicleOwnership.getOwner();
		if(containsOwner) potentialOwner = ownerService.getOwnerById(body.getOwner().getId());
		List<VehicleOwnership> vehicleOwnershipList = repo.findAllByOwner(potentialOwner);
	    for(VehicleOwnership currentOwnership : vehicleOwnershipList) {
            if (!currentOwnership.getId().equals(existingVehicleOwnership.getId()) && 
            	Objects.equals(currentOwnership.getVehicle().getPlaque(), potentialVehicle.getPlaque())) 
            		throw new ResourceAlreadyExistsException("The owner already possesses this vehicle with plaque: "+potentialVehicle.getPlaque());
	    }
	    existingVehicleOwnership.setOwner(potentialOwner);
	    existingVehicleOwnership.setVehicle(potentialVehicle);
		return repo.saveAndFlush(existingVehicleOwnership);
	}
	
	public boolean deleteVehicleOwnership(Long id) {
		boolean existingVehicleOwnership = repo.existsById(id);
		if(!existingVehicleOwnership) throw new ResourceNotFoundException("No ressource found witha id: "+id);
		repo.deleteById(id);
		return true;
	}
}
