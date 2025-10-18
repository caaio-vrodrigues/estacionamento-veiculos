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
	
	@Column(name="place_id", nullable=false, unique=true)
	private String placeId;
	
	@Column(name="type", nullable=false)
	private ParkingSpacePrice type;
	
	@Column(name="price", nullable=false)
	@Nullable
	private BigDecimal price;
	
	@Column(name="occupied", nullable=false)
	@Nullable
	private Boolean occupied;
}
