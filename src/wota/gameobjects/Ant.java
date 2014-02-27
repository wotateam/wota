package wota.gameobjects;

import wota.utility.Vector;


/**
 * Ant as seen by AIs. (In contrast to AntObject which contains all information)
 * After creation it is read-only
 * 
 * This is what an AI gets when it sees any ants. 
 * Created once per round for all the ants.
 * 
 * The purpose of this split up between Ant and AntObject is to provide this Object to 
 * AIs without giving them a reference to the actual AntObject which would change its
 * properties in every tick. 
 */
public class Ant extends BaseAnt {
	
	/** health is decreased by attacking enemies. Ant dies if health reaches 0. */
	public final double health;
	
	/** amount of sugar which is carried */
	public final int sugarCarry;
	
	/** The name of this ant's AI class, not including the package name.*/
	public final String antAIClassName;
	
	/** corresponding physical element of this Ant */ 
	final AntObject antObject; // should only be accessible for objects in the same package
	
	Ant(AntObject antObject) {
		super(antObject);
		health = antObject.getHealth();
		sugarCarry = antObject.getSugarCarry();
		antAIClassName = antObject.getAI().getClass().getSimpleName();
		this.antObject = antObject;
	}
	
	/* (non-Javadoc)
	 * @see wota.gameobjects.Snapshot#hasSameOriginal(wota.gameobjects.Snapshot)
	 */
	@Override
	public boolean hasSameOriginal(Snapshot other) {
		if (other == null) {
			return false;
		}
		if (other instanceof AntCorpse) {
			return ((Ant) other).antObject.equals(this.antObject);
		}
		else {
			return false;
		}
	}
	
}
