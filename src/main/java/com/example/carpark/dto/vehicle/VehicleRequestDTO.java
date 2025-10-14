package com.example.carpark.dto.vehicle;

import com.example.carpark.domain.ParkingSpacePrice;
import com.example.carpark.domain.VehicleBrand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VehicleRequestDTO {

	@NotEmpty(message="Property 'model' can't be 'empty'")
    @NotBlank(message="Property 'model' can't be 'blank'")
    private String model;

    @NotNull(message="Property 'brand' can't be 'null'")
    private VehicleBrand brand;

    @NotEmpty(message="Property 'plaque' can't be 'empty'")
    @NotBlank(message="Property 'plaque' can't be 'blank'")
    private String plaque;

    @NotNull(message="Property 'type' can't be 'null'")
    private ParkingSpacePrice type;
}
