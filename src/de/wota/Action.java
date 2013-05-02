package de.wota;

import de.wota.gameobjects.Ant;

/**
 * Gibt die Aktion einer Ant an.
 * Besteht aus Message, Angriffsziel (AntObject) und Bewegungsrichtung / Amplitude
 * @author pascal
 *
 */
public class Action {
	
	private Ant actor;
	private Message message;
	private Ant attackTarget;
	/** from 0 to GameWordl.MAX_MOVEMENT_DISTANCE */
	private double movementDistance;
	/** from 0 to 360 */
	private double movementDirection;

	/** do nothing */
	public Action() {
		actor = null;
		message = null;
		attackTarget = null;
		movementDistance = 0;
		movementDirection = 0;
	}
	
	public Action(Message message, Ant attackTarget,
			double movementDistance, double movementDirection) {
		this.message = message;
		this.attackTarget = attackTarget;
		this.movementDirection = movementDirection;
		this.movementDistance = movementDistance;
	}
	
	public Ant getAttackTarget() {
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
	
	public Ant getActor() {
		return actor;
	}
	
	public void attack(Ant target) {
		attackTarget = target;
	}
	
	public void setActor(Ant actor) {
		this.actor = actor;
		if (message != null) {
			message.setSender(actor);
		}
	}
	
	/**
	 * Send a message
	 * @param message content of the message
	 */
	public void talk(int message) {
		this.message = new Message(message);
	}
	/** TODO add Constructors/Setters */
}
