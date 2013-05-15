package de.wota.gamemaster;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import de.wota.gameobjects.GameWorld;
import de.wota.graphics.View;

/**
 * Contains the main loop that calls tick() and updates both the view and the
 * statistics.
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

	/**
	 * Advance the game world by one tick and check for victory.
	 */
	private void tick() {
		gameWorld.tick();

		// check for victory condition
		GameWorld.Player winner = gameWorld.checkVictoryCondition();
		if (winner != null) {
			System.out.println(winner.name + " has won the game in tick "
					+ tickCount);
			running = false;
		}
	}

	/*
	 * TODO: Die Simulation bekommt eine Ausgangsstellung, keine GameWorld
	 */
	/**
	 * Creates a runnable simulation from a SimulationInstance with the option
	 * for visualization.
	 * 
	 * @param inst the instance to be simulated
	 * @param isGraphical true, if the simulation should be graphical
	 */
	public Simulation(SimulationInstance inst, boolean isGraphical) {
		N_PLAYER = inst.getNumPlayers();
		this.isGraphical = isGraphical;

		gameWorld = inst.constructGameWorld();

		gameWorld.registerLogger(new TestLogger());

		if (isGraphical) {
			view = new View(gameWorld, width, height);
			try {
				Display.setDisplayMode(new DisplayMode(width, height));
				Display.create();
			} catch (LWJGLException e) {
				e.printStackTrace();
				System.exit(0);
			}

			view.setup();
		}

		running = false;
		tickCount = 0;
	}

	/**
	 * Start the simulation and keep the view up to date.
	 */
	public void runSimulation() {
		running = true;

		long startTime = System.currentTimeMillis();
		long time;

		while (running) {
			tick();

			time = System.currentTimeMillis();
			tickCount++;

			if (tickCount % 100 == 0) {
				System.out.format("TPS: %.1f\n",
						(100.0 * 1000.0 / (time - startTime)));
				startTime = time;
			}

			if (isGraphical) {
				view.render();
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
		ais.add("DummyQueenAI");
		ais.add("DummyQueenAI");
		ais.add("DummyQueenAI");
		ais.add("DummyQueenAI");

		SimulationInstance inst = new SimulationInstance(ais, 42);

		Simulation sim = new Simulation(inst, true);
		sim.runSimulation();
	}

}
