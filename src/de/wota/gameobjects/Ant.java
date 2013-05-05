package de.wota.gameobjects;


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
	
	private final double health;
	private final double speed;
	/** Angriffspunkte */
	private final double attack;
	final AntObject antObject; // should only be accessible for objects in the same package
	
	public Ant(AntObject antObject) {
		health = antObject.getHealth();
		speed = antObject.getSpeed();
		attack = antObject.getAttack();
		this.antObject = antObject;
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
	
	public enum Caste {
		GATHERER, SOLDIER, QUEEN
	}
}
