package wota.testing;

import java.util.Iterator;
import java.util.List;

import wota.gameobjects.Ant;
import wota.gameobjects.AntAI;


public class SitAndHitAI extends AntAI {

	@Override
	public void tick() throws Exception {
		List<Ant> visibleEnemies = visibleEnemies();
		
		Iterator<Ant> enemyIterator = visibleEnemies.iterator();
		
		while(enemyIterator.hasNext()) {
			Ant enemyAnt = enemyIterator.next();
			if (vectorTo(enemyAnt).length() <= parameters.ATTACK_RANGE) {
				attack(enemyAnt);
				break;
			}
		}
	}

}
