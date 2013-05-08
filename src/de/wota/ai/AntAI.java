package de.wota.ai;

import java.util.List;

import de.wota.Action;
import de.wota.Message;
import de.wota.gameobjects.Ant;
import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.GameWorldParameters;
import de.wota.gameobjects.Sugar;


/** 
 * Basisklasse für die Ant-KI 
 */
public abstract class AntAI {		
	public List<Ant> visibleAnts;
	public List<Sugar> visibleSugar;
	public List<Message> incomingMessages;
	protected Action action = new Action(); // FIXME really protected?
	/** Reference to Ant itself */
	protected Ant self; // user AI may have changed this value!
	
	public abstract void tick();
	
	/** Gets called when Ant dies. */
	public void die() {
		
	}
	
	/** Attack target of type Ant */
	protected void attack(Ant target) {
		action.setAttackTarget(target);
	}
	
	/** Pick up sugar */
	protected void pickUpSugar(Sugar source) {
		action.setSugarSource(source);
	}
	
	/** Send message of type int */
	protected void talk(int content) {
		action.setMessageContent(content);
	}
	
	/** Move in certain direction with maximum distance
	 * @param direction measured in degrees (0 = East, 90 = North, 180 = West, 270 = South)
	 */
	protected void moveInDirection(double direction) {
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
	
	/** CAUTION! THIS METHOD DELETES THE ACTION */
	public Action popAction() {
		Action returnAction = action;
		action = new Action();
		return returnAction;
	}
}
