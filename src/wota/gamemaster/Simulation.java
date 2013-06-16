package wota.gamemaster;

import java.util.LinkedList;
import java.util.List;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.swing.SwingUtilities;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import wota.gameobjects.GameWorld;
import wota.gameobjects.LeftoverParameters;
import wota.graphics.GameView;
import wota.graphics.StatisticsView;


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
	private GameView gameView;
	private StatisticsView statisticsView;

	private double measuredFramesPerSecond;
	private double measuredTicksPerSecond;
	
	/** 
	 * reference values for call of tick().
	 * Should be independent of measering system. 
	 * Otherwise some articfacts pop up.
	 */
	private long referenceTime = System.nanoTime();
	private int referenceTickCount = 0;
	private int referenceFrameCount = 0;
	
	/** time between two measurements of FPS / TPS in seconds */
	private final double MEASUREMENT_INTERVAL = 2.0; 
	
	/** ticks are skipped if delayed by this number */
	private final int SKIP_TICKS_THRESHOLD = 3;
	
	/** time of simulation start in nano seconds. */
	private long startTime; 
	
	private boolean running;
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
	
		gameWorld = inst.getGameWorld();
	
		gameWorld.registerLogger(new TestLogger());
			
		statisticsView = new StatisticsView(gameWorld);
        // Schedules the application to be run at the correct time in the event queue.
        SwingUtilities.invokeLater(statisticsView);
        
		if (isGraphical) {
			gameView = new GameView(gameWorld, width, height, inst.getParameters());
			try {
				Display.setDisplayMode(new DisplayMode(width, height));
				Display.create(new PixelFormat(8,0,0,0));
			} catch (LWJGLException e) {
				e.printStackTrace();
				System.exit(0);
			}
	
			gameView.setup();

			createKeyboard();	
		}
	
		
		running = false;
	}

	/**
	 * Advance the game world by one tick and check for victory.
	 */
	private void tick() {
		gameWorld.tick();

		if (gameWorld.checkVictoryCondition()) {
			GameWorld.Player winner = gameWorld.getWinner();
			if (winner != null) {
				System.out.println(winner.name + " has won the game in tick "
						+ gameWorld.tickCount());
				running = false;
			}
			else {
				System.out.println("draw! nobody has won the game after " + gameWorld.tickCount() + " ticks.");
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
		
		resetReferenceValues();
		
		long lastMeasurementTime = System.nanoTime(); // time for TPS / FPS measurements
		int measureFrameCount = 0; // Frame counter to determine TPS
		int measureTickCount = 0; // Tick counter to determine FPS
		
		// events for graphics update and tick are created uniformly. Call them with priority on graphics
		while (running) { 
			
			handleKeyboardInputs();
			
			if (!isGraphical) {
				tick();
				measureTickCount++;
			} else {
				
				// now update simulation if tick event 
				if (ticksToDo() > SKIP_TICKS_THRESHOLD) {
					resetReferenceValues();
				}
				if (ticksToDo() > 0 ) {
					tick();
					measureTickCount++;
					referenceTickCount++;
				}
				
				// Update Graphics if event for graphic update is swept.
				if (isGraphical && framesToDo() > 0) {
					frameCount++;
					measureFrameCount++;
					referenceFrameCount++;
					gameView.render();
					Display.update();
					
					statisticsView.refresh();
					
					running = !Display.isCloseRequested();
				}
				
			}

			// measurements of FPS / TPS 
			long timeDiff = System.nanoTime() - lastMeasurementTime;
			if (timeDiff > 1.e9*MEASUREMENT_INTERVAL) {
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
		statisticsView.frame.dispose();
		
	}
	
	private static void createKeyboard() {
		try {
			Keyboard.create();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private void handleKeyboardInputs() {
		
		// Kedboard.poll() checks for keyboard input, buffered
		Keyboard.poll();
		
		while (Keyboard.next()) {
			// only consider KeyDown Events
			if ( !Keyboard.getEventKeyState()) {
				continue;
			}
			switch (Keyboard.getEventKey()) {
			case Keyboard.KEY_ESCAPE:
				running = false;
				break;
			case Keyboard.KEY_S:
				gameView.drawSightRange = !gameView.drawSightRange;
				break;
			case Keyboard.KEY_M:
				gameView.drawMessages = !gameView.drawMessages;
				break;
			case Keyboard.KEY_PERIOD:
				LeftoverParameters.ticksPerSecond *= 1.3;
				resetReferenceValues();
				break;
			case Keyboard.KEY_COMMA:
				LeftoverParameters.ticksPerSecond /= 1.3;
				resetReferenceValues();
				break;
			}
		}
	}
	
	private int framesToDo() {
		return (int)((System.nanoTime() - referenceTime) / 1.e9 * LeftoverParameters.framesPerSecond) -
		  referenceFrameCount + 1;
	}
	
	private int ticksToDo() {
		return (int)((System.nanoTime() - referenceTime) / 1.e9 * LeftoverParameters.ticksPerSecond) -
		referenceTickCount + 1;
	}
	
	private void resetReferenceValues() {
		referenceTime = System.nanoTime();
		referenceTickCount = 0;
		referenceFrameCount = 0;
	}
}
