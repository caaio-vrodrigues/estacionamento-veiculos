package com.example.carpark.dto.parkingspacerental;

import java.time.LocalDateTime;

import com.example.carpark.infrastructure.entity.ParkingSpace;
import com.example.carpark.infrastructure.entity.VehicleOwnership;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ParkingSpaceRentalUpdateDTO {

	private ParkingSpace parkingSpace;
	private VehicleOwnership vehicleOwnership;
	private LocalDateTime endRenting;
}
