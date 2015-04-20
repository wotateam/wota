package wota.gamemaster;

import java.util.Properties;


public class SimulationParameters {
	public final boolean IS_GRAPHICAL;
	public final double FRAMES_PER_SECOND;
	public final double INITIAL_TICKS_PER_SECOND;
	public final int NUMBER_OF_ROUNDS;
	public final boolean HOME_AND_AWAY;
	public final boolean TOURNAMENT;
	public final int DISPLAY_WIDTH;
	public final int DISPLAY_HEIGHT;
	
	public final String[] AI_PACKAGE_NAMES; // do not modify
	
	public SimulationParameters(Properties p) {
		IS_GRAPHICAL = Boolean.parseBoolean(p.getProperty("IS_GRAPHICAL"));
		NUMBER_OF_ROUNDS = Integer.parseInt(p.getProperty("NUMBER_OF_ROUNDS"));
		FRAMES_PER_SECOND = Double.parseDouble(p.getProperty("FRAMES_PER_SECOND"));
		INITIAL_TICKS_PER_SECOND = Double.parseDouble(p.getProperty("INITIAL_TICKS_PER_SECOND"));
		TOURNAMENT = Boolean.parseBoolean(p.getProperty("TOURNAMENT"));
		HOME_AND_AWAY = Boolean.parseBoolean(p.getProperty("HOME_AND_AWAY"));
		
		DISPLAY_WIDTH = Integer.parseInt(p.getProperty("DISPLAY_WIDTH"));
		DISPLAY_HEIGHT = Integer.parseInt(p.getProperty("DISPLAY_HEIGHT"));
		
		AI_PACKAGE_NAMES = p.getProperty("AI_PACKAGE_NAMES").split(",");
		for (int i = 0; i < AI_PACKAGE_NAMES.length; i++) {
			AI_PACKAGE_NAMES[i] = AI_PACKAGE_NAMES[i].trim();
		}
	}
}
