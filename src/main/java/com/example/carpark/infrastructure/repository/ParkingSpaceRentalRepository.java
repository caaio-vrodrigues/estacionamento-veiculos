package com.example.carpark.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.carpark.infrastructure.entity.ParkingSpace;
import com.example.carpark.infrastructure.entity.ParkingSpaceRental;
import com.example.carpark.infrastructure.entity.VehicleOwnership;

@Repository
public interface ParkingSpaceRentalRepository extends JpaRepository<ParkingSpaceRental, Long> {
	public List<ParkingSpaceRental> findAllByVehicleOwnership(VehicleOwnership vehicleOwnership);
	public List<ParkingSpaceRental> findByParkingSpaceAndEndRentingIsNull(ParkingSpace parkingSpace);
	public List<ParkingSpaceRental> findAllByVehicleOwnershipAndEndRentingIsNull(VehicleOwnership vehicleOwnership);
	public List<ParkingSpaceRental> findAllByEndRentingIsNull();
}
