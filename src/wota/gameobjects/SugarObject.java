package wota.gameobjects;

import java.util.LinkedList;

import wota.utility.Vector;


/**
 * A hill of sugar. 
 * 
 * Sugar can be picked up by first come first serve principle.
 *
 */
public class SugarObject extends GameObject {
	
	private int amount;
	private int visibleAmount; // always at least as big as amount 
	private Sugar sugar;
	/** List of Ants which wait to receive sugar */
	private LinkedList<AntObject> serviceQueue = new LinkedList<AntObject>();
	/** number of ticks an ant will freeze when picking up */
	private int ticksToNextService;
	
	public SugarObject(Vector position, Parameters parameters) {
		super(position, parameters);
		ticksToNextService = parameters.TICKS_SUGAR_PICKUP;
		amount = parameters.INITIAL_SUGAR_IN_SOURCE;
		this.visibleAmount = amount;
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
				visibleAmount -= theServiced.getAmountPickedUpLastTime();
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
		if (serviceQueue.getFirst() == antObject) {
			ticksToNextService = parameters.TICKS_SUGAR_PICKUP;
		}
		amount += amountPickedUp;
		antObject.unsetSugarTarget();
		serviceQueue.remove(antObject);
	}
	
	public double getRadius() {
		return parameters.INITIAL_SUGAR_RADIUS * Math.sqrt((double) visibleAmount / parameters.INITIAL_SUGAR_IN_SOURCE);
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
