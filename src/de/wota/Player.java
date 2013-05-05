package de.wota;

import java.util.LinkedList;
import java.util.List;

import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.HillObject;
import de.wota.utility.Vector;

public class Player {
	public final List<AntObject> antObjects = new LinkedList<AntObject>();
	public final HillObject hillObject;

	private final int id;
	private static int nextId = 0;

	public int getId() {
		return id;
	}

	public Player(Vector position)
			throws InstantiationException, IllegalAccessException {
		hillObject = new HillObject(position, this);

		// TODO fail early w.r.t. to ants, too, by creating one to test ant
		// creation
		id = nextId;
		nextId++;
	}
}
