package de.wota.gamemaster;

import java.util.LinkedList;
import java.util.List;

import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import de.wota.gameobjects.GameWorld;
import de.wota.gameobjects.Parameters;
import de.wota.graphics.View;

/**
 * Contains the main loop that calls tick() and updates both the view and the
 * statistics. Also handels keyboard.
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
	
	public final int FRAMES_PER_SECOND = Parameters.FRAMES_PER_SECOND;
	public final int TICKS_PER_SECOND = Parameters.TICKS_PER_SECOND;

	private double measuredFramesPerSecond;
	private double measuredTicksPerSecond;
	
	/** time between two measurements of FPS / TPS in seconds */
	private double measurementInterval = 1.0; 
	
	/** time of simulation start in nano seconds. */
	private long startTime; 
	
	private boolean running;
	private int tickCount;
	private int frameCount;

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
				Display.create(new PixelFormat(8,0,0,0));
			} catch (LWJGLException e) {
				e.printStackTrace();
				System.exit(0);
			}
	
			view.setup();
		}
	
		createKeyboard();
		
		running = false;
		tickCount = 0;
	}

	/**
	 * Advance the game world by one tick and check for victory.
	 */
	private void tick() {
		gameWorld.tick();

		// check for victory condition
		if (gameWorld.checkVictoryCondition()) {
			GameWorld.Player winner = gameWorld.getWinner();
			if (winner != null) {
				System.out.println(winner.name + " has won the game in tick "
						+ tickCount);
				running = false;
			}
			else {
				System.out.println("draw! nobody has won the game after " + tickCount + " ticks.");
				running = false;
			}
		}
	}

	/**
	 * Start the simulation and keep the view up to date.
	 * 1. Update the Graphics at the rate FRAMES_PER_SECOND
	 * 2. Update the Simulation with rate TICKS_PER_SECOND 
	 * do nothing in the remaining time or if times get in conflict only update the graphics.
	 * 
	 * keyboard/mouse input should be fetched before graphics in every loop. 
	 */
	public void runSimulation() {
		running = true;
		startTime = System.nanoTime();
		
		long lastMeasurementTime = System.nanoTime(); // time for TPS / FPS measurements
		int measureFrameCount = 0; // Frame counter to determine TPS
		int measureTickCount = 0; // Tick counter to determine FPS
		
		// events for graphics update and tick are created uniformly. Call them with priority on graphics
		while (running) { 

			// check for keyboard input, buffered
			// 
			Keyboard.poll();
			
			while (Keyboard.next()) {
				switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_ESCAPE:
					running = false;
					break;
				}
			}
			
			// Update Graphics if event for graphic update is swept.
			if (isGraphical && frameCount <= (System.nanoTime() - startTime) / 1.e9 * FRAMES_PER_SECOND) {
				frameCount++;
				measureFrameCount++;
				view.render();
				Display.update();
				if (running) {
					running = !Display.isCloseRequested();
				}
			}
			
			// now update simulation if tick event 
			if ((double)tickCount < (System.nanoTime() - startTime) / 1.e9 * TICKS_PER_SECOND ) {
				tick();
				tickCount++;
				measureTickCount++;
			}

			// measurements of FPS / TPS 
			long timeDiff = System.nanoTime() - lastMeasurementTime;
			if (timeDiff > 1.e9*measurementInterval) {
				measuredFramesPerSecond = measureFrameCount * 1.e9 / timeDiff;
				measuredTicksPerSecond = measureTickCount * 1.e9 / timeDiff;
				
				measureFrameCount = 0;
				measureTickCount = 0;
				
				lastMeasurementTime = System.nanoTime();
				
				System.out.format("Frames per second: %.1f\n",
					measuredFramesPerSecond);
				System.out.format("Ticks per second: %.1f\n",
						measuredTicksPerSecond);
			}
		}

		if (isGraphical) {
			Display.destroy();
		}
	}
	
	private static void createKeyboard() {
		try {
			Keyboard.create();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
