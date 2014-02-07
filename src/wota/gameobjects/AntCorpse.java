/**
 * 
 */
package wota.gameobjects;

import wota.utility.Vector;

/**
 *
 */
public class AntCorpse extends BaseAnt {

	/** corresponding physical element of this Ant */ 
	final AntCorpseObject antCorpseObject; // should only be accessible for objects in the same package

	public AntCorpse(AntCorpseObject antCorpseObject) {
		super(antCorpseObject);
		this.antCorpseObject = antCorpseObject;
	}
	
	/* (non-Javadoc)
	 * @see wota.gameobjects.Snapshot#hasSameOriginal(wota.gameobjects.Snapshot)
	 */
	@Override
	public boolean hasSameOriginal(Snapshot other) {
		if (other == null) {
			return false;
		}
		if (other instanceof AntCorpse) {
			return ((AntCorpse) other).antCorpseObject.equals(this.antCorpseObject);
		}
		else {
			return false;
		}
	}
}
