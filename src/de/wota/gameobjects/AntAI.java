package de.wota.gameobjects;

import java.util.List;

import de.wota.utility.Vector;


/** 
 * Basisklasse für die Ant-KI 
 */
public abstract class AntAI {		
	public List<Ant> visibleAnts;
	public List<Sugar> visibleSugar;
	public List<Hill> visibleHills;
	public List<Message> incomingMessages;
	private Action action = new Action();
	/** Reference to Ant itself */
	protected Ant self; // user AI may have changed this value! Use antObject instead.
	private AntObject antObject;
			
	void setAntObject(AntObject antObject) {
		this.antObject = antObject;
	}
	
	public abstract void tick() throws Exception;
	
	/** Gets called when Ant dies. */
	public void die() {
		
	}
	
	/** Attack target of type Ant */
	protected void attack(Ant target) {
		action.attackTarget = target;
	}
	
	/** Pick up sugar */
	protected void pickUpSugar(Sugar source) {
		action.sugarTarget = source;
	}
	
	/** Send message of type int */
	protected void talk(int content) {
		action.messageContent = content;
	}
	
	/** Move in certain direction with maximum distance
	 * @param direction measured in degrees (0 = East, 90 = North, 180 = West, 270 = South)
	 */
	protected void moveInDirection(double direction) {
		moveInDirection(direction, GameWorldParameters.MAX_MOVEMENT_DISTANCE);
	}
	
	/** Move in direction with specified distance
	 * @param direction measured in degrees (0 = East, 90 = North, 180 = West, 270 = South)
	 * @param distance distance to move in one tick
	 */
	protected void moveInDirection(double direction, double distance) {
		action.movement = Vector.fromPolar(distance, direction);
	}
	
	/** Move in direction of an Object
	 * @param target can be anything like Ant, Sugar, ...
	 */
	protected void moveTo(Snapshot target) { // TODO rename me to moveTowards
		moveTo(target, GameWorldParameters.MAX_MOVEMENT_DISTANCE);
	}
	
	protected void moveTo(Snapshot target, double distance) {
		action.movement = Vector.subtract(target.getPosition(), antObject.getPosition())
				.scaleTo(distance);
	}
	
	protected void moveHome() {
		moveTo(antObject.player.hillObject.getHill());
	}
	
	/** return true if target is in view range. */
	private boolean isInView(Snapshot target) {
		return (Vector.distanceBetween(target.getPosition(), antObject.getPosition()) <= antObject.getCaste().SIGHT_RANGE);
	}
		
	/** 
	 * returns the Vector between the Ant itself and target
	 * Is null if the target is not in view.
	 * @param start
	 * @param end
	 * @return
	 */
	protected Vector vectorTo(Snapshot target) {
		if (isInView(target)) {
			return Vector.subtract(target.getPosition(), antObject.getPosition());
		}
		else
			return null;
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
			return Vector.subtract(end.getPosition(), start.getPosition());
		}
		else
			return null;
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
	Action popAction() {
		Action returnAction = action;
		action = new Action();
		return returnAction;
	}
}
