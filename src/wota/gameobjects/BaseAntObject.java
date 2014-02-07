/**
 * 
 */
package wota.gameobjects;

import wota.utility.Vector;

/**
 * Base class for living (AntObject) and dead (AntCorpseObject) Ants. 
 */
public abstract class BaseAntObject extends GameObject {	
	public final int id;
	public final Caste caste;
	public final GameWorld.Player player;
	
	public BaseAntObject(Vector position, Caste caste, int id,
					     GameWorld.Player player, Parameters parameters) {
		super(position, parameters);

		this.id = id;
		this.caste = caste;
		this.player = player;
	}
	
	public BaseAntObject(BaseAntObject baseAntObject) {
		super(baseAntObject.getPosition(), baseAntObject.parameters);
		this.id = baseAntObject.id;
		this.caste = baseAntObject.caste;
		this.player = baseAntObject.player;
	}
	
	/*
	public Caste getCaste() {
		return caste;
	}*/
}
