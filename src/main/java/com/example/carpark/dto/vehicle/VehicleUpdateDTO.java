package com.example.carpark.dto.vehicle;

import com.example.carpark.domain.ParkingSpacePrice;
import com.example.carpark.domain.VehicleBrand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VehicleUpdateDTO {
	
	private String model;
    private VehicleBrand brand;
    private String plaque;
    private ParkingSpacePrice type;
}
