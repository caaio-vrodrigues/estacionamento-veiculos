package com.example.carpark.infrastructure.entity;

import java.math.BigDecimal;

import com.example.carpark.domain.ParkingSpacePrice;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="parking_space")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ParkingSpace {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="type", nullable=false)
	@NotNull
	private ParkingSpacePrice type;
	
	@Column(name="price", nullable=false)
	@Nullable
	private BigDecimal price;
	
	@Column(name="occupied", nullable=false)
	@Nullable
	private Boolean occupied;
}
