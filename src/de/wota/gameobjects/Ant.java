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
	
	public final double health;
	public final double speed;
	/** Damage caused when attacking */
	public final double attack;
	public final int sugarCarry;
	public final Caste caste;
	final AntObject antObject; // should only be accessible for objects in the same package
	
	public Ant(AntObject antObject) {
		health = antObject.getHealth();
		speed = antObject.getSpeed();
		attack = antObject.getAttack();
		sugarCarry = antObject.getSugarCarry();
		caste = antObject.getCaste();
		this.antObject = antObject;
	}

	@Override
	Vector getPosition() {
		return antObject.getPosition();
	}
	
}
