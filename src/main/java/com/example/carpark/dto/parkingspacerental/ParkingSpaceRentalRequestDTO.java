package com.example.carpark.dto.parkingspacerental;

import com.example.carpark.infrastructure.entity.ParkingSpace;
import com.example.carpark.infrastructure.entity.VehicleOwnership;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ParkingSpaceRentalRequestDTO {

	@NotNull
	private ParkingSpace parkingSpace;
	
	@NotNull
	private VehicleOwnership vehicleOwnership;
}
