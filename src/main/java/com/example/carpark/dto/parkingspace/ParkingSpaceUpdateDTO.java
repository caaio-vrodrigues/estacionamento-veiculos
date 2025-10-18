package com.example.carpark.dto.parkingspace;

import com.example.carpark.domain.ParkingSpacePrice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ParkingSpaceUpdateDTO {
	
	private ParkingSpacePrice type;
	private String placeId;
	private Boolean occupied;
}
