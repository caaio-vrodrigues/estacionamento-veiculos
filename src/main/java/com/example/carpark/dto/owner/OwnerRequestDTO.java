package com.example.carpark.dto.owner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerRequestDTO {

	@NotEmpty(message = "The property 'fullName' can't be 'empty'")
    @NotBlank(message = "The property 'fullName' can't be 'blank'")
    private String fullName;

    @NotEmpty(message = "The property 'driversLicense' can't be 'empty'")
    @NotBlank(message = "The property 'driversLicense' can't be 'blank'")
    private String driversLicense;
}
