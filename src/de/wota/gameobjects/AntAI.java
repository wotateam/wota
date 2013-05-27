package de.wota.gameobjects;

import java.util.LinkedList;
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
	public List<Message> audibleMessages;
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
	
	protected List<Ant> visibleFriends() {
		LinkedList<Ant> output = new LinkedList<Ant>();
		for (Ant ant : visibleAnts) {
			if (ant.playerID == antObject.player.getId()) {
				output.add(ant);
			}
		}
		return output;
	}
	
	protected List<Ant> visibleEnemies() {
		LinkedList<Ant> output = new LinkedList<Ant>();
		for (Ant ant : visibleAnts) {
			if (ant.playerID != antObject.player.getId()) {
				output.add(ant);
			}
		}
		return output;
	}
	
	protected List<Ant> visibleAntsOfPlayer(int playerId) {
		LinkedList<Ant> output = new LinkedList<Ant>();
		for (Ant ant : visibleAnts) {
			if (ant.playerID == playerId) {
				output.add(ant);
			}
		}
		return output;
	}
	
	protected Ant getClosestAnt(List<Ant> antsToConsider) {
		Ant closest = null;
		double distance = Double.MAX_VALUE;
		for (Ant ant : antsToConsider) {
			if (vectorTo(ant).length() < distance) {
				closest = ant;
				distance = vectorTo(ant).length();
			}
		}
		return closest;
	}
	
	/** Attack target of type Ant */
	protected void attack(Ant target) {
		action.attackTarget = target;
		action.dropItem = true;
	}
	
	/** Pick up sugar */
	protected void pickUpSugar(Sugar source) {
		action.sugarTarget = source;
	}
	
	/** Drop sugar if some is carried. Can not be undone. */
	protected void dropSugar() {
		action.dropItem = true;
	}
	
	/** Send message of type int */
	protected void talk(int content) {
		MessageObject mo = new MessageObject(
											self.getPosition(),
											self,
											content);
			
		action.messageObject = mo;
	}
	
	/** Move in certain direction with maximum distance
	 * @param direction measured in degrees (0 = East, 90 = North, 180 = West, 270 = South)
	 */
	protected void moveInDirection(double direction) {
		moveInDirection(direction, Parameters.MAX_MOVEMENT_DISTANCE);
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
	protected void moveToward(Snapshot target) {
		moveToward(target, Parameters.MAX_MOVEMENT_DISTANCE);
	}
	
	/** Move in direction of target but only the specified distance.
	 * @param target Target to move towards.
	 */
	protected void moveToward(Snapshot target, double distance) {
		if (isInView(target)) {
			uncheckedMoveToward(target, distance);
		}
	}

	private void uncheckedMoveToward(Snapshot target) {
		uncheckedMoveToward(target, Parameters.MAX_MOVEMENT_DISTANCE);
	}
	
	private void uncheckedMoveToward(Snapshot target, double distance) {
		action.movement = Parameters.shortestDifferenceOnTorus(target.getPosition(), antObject.getPosition()).boundLengthBy(distance);
	}
	
	/**
	 * Move maximal distance in direction of the own hill, even if it is not visible. 
	 */
	protected void moveHome() {
		uncheckedMoveToward(antObject.player.hillObject.getHill());
	}
	
	protected double getHomeDirection() {
		return Parameters.shortestDifferenceOnTorus(antObject.player.hillObject.getPosition(), antObject.getPosition()).angle();
	}
	
	/** returns true if target is in view range. */
	private boolean isInView(Snapshot target) {
		return (Parameters.distance(target.getPosition(), antObject.getPosition()) <= antObject.getCaste().SIGHT_RANGE);
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
			return Parameters.shortestDifferenceOnTorus(target.getPosition(), antObject.getPosition());
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
			return Parameters.shortestDifferenceOnTorus(end.getPosition(), start.getPosition());
		}
		else {
			return null;
		}
	}
	
	public void setAnt(Ant ant) {
		self = ant;
	}
	
	/** CAUTION! THIS METHOD DELETES THE ACTION */
	Action popAction() {
		Action returnAction = action;
		action = new Action();
		return returnAction;
	}
}
