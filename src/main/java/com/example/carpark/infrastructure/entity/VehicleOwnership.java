package com.example.carpark.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="vehicle_ownership")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VehicleOwnership {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="vehicle", nullable=false)
	private Vehicle vehicle;
	
	@ManyToOne
	@JoinColumn(name="name", nullable=false)
	private Owner owner;
}
