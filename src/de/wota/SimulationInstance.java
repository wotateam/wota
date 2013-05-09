package de.wota;

import java.util.List;

import de.wota.gameobjects.GameWorld;
import de.wota.gameobjects.GameWorldParameters;
import de.wota.gameobjects.SugarObject;
import de.wota.plugin.AILoader;
import de.wota.utility.SeededRandomizer;
import de.wota.utility.Vector;

public class SimulationInstance {
	private final List<String> aiList;
	private final AILoader aiLoader;
	private final long seed;

	public SimulationInstance(List<String> aiList, long seed) {
		this.aiList = aiList;
		this.seed = seed;

		aiLoader = new AILoader();
	}

	public GameWorld constructGameWorld() {
		GameWorld world = new GameWorld();

		SeededRandomizer.resetSeed(seed);

		for (String aiName : aiList) {
			Player player = new Player(new Vector(
					SeededRandomizer.nextInt(700),
					SeededRandomizer.nextInt(700)), aiLoader.loadQueen(aiName));
			world.addPlayer(player);
		}
		
		// add sugar for test usage
		for (int i=0; i<10; i++) {
			SugarObject sugarObject = new SugarObject(GameWorldParameters.INITIAL_SUGAR,
					new Vector(GameWorldParameters.SIZE_X*SeededRandomizer.nextDouble(),
							GameWorldParameters.SIZE_Y*SeededRandomizer.nextDouble()));
			world.addSugarObject(sugarObject);
		}

		return world;
	}

	public int getNumPlayers() {
		return aiList.size();
	}
}
