package de.wota;

import de.wota.gameobjects.Ant;
import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.Sugar;

/**
 * Gibt die Aktion einer Ant an.
 * Besteht aus Message, Angriffsziel (AntObject) und Bewegungsrichtung / Amplitude
 * 
 * @author pascal
 *
 */
public class Action {
	
	private Message message;
	private Ant attackTarget;
	private Sugar sugarSource;
	/** from 0 to GameWordl.MAX_MOVEMENT_DISTANCE */
	private double movementDistance;
	/** from 0 to 360 */
	private double movementDirection;

	/** do nothing */
	public Action() {
	//	actor = null;
		message = null;
		attackTarget = null;
		sugarSource = null;
		movementDistance = 0;
		movementDirection = 0;
	}
	
	public Action(Message message, Ant attackTarget, Sugar sugarSource,
			double movementDistance, double movementDirection) {
		this.message = message;
		this.attackTarget = attackTarget;
		this.sugarSource = sugarSource;
		this.movementDirection = movementDirection;
		this.movementDistance = movementDistance;
	}
	
	
	public Ant getAttackTarget() {
		return attackTarget;
	}
	
	public Sugar getSugarSource() {
		return sugarSource;
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
	
	/*public AntObject getActor() {
		return actor;
	}*/
	
	/*public void setActor(AntObject actor) {
		this.actor = actor;
	}*/
	
	public void setMessage(Message message) {
		this.message = message;
	}

	public void setAttackTarget(Ant attackTarget) {
		this.attackTarget = attackTarget;
	}
	
	public void setSugarSource(Sugar sugarSource) {
		this.sugarSource = sugarSource;
	}

	public void setMovementDistance(double movementDistance) {
		this.movementDistance = movementDistance;
	}

	public void setMovementDirection(double movementDirection) {
		this.movementDirection = movementDirection;
	}
}
