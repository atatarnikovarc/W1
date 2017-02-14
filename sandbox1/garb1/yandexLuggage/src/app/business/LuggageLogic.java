package app.business;

import java.util.List;

import app.business.dto.PassengerDTO;
import app.database.Data;
import app.xml.XmlHelper;

public class LuggageLogic {
	
	public void getLuggageByPassengerName(String passengerName) {
		List<PassengerDTO> passList = XmlHelper.getInstance().getPassengersByName(passengerName);
		
		System.out.println();
		
		for (int i = 0; i < passList.size(); i++) {
			PassengerDTO passDTO = passList.get(i);
			System.out.println("==Passenger: " + passDTO.getPassengerName());
			System.out.println("Flight number: " + passDTO.getFlightNumber());
			Data.getInstance().printLuggageById(passDTO.getLuggageId());
		}
		
		if (passList.size() == 0)
			System.out.println("No Passenger found");
	}
}
