package com.example.carpark.domain;

public enum VehicleBrand {
	FORD("Ford", "Estados Unidos"),
	PORSHE("Porshe", "Alemanha"),
	FERRARI("Ferrari", "Itália"),
	MERCEDES_BENZ("Mercedes-benz", "Alemanha"),
	FIAT("Fiat", "Itália"),
	PEUGEOT("Peugeot", "França"),
	SUZUKI("Suzuki", "Japão");
	
	private final String name;
	private final String country;
	
	VehicleBrand(String name, String country){
		this.name = name;
		this.country = country;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCountry() {
		return country;
	}
}
