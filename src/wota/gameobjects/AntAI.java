package wota.gameobjects;

import java.util.LinkedList;
import java.util.List;

import wota.utility.Modulo;
import wota.utility.Vector;


/** 
 * Basisclass for antais by the user.
 * Contains several lists describing the objects which are visible to the ant.
 */
public abstract class AntAI extends AI{		
	
	/** Reference to Ant itself */
	protected Ant self; // user AI may have changed this value! Use antObject instead.
		
	/** Action object contains the things the ant wants to do */
	private Action action = new Action();
	
	/** AntObject includes all information of this Ant */
	private AntObject antObject;
			
	
	/** 
	 * DON'T USE THIS METHOD IF YOU ARE PROGRAMMING THE AI !
	 * 
	 * sets self to ant.
	 */
	public void setAnt(Ant ant) {
		self = ant;
	}

	/** 
	 * DON'T USE THIS METHOD IF YOU ARE PROGRAMMING THE AI !
	 */
	void setAntObject(AntObject antObject) {
		this.antObject = antObject;
		setPosition(antObject.getPosition());
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
		talk(content, null);
	}
	
	/** Send message of combined int with Snaphshot (Ant, Hill, Sugar, ...) */
	protected void talk(int content, Snapshot snapshot) {
		AntMessageObject mo = new AntMessageObject(self.getPosition(), self, content, snapshot, parameters);
			
		action.antMessageObject = mo;
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
	
	/** Move toward absolute position on the map.
	 * Stops when position is reached.
	 * @param target absolute coordinates, NOT relatve to Ant
	 */
	protected void moveToward(Vector target) {
		action.movement = parameters.shortestDifferenceOnTorus(target, self.getPosition());
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
		action.movement = parameters.shortestDifferenceOnTorus(target.getPosition(), antObject.getPosition()).boundLengthBy(distance);
	}
	
	/**
	 * Move in the same direction like last tick.
	 * Moves in direction 0 if this is the first tick.
	 */
	protected void moveAhead() {
		moveInDirection(antObject.getLastMovementDirection());
	}
	
	/**
	 * Move maximal distance in direction of the own hill, even if it is not visible. 
	 */
	protected void moveHome() {
		moveToward(antObject.player.hillObject.getHill());
	}

	protected Vector vectorToHome() {
		return parameters.shortestDifferenceOnTorus(antObject.player.hillObject.getPosition(), antObject.getPosition());
	}
	
	/** returns true if target is in view range. */
	public boolean isInView(Snapshot target) {
		return (parameters.distance(target.getPosition(), antObject.getPosition()) <= antObject.getCaste().SIGHT_RANGE);
	}
	
	/** get a List of visible Ants of the own tribe */
	protected List<Ant> visibleFriends() {
		LinkedList<Ant> output = new LinkedList<Ant>();
		for (Ant ant : visibleAnts) {
			if (ant.playerID == antObject.player.id()) {
				output.add(ant);
			}
		}
		return output;
	}
	
	/** get a List of visible Ants of all enemy tribes */
	protected List<Ant> visibleEnemies() {
		LinkedList<Ant> output = new LinkedList<Ant>();
		for (Ant ant : visibleAnts) {
			if (ant.playerID != antObject.player.id()) {
				output.add(ant);
			}
		}
		return output;
	}
	
	/** CAUTION! THIS METHOD DELETES THE ACTION */
	Action popAction() {
		Action returnAction = action;
		action = new Action();
		return returnAction;
	}
}
