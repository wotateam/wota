package de.wota.gameobjects;

import de.wota.utility.Vector;

/**
 * Large amount of sugar which wants to be collected
 * @author pascal
 *
 */
public class Sugar extends Snapshot {
	public final int amount;
	final SugarObject sugarObject;
	
	public Sugar(SugarObject sugarObject) {
		this.sugarObject = sugarObject;
		amount = sugarObject.getAmount();
	}

	@Override
	Vector getPosition() {
		return sugarObject.getPosition();
	}
}
