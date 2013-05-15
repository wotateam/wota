package de.wota.gameobjects;

/**
 * Contains the constants of different castes
 */
public enum Caste {
	Gatherer(
			100, // INITIAL_HEALTH
			1, 	// SPEED
			5,	// ATTACK
			10,	// MAX_SUGAR_CARRY
			50, // SIGHT_RANGE
			50 // HEARING_RANGE
	),

	Soldier(
			100, // INITIAL_HEALTH
			0.5, 	// SPEED
			10,	// ATTACK
			5,	// MAX_SUGAR_CARRY
			50, // SIGHT_RANGE
			50 // HEARING_RANGE
	),

	Queen(
			1000, // INITIAL_HEALTH
			0, 	// SPEED
			0,	// ATTACK
			0,	// MAX_SUGAR_CARRY
			50, // SIGHT_RANGE
			50 // HEARING_RANGE
	);
	
	public final double INITIAL_HEALTH;
	public final double SPEED;
	/** Angriffspunkte */
	public final double ATTACK;
	public final int MAX_SUGAR_CARRY;	
	public final double SIGHT_RANGE;
	public final double HEARING_RANGE;
	
	private Caste(double INITIAL_HEALTH, double SPEED, double ATTACK,
			int MAX_SUGAR_CARRY, double SIGHT_RANGE, double HEARING_RANGE) {
		this.INITIAL_HEALTH = INITIAL_HEALTH;
		this.SPEED = SPEED;
		this.ATTACK = ATTACK;
		this.MAX_SUGAR_CARRY = MAX_SUGAR_CARRY;
		this.SIGHT_RANGE = SIGHT_RANGE;
		this.HEARING_RANGE = HEARING_RANGE;
	}
}
