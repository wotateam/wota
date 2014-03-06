/**
 * 
 */
package wota.ai.solitary;

import wota.gameobjects.*;
import wota.utility.SeededRandomizer;

/**
 *
 */
public class SoldierAI extends AntAI {
	int direction = random.getInt(360);
	@Override
	public void tick() throws Exception {
		boolean hasTarget = false;
		for (Ant ant : visibleAnts) {
			if (ant.playerID != self.playerID) {
				if (true) { //(ant.caste == Caste.Queen) {
					moveToward(ant);
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
