package com.example.carpark.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.carpark.dto.vehicle.VehicleRequestDTO;
import com.example.carpark.dto.vehicle.VehicleUpdateDTO;
import com.example.carpark.infrastructure.entity.Vehicle;
import com.example.carpark.service.VehicleService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicle")
@Validated
public class VehicleController {
	
	private final VehicleService service;
	
	@PostMapping
	public ResponseEntity<Vehicle> newVehicle(
		@Valid @RequestBody VehicleRequestDTO vehicle
	){
		return ResponseEntity.ok(service.createVehicle(vehicle));
	}
	
	@GetMapping
	public ResponseEntity<List<Vehicle>> listVehicles(){
		return ResponseEntity.ok(service.getAllVehicles());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Vehicle> searchVehicleById(
		@PathVariable @Min(1) Long id
	){
		return ResponseEntity.ok(service.getVehicleById(id));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Vehicle> editVehicle(
		@PathVariable @Min(1) Long id,
		@Valid @RequestBody VehicleUpdateDTO body
	){
		return ResponseEntity.ok(service.updateVehicle(id, body));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> excludeVehicle(
		@PathVariable @Min(1) Long id
	){
		return ResponseEntity.ok(service.deleteVehicle(id));
	}
}
