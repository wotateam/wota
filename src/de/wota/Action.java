package de.wota;

/**
 * Gibt die Aktion einer Ant an.
 * Besteht aus Message, Angriffsziel (AntObject) und Bewegungsrichtung / Amplitude
 * @author pascal
 *
 */
public class Action {
	
	private Message message;
	private AntObject attackTarget;
	/** from 0 to GameWordl.MAX_MOVEMENT_DISTANCE */
	private double movementDistance;
	/** from 0 to 360 */
	private double movementDirection;

	/** do nothing */
	public Action() {
		message = null;
		attackTarget = null;
		movementDistance = 0;
		movementDirection = 0;
	}
	
	public Action(Message message, AntObject attackTarget,
			double movementDistance, double movementDirection) {
		this.message = message;
		this.attackTarget = attackTarget;
		this.movementDirection = movementDirection;
		this.movementDistance = movementDistance;
	}
	
	public AntObject getAttackTarget() {
		return attackTarget;
	}

	public double getMovementDistance() {
		return movementDistance;
	}

	public double getMovementDirection() {
		return movementDirection;
	}
	
	public Message getMessage() {
		return message;
	}
	
	/** TODO add Constructors/Setters */
}
