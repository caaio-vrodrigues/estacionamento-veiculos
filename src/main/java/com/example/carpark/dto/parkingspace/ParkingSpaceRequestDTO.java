package com.example.carpark.dto.parkingspace;

import com.example.carpark.domain.ParkingSpacePrice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ParkingSpaceRequestDTO {
	
	@NotNull(message="Property 'type' cannot be 'null'")
	private ParkingSpacePrice type;
	
	@NotEmpty(message="Property 'placeId' cannot be 'empty'")
	@NotBlank(message="Property 'placeId' cannot be 'blank'")
	private String placeId;
}
