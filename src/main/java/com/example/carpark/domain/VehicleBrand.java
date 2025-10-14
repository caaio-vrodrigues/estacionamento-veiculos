package com.example.carpark.domain;

public enum VehicleBrand {
	FORD("Ford", "Estados Unidos", ParkingSpacePrice.CAR),
	PORSHE("Porshe", "Alemanha", ParkingSpacePrice.CAR),
	FERRARI("Ferrari", "Itália", ParkingSpacePrice.CAR),
	MERCEDES_BENZ("Mercedes-benz", "Alemanha", ParkingSpacePrice.CAR),
	FIAT("Fiat", "Itália", ParkingSpacePrice.CAR),
	PEUGEOT("Peugeot", "França", ParkingSpacePrice.CAR),
	SUZUKI("Suzuki", "Japão", ParkingSpacePrice.MOTORCYCLE),
	YAMAHA("Yamaha", "Japão", ParkingSpacePrice.MOTORCYCLE);
	
	private final String name;
	private final String country;
	private final ParkingSpacePrice type;
	
	VehicleBrand(String name, String country, ParkingSpacePrice type){
		this.name = name;
		this.country = country;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCountry() {
		return country;
	}
	
	public ParkingSpacePrice getType() {
		return type;
	}
}
