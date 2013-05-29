package de.wota.gameobjects;

import java.util.List;

import de.wota.utility.Vector;

public class QueenObject extends AntObject {
	public final QueenAI queenAI;
	
	public QueenObject(Vector position, Class<? extends QueenAI> queenAIClass, GameWorld.Player player, Parameters parameters) {
		super(position, Caste.Queen, queenAIClass, player, parameters);
		this.queenAI = (QueenAI) ai;
	}
	
	public List<AntOrder> getAntOrders() {
		return queenAI.popAntOrders();
	}
	
	public boolean isDead() {
		return (health <= 0);
	}
}
