package com.example.carpark.infrastructure.entity;

import com.example.carpark.domain.VehicleBrand;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="vehicle")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Vehicle {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="model", nullable=false, unique=true)
	private String model;
	
	@Column(name="brand", nullable=false)
	private VehicleBrand brand;
	
	@Column(name="country", nullable=false)
	private String country;
	
	@Column(name="plaque", nullable=false, unique=true)
	private String plaque;
}
	
