package com.example.carpark.domain;

import java.math.BigDecimal;

public enum ParkingSpacePrice {
	
	CAR("Car", BigDecimal.valueOf(10.0)),
	MOTORCYCLE("Motorcycle", BigDecimal.valueOf(5.0));
	
	private final BigDecimal price;
	private final String type;
	
	ParkingSpacePrice(String type, BigDecimal price){
		this.type = type;
		this.price = price;
	}
	
	public String getType() {
		return type;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
}
