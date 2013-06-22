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
import wota.graphics.GameView;
import wota.graphics.StatisticsView;
import wota.gameobjects.SimulationParameters;


/**
 * Contains the main loop that calls tick() and updates both the view and the
 * statistics. Also handels keyboard.
 * 
 */
public class Simulation {
	// FIXME: N_PLAYER durch Karte/Ausgangsstellung ersetzen
	private final int N_PLAYER;
	private final boolean isGraphical; // only saves typing

	final int width = 700;
	final int height = 700;

	private GameWorld gameWorld;
	private GameView gameView;
	private StatisticsView statisticsView;

	private double framesPerSecond;
	private double ticksPerSecond;
	
	private double measuredFramesPerSecond;
	private double measuredTicksPerSecond;
	
	/** 
	 * reference values for call of tick().
	 * Should be independent of measuring system. 
	 * Otherwise some artifacts pop up.
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
	private boolean paused = false;
	private int frameCount;
	
	/** maximum number of ticks before the game ends regardless of victory condition **/
	private int maxTicksBeforeEnd;
	
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
	public Simulation(SimulationInstance inst) {
		N_PLAYER = inst.getNumPlayers();
		this.isGraphical = inst.getSimulationParameters().IS_GRAPHICAL; 
		framesPerSecond = inst.getSimulationParameters().FRAMES_PER_SECOND;
		ticksPerSecond = inst.getSimulationParameters().INITIAL_TICKS_PER_SECOND;
		maxTicksBeforeEnd = inst.getSimulationParameters().MAX_TICKS_BEFORE_END;
	
		gameWorld = inst.getGameWorld();
	
		StatisticsLogger logger = new StatisticsLogger(gameWorld.getPlayers());
		gameWorld.setLogger(logger);
			
		statisticsView = new StatisticsView(gameWorld, logger);
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
	 * Advance the game world by one tick and check for victory / end after fixed number of ticks.
	 */
	private void tick() {
		gameWorld.tick();

		GameWorld.Player winner = gameWorld.getWinner();
		running = false; // set to true if none of the victory conditions actually apply
		if (winner != null) {
			System.out.println("#" + (winner.id() +1) + " " + winner.name + " written by " + winner.creator + " has won the game in tick "
					+ gameWorld.tickCount());
		} 
		else if (gameWorld.allPlayersDead()) {
			System.out.println("Draw! Nobody has won the game after " + gameWorld.tickCount() + " ticks.");
		}
		// End the game after fixed number of ticks - players with most ants win.
		else if (gameWorld.tickCount() >= maxTicksBeforeEnd) {
			List<GameWorld.Player> winners = gameWorld.getPlayersWithMostAnts();
			if (winners.size() == gameWorld.getPlayers().size()) {
				System.out.println("Draw! Game was stopped after " + gameWorld.tickCount() + " ticks. All players have the same number of ants.");
			}
			else {
				System.out.println("Game was stopped after " + gameWorld.tickCount() + " ticks. The following player(s) have won:");
				for (GameWorld.Player aWinner : winners) {
					System.out.println(aWinner.name);
				}
			}
		}
		else {
			running = true;
		}
		
	}

	/**
	 * Start the simulation and keep the view up to date.
	 * 1. Update the Graphics at the rate framesPerSecond
	 * 2. Update the Simulation at the rate ticksPerSecond 
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
			if (!isGraphical) {
				tick();
				measureTickCount++;
			} else {
				handleKeyboardInputs();
				
				// now update simulation if tick event 
				if (ticksToDo() > SKIP_TICKS_THRESHOLD) {
					resetReferenceValues();
				}
				if (ticksToDo() > 0 && !paused) {
					tick();
					measureTickCount++;
					referenceTickCount++;
				}
				
				// Update Graphics if event for graphic update is swept.
				if (isGraphical && framesToDo() > 0) {
					if (!paused) { // calculate new graphics output
						frameCount++;
						measureFrameCount++;
						referenceFrameCount++;
						gameView.render();
						statisticsView.refresh();
						
						if (Display.isCloseRequested()) {
							running = false;
						}
					}
					Display.update(); // must be called in any case to catch keyboard input
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
				ticksPerSecond *= 1.3;
				resetReferenceValues();
				break;
			case Keyboard.KEY_COMMA:
				ticksPerSecond /= 1.3;
				resetReferenceValues();
				break;
			case Keyboard.KEY_P:
				paused = !paused;
				break;
			}
		}
	}
	
	private int framesToDo() {
		return (int)((System.nanoTime() - referenceTime) / 1.e9 * framesPerSecond) -
		  referenceFrameCount + 1;
	}
	
	private int ticksToDo() {
		return (int)((System.nanoTime() - referenceTime) / 1.e9 * ticksPerSecond) -
		referenceTickCount + 1;
	}
	
	private void resetReferenceValues() {
		referenceTime = System.nanoTime();
		referenceTickCount = 0;
		referenceFrameCount = 0;
	}
}
