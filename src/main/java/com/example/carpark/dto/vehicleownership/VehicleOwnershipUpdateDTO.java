package com.example.carpark.dto.vehicleownership;

import com.example.carpark.infrastructure.entity.Owner;
import com.example.carpark.infrastructure.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VehicleOwnershipUpdateDTO {

	private Vehicle vehicle;
	private Owner owner;
}
