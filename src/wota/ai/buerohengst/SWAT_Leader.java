package wota.ai.buerohengst;

import java.util.LinkedList;
import java.util.Random;

import wota.gameobjects.*;
import wota.utility.SeededRandomizer;

public class SWAT_Leader extends AntAI {

	Random random = new Random();
	int tick = 0;
	int direction = SeededRandomizer.getInt(360);
	LinkedList<Ant> team = new LinkedList<Ant>();
	
	@Override
	public void tick() throws Exception {
		tick++;
		
		System.out.println(self.health);
		
		listenForNewMember();
		
		// only act after team has total size 6
		if (team.size() <= 4) {
			return;
		}
		
		if (tick % 20 == 0) {
			direction = SeededRandomizer.getInt(360);
			System.out.println("changedDirection: " + direction);
		}
		
		if (visibleEnemies().size() != 0) {
			attackWeakest();
		}
		else {
			moveInDirection(direction);
		}
		
	}

	private void attackWeakest() {
		Ant weakest = BrutalAttacker.getWeakest(visibleEnemies());
		moveToward(weakest);
		reportAttackAndGoAway(weakest);		
	}

	private void listenForNewMember() {
		for (Message message : audibleMessages) {
			if (message.content == QueenAI.FOUND_LEADER) {
				if (message.contentAnt.hasSameOriginal(self)) {
					team.add(message.sender);
				}
			}
		}
	}


	private void reportAttackAndGoAway(Ant target) {
		if (vectorTo(target).length() <= parameters.ATTACK_RANGE*2) {
			attack(target);
			// move away
			moveInDirection(vectorTo(target).scale(-1).angle());
		}
		talk(QueenAI.ATTACK, target);
	}
}
