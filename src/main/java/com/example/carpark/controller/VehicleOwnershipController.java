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

import com.example.carpark.infrastructure.entity.VehicleOwnership;
import com.example.carpark.service.VehicleOwnershipService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicle-ownership")
public class VehicleOwnershipController {

	private final VehicleOwnershipService service;
	
	@PostMapping
	public ResponseEntity<VehicleOwnership> newVehicleOwnership(
		@RequestBody VehicleOwnership VehicleOwnership
	){
		return ResponseEntity.ok(service.createVehicleOwnership(VehicleOwnership));
	}
	
	@GetMapping
	public ResponseEntity<List<VehicleOwnership>> listVehicleOwnerships(){
		return ResponseEntity.ok(service.getAllVehicleOwnerships());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<VehicleOwnership> searchVehicleOwnershipById(
		@PathVariable Long id
	){
		return ResponseEntity.ok(service.getVehicleOwnershipById(id));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<VehicleOwnership> editVehicleOwnership(
		@PathVariable Long id,
		@RequestBody VehicleOwnership body
	){
		return ResponseEntity.ok(service.updateVehicleOwnership(id, body));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> excludeVehicleOwnership(
		@PathVariable Long id
	){
		return ResponseEntity.ok(service.deleteVehicleOwnership(id));
	}
}
