/**
 * 
 */
package wota.gameobjects;

/**
 *  AntObject after its death.
 */
public class AntCorpseObject extends BaseAntObject {
	private AntCorpse antCorpse;
	private int ticksUntilDecay; // counts down the ticks after Ant has died until its corpse will be removed
	
	public AntCorpseObject(AntObject dyingAntObject) {
		super(dyingAntObject);
		ticksUntilDecay = parameters.CORPSE_DECAY_TIME;
	}
	
	public void tick() {
		ticksUntilDecay--;
	}
	
	/** if true, AntObject can be entirely removed */
	public boolean isDecayed() {
		return ticksUntilDecay <= 0;
	}

	public void createAntCorpse() {
		antCorpse = new AntCorpse(this);
	}
	
	public AntCorpse getAntCorpse() {
		return antCorpse;
	}
}
