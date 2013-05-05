package de.wota.gameobjects;

import java.util.LinkedList;
import java.util.List;

import de.wota.AntOrder;
import de.wota.Player;
import de.wota.ai.AntAI;
import de.wota.ai.QueenAI;
import de.wota.gameobjects.Ant;
import de.wota.utility.Vector;

public class QueenObject extends AntObject {
	public final QueenAI queenAI;
	
	public QueenObject(Vector position, Class<? extends QueenAI> queenAIClass, Player player) {
		super(position, Ant.Caste.QUEEN, queenAIClass, player);
		this.queenAI = (QueenAI) ai;
	}
	
	public List<AntOrder> getAntOrders() {
		return queenAI.popAntOrders();
	}
}
