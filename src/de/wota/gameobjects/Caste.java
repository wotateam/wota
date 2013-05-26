package de.wota.gameobjects;

/**
 * Contains the constants of different castes
 */
public enum Caste {
	Gatherer(
			100, // INITIAL_HEALTH
			1.0, // SPEED
			0.5, // SPEED_WHILE_ATTACKING TODO Gatherer SPEED_WHILE_ATTACKING too high?
			0.5, // SPEED_WHILE_CARRYING_SUGAR
			5,	 // ATTACK
			10,	 // MAX_SUGAR_CARRY
			50,  // SIGHT_RANGE
			50   // HEARING_RANGE
	),

	Soldier(
			100, // INITIAL_HEALTH
			1.0, // SPEED
			0.5, // SPEED_WHILE_ATTACKING
			0.5, // SPEED_WHILE_CARRYING_SUGAR
			10,  // ATTACK
			5,   // MAX_SUGAR_CARRY
			50,  // SIGHT_RANGE
			50   // HEARING_RANGE
	),
	
	Scout(
			100, // INITIAL_HEALTH
			1.5, // SPEED
			1.5, // SPEED_WHILE_ATTACKING
			0.5, // SPEED_WHILE_CARRYING_SUGAR
			0.0, // ATTACK
			0,	 // MAX_SUGAR_CARRY
			100, // SIGHT_RANGE 
			100  // HEARING_RANGE TODO should hearing range == sight range?
	),

	Queen(
			Integer.MAX_VALUE, // INITIAL_HEALTH TODO
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
