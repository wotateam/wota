package de.wota.testing;

import de.wota.ai.MoveAI;
import de.wota.gamemaster.AILoader;
import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.Caste;
import de.wota.gameobjects.GameWorld;
import de.wota.utility.Vector;

public class TestWorld {

	public static GameWorld testWorld() throws InstantiationException, IllegalAccessException {
		AILoader loader = new AILoader();
		
		GameWorld world = new GameWorld();
		for (int i = 0; i < 2; i++) {
			GameWorld.Player player = world.new Player(new Vector(100 + i * 200, 100 + i * 200), 
					loader.loadQueen("de.wota.testing.DummyQueenAI"));
			for (int j = 0; j < 0; j++) {
				AntObject antObject = new AntObject(new Vector(j * 10, 20 + i * 20), Caste.Gatherer, MoveAI.class, player);
				player.addAntObject(antObject);
			}
			world.addPlayer(player);
		}
		return world;
	}

}