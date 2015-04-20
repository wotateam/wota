package wota.ai.loadingpleasewait;

import java.util.LinkedList;

import wota.gameobjects.Ant;
import wota.gameobjects.AntAI;
import wota.gameobjects.Caste;
import wota.gameobjects.Hill;

/**
 * These ants just kill other ants
 */
public class CommandoAntAI extends AntAI{
	
	private LinkedList<Ant> enemyCarriers = new LinkedList<Ant>();
	private LinkedList<Ant> enemyGatherers = new LinkedList<Ant>();
	private Hill enemyHill;
	private boolean departingEnemyHill;
	
	@Override
	public void tick() {
		//main goal is to kill enemy gatherers
		
		enemyCarriers.clear();
		enemyGatherers.clear();
		
		for(Ant enemy : visibleEnemies()){
			if(enemy.sugarCarry > 0)
				enemyCarriers.add(enemy);
			if(enemy.caste.equals(Caste.Gatherer))
				enemyGatherers.add(enemy);
		}
		
		if(enemyHill == null || vectorTo(enemyHill).length() > 70 || enemyGatherers.isEmpty())
			departingEnemyHill = false;
		
		if(!enemyCarriers.isEmpty())
			attack(closest(enemyCarriers));
		else if(!enemyGatherers.isEmpty())
			attack(closest(enemyGatherers));
		else
			attack(closest(visibleEnemies()));
		
		int guardCount = 0;
		//don't sit on a hill with just guards
		for(Ant ant : visibleEnemies())
			if(ant.caste.equals(Caste.Soldier) && vectorTo(ant).length() < 20)
				guardCount++;
		
		if(departingEnemyHill){
			moveInDirection(getLastMovementDirection());//move away from enemy hill
		}else if(!visibleHills.isEmpty() && vectorToHome().length() > 110 && (!visibleEnemies().isEmpty() || parameters.NUMBER_OF_PLAYERS == 2)){
			enemyHill = closest(visibleHills);
			if(guardCount > 3 && enemyGatherers.size() > 3){
				moveToward(closest(enemyGatherers));
				departingEnemyHill = true;
			}else{
				moveToward(enemyHill);
			}
		}else if(!enemyCarriers.isEmpty())
			moveToward(closest(enemyCarriers));
		else if(!enemyGatherers.isEmpty())
			moveToward(closest(enemyGatherers));
		else if(!visibleEnemies().isEmpty())
			moveToward(closest(visibleEnemies()));
		else
			moveInDirection(235);
	}

}
