package com.example.carpark.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.carpark.infrastructure.entity.Owner;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
	public Owner findByDriversLicense(String license);
}
