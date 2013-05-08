package de.wota;

import de.wota.gameobjects.Ant;
import de.wota.gameobjects.MessageObject;
import de.wota.gameobjects.Sugar;

/**
 * Gibt die Aktion einer Ant an.
 * Besteht aus Message, Angriffsziel (AntObject) und Bewegungsrichtung / Amplitude
 * 
 * @author pascal
 *
 */
public class Action {
	public static final int NO_MESSAGE = -1;
	private int messageContent;
	private Ant attackTarget;
	private Sugar sugarSource;
	/** from 0 to GameWordl.MAX_MOVEMENT_DISTANCE */
	private double movementDistance;
	/** from 0 to 360 */
	private double movementDirection;
	// See comment for setMessageObject.
	private MessageObject messageObject;

	/** do nothing */
	public Action() {
	//	actor = null;
		messageContent = NO_MESSAGE;
		attackTarget = null;
		sugarSource = null;
		movementDistance = 0;
		movementDirection = 0;
	}
	
	/**
	 * In contrast to other setters, this setter is not intended to be called by methods callable
	 * from AntAI.tick. Instead, it is intended to called afterwards using messageContent.
	 * @param messageObject
	 */
	public void setMessageObject(MessageObject messageObject) {
		this.messageObject = messageObject;
	}
	
	public MessageObject getMessageObject() {
		return messageObject;
	}

	/**
	 * 
	 * @param messageContent Must be non-negative. Negative values are reserved.
	 * @param attackTarget
	 * @param sugarSource
	 * @param movementDistance
	 * @param movementDirection
	 */
	public Action(int messageContent, Ant attackTarget, Sugar sugarSource,
			double movementDistance, double movementDirection) {
		if (messageContent < 0) {
			throw new Error("messageContent < 0");
		}
		this.messageContent = messageContent;
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

	public int getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(int messageContent) {
		this.messageContent = messageContent;
	}
}
