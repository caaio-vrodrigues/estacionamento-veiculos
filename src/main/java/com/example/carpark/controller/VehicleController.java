package com.example.carpark.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.carpark.infrastructure.entity.Vehicle;
import com.example.carpark.service.VehicleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicle")
public class VehicleController {
	
	private final VehicleService service;
	
	@PostMapping
	public ResponseEntity<Vehicle> newVehicle(
		@RequestBody Vehicle vehicle
	){
		return ResponseEntity.ok(service.createVehicle(vehicle));
	}
	
	@GetMapping
	public ResponseEntity<List<Vehicle>> listVehicles(){
		return ResponseEntity.ok(service.getAllVehicles());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Vehicle> searchVehicleById(
		@PathVariable Long id
	){
		return ResponseEntity.ok(service.getVehicleById(id));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Vehicle> editVehicle(
		@PathVariable Long id,
		@RequestBody Vehicle body
	){
		return ResponseEntity.ok(service.updateVehicle(id, body));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> excludeVehicle(
		@PathVariable Long id
	){
		return ResponseEntity.ok(service.deleteVehicle(id));
	}
}
