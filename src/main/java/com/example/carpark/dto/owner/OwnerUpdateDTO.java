package com.example.carpark.dto.owner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerUpdateDTO {

	private String fullName;
	private String driversLicense;
}
