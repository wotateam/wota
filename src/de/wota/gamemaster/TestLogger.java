package de.wota.gamemaster;

import de.wota.gameobjects.Parameters;

public class TestLogger extends AbstractLogger {

	@Override
	public void log(LogEventType event) {
		switch (event) {
		case TICK:
			if (Parameters.DEBUG) {
				System.out.println("TICK");
			}
			break;	
		}
	}

}
