/**
 * 
 */
package wota.ai.loadingpleasewait;

import java.util.LinkedList;

import wota.gameobjects.Ant;
import wota.gameobjects.AntMessage;
import wota.gameobjects.Caste;

/**
 * This ant protects the gatherers insert pun about the word troll here
 */
public class PatrollAntAI extends AntAILPW {

	private static final int TARGET_DOWN = -4;

	private LinkedList<Ant> deadEnemies = new LinkedList<Ant>();
	private LinkedList<Ant> friendlyGatherers = new LinkedList<Ant>();
	private Ant enemyThreat;

	@Override
	public void tick() {
		assert (self.caste.equals(Caste.Soldier)) : "Patrol ant was a " + self.caste;
		
		friendlyGatherers.clear();
		for (Ant friendly : visibleFriends())
			if (friendly.caste.equals(Caste.Gatherer))
				friendlyGatherers.add(friendly);

		listen();

		if (enemyThreat == null || (enemySoldierCount() > 0 && !friendlyGatherers.isEmpty()))
			enemyThreat = closest(visibleEnemies());
		else if(enemyThreat != null && vectorTo(enemyThreat).length() > 50)
			enemyThreat = null;
		if(enemyThreat != null && vectorTo(enemyThreat).length() < 1 && visibleEnemies().isEmpty()){
			deadEnemies.add(enemyThreat);
			enemyThreat = null;
		}
		
		if(preventGettingStuck())
			return;
		
		if (canPickUpSugar() && friendlyGatherers.isEmpty()) {
			//don't sit at an unused sugar site
			setReturningHome(true);
			attack(closest(visibleEnemies()));
			moveHome();
		} else if(isSitting(true) && inMiddleOfRoute()){
			//don't get stuck
			setReturningHome(true);
			setStuck(true);
			setTarget(null);
			enemyThreat = null;
			moveHome();
		} else if (isHomeUnderAttack() || (vectorToHome().length() < 70 && !visibleEnemies().isEmpty())) {
			// protect the hill
			attack(closest(visibleEnemies()));
			if(vectorToHome().length() > 20 || visibleEnemies().isEmpty())
				moveHome();
			else
				moveToward(closest(visibleEnemies()));
			setHomeUnderAttack(enemySoldierCount() > 0);
		} else if (enemyThreat == null) {
			super.tick();
		} else {
			attack(enemyThreat);
			if(!friendlyGatherers.isEmpty() && enemyThreat.caste.equals(Caste.Soldier))
				moveToward(enemyThreat);
		}

		if (enemyThreat != null) {
			if (enemyThreat.health > 0.2 && enemyThreat.caste.equals(Caste.Soldier))
				talk(AntAILPW.BACKUP_CALL, enemyThreat);
			else
				talk(PatrollAntAI.TARGET_DOWN, enemyThreat);
		}
		
		setLastLocation(self.getPosition());
	}

	@Override
	protected void listen() {
		super.listen();
		enemyThreat = null;
		//listen to information on enemy ants
		for (AntMessage message : audibleAntMessages) {
			if (message.contentAnt != null) {
				if (message.content == AntAILPW.BACKUP_CALL) {
					if (enemyThreat == null || vectorTo(message.contentAnt).length() < vectorTo(enemyThreat).length())
						enemyThreat = message.contentAnt;
				} else if (message.content == TARGET_DOWN && !deadEnemies.contains(message.contentAnt)) {
					deadEnemies.add(message.contentAnt);
				}
			}
		}
		for (Ant enemy : deadEnemies)
			if (enemy.hasSameOriginal(enemyThreat))
				enemyThreat = null;
	}

	@Override
	protected boolean isSitting() {
		return isSitting(false);
	}
	
	/**
	 * @param increment whether or not to increment sittingDuration
	 * @return isSitting()
	 */
	protected boolean isSitting(boolean increment) {
		int previousDuration = getSittingDuration();
		boolean output = super.isSitting();
		if(!increment)
			setSittingDuration(previousDuration);
		return output;
	}
	
	

}
