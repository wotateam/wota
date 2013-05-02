package de.wota;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import de.wota.ai.DummyHillAI;
import de.wota.gameobjects.Ant;
import de.wota.gameobjects.Sugar;
import de.wota.test.DummyAntAI;

/**
 * Enthält alle Elemente der Spielwelt.
 * 
 * @author pascal
 */
public class GameWorld {

	static final double MAX_MOVEMENT_DISTANCE = 5;
	static final double SIZE_X = 1000;
	static final double SIZE_Y = 1000;
	public static final double HILL_RADIUS = 20; // TODO class for constants

	public final List<Player> players = new LinkedList<Player>();
	private LinkedList<Sugar> sugars;

	public void tick() {
		for (Player player : players) {
			for (Ant ant : player.ants) {
				ant.tick();
			}
		}

		for (Player player : players) {
			for (Ant ant : player.ants) {
				Action action = ant.getAction();
				executeAction(ant, action);
			}
		}
	}

	/** führt die Aktion für das AntObject aus */
	private void executeAction(Ant ant, Action action) {
		// TODO executeAction schreiben
	}

	public static GameWorld testWorld() throws InstantiationException, IllegalAccessException {
		GameWorld world = new GameWorld();
		for (int i = 0; i < 2; i++) {
			Player player = new Player(DummyAntAI.class, DummyHillAI.class, new Point2D.Double(100+i*200,100+i*200));
			for (int j = 0; j < 10; j++) {
				Ant ant = new Ant(new Point2D.Double(j * 10, 20 + i * 20));
				player.ants.add(ant);
			}
			world.players.add(player);
		}
		return world;
	}
}
