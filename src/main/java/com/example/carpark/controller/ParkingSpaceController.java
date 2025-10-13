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

import com.example.carpark.infrastructure.entity.ParkingSpace;
import com.example.carpark.service.ParkingSpaceService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parking-space")
@Validated
public class ParkingSpaceController {

	private final ParkingSpaceService service;
	
	@PostMapping
	public ResponseEntity<ParkingSpace> newParkingSpace(
		@Valid @RequestBody ParkingSpace parkingSpace
	){
		return ResponseEntity.ok(service.createParkingSpace(parkingSpace));
	}
	
	@GetMapping
	public ResponseEntity<List<ParkingSpace>> listParkingSpaces(){
		return ResponseEntity.ok(service.getAllParkingSpaces());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ParkingSpace> searchParkingSpaceById(
		@PathVariable @Min(1) Integer id
	){
		return ResponseEntity.ok(service.getParkingSpaceById(id));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ParkingSpace> editParkingSpace(
		@PathVariable @Min(1) Integer id,
		@Valid @RequestBody ParkingSpace body
	){
		return ResponseEntity.ok(service.updateParkingSpace(id, body));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> excludeParkingSpace(
		@PathVariable @Min(1) Integer id
	){
		return ResponseEntity.ok(service.deleteParkingSpace(id));
	}
}
