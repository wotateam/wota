package de.wota.gameobjects;

import java.util.LinkedList;
import java.util.List;

import de.wota.utility.Modulo;
import de.wota.utility.Vector;


/** 
 * Basisclass for ais by the user.
 * Contains several lists describing the objects which are visible to the ant.
 */
public abstract class AntAI {		
	/** Ants which are seen by self */
	public List<Ant> visibleAnts; 
	
	/** Sugar which is seen by self */
	public List<Sugar> visibleSugar;
	
	/** Hills which are seen by self */
	public List<Hill> visibleHills;
	
	/** Messages which are heard in this tick */
	public List<Message> audibleMessages;
	
	/** Reference to Ant itself */
	protected Ant self; // user AI may have changed this value! Use antObject instead.
	
	/** Reference to the parameters of the game. e.g. cost of a new ant. */
	protected Parameters parameters;
	
	/** Action object contains the things the ant wants to do */
	private Action action = new Action();
	
	/** AntObject includes all information of this Ant */
	private AntObject antObject;
			
	
	void setAntObject(AntObject antObject) {
		this.antObject = antObject;
	}
	
	void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
	
	/** tick() gets called in every step of the game. 
	 *  The ai has to call methods of AntAI to specify the desired action.
	 * @throws Exception Any Exception generated in tick() gets thrown!
	 */
	public abstract void tick() throws Exception;
	
	/** get a List of visible Ants of the own tribe */
	protected List<Ant> visibleFriends() {
		LinkedList<Ant> output = new LinkedList<Ant>();
		for (Ant ant : visibleAnts) {
			if (ant.playerID == antObject.player.getId()) {
				output.add(ant);
			}
		}
		return output;
	}
	
	/** get a List of visible Ants of all enemy tribes */
	protected List<Ant> visibleEnemies() {
		LinkedList<Ant> output = new LinkedList<Ant>();
		for (Ant ant : visibleAnts) {
			if (ant.playerID != antObject.player.getId()) {
				output.add(ant);
			}
		}
		return output;
	}
	
	/** 
	 * get a List of visible Ants of a specific player
	 * 
	 * @param playerId id of the specific player
	 */
	protected List<Ant> visibleAntsOfPlayer(int playerId) {
		LinkedList<Ant> output = new LinkedList<Ant>();
		for (Ant ant : visibleAnts) {
			if (ant.playerID == playerId) {
				output.add(ant);
			}
		}
		return output;
	}
	
	/**
	 * Determines the object of a list of candidates which is closest to the player. 
	 * @param toConsider list of Snapshots (e.g. Ants, Sugars, Hills) from which the closest gets chosen
	 * @return null if toConsider is empty, otherwise the closest in toConsider
	 */
	protected <T extends Snapshot> T closest(List<T> toConsider) {
		T closest = null;
		double distance = Double.MAX_VALUE;
		for (T current : toConsider) {
			if (vectorTo(current).length() < distance) {
				closest = current;
				distance = vectorTo(current).length();
			}
		}
		return closest;
	}
	
	/**
	 * get the direction of the last movement.
	 * @return
	 * 			direction of last movement. 0 if this is the first tick.
	 */
	public double getLastMovementDirection() {
		return antObject.getLastMovementDirection();
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
		MessageObject mo = new MessageObject(self.getPosition(), self, content, parameters);
			
		action.messageObject = mo;
	}
	
	/** Move in certain direction with maximum distance
	 * @param direction measured in degrees (0 = East, 90 = North, 180 = West, 270 = South)
	 */
	protected void moveInDirection(double direction) {
		moveInDirection(direction, parameters.SIZE_X/2);
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
		moveToward(target, parameters.SIZE_X/2);
	}
	
	/** Move in direction of target but only the specified distance.
	 * @param target Target to move towards.
	 */
	protected void moveToward(Snapshot target, double distance) {
		if (isInView(target)) {
			uncheckedMoveToward(target, distance);
		}
	}
	
	/**
	 * Move in the same direction like last tick.
	 * Moves in direction 0 if this is the first tick.
	 */
	protected void moveAhead() {
		moveInDirection(antObject.getLastMovementDirection());
	}

	private void uncheckedMoveToward(Snapshot target) {
		uncheckedMoveToward(target, parameters.SIZE_X);
	}
	
	private void uncheckedMoveToward(Snapshot target, double distance) {
		action.movement = parameters.shortestDifferenceOnTorus(target.getPosition(), antObject.getPosition()).boundLengthBy(distance);
	}
	
	/**
	 * Move maximal distance in direction of the own hill, even if it is not visible. 
	 */
	protected void moveHome() {
		uncheckedMoveToward(antObject.player.hillObject.getHill());
	}
	
	protected Vector vectorToHome() {
		return parameters.shortestDifferenceOnTorus(antObject.player.hillObject.getPosition(), antObject.getPosition());
	}
	
	/** returns true if target is in view range. */
	private boolean isInView(Snapshot target) {
		return (parameters.distance(target.getPosition(), antObject.getPosition()) <= antObject.getCaste().SIGHT_RANGE);
	}
	
	/** 
	 * returns the Vector between the Ant itself and target
	 * Is null if the target is not in view.
	 * @param target
	 * @return vector between this ant and target
	 */
	protected Vector vectorTo(Snapshot target) {
		if (isInView(target)) {
			return parameters.shortestDifferenceOnTorus(target.getPosition(), antObject.getPosition());
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
			return parameters.shortestDifferenceOnTorus(end.getPosition(), start.getPosition());
		}
		else {
			return null;
		}
	}
	
	/** 
	 * DON'T USE THIS METHOD IF YOU ARE PROGRAMMING THE AI !
	 * 
	 * sets self to ant.
	 */
	public void setAnt(Ant ant) {
		self = ant;
	}
	
	/** CAUTION! THIS METHOD DELETES THE ACTION */
	Action popAction() {
		Action returnAction = action;
		action = new Action();
		return returnAction;
	}
	
	/**
	 * Modulo operation returning only non-negative numbers.
	 * @return x mod m
	 */
	public static int mod(int x, int m) {
		return Modulo.mod(x, m);
	}
	

	/**
	 * Modulo operation returning only non-negative numbers.
	 * @return x mod m
	 */
	public static double mod(double x, double m) {
		return Modulo.mod(x, m);
	}
}
