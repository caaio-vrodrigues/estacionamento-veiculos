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

import com.example.carpark.infrastructure.entity.ParkingSpaceRental;
import com.example.carpark.service.ParkingSpaceRentalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parkingspace-rental")
public class ParkingSpaceRentalController {

	private final ParkingSpaceRentalService service;
	
	@PostMapping
	public ResponseEntity<ParkingSpaceRental> newParkingSpaceRental(
		@RequestBody ParkingSpaceRental parkingSpaceRental
	){
		return ResponseEntity.ok(service
			.createParkingSpaceRental(parkingSpaceRental));
	}
	
	@GetMapping
	public ResponseEntity<List<ParkingSpaceRental>> listParkingSpaceRentals(){
		return ResponseEntity.ok(service.getAllParkingSpaceRentals());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ParkingSpaceRental> searchParkingSpaceRentalById(
		@PathVariable Long id
	){
		return ResponseEntity.ok(service.getParkingSpaceRentalById(id));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ParkingSpaceRental> editParkingSpaceRental(
		@PathVariable Long id,
		@RequestBody ParkingSpaceRental body
	){
		return ResponseEntity.ok(service.updateParkingSpaceRental(id, body));
	}
	
	@PutMapping("end-rental/{id}")
	public ResponseEntity<ParkingSpaceRental> finalizeRental(
		@PathVariable Long id
	){
		return ResponseEntity.ok(service.endParkingSpaceRental(id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> excludeParkingSpaceRental(
		@PathVariable Long id
	){
		return ResponseEntity.ok(service.deleteParkingSpaceRental(id));
	}
}
