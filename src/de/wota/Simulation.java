package de.wota;

import de.wota.gameobjects.GameWorld;
import de.wota.graphics.DisplayMode;
import de.wota.graphics.LWJGLException;
import de.wota.graphics.View;
import de.wota.testing.TestWorld;

/**
 * Enthält die Hauptschleife des Spiels, aktualisiert View und Statistik.
 * @author Fabian
 *
 */
public class Simulation {
	//FIXME: N_PLAYER durch Karte/Ausgangsstellung ersetzen
	private final int N_PLAYER;
	private boolean isGraphical;
	
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
	public Simulation(int nPlayer, boolean isGraphical) {
		N_PLAYER = nPlayer;
		this.isGraphical = isGraphical;
		
		//FIXME: Solange es keine Karten gibt, Testwelt verwenden
		gameWorld = TestWorld.testWorld();
		
		if (isGraphical = true)
		{
			View view = new View(gameWorld);
			final int width = 700;
			final int height = 700;
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
	
	public void runSimulation()
	{
		running = true;
		
		while (running)
		{
			gameWorld.tick();
			tickCount++;			
			
			if (isGraphical)
			{
				view.render(width, height);
				Display.update();
				running = !Display.isCloseRequested();
			}
		}		
		
		if (isGraphical)
			Display.destroy();
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException
	{
		Simulation sim = new Simulation(2, true);
		sim.runSimulation();
		
	}
	
}
