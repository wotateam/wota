package wota.gameobjects;

import wota.utility.Vector;

/**
 * Large amount of sugar which wants to be collected
 *
 */
public class Sugar implements Snapshot {
	public final int amount;
	public final double radius;
	public final Vector position;
	final SugarObject sugarObject;
	
	Sugar(SugarObject sugarObject) {
		this.sugarObject = sugarObject;
		amount = sugarObject.getAmount();
		radius = sugarObject.getRadius();
		position = sugarObject.getPosition();
	}

	public Vector getPosition() {
		return position;
	}
	
	/* (non-Javadoc)
	 * @see wota.gameobjects.Snapshot#hasSameOriginal(wota.gameobjects.Snapshot)
	 */
	@Override
	public boolean hasSameOriginal(Snapshot other) {
		if (other == null) {
			return false;
		}
		if (other instanceof Sugar) {
			return ((Sugar) other).sugarObject.equals(this.sugarObject);
		}
		else {
			return false;
		}
	}
}
