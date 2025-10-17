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

import com.example.carpark.dto.parkingspace.ParkingSpaceRequestDTO;
import com.example.carpark.dto.parkingspace.ParkingSpaceUpdateDTO;
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
		@Valid @RequestBody ParkingSpaceRequestDTO parkingSpace
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
	
	@GetMapping("place/{placeId}")
	public ResponseEntity<ParkingSpace> searchParkingSpaceById(
		@PathVariable String placeId
	){
		return ResponseEntity.ok(service.getParkingSpaceByPlaceId(placeId));
	}
	
	@PutMapping("/{placeId}")
	public ResponseEntity<ParkingSpace> editParkingSpace(
		@PathVariable String placeId,
		@Valid @RequestBody ParkingSpaceUpdateDTO body
	){
		return ResponseEntity.ok(service.updateParkingSpaceByPlaceId(placeId, body));
	}
	
	@DeleteMapping("/{placeId}")
	public ResponseEntity<Boolean> excludeParkingSpace(
		@PathVariable String placeId
	){
		return ResponseEntity.ok(service.deleteParkingSpaceByPlaceId(placeId));
	}
}
