package de.wota.gameobjects;


public class LeftoverParameters {
	public static final int FRAMES_PER_SECOND = 40;
	public static final int TICKS_PER_SECOND = 20;
	
	public static final boolean DEBUG = false;
	
	public static final VictoryCondition VICTORY_CONDITION = VictoryCondition.KILL_ANTS;
	public static final boolean QUEEN_IS_VISIBLE = false;
	public enum VictoryCondition {
		KILL_QUEEN,
		KILL_ANTS;
	}
}
