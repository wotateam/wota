package de.wota.gameobjects;

import java.util.LinkedList;
import java.util.List;

import de.wota.AntOrder;
import de.wota.Player;
import de.wota.ai.Hill;
import de.wota.utility.Vector;

public class HillObject extends GameObject {
	private final Hill hill;
	private Player player;

	public HillObject(Vector position, Player player) {
		super(position);
		this.hill = new Hill();
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
}
