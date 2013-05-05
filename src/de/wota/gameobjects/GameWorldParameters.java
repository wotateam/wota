package de.wota.gameobjects;

import de.wota.utility.Vector;
import de.wota.utility.Modulo;

public class GameWorldParameters {
	public static final double MAX_MOVEMENT_DISTANCE = 5;
	public static final double SIZE_X = 1000;
	public static final double SIZE_Y = 1000;
	public static final double HILL_RADIUS = 20;
	
	public class Gatherer {
		public static final double ANT_HEALTH_INIT = 100;
		public static final double ANT_SPEED = 1;
		/** Angriffspunkte */
		public static final double ANT_ATTACK = 5;
		public static final int MAX_SUGAR_CARRY = 10;
	}
	
	public class Soldier {
		public static final double ANT_HEALTH_INIT = 100;
		public static final double ANT_SPEED = 0.5;
		/** Angriffspunkte */
		public static final double ANT_ATTACK = 10;
		public static final int MAX_SUGAR_CARRY = 10;
	}
	
	public class Queen {
		public static final double ANT_HEALTH_INIT = 1000;
		public static final double ANT_SPEED = 0;
		/** Angriffspunkte */
		public static final double ANT_ATTACK = 0;
		public static final int MAX_SUGAR_CARRY = 0;
	}
	
	public static Vector normalize(Vector p) {
		Vector r = new Vector(p);
		r.x = Modulo.mod(r.x, GameWorldParameters.SIZE_X);
		r.y = Modulo.mod(r.y, GameWorldParameters.SIZE_Y);
		return r;
	}
	
	
	/**
	 * Assumes that p1 and p2 are in the fundamental region.
	 * @param p1
	 * @param p2
	 * @return The shortest vector from p1 to a point equivalent to p2.
	 */
	public static Vector shortestDifferenceOnTorus(Vector p1, Vector p2) {
		Vector d = Vector.add(p1, Vector.scale(-1,p2));
		if (d.x > SIZE_X) {
			d.x = SIZE_X - d.x; 
		}
		if (d.y > SIZE_Y) {
			d.y = SIZE_Y - d.y; 
		}
		return d;
	}
}
