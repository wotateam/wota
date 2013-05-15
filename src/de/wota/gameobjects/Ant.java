package de.wota.gameobjects;

import de.wota.gameobjects.caste.Caste;
import de.wota.utility.Vector;


/**
 * Beschreibt die Ameisen.
 * Ist nach dem Erzeugen read-only
 * 
 * KI sieht eigene und fremde Ameisen in dieser Form. Wird einmal pro Rund für
 * alle Ameisen erstellt.
 * 
 * @author pascal
 */
public class Ant extends Snapshot{
	
	public final double health;
	public final double speed;
	/** Angriffspunkte */
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
