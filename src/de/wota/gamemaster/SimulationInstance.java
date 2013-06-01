package de.wota.gamemaster;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import de.wota.gameobjects.GameWorld;
import de.wota.gameobjects.GameWorld.Player;
import de.wota.gameobjects.Parameters;
import de.wota.gameobjects.SugarObject;
import de.wota.utility.SeededRandomizer;
import de.wota.utility.Vector;

/**
 * Contains all the information needed for one round of simulation.
 */
public class SimulationInstance {
	private final String[] aiArray;
	private final AILoader aiLoader;
	private final long seed;
	private final Parameters parameters;
	
	/**
	 * Create an instance from a seed used to
	 * generate a map and initialize the RNGs.
	 * 
	 * Participating ais are read from the file "settings.txt"
	 * 
	 * Game parameters are read from "parameters.txt"
	 * 
	 * @param seed
	 *            initial seed of the RNG
	 */
	public SimulationInstance(long seed) {
		this.seed = seed;

		aiLoader = new AILoader("./");
		
		Properties propertiesForSettings = new Properties();
		try {
			propertiesForSettings.load(new FileReader("settings.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("settings.txt not found.");
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		String aiString = propertiesForSettings.getProperty("aiString");
		String[] ais = aiString.split(",");
		for (int i=0; i<ais.length; i++) {
			ais[i] = ais[i].trim();
			System.out.println("AI #" + (i+1) + " " + ais[i] );
		}
		this.aiArray = ais;
		
		Properties propertiesForParameters = new Properties();
		try {
			propertiesForParameters.load(new FileReader("parameters.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("parameters.txt not found.");
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		parameters = new Parameters(propertiesForParameters);
	}
	
	/**
	 * Create an instance from a randomly choosen seed
	 * used to generate a map and initialize the RNGs.
	 * 
	 * Participating ais are read from the file "settings.txt"
	 * 
	 * Game parameters are read from "parameters.txt"
	 */
	public SimulationInstance() {
		this((new Random()).nextLong());
	}

	/**
	 * Deterministically constructs and populates a GameWorld instance with
	 * hills and resources from the given seed.
	 * 
	 * @return a GameWorld instance to start the simulation with
	 */
	public GameWorld constructGameWorld() {
		GameWorld world = new GameWorld(parameters);

		SeededRandomizer.resetSeed(seed);

		for (String aiName : aiArray) {
			GameWorld.Player player = world.new Player(new Vector(
					SeededRandomizer.nextInt(700),
					SeededRandomizer.nextInt(700)), aiLoader.loadQueen(aiName));
			world.addPlayer(player);
		}

		// add sugar for test usage
		for (int i = 0; i < 10; i++) {
			SugarObject sugarObject = new SugarObject(
					parameters.INITIAL_SUGAR, new Vector(
							parameters.SIZE_X
									* SeededRandomizer.nextDouble(),
							parameters.SIZE_Y
									* SeededRandomizer.nextDouble()), parameters);
			world.addSugarObject(sugarObject);
		}
		/*
		// add queens
		for (Player player : world.getPlayers()) {
			player.
		}*/

		return world;
	}

	public int getNumPlayers() {
		return aiArray.length;
	}

	public Parameters getParameters() {
		return parameters;
	}
}
