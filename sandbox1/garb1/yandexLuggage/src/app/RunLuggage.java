package app;

import java.io.IOException;

import app.business.LuggageLogic;
import app.database.Data;

public class RunLuggage {
	private RunLuggage() {
		Run();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		new RunLuggage();
	}

	private void Run() {

		java.io.BufferedReader stdin = new java.io.BufferedReader(
				new java.io.InputStreamReader(System.in));

		System.out.println();
		System.out.println("****Starting application");
		System.out.println();
		
		while (true) {
			try {
				System.out.print("********Enter passenger name: ");
				String passengerName = stdin.readLine();

				if ((passengerName == null) || passengerName.equals("hihi"))
					break;
				new LuggageLogic().getLuggageByPassengerName(passengerName);
				System.out.println();
			}

			catch (java.io.IOException e) {
				System.out.println(e);
			} catch (NumberFormatException e) {
				System.out.println(e);
			}
		}

		System.out.println();
		System.out.println("****Stopping application");
		System.out.println();
		Data.getInstance().closeConnection();
	}

}
