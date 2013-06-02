package de.wota.gameobjects;

import java.util.Properties;

import de.wota.utility.Modulo;
import de.wota.utility.Vector;

// Please put comments in /parameters.txt, because this is where they are set.
public class Parameters {

	public final double SIZE_X;
	public final double SIZE_Y;
	public final double HILL_RADIUS;
	public final double ATTACK_RANGE;
	public final double COLLATERAL_DAMAGE_FACTOR;
	public final double VULNERABILITY_WHILE_CARRYING;
	public final double INITIAL_SUGAR_RADIUS;
	public final int INITIAL_SUGAR;
	public final int ANT_COST;
	public final int STARTING_FOOD;
	public final double ANGLE_ERROR_PER_DISTANCE;
	public final int TICKS_TO_LIVE;
	public final int TICKS_SUGAR_PICKUP;
	public final int N_SUGAR_SOURCES;
	
	public Parameters(Properties p) {
		SIZE_X = Double.parseDouble(p.getProperty("SIZE_X"));
		SIZE_Y = Double.parseDouble(p.getProperty("SIZE_Y"));
		HILL_RADIUS = Double.parseDouble(p.getProperty("HILL_RADIUS"));
		ATTACK_RANGE = Double.parseDouble(p.getProperty("ATTACK_RANGE"));
		COLLATERAL_DAMAGE_FACTOR = Double.parseDouble(p.getProperty("COLLATERAL_DAMAGE_FACTOR"));
		VULNERABILITY_WHILE_CARRYING = Double.parseDouble(p.getProperty("VULNERABILITY_WHILE_CARRYING"));
		INITIAL_SUGAR_RADIUS = Double.parseDouble(p.getProperty("INITIAL_SUGAR_RADIUS"));
	
		INITIAL_SUGAR = Integer.parseInt(p.getProperty("INITIAL_SUGAR"));
		ANT_COST = Integer.parseInt(p.getProperty("ANT_COST"));
		STARTING_FOOD = Integer.parseInt(p.getProperty("STARTING_FOOD"));
		
		ANGLE_ERROR_PER_DISTANCE  = Double.parseDouble(p.getProperty("ANGLE_ERROR_PER_DISTANCE"));
		
		TICKS_TO_LIVE = Integer.parseInt(p.getProperty("TICKS_TO_LIVE"));
		TICKS_SUGAR_PICKUP = Integer.parseInt(p.getProperty("TICKS_SUGAR_PICKUP"));
		N_SUGAR_SOURCES = Integer.parseInt(p.getProperty("N_SUGAR_SOURCES"));
	}
	
	public Vector normalize(Vector p) {
		Vector r = new Vector(p);
		r.x = Modulo.mod(r.x, SIZE_X);
		r.y = Modulo.mod(r.y, SIZE_Y);
		return r;
	}
	/**
	 * 
	 * @param p1
	 * @param p2
	 * @return The shortest vector from p2 to a point equivalent to p1.
	 */
	public Vector shortestDifferenceOnTorus(Vector p1, Vector p2) {
		return shortestVectorOnTorusTo(Vector.subtract(p1,p2));
	}
	
	/**
	 * Returns the shortest vector from the origin to a point equivalent to the argument on the torus.
	 * @param p Point on the torus.
	 * @return Shortest vector to p on the torus.
	 */
	public Vector shortestVectorOnTorusTo(Vector p) {
		Vector lowerLeftCornerOfImageOfExpMap = new Vector(-SIZE_X/2.,-SIZE_Y/2.);
		Vector differenceShifted = Vector.subtract(p,lowerLeftCornerOfImageOfExpMap);
		Vector differenceShiftedNormalized = 
				new Vector(Modulo.mod(differenceShifted.x, SIZE_X), Modulo.mod(differenceShifted.y, SIZE_Y));
		return Vector.add(differenceShiftedNormalized, lowerLeftCornerOfImageOfExpMap);
	}
	
	public double distance(Vector p1, Vector p2) {
		return shortestDifferenceOnTorus(p1, p2).length();
	}

}
