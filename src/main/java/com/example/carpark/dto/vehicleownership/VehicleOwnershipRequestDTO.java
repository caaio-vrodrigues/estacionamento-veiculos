package com.example.carpark.dto.vehicleownership;

import com.example.carpark.infrastructure.entity.Owner;
import com.example.carpark.infrastructure.entity.Vehicle;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VehicleOwnershipRequestDTO {
	
	@NotNull
	private Vehicle vehicle;
	
	@NotNull
	private Owner owner;
}
