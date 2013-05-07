package de.wota.statistics;

import de.wota.gameobjects.GameWorldParameters;

public class TestLogger extends AbstractLogger {

	@Override
	public void log(LogEventType event) {
		if (GameWorldParameters.DEBUG)
			System.out.println("TICK");
	}

}
