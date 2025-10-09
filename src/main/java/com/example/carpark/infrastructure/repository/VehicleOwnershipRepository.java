package com.example.carpark.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.carpark.infrastructure.entity.VehicleOwnership;

@Repository
public interface VehicleOwnershipRepository extends JpaRepository<VehicleOwnership, Long> {}
