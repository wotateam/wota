package de.wota.gameobjects;

import java.util.List;

import de.wota.utility.Vector;


/** 
 * Basisclass for ais by the user.
 * Contains several lists describing the objects which are visible to the ant.
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
	
	/** tick() gets called in every step of the game. 
	 *  The ai has to call methods of AntAI to specify the desired action.
	 * @throws Exception Any Exception generated in tick() gets thrown!
	 */
	public abstract void tick() throws Exception;
	
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
		MessageObject mo = new MessageObject(
											self.getPosition(),
											self,
											content);
			
		action.setMessageObject(mo);
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
	 *  Stops when target is reached.
	 * @param target can be anything like Ant, Sugar, ...
	 */
	protected void moveTowards(Snapshot target) {
		moveTowards(target, Math.min(GameWorldParameters.MAX_MOVEMENT_DISTANCE,
								Vector.distanceBetween(target.getPosition(), antObject.getPosition()))
			   );
	}
	
	/** Move in direction of target but only the specified distance.
	 * @param target Target to move to.
	 */
	protected void moveTowards(Snapshot target, double distance) {
		action.movement = Vector.subtract(target.getPosition(), antObject.getPosition())
				.scaleTo(distance);
	}
	
	/**
	 * Move maximal distance in direction of the own hill.
	 */
	protected void moveHome() {
		moveTowards(antObject.player.hillObject.getHill());
	}
	
	/** returns true if target is in view range. */
	private boolean isInView(Snapshot target) {
		return (Vector.distanceBetween(target.getPosition(), antObject.getPosition()) <= antObject.getCaste().SIGHT_RANGE);
	}
		
	/** 
	 * returns the Vector between the Ant itself and target
	 * Is null if the target is not in view.
	 * @param start
	 * @param end
	 * @return vector between this ant and target
	 */
	protected Vector vectorTo(Snapshot target) {
		if (isInView(target)) {
			return Vector.subtract(target.getPosition(), antObject.getPosition());
		}
		else
			return null;
	}
	
	/** 
	 * Is null if the targets are not in view.
	 * @param start
	 * @param end
	 * @return the Vector between start and end.
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
