package com.example.carpark.customexception;

public class IncompatibleParkingSpaceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IncompatibleParkingSpaceException(String msg) {
		super(msg);
	}
}
