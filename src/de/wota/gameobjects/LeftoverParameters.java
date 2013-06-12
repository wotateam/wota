package de.wota.gameobjects;


public class LeftoverParameters {
	public static float framesPerSecond = 40;
	public static float ticksPerSecond = 5;
	
	public static final boolean DEBUG = false;
	
	public static final VictoryCondition VICTORY_CONDITION = VictoryCondition.KILL_ANTS;
	public static final boolean QUEEN_IS_VISIBLE = false;
	public enum VictoryCondition {
		KILL_QUEEN,
		KILL_ANTS;
	}
}
