package com.example.carpark.customexception;

public class VehicleOwnershipAlreadyInUseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public VehicleOwnershipAlreadyInUseException(String msg) {
		super(msg);
	}
}
