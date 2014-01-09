/**
 * 
 */
package wota.ai.clonewarriors;

import java.util.HashMap;
import java.util.Set;

import wota.gameobjects.*;
import wota.utility.SeededRandomizer;

/**
 *
 */
public class SoldierAI extends AntAI {
	int direction = SeededRandomizer.getInt(360);
	boolean firstTick = true;
	HashMap<Integer, Hill> enemyHills = new HashMap<Integer, Hill>();
	int num_tick;
	
	final int TICK_ORDER_66 = 100;
	final int ORDER_66_RANGE = 50;
	
	enum State {ORDER_66, NORMAL_ATTACK};
	
	private State state = State.NORMAL_ATTACK;
	
	@Override
	public void tick() throws Exception {
						
		// get num_tick from Queen
		if(firstTick) { 
			num_tick = get_num_tick();
			firstTick = false;
		}
		else {
			num_tick++;
		}
		
		if (num_tick >= TICK_ORDER_66 && enemyHills.size() > 1) {
			Hill enemyHill = get_enemy_hill();
			if (vectorTo(enemyHill).length() > ORDER_66_RANGE) {
				state = State.ORDER_66;
			}
		}
		
		detect_enemy_hills();
		
		switch (state) {
		case NORMAL_ATTACK: normal_attack(); break;
		case ORDER_66: order_66(); break;
		}
		
		
	}
	
	private boolean isAt(Snapshot target) {
		return vectorTo(target).length() < 1.e-6;
	}
	
	private Hill get_enemy_hill() {
		Hill enemyHill = null;
		Set<Integer> ids =  enemyHills.keySet();
		for (Integer i : ids) {
			if (i.intValue() != self.playerID) {
				enemyHill = enemyHills.get(i);
			}
		}
		return enemyHill;
	}

	private void order_66() {
		Hill enemyHill = get_enemy_hill();
		moveToward(enemyHill);
		if (isAt(enemyHill)) {
			state = State.NORMAL_ATTACK;
		}
	}

	
	private void normal_attack() {
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
	
	private int get_num_tick() {
		for (Message message : audibleMessages) {
			if (message.sender.caste == Caste.Queen) {
				return message.content;
			}
		}
		return -1;
	}
	
	private void detect_enemy_hills() {
		for (Hill hill : visibleHills) {
			enemyHills.put(hill.playerID, hill);
		}
	}
	
}
