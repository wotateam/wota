package de.wota.ai;

import de.wota.Action;
import de.wota.Message;
import de.wota.gameobjects.Ant;
import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.GameWorldParameters;


/** 
 * Basisklasse für die Ant-KI 
 */
public abstract class AntAI extends BaseAI{		
	/** Reference to Ant itself */
	protected Ant self; // user AI may have changed this value!
	
	/** Attack target of type Ant */
	protected void attack(Ant target) {
		action.setAttackTarget(target);
	}
	
	/** Send message of type int */
	protected void talk(int content) {
		Message message = new Message(content);
		action.setMessage(message);
	}
	
	/** Move in certain direction with maximum distance
	 * @param direction measured in degrees (0 = East, 90 = North, 180 = West, 270 = South)
	 */
	protected void moveInDirection(double direction) { // TODO name
		action.setMovementDirection(direction);
		action.setMovementDistance(GameWorldParameters.MAX_MOVEMENT_DISTANCE);
	}
	
	/** Move in direction with specified distance
	 * @param direction measured in degrees (0 = East, 90 = North, 180 = West, 270 = South)
	 * @param distance distance to move in one tick
	 */
	protected void moveInDirection(double direction, double distance) {
		action.setMovementDirection(direction);
		action.setMovementDistance(distance);
	}
	
	public void setAnt(Ant ant) {
		self = ant;
	}
	
	/**
	 * CAUTION! USER AI MAY HAVE CHANGED THIS
	 */
	public Ant getAnt() {
		return self;
	}
}
