package wota.gamemaster;

import java.util.Properties;


public class SimulationParameters {
	public final boolean IS_GRAPHICAL;
	public final double FRAMES_PER_SECOND;
	public final double INITIAL_TICKS_PER_SECOND;
	public final int MAX_TICKS_BEFORE_END;
	
	public final String[] AI_PACKAGE_NAMES; // do not modify
	
	public SimulationParameters(Properties p) {
		IS_GRAPHICAL = Boolean.parseBoolean(p.getProperty("IS_GRAPHICAL"));
		FRAMES_PER_SECOND = Double.parseDouble(p.getProperty("FRAMES_PER_SECOND"));
		INITIAL_TICKS_PER_SECOND = Double.parseDouble(p.getProperty("INITIAL_TICKS_PER_SECOND"));
		MAX_TICKS_BEFORE_END = Integer.parseInt(p.getProperty("MAX_TICKS_BEFORE_END"));
		
		AI_PACKAGE_NAMES = p.getProperty("AI_PACKAGE_NAMES").split(",");
		for (int i = 0; i < AI_PACKAGE_NAMES.length; i++) {
			AI_PACKAGE_NAMES[i] = AI_PACKAGE_NAMES[i].trim();
		}
	}
}
