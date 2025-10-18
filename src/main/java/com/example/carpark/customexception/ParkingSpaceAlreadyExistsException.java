package com.example.carpark.customexception;

public class ParkingSpaceAlreadyExistsException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public ParkingSpaceAlreadyExistsException(String msg) {
		super(msg);
	}
}
