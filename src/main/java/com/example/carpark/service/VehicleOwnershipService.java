package com.example.carpark.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
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
	
	public VehicleOwnership createVehicleOwnership(VehicleOwnership body) {
		Owner owner = ownerService.getOwnerById(body.getOwner().getId());
		Vehicle vehicle = vehicleService.getVehicleById(body.getVehicle().getId());
		List<VehicleOwnership> vehicleOwnershipList = repo.findAllByOwner(owner);
		vehicleOwnershipList.forEach(vehicleOwnership -> {
			String existingVehiclePlaque = vehicleOwnership.getVehicle().getPlaque();
			String newVehiclePlaque = vehicle.getPlaque();
			boolean samePlaque = Objects.equals(existingVehiclePlaque, newVehiclePlaque);
			if(samePlaque)
				throw new ResourceAlreadyExistsException("The client already exists");
		});
		body.setOwner(owner);
		body.setVehicle(vehicle);
		return repo.saveAndFlush(body);
	}
	
	public List<VehicleOwnership> getAllVehicleOwnerships(){
		return repo.findAll();
	}
	
	public VehicleOwnership getVehicleOwnershipById(Long id) {
		return repo.findById(id).orElseThrow(()->
			new ResourceNotFoundException("No ressource found with id: "+id));
	}
	
	public VehicleOwnership updateVehicleOwnership(Long id, VehicleOwnership body) {
		VehicleOwnership vehicleOwnership = getVehicleOwnershipById(id);
		Owner owner = ownerService.getOwnerById(body.getOwner() != null ? 
			body.getOwner().getId() : vehicleOwnership.getOwner().getId());
		Vehicle vehicle = vehicleService.getVehicleById(body.getVehicle() != null ? 
			body.getVehicle().getId() : vehicleOwnership.getVehicle().getId());
		body.setId(vehicleOwnership.getId());
		body.setOwner(owner);
		body.setVehicle(vehicle);
		return repo.saveAndFlush(body);
	}
	
	public boolean deleteVehicleOwnership(Long id) {
		boolean existingOwner = repo.existsById(id);
		if(!existingOwner) 
			throw new ResourceNotFoundException("No ressource found witha id: "+id);
		repo.deleteById(id);
		return true;
	}
}
