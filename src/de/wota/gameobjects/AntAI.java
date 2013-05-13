package de.wota.gameobjects;

import java.util.List;

import de.wota.Action;
import de.wota.Message;
import de.wota.utility.Vector;


/** 
 * Basisklasse für die Ant-KI 
 */
public abstract class AntAI {		
	public List<Ant> visibleAnts;
	public List<Sugar> visibleSugar;
	public List<Hill> visibleHills;
	public List<Message> incomingMessages;
	protected Action action = new Action(); // FIXME really protected?
	/** Reference to Ant itself */
	protected Ant self; // user AI may have changed this value!
	private AntObject antObject;
	
	public AntAI() {
		ownHill = antObject.player.hillObject.getHill();
	}
	
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
	
	/** Move in direction of an Object
	 * @param target can be anything like Ant, Sugar, ...
	 */
	protected void moveTo(Snapshot target) {
		action.setMovementTarget(target);
		action.setMovementDistance(GameWorldParameters.MAX_MOVEMENT_DISTANCE);
	}
	
	protected void moveTo(Snapshot target, double distance) {
		action.setMovementTarget(target);
		action.setMovementDistance(distance);
	}
	
	protected void moveHome() {
		action.setMovementTarget(antObject.player.hillObject.getHill());
	}
	
	/** 
	 * returns the Vector between start and end.
	 * Is null if the objects are not in view.
	 * @param start
	 * @param end
	 * @return
	 */
	protected Vector vectorBetween(Snapshot start, Snapshot end) {
		if (isInView(start) && isInView(end)) {
			return Vector.subtract(end.getCoordinates(), start.getCoordinates());
		}
		else
			return null;
	}
	
	// TODO do not use self here! Use AntObject instead!
	
	/** 
	 * returns the Vector between the Ant itself and target
	 * Is null if the target is not in view.
	 * @param start
	 * @param end
	 * @return
	 */
	protected Vector vectorTo(Snapshot target) {
		if (isInView(target)) {
			return Vector.subtract(target.getCoordinates(), self.getCoordinates());
		}
		else
			return null;
	}
	
	/** return true if target is in view range. */
	private boolean isInView(Snapshot target) {
		return (Vector.distanceBetween(target.getCoordinates(), self.getCoordinates()) <= self.caste.SIGHT_RANGE);
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
