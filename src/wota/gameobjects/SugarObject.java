package wota.gameobjects;

import java.util.LinkedList;

import wota.utility.Vector;


/**
 * A hill of sugar. 
 * 
 * Sugar can be picked up by first come first serve principle:
 * SugarObject has a field ticksToWait which indicates the number of 
 * ticks an Ant freezes before the sugar is picked up. This number will
 * be increased by Parameters.TICKS_SUGAR_PICKUP when an Ant picks up some
 * sugar and decreased by one every tick.
 *
 */
public class SugarObject extends GameObject {
	
	private int amount;
	private Sugar sugar;
	/** List of Ants which wait to receive sugar */
	private LinkedList<AntObject> serviceQueue = new LinkedList<AntObject>();
	/** number of ticks an ant will freeze when picking up */
	private int ticksToNextService;
	
	public SugarObject(Vector position, Parameters parameters) {
		super(position, parameters);
		ticksToNextService = parameters.TICKS_SUGAR_PICKUP;
		this.amount = parameters.INITIAL_SUGAR_IN_SOURCE;
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
	
	/** 
	 * returns if antObject is able to pick up sugar now
	 */
	public boolean canPickUpSugarNow(AntObject antObject) {
		return (serviceQueue.getFirst() == antObject) && (ticksToNextService == 0);
	}
	
	public void tick() {
		if ( !serviceQueue.isEmpty() ) {
			ticksToNextService--;
			if (ticksToNextService == 0) {
				serviceQueue.removeFirst().unsetSugarTarget();
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
