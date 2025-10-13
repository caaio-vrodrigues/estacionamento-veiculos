package com.example.carpark.infrastructure.entity;

import com.example.carpark.domain.ParkingSpacePrice;
import com.example.carpark.domain.VehicleBrand;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
	
	@Column(name="model", nullable=false)
	@NotBlank @NotEmpty
	private String model;
	
	@Column(name="brand", nullable=false)
	@NotNull
	private VehicleBrand brand;
	
	@Column(name="country", nullable=false)
	@Nullable
	private String country;
	
	@Column(name="plaque", nullable=false, unique=true)
	@NotBlank @NotEmpty
	private String plaque;
	
	@Column(name="type", nullable=false)
	@NotNull
	private ParkingSpacePrice type;
}
	
