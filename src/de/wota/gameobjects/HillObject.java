package de.wota.gameobjects;

import java.util.List;

import de.wota.Vector;
import de.wota.AntOrder;
import de.wota.Player;
import de.wota.ai.Hill;
import de.wota.ai.HillAI;

public class HillObject extends GameObject {
	private final HillAI ai;
	private final Hill hill;
	private Player player;

	public HillObject(HillAI ai, Vector position, Player player) {
		super(position);
		this.hill = new Hill();
		this.ai = ai;
		this.player = player;
	}
	
	public List<AntOrder> getAntOrders() {
		return ai.popAntOrders();
	}
	
	public Player getPlayer() {
		return player;
	}
}
