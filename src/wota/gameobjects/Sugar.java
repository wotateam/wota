package wota.gameobjects;

import wota.utility.Vector;

/**
 * Large amount of sugar which wants to be collected
 * @author pascal
 *
 */
public class Sugar implements Snapshot {
	public final int amount;
	public final double radius;
	public final int waitingAnts;
	final SugarObject sugarObject;
	
	Sugar(SugarObject sugarObject) {
		this.sugarObject = sugarObject;
		amount = sugarObject.getAmount();
		radius = sugarObject.getRadius();
		waitingAnts = sugarObject.getQueueSize();
	}

	public Vector getPosition() {
		return sugarObject.getPosition();
	}
}
