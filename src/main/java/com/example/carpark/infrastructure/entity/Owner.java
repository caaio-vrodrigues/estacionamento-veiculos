package com.example.carpark.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="owner")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Owner {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="full_name", nullable=false)
	@NotEmpty @NotBlank
	private String fullName;
	
	@Column(name="drivers_license", nullable=false, unique=true)
	@NotEmpty @NotBlank
	private String driversLicense;
}
