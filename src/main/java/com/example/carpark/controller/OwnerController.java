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

import com.example.carpark.infrastructure.entity.Owner;
import com.example.carpark.service.OwnerService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner")
@Validated
public class OwnerController {

	private final OwnerService service;
	
	@PostMapping
	public ResponseEntity<Owner> newOwner(
		@Valid @RequestBody Owner owner
	){
		return ResponseEntity.ok(service.createOwner(owner));
	}
	
	@GetMapping
	public ResponseEntity<List<Owner>> listOwners(){
		return ResponseEntity.ok(service.getAllOwners());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Owner> searchOwnerById(
		@PathVariable @Min(1) Long id
	){
		return ResponseEntity.ok(service.getOwnerById(id));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Owner> editOwner(
		@PathVariable @Min(1) Long id,
		@Valid @RequestBody Owner body
	){
		return ResponseEntity.ok(service.updateOwner(id, body));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> excludeOwner(
		@PathVariable @Min(1) Long id
	){
		return ResponseEntity.ok(service.deleteOwner(id));
	}
}
