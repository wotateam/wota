package de.wota;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import de.wota.gameobjects.GameWorld;
import de.wota.graphics.View;
import de.wota.statistics.TestLogger;
import de.wota.testing.TestWorld;

/**
 * Contains the main loop that calls tick() and updates both the view and the
 * statistics.
 * 
 * @author Fabian
 * 
 */
public class Simulation {
	// FIXME: N_PLAYER durch Karte/Ausgangsstellung ersetzen
	private final int N_PLAYER;
	private boolean isGraphical;

	final int width = 700;
	final int height = 700;

	private GameWorld gameWorld;
	private View view;

	private boolean running;
	private int tickCount;

	public void tick() {
		gameWorld.tick();
	}

	/*
	 * TODO: Die Simulation bekommt eine Ausgangsstellung, keine GameWorld
	 */
	public Simulation(SimulationInstance inst, boolean isGraphical) {
		N_PLAYER = inst.getNumPlayers();
		this.isGraphical = isGraphical;

		gameWorld = inst.constructGameWorld();

		gameWorld.registerLogger(new TestLogger());

		if (isGraphical) {
			view = new View(gameWorld);
			try {
				Display.setDisplayMode(new DisplayMode(width, height));
				Display.create();
			} catch (LWJGLException e) {
				e.printStackTrace();
				System.exit(0);
			}

			view.setup(width, height);
		}

		running = false;
		tickCount = 0;
	}

	public void runSimulation() {
		running = true;

		while (running) {
			gameWorld.tick();
			tickCount++;

			if (isGraphical) {
				view.render(width, height);
				Display.update();
				running = !Display.isCloseRequested();
			}
		}

		if (isGraphical)
			Display.destroy();
	}

	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {
		List<String> ais = new LinkedList<String>();
		ais.add("de.wota.testing.DummyQueenAI");
		ais.add("de.wota.testing.DummyQueenAI");
		ais.add("de.wota.testing.DummyQueenAI");

		SimulationInstance inst = new SimulationInstance(ais, 42);
		
		Simulation sim = new Simulation(inst, true);
		sim.runSimulation();
	}

}
