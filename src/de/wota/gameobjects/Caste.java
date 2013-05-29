package de.wota.gameobjects;

/**
 * Contains the constants of different castes
 * 
 * Normierung so, dass
 * 100 ticks = einmal die Karte laufen für einen Arbeiter ohne Nahrung
 * 5 ticks = einmal Radius der Sichtbarkeit durchlaufen
 * 1 tick laufen = Angriffsreichweite
 * Soldier vs. Soldier: Kampf dauert 5 Spielfeldlängen = 500 ticks
 * Soldier vs. Gatherer: noch nicht festgelegt (es gibt VULNERABILITY_WHILE_CARRYING in Parameters)
 */
public enum Caste {
	Gatherer(
			40.0, // INITIAL_HEALTH
			10.0, // SPEED
			5.0, // SPEED_WHILE_ATTACKING 
			5.0, // SPEED_WHILE_CARRYING_SUGAR
			0.15,	 // ATTACK
			10,	 // MAX_SUGAR_CARRY
			50,  // SIGHT_RANGE
			50   // HEARING_RANGE
	),

	Soldier(
			100.0, // INITIAL_HEALTH
			10.0, // SPEED
			5.0, // SPEED_WHILE_ATTACKING
			5.0, // SPEED_WHILE_CARRYING_SUGAR
			0.2,  // ATTACK
			5,   // MAX_SUGAR_CARRY
			50,  // SIGHT_RANGE
			50   // HEARING_RANGE
	),
	
	Scout(
			100, // INITIAL_HEALTH
			15.0, // SPEED
			15.0, // SPEED_WHILE_ATTACKING
			5.0, // SPEED_WHILE_CARRYING_SUGAR
			0.0, // ATTACK
			0,	 // MAX_SUGAR_CARRY
			100, // SIGHT_RANGE 
			100  // HEARING_RANGE TODO should hearing range == sight range?
	),

	Queen(
			Integer.MAX_VALUE, // INITIAL_HEALTH 
			0, 	// SPEED
			0, // SPEED_WHILE_ATTACKING
			0, // SPEED_WHILE_CARRYING_SUGAR
			0,	// ATTACK
			0,	// MAX_SUGAR_CARRY
			50, // SIGHT_RANGE
			50 // HEARING_RANGE
	);
	
	public final double INITIAL_HEALTH;
	public final double SPEED;
	public final double SPEED_WHILE_ATTACKING;
	public final double SPEED_WHILE_CARRYING_SUGAR;
	/** Damage caused when attacking */
	public final double ATTACK;
	public final int MAX_SUGAR_CARRY;	
	public final double SIGHT_RANGE;
	public final double HEARING_RANGE;
	
	private Caste(
			double INITIAL_HEALTH, 
			double SPEED, 
			double SPEED_WHILE_ATTACKING, 
			double SPEED_WHILE_CARRYING_SUGAR, 
			double ATTACK,
			int MAX_SUGAR_CARRY, 
			double SIGHT_RANGE, 
			double HEARING_RANGE) {
		this.INITIAL_HEALTH = INITIAL_HEALTH;
		this.SPEED = SPEED;
		this.SPEED_WHILE_ATTACKING = SPEED_WHILE_ATTACKING;
		this.SPEED_WHILE_CARRYING_SUGAR = SPEED_WHILE_CARRYING_SUGAR;
		this.ATTACK = ATTACK;
		this.MAX_SUGAR_CARRY = MAX_SUGAR_CARRY;
		this.SIGHT_RANGE = SIGHT_RANGE;
		this.HEARING_RANGE = HEARING_RANGE;
	}
}
