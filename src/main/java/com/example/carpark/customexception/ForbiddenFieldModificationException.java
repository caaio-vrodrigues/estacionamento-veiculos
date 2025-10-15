package com.example.carpark.customexception;

public class ForbiddenFieldModificationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ForbiddenFieldModificationException(String msg) {
		super(msg);
	}
}
