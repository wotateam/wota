package de.wota.gamemaster;

import de.wota.gameobjects.GameWorldParameters;

public class TestLogger extends AbstractLogger {

	@Override
	public void log(LogEventType event) {
		switch (event) {
		case TICK:
			if (GameWorldParameters.DEBUG) {
				System.out.println("TICK");
			}
			break;	
		}
	}

}
