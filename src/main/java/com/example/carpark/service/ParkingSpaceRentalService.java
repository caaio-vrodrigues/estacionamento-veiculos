package com.example.carpark.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.carpark.customexception.ClosedRentalServiceException;
import com.example.carpark.customexception.IncompatibleParkingSpaceException;
import com.example.carpark.customexception.IncompatibleTypeOfVehicleException;
import com.example.carpark.customexception.OccupiedParkingSpaceException;
import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.example.carpark.customexception.VehicleOwnershipAlreadyInUseException;
import com.example.carpark.dto.parkingspace.ParkingSpaceUpdateDTO;
import com.example.carpark.dto.parkingspacerental.ParkingSpaceRentalRequestDTO;
import com.example.carpark.dto.parkingspacerental.ParkingSpaceRentalUpdateDTO;
import com.example.carpark.infrastructure.entity.ParkingSpace;
import com.example.carpark.infrastructure.entity.ParkingSpaceRental;
import com.example.carpark.infrastructure.entity.Vehicle;
import com.example.carpark.infrastructure.entity.VehicleOwnership;
import com.example.carpark.infrastructure.repository.ParkingSpaceRentalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingSpaceRentalService {
	
	private final ParkingSpaceRentalRepository repo;
	private final ParkingSpaceService parkingSpaceService;
	private final VehicleOwnershipService vehicleOwnershipService;
	private final VehicleService vehicleService;
	
	public ParkingSpaceRental createParkingSpaceRental(ParkingSpaceRentalRequestDTO body) {
		ParkingSpace parkingSpace = parkingSpaceService
			.getParkingSpaceByPlaceId(body.getParkingSpace().getPlaceId());
		boolean isParkingSpaceOccupied = parkingSpace.getOccupied() == true;
		if(isParkingSpaceOccupied) throw new OccupiedParkingSpaceException("The parking space:"+parkingSpace.getPlaceId()+" is already in use");
		VehicleOwnership vehicleOwnership = vehicleOwnershipService
			.getVehicleOwnershipById(body.getVehicleOwnership().getId());
		Vehicle vehicle = vehicleService.getVehicleById(vehicleOwnership.getVehicle().getId());
		List<VehicleOwnership> vehicleOwnershipList = vehicleOwnershipService
			.getAllVehicleOwnershipByVehicle(vehicle);
		for(VehicleOwnership vehicleOwner : vehicleOwnershipList) {
			List<ParkingSpaceRental> isVehicleAlreadyInOpenParkingSpaceRental = repo
					.findAllByVehicleOwnershipAndEndRentingIsNull(vehicleOwner);
				if(!isVehicleAlreadyInOpenParkingSpaceRental.isEmpty()) 
					throw new ResourceAlreadyExistsException("The vehicle is already in the parking space. Vehicle plaque: "+vehicleOwnership.getVehicle().getPlaque());
		}
		boolean isSameType = parkingSpace.getType() == vehicle.getType();
		if(!isSameType) throw new IncompatibleTypeOfVehicleException("Incompatible type of vehicle "+vehicle.getType()+" with type of parking space "+parkingSpace.getType());
		parkingSpace.setOccupied(true);
		ParkingSpaceUpdateDTO parkingSpaceDTO = ParkingSpaceUpdateDTO.builder()
			.occupied(true)
			.build();
		parkingSpaceService.updateParkingSpaceByPlaceId(parkingSpace.getPlaceId(), parkingSpaceDTO);
		ParkingSpaceRental newParkingSpaceRental = ParkingSpaceRental.builder()
			.parkingSpace(parkingSpace)
			.vehicleOwnership(vehicleOwnership)
			.startRenting(LocalDateTime.now())
			.build();
		return repo.saveAndFlush(newParkingSpaceRental);
	}
	
	public List<ParkingSpaceRental> getAllParkingSpaceRentals(){
		return repo.findAll();
	}
	
	public ParkingSpaceRental getParkingSpaceRentalById(Long id) {
		return repo.findById(id).orElseThrow(()->
			new ResourceNotFoundException("No resource found with id: "+id));
	}
	
	public ParkingSpaceRental updateParkingSpaceRental(Long id, ParkingSpaceRentalUpdateDTO body) {
		ParkingSpaceRental existingParkingSpaceRental = getParkingSpaceRentalById(id);
		boolean bodyConatinsVehicleOwnership = body.getVehicleOwnership() != null;
		boolean bodyContainsParkingSpace = body.getParkingSpace() != null;
		boolean isOpenRental = existingParkingSpaceRental.getEndRenting() == null;
		if(!isOpenRental) throw new ClosedRentalServiceException("It is not possible to modify a completed rental service");
		if(bodyContainsParkingSpace) {
			ParkingSpace incomingParkingSpace = parkingSpaceService
				.getParkingSpaceByPlaceId(body.getParkingSpace().getPlaceId());
			ParkingSpace existingParkingSpaceToUpdate = parkingSpaceService
				.getParkingSpaceById(existingParkingSpaceRental.getParkingSpace().getId());
			String existingVehiclePlaque = existingParkingSpaceRental
					.getVehicleOwnership().getVehicle().getPlaque();
			boolean isSameParkingSpace = incomingParkingSpace.getId().equals(
				existingParkingSpaceToUpdate.getId());
			boolean isSameType = incomingParkingSpace.getType().equals(
				existingParkingSpaceRental.getParkingSpace().getType());
			if(!isSameParkingSpace) {
				boolean isnewParkingSpaceOccupied = incomingParkingSpace.getOccupied() == true;
				if(isnewParkingSpaceOccupied) {
					List<ParkingSpaceRental> parkingSpaceRentalListByIncomingParkingSpace = repo
						.findByParkingSpaceAndEndRentingIsNull(incomingParkingSpace);
					if(!parkingSpaceRentalListByIncomingParkingSpace.isEmpty()) {
						ParkingSpaceRental existingOpenRentalInSpace = 
							parkingSpaceRentalListByIncomingParkingSpace.get(0);
						String vehicleInSpacePlaque = existingOpenRentalInSpace
							.getVehicleOwnership().getVehicle().getPlaque();
						boolean isSameVehiclePlaque = existingVehiclePlaque
							.equals(vehicleInSpacePlaque);
						if(!isSameVehiclePlaque) throw new OccupiedParkingSpaceException("This parking space is already in use by a vehicle with plaque: "+vehicleInSpacePlaque);
					}
				}
			}
			if(!isSameType) throw new IncompatibleParkingSpaceException("The vehicle with plaque: "+existingVehiclePlaque+" is not compatible with parking space type: "+incomingParkingSpace.getType());
			ParkingSpaceUpdateDTO oldParkingSpaceUpdateDTO = ParkingSpaceUpdateDTO.builder()
				.occupied(false)
				.build();
			ParkingSpaceUpdateDTO newParkingSpaceUpdateDTO = ParkingSpaceUpdateDTO.builder()
				.occupied(true)
				.build();
			parkingSpaceService.updateParkingSpaceByPlaceId(
				existingParkingSpaceRental.getParkingSpace().getPlaceId(), 
				oldParkingSpaceUpdateDTO);
			parkingSpaceService.updateParkingSpaceByPlaceId(
				incomingParkingSpace.getPlaceId(), 
				newParkingSpaceUpdateDTO);
			existingParkingSpaceRental.setParkingSpace(incomingParkingSpace);
		}
		if(bodyConatinsVehicleOwnership) {
			VehicleOwnership existingVehicleOwnershipToUpdate = vehicleOwnershipService
				.getVehicleOwnershipById(existingParkingSpaceRental.getVehicleOwnership().getId());
			VehicleOwnership incomingVehicleOwnership = vehicleOwnershipService
				.getVehicleOwnershipById(body.getVehicleOwnership().getId());
			boolean isSameVehicleOwnership = incomingVehicleOwnership
				.getId().equals(existingVehicleOwnershipToUpdate.getId());
			if(!isSameVehicleOwnership) {
				List<ParkingSpaceRental> openedRentals = repo.findAllByEndRentingIsNull();
				openedRentals.forEach(parkingSpaceRental -> {
					String parkedVehiclePlaque = parkingSpaceRental
						.getVehicleOwnership().getVehicle().getPlaque();
					String incomingVehiclePlaque = incomingVehicleOwnership
						.getVehicle().getPlaque();
					boolean vehicleIsAlreadyParked = parkedVehiclePlaque.equals(incomingVehiclePlaque);
					if(vehicleIsAlreadyParked) throw new VehicleOwnershipAlreadyInUseException("The vehicle witha plaque: "+incomingVehiclePlaque+" is already parked by the client: "+parkingSpaceRental.getVehicleOwnership().getOwner().getFullName());
				});
				boolean isSameType = existingVehicleOwnershipToUpdate.getVehicle().getType() ==
					incomingVehicleOwnership.getVehicle().getType();
				if(!isSameType) throw new IncompatibleTypeOfVehicleException("Vehicle with plaque: "+incomingVehicleOwnership.getVehicle().getPlaque()+" is not compatible with type of parking space: "+existingVehicleOwnershipToUpdate.getVehicle().getType());
				existingParkingSpaceRental.setVehicleOwnership(incomingVehicleOwnership);
			}
		}
		return repo.saveAndFlush(existingParkingSpaceRental);
	}
	
	public boolean deleteParkingSpaceRental(Long id) {
		boolean existingParkingSpaceRental = repo.existsById(id);
		if(!existingParkingSpaceRental) throw new ResourceNotFoundException("No resource found with id: "+id);
		ParkingSpaceRental parkingSpaceRental = getParkingSpaceRentalById(id);
		ParkingSpaceUpdateDTO parkingSpaceRentalUpdateDTO = ParkingSpaceUpdateDTO.builder()
			.type(parkingSpaceRental.getParkingSpace().getType())
			.build();
		parkingSpaceRental.getParkingSpace().setOccupied(false);
		parkingSpaceService.updateParkingSpaceByPlaceId(
			parkingSpaceRental.getParkingSpace().getPlaceId(), 
			parkingSpaceRentalUpdateDTO);
		repo.deleteById(id);
		return true;
	}
	
	public ParkingSpaceRental endParkingSpaceRental(Long id) {
		ParkingSpaceRental parkingSpaceRental = getParkingSpaceRentalById(id);
		ParkingSpaceUpdateDTO parkingSpaceRentalUpdateDTO = ParkingSpaceUpdateDTO.builder()
			.type(parkingSpaceRental.getParkingSpace().getType())
			.build();
		parkingSpaceRental.setEndRenting(LocalDateTime.now());
		parkingSpaceRental.getParkingSpace().setOccupied(false);
		parkingSpaceService.updateParkingSpaceByPlaceId(
			parkingSpaceRental.getParkingSpace().getPlaceId(), 
			parkingSpaceRentalUpdateDTO);
		LocalDateTime startRenting = parkingSpaceRental.getStartRenting();
		LocalDateTime endRenting = parkingSpaceRental.getEndRenting();
		BigDecimal durationInHours = new BigDecimal(Duration
			.between(startRenting, endRenting).toHours());
		BigDecimal hourPrice = parkingSpaceRental.getParkingSpace().getPrice();
		parkingSpaceRental.setTotalRent(durationInHours.multiply(hourPrice).add(hourPrice));
		return repo.saveAndFlush(parkingSpaceRental);
	}
}
