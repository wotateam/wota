package de.wota.gameobjects;

import de.wota.utility.Vector;
import de.wota.utility.Modulo;

public class Parameters {
	public static final double SIZE_X = 1000;
	public static final double SIZE_Y = 1000;
	public static final double HILL_RADIUS = 20;
	public static final double ATTACK_RANGE = 15; 
	/** Ration of damage: normal/collateral */
	public static final double COLLATERAL_DAMAGE_FACTOR = 0.5;
	/** Factor with which taken damage gets multiplied while carrying stuff */
	public static final double VULNERABILITY_WHILE_CARRYING = 5;
	
	public static final double INITIAL_SUGAR_RADIUS = 10;	
	/** Amount of sugar in a new source. */
	public static final int INITIAL_SUGAR = 500;
	public static final int ANT_COST = 100;
	public static final int STARTING_FOOD = 10000;
	// MAX_MOVEMENT_DISTANCE needs to greater than the speed of all castes.
	public static final double MAX_MOVEMENT_DISTANCE = 5;
	public static final double ANGLE_ERROR_PER_DISTANCE = 0; 
	
	public static final int TICKS_TO_LIVE = 5000;
	
	/** number of ticks picking up sugar takes */
	public static final int TICKS_SUGAR_PICKUP = 100;
	
	public static final int FRAMES_PER_SECOND = 40;
	public static final int TICKS_PER_SECOND = 10;
	
	public static final boolean DEBUG = false;
	
	public static final VictoryCondition VICTORY_CONDITION = VictoryCondition.KILL_ANTS;
	public static final boolean QUEEN_IS_VISIBLE = false;
	public enum VictoryCondition {
		KILL_QUEEN,
		KILL_ANTS;
	}
	
	public static Vector normalize(Vector p) {
		Vector r = new Vector(p);
		r.x = Modulo.mod(r.x, Parameters.SIZE_X);
		r.y = Modulo.mod(r.y, Parameters.SIZE_Y);
		return r;
	}
	
	
	/**
	 * Assumes that p1 and p2 are in the fundamental region.
	 * @param p1
	 * @param p2
	 * @return The shortest vector from p2 to a point equivalent to p1.
	 */
	public static Vector shortestDifferenceOnTorus(Vector p1, Vector p2) {
		Vector lowerLeftCornerOfImageOfGaussDiffeom = new Vector(-SIZE_X/2.,-SIZE_Y/2.);
		Vector differenceShifted = Vector.subtract(Vector.subtract(p1,p2),lowerLeftCornerOfImageOfGaussDiffeom);
		Vector differenceShiftedNormalized = 
				new Vector(Modulo.mod(differenceShifted.x, SIZE_X), Modulo.mod(differenceShifted.y, SIZE_Y));
		return Vector.add(differenceShiftedNormalized, lowerLeftCornerOfImageOfGaussDiffeom);
	}
	
	public static double distance(Vector p1, Vector p2) {
		return shortestDifferenceOnTorus(p1, p2).length();
	}
}
