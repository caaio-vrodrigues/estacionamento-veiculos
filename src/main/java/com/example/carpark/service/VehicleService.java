package com.example.carpark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.MissingRequiredFieldException;
import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.example.carpark.infrastructure.entity.Vehicle;
import com.example.carpark.infrastructure.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {

	private final VehicleRepository repo;
	
	public Vehicle createVehicle(Vehicle body) {
		boolean missingField = body.getBrand() == null || body.getModel() == null || 
			body.getPlaque() == null || body.getType() == null;
		if(missingField) throw new MissingRequiredFieldException("Incomplete fields in the request");
		boolean existingVehiclePlaque = repo.existsByPlaque(body.getPlaque());
		if(existingVehiclePlaque) throw new ResourceAlreadyExistsException("The vehicle with plaque "+body.getPlaque()+" already exists");
		body.setCountry(body.getBrand().getCountry());
		return repo.saveAndFlush(body);
	}
	
	public List<Vehicle> getAllVehicles(){
		return repo.findAll();
	}
	
	public Vehicle getVehicleById(Long id) {
		return repo.findById(id).orElseThrow(()-> new ResourceNotFoundException("No resource found with id: "+id));
	}
	
	public Vehicle updateVehicle(Long id, Vehicle body) {
		Vehicle existingVehicle = getVehicleById(id);
		boolean containsPlaque = body.getPlaque() != null;
		boolean vehicleAlreadyExistsByPlaque = containsPlaque && 
			repo.existsByPlaque(body.getPlaque()) && 
			!body.getPlaque().equals(existingVehicle.getPlaque());
		if(vehicleAlreadyExistsByPlaque) throw new ResourceAlreadyExistsException("The vehicle with plaque "+body.getPlaque()+" already exists");
		boolean containsModel = body.getModel() != null;
		boolean containsBrand = body.getBrand() != null;
		boolean containsType = body.getType() != null;
		body.setId(existingVehicle.getId());
		body.setPlaque(containsPlaque ? body.getPlaque() : existingVehicle.getPlaque());
		body.setModel(containsModel ? body.getModel() : existingVehicle.getModel());
		body.setType(containsType ? body.getType() : existingVehicle.getType());
		body.setBrand(containsBrand ? body.getBrand() : existingVehicle.getBrand());
		body.setCountry(containsBrand ? body.getBrand().getCountry() : existingVehicle.getCountry());
		return repo.saveAndFlush(body);
	}
	
	public boolean deleteVehicle(Long id) {
		boolean existingVehicle = repo.existsById(id);
		if(!existingVehicle) throw new ResourceNotFoundException("No ressource found with id: "+id);
		repo.deleteById(id);
		return true;
	}
}
