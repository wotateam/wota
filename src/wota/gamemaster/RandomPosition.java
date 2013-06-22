package wota.gamemaster;


import java.util.List;

import wota.gameobjects.Parameters;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;

/**
 * Get a random position satisfying some constraint, such as a minimum distance from a list
 * of positions, e.g. the positions of already placed players.
 */
// To understand the implementation, look at position(KindOfPosition) first.
public class RandomPosition {
	// KindOfPosition has two abstract methods, because we want always want to return a position.
	private Vector position(KindOfPosition kind) {
		int numberOfTries = 0;
		Vector v;
		do {
			numberOfTries += 1;
			v = kind.position();
		} while (!kind.isSatisfiedBy(v) && numberOfTries <= MAX_NUMBER_OF_TRIES);
		
		return v;
	}
	
	private final Parameters parameters;
	
	public RandomPosition(Parameters parameters) {
		this.parameters = parameters;
	}
	
	private static final int MAX_NUMBER_OF_TRIES = 1000;
	
	public Vector hillPosition(final List<Vector> otherHillPositions) {
		return position(new KindOfPosition() {
			
			@Override
			public Vector position() {
				return randomPosition();
			}
			
			@Override
			public boolean isSatisfiedBy(Vector v) {
				for (Vector otherHillPosition : otherHillPositions) {
					if (parameters.distance(otherHillPosition, v) < parameters.MINIMUM_DISTANCE_BETWEEN_HILLS) {
						return false;
					}
				}
				return true;
			}
		});
	}
	
	public Vector startingSugarPosition(final Vector hillPosition, 
			final List<Vector> otherSugarPositions, final List<Vector> otherHillPositions) {
		return position(new KindOfPosition() {
			
			@Override
			public Vector position() {
				final double distance = SeededRandomizer.getDouble() * 
					(parameters.MAXIMUM_STARTING_SUGAR_DISTANCE - parameters.MINIMUM_STARTING_SUGAR_DISTANCE) 
					+ parameters.MINIMUM_STARTING_SUGAR_DISTANCE;
				final double direction = SeededRandomizer.getDouble() * 360;
				return Vector.add(hillPosition, Vector.fromPolar(distance, direction));
			}
			
			@Override
			public boolean isSatisfiedBy(Vector v) {
				for (Vector otherSugarPosition : otherSugarPositions) {
					if (parameters.distance(otherSugarPosition, v) < parameters.MINIMUM_STARTING_SUGAR_DISTANCE) {
						return false;
					}
				}
				
				for (Vector otherHillPosition : otherHillPositions) {
					if (parameters.distance(otherHillPosition,v) < parameters.MINIMUM_STARTING_SUGAR_DISTANCE_TO_OTHER_HILLS) {
						return false;
					}
				}
				
				return true;
			}
		});
	}
	
	public Vector sugarPosition(final List<Vector> hillPositions, final List<Vector> sugarPositions) {
		return position(new KindOfPosition() {
			
			@Override
			public Vector position() {
				return randomPosition();
			}
			
			@Override
			public boolean isSatisfiedBy(Vector v) {
				for (Vector hillPosition : hillPositions) {
					if (parameters.distance(hillPosition, v) < parameters.MINIMUM_SUGAR_DISTANCE) {
						return false;
					}
				}
				
				for (Vector sugarPosition : sugarPositions) {
					if (parameters.distance(sugarPosition, v) < parameters.MINIMUM_SUGAR_DISTANCE_TO_OTHER_SUGAR) {
						return false;
					}
				}
				
				return true;
			}
		});	
	}
	
	private Vector randomPosition() {
		return new Vector(SeededRandomizer.getDouble()*parameters.SIZE_X, SeededRandomizer.getDouble()*parameters.SIZE_Y);
	}
	
	private static abstract class KindOfPosition {
		public abstract Vector position();
		public abstract boolean isSatisfiedBy(Vector v);
	}
}
