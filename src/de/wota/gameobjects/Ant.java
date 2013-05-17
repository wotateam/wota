package de.wota.gameobjects;

import de.wota.utility.Vector;


/**
 * Describes an ant.
 * After creation it is read-only
 * 
 * This is what an AI gets when it sees any ants. 
 * Created once per round for all the ants.
 * 
 * @author pascal
 */
public class Ant extends Snapshot{
	
	/** health is decreased by attacking enemies. Ant dies if health reaches 0. */
	public final double health;
	
	/** distance with which Ants can move each tick */
	public final double speed;
	
	/** Damage caused when attacking */
	public final double attack;
	
	/** amount of sugar which is carried */
	public final int sugarCarry;
	
	/** Caste which this ant belongs to */
	public final Caste caste;
	
	public final int playerID;
	
	/** corresponding physical element of this Ant */ 
	final AntObject antObject; // should only be accessible for objects in the same package
	
	public Ant(AntObject antObject) {
		health = antObject.getHealth();
		speed = antObject.getSpeed();
		attack = antObject.getAttack();
		sugarCarry = antObject.getSugarCarry();
		caste = antObject.getCaste();
		playerID = antObject.id;
		this.antObject = antObject;
	}

	/** returns the vector of this ant */
	@Override
	Vector getPosition() {
		return antObject.getPosition();
	}
	
}
