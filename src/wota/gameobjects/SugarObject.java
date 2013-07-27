package wota.gameobjects;

import java.util.LinkedList;

import wota.utility.Vector;


/**
 * A sugar source. 
 * 
 * Sugar can be picked up by first come first serve principle.
 *
 */
public class SugarObject extends GameObject {
	
	private int amount; 
	private Sugar sugar;
	/** List of Ants which wait to receive sugar */
	private LinkedList<AntObject> serviceQueue = new LinkedList<AntObject>();
	/** number of ticks an ant will freeze when picking up */
	private int ticksToNextService;
	private boolean isInSpacePartitioning = true; 
	
	public SugarObject(Vector position, Parameters parameters) {
		super(position, parameters);
		ticksToNextService = parameters.TICKS_SUGAR_PICKUP;
		amount = parameters.INITIAL_SUGAR_IN_SOURCE;
	}
	
	public boolean isInSpacePartitioning() {
		return isInSpacePartitioning;
	}
	
	public void setIsInSpacePartitioning(boolean isInSpacePartitioning) {
		this.isInSpacePartitioning = isInSpacePartitioning;
	}
	
	public void createSugar() {
		this.sugar = new Sugar(this);
	}
	
	public Sugar getSugar() {
		return sugar;
	}
	
	public int getAmount() {
		return amount;
	}
	
	/** 
	 * @return the number of AntObjects waiting in the serviceQueue
	 */
	public int getQueueSize() {
		return serviceQueue.size();
	}
	
	public void tick() {
		if ( !serviceQueue.isEmpty() ) {
			ticksToNextService--;
			if (ticksToNextService == 0) {
				AntObject theServiced = serviceQueue.removeFirst();
				theServiced.unsetSugarTarget();
				ticksToNextService = parameters.TICKS_SUGAR_PICKUP;
			}
		}
	}
	
	public void requestSugarPickup(AntObject antObject, int amountToPickUp) {
		serviceQueue.add(antObject);
		amount -= amountToPickUp;
	}
	
	/**
	 * removes antObject from serviceQueue
	 * @param antObject
	 */
	public void removeFromQueueEarly(AntObject antObject, int amountPickedUp) {
		amount += amountPickedUp;
		antObject.unsetSugarTarget();
		
		// It is possible that the serviceQueue is empty, because the ant could be
		// killed during the tick it is done waiting.
		if (!serviceQueue.isEmpty()) { 
			if (serviceQueue.getFirst() == antObject) {
				ticksToNextService = parameters.TICKS_SUGAR_PICKUP;
			}
			serviceQueue.remove(antObject);
		}
	}
	
	public double getRadius() {
		return parameters.INITIAL_SUGAR_RADIUS * Math.sqrt((double) amount / parameters.INITIAL_SUGAR_IN_SOURCE);
	}

	/**
	 * Gets called by GameWorld when this SugarObject getsRemoved
	 */
	public void getsRemoved() {
		for (AntObject antObject : serviceQueue) {
			antObject.unsetSugarTarget();
		}
	}
}
