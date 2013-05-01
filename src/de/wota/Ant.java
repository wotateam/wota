package de.wota;

import java.awt.geom.Point2D;

/**
 * Interne Darstellung von Ants.
 * 
 * @author pascal
 * 
 */
public class Ant extends GameObject {

	public Ant(Point2D.Double position) {
		super(position);
	}
	
	private AntAI ai;
	private double health;
	private double speed;
	/** Angriffspunkte */
	private double attack;

	public AntAI getAI() {
		return ai;
	}

	public double getHealth() {
		return health;
	}

	public double getSpeed() {
		return speed;
	}

	public double getAttack() {
		return attack;
	}

	public void tick() {
		ai.tick();
	}

	public Action getAction() {
		return ai.getAction();
	}

}
