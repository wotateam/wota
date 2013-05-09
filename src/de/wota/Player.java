package de.wota;

import java.util.LinkedList;
import java.util.List;

import de.wota.ai.QueenAI;
import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.HillObject;
import de.wota.gameobjects.QueenObject;
import de.wota.plugin.AILoader;
import de.wota.utility.Vector;

public class Player {
	public final List<AntObject> antObjects = new LinkedList<AntObject>();
	public final HillObject hillObject;
	public final QueenObject queenObject;
	
	public final String name;
	
	private final int id;
	private static int nextId = 0;

	public int getId() {
		return id;
	}

	public Player(Vector position, Class<? extends QueenAI> queenAIClass)
			throws InstantiationException, IllegalAccessException {
		hillObject = new HillObject(position, this);
		queenObject = new QueenObject(position, queenAIClass, this);
		antObjects.add(queenObject);
	
		name = AILoader.getAIName(queenAIClass);

		// TODO fail early w.r.t. to ants, too, by creating one to test ant
		// creation
		id = nextId;
		nextId++;
	}
}
