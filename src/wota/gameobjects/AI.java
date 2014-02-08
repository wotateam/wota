/**
 * 
 */
package wota.gameobjects;

import java.util.LinkedList;
import java.util.List;

import wota.gameobjects.GameWorld.Player;
import wota.utility.Modulo;
import wota.utility.Vector;

/**
 * Base class for AntAI and HillAI
 * Contains several lists describing the objects which are visible to it.
 */
public abstract class AI {
	/** Ants which are seen by self */
	public List<Ant> visibleAnts; 
	
	/** Dead Ants = Corpses which are seen by self */
	public List<AntCorpse> visibleCorpses;
	
	/** AntMessages which are heard in this tick */
	public List<AntMessage> audibleAntMessages;
	
	/** HillMessage which is heard in this tick (null if non existant) */
	public HillMessage audibleHillMessage;

	/** Reference to the parameters of the game. e.g. cost of a new ant. */
	protected Parameters parameters;
	
	private Vector position;
		
	void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
	
	/** tick() gets called in every step of the game. 
	 *  The ai has to call methods of AntAI/HillAI/AI to specify the desired action.
	 * @throws Exception Any Exception generated in tick() gets thrown!
	 */
	public abstract void tick() throws Exception;
	
	
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
	 * @param start
	 * @param end
	 * @return the Vector between start and end.
	 */
	protected Vector vectorBetween(Snapshot start, Snapshot end) {
		return parameters.shortestDifferenceOnTorus(end.getPosition(), start.getPosition());
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
	 * returns the Vector between the Ant itself and target
	 * @param target
	 * @return vector between this ant and target
	 */
	protected Vector vectorTo(Snapshot target) {
		return parameters.shortestDifferenceOnTorus(target.getPosition(), position);
	}
	
	void setPosition(Vector position) {
		this.position = position;
	}
}
