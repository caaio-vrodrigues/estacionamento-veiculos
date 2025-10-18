package com.example.carpark.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.carpark.infrastructure.entity.ParkingSpace;

@Repository
public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Integer> {
	public Optional<ParkingSpace> findByPlaceId(String placeId);
	public boolean existsByPlaceId(String placeId);
	public ParkingSpace deleteByPlaceId(String placeId);
}
