package de.wota.ai;

import de.wota.gameobjects.AntObject;

/**
 * Beschreibt die Ameisen.
 * Ist nach dem Erzeugen read-only
 * 
 * KI sieht eigene und fremde Ameisen in dieser Form. Wird einmal pro Rund für
 * alle Ameisen erstellt.
 * 
 * @author pascal
 */
public class Ant {
	
	public final int ID; // same as for antObject
	private final double health;
	private final double speed;
	/** Angriffspunkte */
	private final double attack;
	
	public Ant(AntObject antObject) {
		health = antObject.getHealth();
		speed = antObject.getSpeed();
		attack = antObject.getAttack();
		ID = antObject.ID;
	}

	public double getHealth() {
		return health;
	}

	public double getSpeed() {
		return speed;
	}

	public double getAttack() {
		return attack;
	}
	
	
}
