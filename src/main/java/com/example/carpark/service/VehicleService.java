package com.example.carpark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.carpark.infrastructure.entity.Vehicle;
import com.example.carpark.infrastructure.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {

	private final VehicleRepository repo;
	
	public Vehicle createVehicle(Vehicle body) {
		body.setBrand(body.getBrand());
		body.setCountry(body.getBrand().getCountry());
		return repo.saveAndFlush(body);
	}
	
	public List<Vehicle> getAllVehicles(){
		return repo.findAll();
	}
	
	public Vehicle getVehicleById(Long id) {
		return repo.findById(id).orElseThrow(()->
			new NullPointerException("No ressource found witha id : "+id));
	}
	
	public Vehicle updateVehicle(Long id, Vehicle body) {
		Vehicle vehicle = getVehicleById(id);
		body.setId(vehicle.getId());
		body.setPlaque(body.getPlaque() != null ? 
			body.getPlaque() : vehicle.getPlaque());
		body.setModel(body.getModel() != null ? 
			body.getModel() : vehicle.getModel());
		body.setBrand(body.getBrand() != null ? 
			body.getBrand() : vehicle.getBrand());
		body.setCountry(body.getBrand() != null ? 
			body.getBrand().getCountry() : vehicle.getCountry());
		return repo.saveAndFlush(body);
	}
	
	public boolean deleteVehicle(Long id) {
		if(!repo.existsById(id)) throw
			new NullPointerException("No ressource found witha id : "+id);
		repo.deleteById(id);
		return !repo.existsById(id);
	}
}
