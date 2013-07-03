package wota.ai.buerohengst;

import wota.gameobjects.*;
import wota.utility.SeededRandomizer;
import java.util.List;

public class BrutalAttacker extends AntAI {

	int tick = 0;
	int direction = SeededRandomizer.getInt(360);
	@Override
	public void tick() throws Exception {
		tick++;
		if (tick % 100 == 0) {
			direction = SeededRandomizer.getInt(360);
		}
		if (visibleEnemies().size() != 0) {
			Ant weakest = getWeakest(visibleEnemies());
			moveToward(weakest);
			attack(weakest);
		}
		else {
			moveInDirection(direction);
		}
	}
	
	public static Ant getWeakest(List<Ant> enemies) {
		Ant weakest = null;
		double health = Double.MAX_VALUE;
		for (Ant enemy : enemies) {
			if (enemy.health < health) {
				health = enemy.health;
				weakest = enemy;
			}
		}
		return weakest;
	}
}
