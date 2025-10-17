package com.example.carpark.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.carpark.infrastructure.entity.Owner;
import com.example.carpark.infrastructure.entity.Vehicle;
import com.example.carpark.infrastructure.entity.VehicleOwnership;

@Repository
public interface VehicleOwnershipRepository extends JpaRepository<VehicleOwnership, Long> {
	public List<VehicleOwnership> findAllByOwner(Owner owner);
	public List<VehicleOwnership> findAllByVehicle(Vehicle vehicle);
}
