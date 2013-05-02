package de.wota.gameobjects;

import java.awt.geom.Point2D;

import de.wota.ai.HillAI;

public class Hill extends GameObject {
	private final HillAI ai;

	public Hill(HillAI ai, Point2D.Double position) {
		super(position);
		this.ai = ai;
	}
}
