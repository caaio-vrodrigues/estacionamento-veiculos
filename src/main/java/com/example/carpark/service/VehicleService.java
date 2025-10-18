package com.example.carpark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.ForbiddenActionException;
import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.example.carpark.domain.ParkingSpacePrice;
import com.example.carpark.domain.VehicleBrand;
import com.example.carpark.dto.vehicle.VehicleRequestDTO;
import com.example.carpark.dto.vehicle.VehicleUpdateDTO;
import com.example.carpark.infrastructure.entity.ParkingSpaceRental;
import com.example.carpark.infrastructure.entity.Vehicle;
import com.example.carpark.infrastructure.entity.VehicleOwnership;
import com.example.carpark.infrastructure.repository.ParkingSpaceRentalRepository;
import com.example.carpark.infrastructure.repository.VehicleOwnershipRepository;
import com.example.carpark.infrastructure.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {

	private final VehicleRepository repo;
	private final VehicleOwnershipRepository vehicleOwnershipRepo;
	private final ParkingSpaceRentalRepository parkingSpaceRentalRepo;
	
	public Vehicle createVehicle(VehicleRequestDTO  body) {
		boolean existingVehiclePlaque = repo.existsByPlaque(body.getPlaque());
		if(existingVehiclePlaque) throw new ResourceAlreadyExistsException("The vehicle with plaque "+body.getPlaque()+" already exists");
		Vehicle newVehicle = Vehicle.builder()
			.model(body.getModel())
			.brand(body.getBrand())
			.plaque(body.getPlaque())
			.type(body.getType())
			.country(body.getBrand().getCountry())
			.build();
		return repo.saveAndFlush(newVehicle);
	}
	
	public List<Vehicle> getAllVehicles(){
		return repo.findAll();
	}
	
	public Vehicle getVehicleById(Long id) {
		return repo.findById(id).orElseThrow(()-> new ResourceNotFoundException("No resource found with id: "+id));
	}
	
	public Vehicle updateVehicle(Long id, VehicleUpdateDTO body) {
		Vehicle existingVehicle = getVehicleById(id);
		List<VehicleOwnership> vehicleOwnershipList = vehicleOwnershipRepo
			.findAllByVehicle(existingVehicle);
		for(VehicleOwnership vehicleOwnership : vehicleOwnershipList) {
			List<ParkingSpaceRental> parkingSpaceRentalList = parkingSpaceRentalRepo
				.findAllByVehicleOwnershipAndEndRentingIsNull(vehicleOwnership);
			if(!parkingSpaceRentalList.isEmpty()) throw new ForbiddenActionException("Cannot modify a vehicle that is in open rental");
		}
		VehicleBrand potentialNewBrand = existingVehicle.getBrand();
        if (body.getBrand() != null) potentialNewBrand = body.getBrand();
        ParkingSpacePrice potentialNewType = existingVehicle.getType();
        if (body.getType() != null) potentialNewType = body.getType();
        if(body.getBrand() != null) {
            existingVehicle.setBrand(potentialNewBrand);
            existingVehicle.setCountry(potentialNewBrand.getCountry());
        }
        if(body.getType() != null) existingVehicle.setType(potentialNewType);
        if(body.getPlaque() != null) {
            boolean vehiclePlaqueAlreadyExists = !existingVehicle.getPlaque()
            	.equals(body.getPlaque()) && repo.existsByPlaque(body.getPlaque());
            if (vehiclePlaqueAlreadyExists) throw new ResourceAlreadyExistsException("The vehicle with plaque "+body.getPlaque()+" already exists");
            existingVehicle.setPlaque(body.getPlaque());
        }
        if(body.getModel() != null) existingVehicle.setModel(body.getModel());
		return repo.saveAndFlush(existingVehicle);
	}
	
	public boolean deleteVehicle(Long id) {
		boolean existingVehicle = repo.existsById(id);
		if(!existingVehicle) throw new ResourceNotFoundException("No ressource found with id: "+id);
		repo.deleteById(id);
		return true;
	}
}
