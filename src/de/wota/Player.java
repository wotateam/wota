package de.wota;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class Player {
	public final Class<? extends AntAI> antAIClass;

	public final List<Ant> ants = new LinkedList<Ant>();
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
