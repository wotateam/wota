package de.wota.statistics;

public class TestLogger extends AbstractLogger {

	@Override
	public void log(LogEventType event) {
		System.out.println("TICK");
	}

}
