package de.wota;

import de.wota.gameobjects.Ant;
import de.wota.gameobjects.MessageObject;
import de.wota.gameobjects.Sugar;
import de.wota.gameobjects.Snapshot;
import de.wota.utility.Vector;

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
	/** sugarSource where sugar should be picked up */
	private Sugar sugarSource;
	
	// MOVEMENT either distance and direction or a target have to be specified
	/** Type of Movement: TARGET or DIRECTION */
	private MovementType movementType;
	/** from 0 to GameWordl.MAX_MOVEMENT_DISTANCE */
	private double movementDistance;
	/** from 0 to 360 */
	private double movementDirection;
	/** Target for Movement */
	private Snapshot movementTarget;
	// See comment for setMessageObject.
	private MessageObject messageObject;

	/** do nothing */
	public Action() {
	//	actor = null;
		messageContent = NO_MESSAGE;
		attackTarget = null;
		sugarSource = null;
		movementType = MovementType.DIRECTION;
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
	
	public MovementType getMovementType() {
		return movementType;
	}
	
	/** Constructor without movement */
	private Action(int messageContent, Ant attackTarget, Sugar sugarSource) {
		if (messageContent < 0) {
			throw new Error("messageContent < 0");
		}
		this.messageContent = messageContent;
		this.attackTarget = attackTarget;
		this.sugarSource = sugarSource;
	}

	/**
	 * Constructor for movement by direction
	 * 
	 * @param messageContent Must be non-negative. Negative values are reserved.
	 * @param attackTarget
	 * @param sugarSource
	 * @param movementDistance
	 * @param movementDirection
	 */
	public Action(int messageContent, Ant attackTarget, Sugar sugarSource,
			double movementDistance, double movementDirection) {
		
		// call constructor without movement
		this(messageContent, attackTarget, sugarSource); 

		// movement by direction
		this.movementType = MovementType.DIRECTION;
		this.movementDirection = movementDirection;
		this.movementDistance = movementDistance;
	}
	
	/**
	 * Constructor for movement by Target
	 * @param messageContent
	 * @param attackTarget
	 * @param sugarSource
	 * @param movementTarget
	 */
	public Action(int messageContent, Ant attackTarget, Sugar sugarSource,
			double movementDistance, Snapshot movementTarget) {
		// call constructor without movement
		this(messageContent, attackTarget, sugarSource); 

		// movement by target
		this.movementType = MovementType.TARGET;
		this.movementDistance = movementDistance;
		this.movementTarget = movementTarget;
	}
	
	public void setMovementTarget(Snapshot target) {
		this.movementTarget = target;
		this.movementType = MovementType.TARGET;
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
		this.movementType = MovementType.DIRECTION;
		this.movementDirection = movementDirection;
	}

	public int getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(int messageContent) {
		this.messageContent = messageContent;
	}
	
	public enum MovementType {
		DIRECTION, TARGET
	}

	public Snapshot getMovementTarget() {
		return movementTarget;
	}
}
