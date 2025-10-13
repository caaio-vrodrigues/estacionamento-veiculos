package com.example.carpark.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="parkingspace_rental")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ParkingSpaceRental {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="parking_space", nullable=false)
	@NotNull
	private ParkingSpace parkingSpace;
	
	@ManyToOne
	@JoinColumn(name="vehicle_ownership", nullable=false)
	@NotNull
	private VehicleOwnership vehicleOwnership;
	
	@Column(name="start_renting", nullable=false)
	@Nullable
	private LocalDateTime startRenting;
	
	@Column(name="end_renting")
	@Nullable
	private LocalDateTime endRenting;
	
	@Column(name="total_rent")
	@Nullable
	private BigDecimal totalRent;
}
