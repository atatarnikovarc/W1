package app.business.dto;

public class PassengerDTO {
	private String passengerName;
	private String flightNumber;
	private String luggageId;
	
	public PassengerDTO(String name, String number, String luggageId) {
		setPassengerName(name);
		setFlightNumber(number);
		setLuggageId(luggageId);
	}
	
	public String getPassengerName() {
		return passengerName;
	}
	private void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}
	public String getFlightNumber() {
		return flightNumber;
	}
	private void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	public String getLuggageId() {
		return luggageId;
	}
	private void setLuggageId(String luggageId) {
		this.luggageId = luggageId;
	}
}
