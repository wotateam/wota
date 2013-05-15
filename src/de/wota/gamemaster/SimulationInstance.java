package de.wota.gamemaster;

import java.util.List;

import de.wota.gameobjects.GameWorld;
import de.wota.gameobjects.GameWorldParameters;
import de.wota.gameobjects.SugarObject;
import de.wota.utility.SeededRandomizer;
import de.wota.utility.Vector;

/**
 * Contains all the information needed for one round of simulation.
 */
public class SimulationInstance {
	private final List<String> aiList;
	private final AILoader aiLoader;
	private final long seed;

	/**
	 * Create an instance from a list of participating AIs and a seed used to
	 * generate a map and initialize the RNGs.
	 * 
	 * @param aiList
	 *            list with class names of the participating AIs
	 * @param seed
	 *            initial seed of the RNG
	 */
	public SimulationInstance(List<String> aiList, long seed) {
		this.aiList = aiList;
		this.seed = seed;

		aiLoader = new AILoader();
	}

	/**
	 * Deterministically constructs and populates a GameWorld instance with
	 * hills and resources from the given seed.
	 * 
	 * @return a GameWorld instance to start the simulation with
	 */
	public GameWorld constructGameWorld() {
		GameWorld world = new GameWorld();

		SeededRandomizer.resetSeed(seed);

		for (String aiName : aiList) {
			GameWorld.Player player = world.new Player(new Vector(
					SeededRandomizer.nextInt(700),
					SeededRandomizer.nextInt(700)), aiLoader.loadQueen(aiName));
			world.addPlayer(player);
		}

		// add sugar for test usage
		for (int i = 0; i < 10; i++) {
			SugarObject sugarObject = new SugarObject(
					GameWorldParameters.INITIAL_SUGAR, new Vector(
							GameWorldParameters.SIZE_X
									* SeededRandomizer.nextDouble(),
							GameWorldParameters.SIZE_Y
									* SeededRandomizer.nextDouble()));
			world.addSugarObject(sugarObject);
		}

		return world;
	}

	public int getNumPlayers() {
		return aiList.size();
	}
}
