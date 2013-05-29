package de.wota.gamemaster;

import de.wota.gameobjects.LeftoverParameters;

public class TestLogger extends AbstractLogger {

	@Override
	public void log(LogEventType event) {
		switch (event) {
		case TICK:
			if (LeftoverParameters.DEBUG) {
				System.out.println("TICK");
			}
			break;	
		}
	}

}
