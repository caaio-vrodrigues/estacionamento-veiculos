package com.example.carpark.customexception;

public class OccupiedParkingSpaceException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public OccupiedParkingSpaceException(String msg) {
		super(msg);
	}
}
