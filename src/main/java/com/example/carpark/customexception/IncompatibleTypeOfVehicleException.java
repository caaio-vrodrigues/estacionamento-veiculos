package com.example.carpark.customexception;

public class IncompatibleTypeOfVehicleException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public IncompatibleTypeOfVehicleException(String msg) {
		super(msg);
	}
}
