package de.wota;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import de.wota.ai.AntAI;
import de.wota.ai.HillAI;
import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.Hill;

public class Player {
	public final Class<? extends AntAI> antAIClass;

	public final List<AntObject> antObjects = new LinkedList<AntObject>();
	public final Hill hill;

	private final int id;
	private static int nextId = 0;

	public int getId() {
		return id;
	}

	public Player(Class<? extends AntAI> antAIClass,
			Class<? extends HillAI> hillAIClass, Point2D.Double position)
			throws InstantiationException, IllegalAccessException {
		this.antAIClass = antAIClass;
		hill = new Hill(hillAIClass.newInstance(), position);

		// TODO fail early w.r.t. to ants, too, by creating one to test ant
		// creation
		id = nextId;
		nextId++;
	}
}
