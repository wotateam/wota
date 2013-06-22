package wota.gameobjects;

import java.util.Properties;

import wota.utility.Modulo;
import wota.utility.Vector;


// Please put comments in /parameters.txt, because this is where they are set.
public class Parameters {

	public final double SIZE_X;
	public final double SIZE_Y;
	public final double HILL_RADIUS;
	public final double ATTACK_RANGE;
	// Ants carrying sugar take this many times the normal damage when under attack:
	public final double VULNERABILITY_WHILE_CARRYING;
	// Radius of a new sugar source:
	public final double INITIAL_SUGAR_RADIUS;
	// Amount of sugar in a new sugar source:
	public final int INITIAL_SUGAR_IN_SOURCE;
	// Cost to produce an ant:
	public final int ANT_COST;
	// Amount of food available at each hill when the game starts:
	public final int STARTING_FOOD;
	// An ant's moving direction may differ this much from the intended direction:
	public final double ANGLE_ERROR_PER_DISTANCE;
	public final int TICKS_TO_LIVE;
	// Number of ticks it takes to pick up sugar: 
	public final int TICKS_SUGAR_PICKUP;
	public final double MINIMUM_DISTANCE_BETWEEN_HILLS;
	public final double MAXIMUM_STARTING_SUGAR_DISTANCE;
	public final double MINIMUM_STARTING_SUGAR_DISTANCE;
	public final double MINIMUM_STARTING_SUGAR_DISTANCE_TO_OTHER_HILLS;
	public final double MINIMUM_SUGAR_DISTANCE;
	public final double MINIMUM_SUGAR_DISTANCE_TO_OTHER_SUGAR;
	// Number of sugar sources per player
	public final int SUGAR_SOURCES_PER_PLAYER;

	public Parameters(Properties p) {
		SIZE_X = Double.parseDouble(p.getProperty("SIZE_X"));
		SIZE_Y = Double.parseDouble(p.getProperty("SIZE_Y"));
		HILL_RADIUS = Double.parseDouble(p.getProperty("HILL_RADIUS"));
		ATTACK_RANGE = Double.parseDouble(p.getProperty("ATTACK_RANGE"));
		VULNERABILITY_WHILE_CARRYING = Double.parseDouble(p.getProperty("VULNERABILITY_WHILE_CARRYING"));
		INITIAL_SUGAR_RADIUS = Double.parseDouble(p.getProperty("INITIAL_SUGAR_RADIUS"));
	
		INITIAL_SUGAR_IN_SOURCE = Integer.parseInt(p.getProperty("INITIAL_SUGAR_IN_SOURCE"));
		ANT_COST = Integer.parseInt(p.getProperty("ANT_COST"));
		STARTING_FOOD = Integer.parseInt(p.getProperty("STARTING_FOOD"));
		
		ANGLE_ERROR_PER_DISTANCE  = Double.parseDouble(p.getProperty("ANGLE_ERROR_PER_DISTANCE"));
		
		TICKS_TO_LIVE = Integer.parseInt(p.getProperty("TICKS_TO_LIVE"));
		TICKS_SUGAR_PICKUP = Integer.parseInt(p.getProperty("TICKS_SUGAR_PICKUP"));
		MINIMUM_DISTANCE_BETWEEN_HILLS = Double.parseDouble(p.getProperty("MINIMUM_DISTANCE_BETWEEN_HILLS"));
		MAXIMUM_STARTING_SUGAR_DISTANCE = Double.parseDouble(p.getProperty("MAXIMUM_STARTING_SUGAR_DISTANCE"));
		MINIMUM_STARTING_SUGAR_DISTANCE = Double.parseDouble(p.getProperty("MINIMUM_STARTING_SUGAR_DISTANCE"));
		MINIMUM_STARTING_SUGAR_DISTANCE_TO_OTHER_HILLS = Double.parseDouble(p.getProperty("MINIMUM_STARTING_SUGAR_DISTANCE_TO_OTHER_HILLS"));
		MINIMUM_SUGAR_DISTANCE = Double.parseDouble(p.getProperty("MINIMUM_SUGAR_DISTANCE"));
		MINIMUM_SUGAR_DISTANCE_TO_OTHER_SUGAR = Double.parseDouble(p.getProperty("MINIMUM_SUGAR_DISTANCE_TO_OTHER_SUGAR"));
		SUGAR_SOURCES_PER_PLAYER = Integer.parseInt(p.getProperty("SUGAR_SOURCES_PER_PLAYER"));
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
