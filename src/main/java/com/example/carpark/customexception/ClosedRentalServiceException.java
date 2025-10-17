package com.example.carpark.customexception;

public class ClosedRentalServiceException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public ClosedRentalServiceException(String msg) {
		super(msg);
	}
}
