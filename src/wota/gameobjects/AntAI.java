package wota.gameobjects;

import java.util.LinkedList;
import java.util.List;

import wota.utility.Vector;


/** 
 * Basisclass for antais by the user.
 * Contains several lists describing the objects which are visible to the ant.
 */
public abstract class AntAI extends AI{		
	// Note [Visibility]
	/*
	 * Everything which is intended to be used by the AI writer should be protected
	 * and come first in this file, to aid someone reading through this file to learn 
	 * how the game works.
	 * Everything else should not be protected, and not public if possible.
	 * Hopefully, this will make generating documentation for AI writers easier.
	 */

	/** Sugar which is seen by self */
	public List<Sugar> visibleSugar;
	
	/** Hills which are seen by self */
	public List<Hill> visibleHills;
	
	/** HillMessage which is heard in this tick (null if non existant) */
	public HillMessage audibleHillMessage;
	
	/** Reference to Ant itself. Use to acces information like health: self.health */
	protected Ant self; // user AI may have changed this value! Use antObject instead.
	
	//-------------------------------------------------------------------------------
	// MOVEMENT
	//-------------------------------------------------------------------------------
	
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
	
	//--------------------------------------------------------
	// NON-MOVEMENT ACTIONS
	//--------------------------------------------------------
	
	/** Attack target of type Ant. Makes you drop sugar.  */
	protected void attack(Ant target) {
		action.attackTarget = target;
		action.dropItem = true;
	}
	
	/** Pick up sugar. Works only if you are close enough to source. */
	protected void pickUpSugar(Sugar source) {
		action.sugarTarget = source;
	}
	
	/** drop sugar e.g. to run faster. Cannot be picked up again. */
	protected void dropSugar() {
		action.dropItem = true;
	}
	
	/** Send message of type int. Can be heard by close friendly ants and hill. */
	protected void talk(int content) {
		talk(content, null);
	}
	
	/** Send message of both int and Snaphshot (Ant, Hill, Sugar, ...) */
	protected void talk(int content, Snapshot snapshot) {
		AntMessage mo = new AntMessage(self.getPosition(), self, content, snapshot, parameters);
			
		action.antMessage = mo;
	}
	
	//--------------------------------------------------------------------
	// INFO ACCESS
	//--------------------------------------------------------------------
	
	/**
	 * get the direction of the last movement in degrees.
	 * @return direction of last movement. 0 if this is the first tick.
	 */
	protected double getLastMovementDirection() {
		return antObject.getLastMovementDirection();
	}
	
	/** gives the shortest vector pointing to home = hill. */ 
	protected Vector vectorToHome() {
		return parameters.shortestDifferenceOnTorus(antObject.player.hillObject.getPosition(), antObject.getPosition());
	}
	
	/** returns true if target is in view range. */
	protected boolean isInView(Snapshot target) {
		return (parameters.distance(target.getPosition(), antObject.getPosition()) <= antObject.caste.SIGHT_RANGE);
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
	
	// ------------------------------------------------------------------------
	// End of methods and fields relevant to AI writers.
	// ------------------------------------------------------------------------
	
	/** CAUTION! THIS METHOD DELETES THE ACTION */
	Action popAction() {
		Action returnAction = action;
		action = new Action();
		return returnAction;
	}
	
	/** Action object contains the things the ant wants to do */
	private Action action = new Action();
	
	/** AntObject includes all information of this Ant */
	private AntObject antObject;
			
	
	/** 
	 * DON'T USE THIS METHOD IF YOU ARE PROGRAMMING THE AI !
	 * 
	 * sets self to ant.
	 */
	void setAnt(Ant ant) {
		self = ant;
	}

	/** 
	 * DON'T USE THIS METHOD IF YOU ARE PROGRAMMING THE AI !
	 */
	void setAntObject(AntObject antObject) {
		this.antObject = antObject;
		setPosition(antObject.getPosition());
	}
	
}
