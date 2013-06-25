package wota.gameobjects;

import wota.utility.Vector;


/**
 * Describes an ant.
 * After creation it is read-only
 * 
 * This is what an AI gets when it sees any ants. 
 * Created once per round for all the ants.
 * 
 * @author pascal
 */
public class Ant implements Snapshot {
	
	/** health is decreased by attacking enemies. Ant dies if health reaches 0. */
	public final double health;
	
	/** distance with which Ants can move each tick */
	public final double speed;
		
	/** amount of sugar which is carried */
	public final int sugarCarry;
	
	/** Caste which this ant belongs to */
	public final Caste caste;
	
	/** The name of this ant's AI class, not including the package name.*/
	public final String antAIClassName;
	
	/** id of this ant */
	public final int id;
	
	/** id of the player which this ant belongs to */
	public final int playerID;
	
	/** corresponding physical element of this Ant */ 
	final AntObject antObject; // should only be accessible for objects in the same package
	
	Ant(AntObject antObject) {
		health = antObject.getHealth();
		speed = antObject.getSpeed();
		sugarCarry = antObject.getSugarCarry();
		caste = antObject.getCaste();
		antAIClassName = antObject.getAI().getClass().getSimpleName();
		id = antObject.id;
		playerID = antObject.player.id();
		this.antObject = antObject;
	}

	/** returns the vector of this ant */
	@Override
	public Vector getPosition() {
		return antObject.getPosition();
	}
	
	/* (non-Javadoc)
	 * @see wota.gameobjects.Snapshot#hasSameOriginal(wota.gameobjects.Snapshot)
	 */
	@Override
	public boolean hasSameOriginal(Snapshot other) {
		if (other instanceof Ant) {
			return ((Ant) other).antObject.equals(this.antObject);
		}
		else {
			return false;
		}
	}
	
}
