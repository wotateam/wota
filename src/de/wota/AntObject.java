package de.wota;

import java.awt.Point;

/**
 * Interne Darstellung von Ants. 
 * 
 * @author pascal
 *
 */
public class AntObject {
	
	private Point position;
	/** Der Spieler dem die Ant gehört */
	private Ant player; 
	private double health;
	private double speed;
	/** Angriffspunkte */
	private double attack;
	
	public Ant getPlayer() {
		return player;
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
		player.tick();
	}
	
	public Action getAction() {
		return player.getAction();
	}
	
}
