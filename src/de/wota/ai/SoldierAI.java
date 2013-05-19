/**
 * 
 */
package de.wota.ai;

import de.wota.gameobjects.*;
import de.wota.utility.SeededRandomizer;

/**
 *
 */
public class SoldierAI extends AntAI {
	int direction = SeededRandomizer.nextInt(360);
	@Override
	public void tick() throws Exception {
		boolean hasTarget = false;
		for (Ant ant : visibleAnts) {
			if (ant.playerID != self.playerID) {
				if (ant.caste == Caste.Queen) {
					moveTowards(ant);
					attack(ant);
					hasTarget = true;
				}
			}
		}
		if (! hasTarget) {
			moveInDirection(direction);
		}
	}
	
	
}
