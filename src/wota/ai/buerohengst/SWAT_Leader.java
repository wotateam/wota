package wota.ai.buerohengst;

import java.util.LinkedList;
import java.util.Random;

import wota.gameobjects.*;
import wota.utility.SeededRandomizer;

public class SWAT_Leader extends AntAI {
	
	public enum State {
		WAIT_FOR_TEAM_TO_COMPLETE,
		LOOK_FOR_SUGAR,
		PROTECT_SUGAR
	}
	
	private State state = State.WAIT_FOR_TEAM_TO_COMPLETE;
	
	/** sugar source to protect */
	private Sugar sugar;
	
	int tick = 0;
	int direction = SeededRandomizer.getInt(360);
	LinkedList<Ant> team = new LinkedList<Ant>();
	
	public final static int TEAM_SIZE = 5;
	
	@Override
	public void tick() throws Exception {
		tick++;
				
		switch (state) {
		case WAIT_FOR_TEAM_TO_COMPLETE:
			listenForNewMember();
			
			if (team.size() >= TEAM_SIZE) {
				state = State.LOOK_FOR_SUGAR;
			}
			break;
			
		case LOOK_FOR_SUGAR:
			if (tick % 40 == 0) {
				direction = SeededRandomizer.getInt(360);
			}
			if ( !visibleSugar.isEmpty() ) {
				sugar = visibleSugar.get(0);
				state = State.PROTECT_SUGAR;
			}
			else {
				moveInDirection(direction);
			}
			break;
			
		case PROTECT_SUGAR:
			if (vectorTo(sugar).length() <= self.caste.SIGHT_RANGE) {
				sugar = SWAT_Member.getRecent(visibleSugar, sugar);
				if (sugar == null) {
					state = State.LOOK_FOR_SUGAR;
				}
				if ( !visibleEnemies().isEmpty() ) {
					attackWeakest();
				}
				else {
					moveToward(sugar);
				}
			}
			else {
				moveToward(sugar);
			}

			break;
		}
		
	}

	private void attackWeakest() {
		Ant weakest = BrutalAttacker.getWeakest(visibleEnemies());
		moveToward(weakest);
		reportAttackAndGoAway(weakest);		
	}

	private void listenForNewMember() {
		for (AntMessage message : audibleAntMessages) {
			if (message.content == HillAI.FOUND_LEADER) {
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
		talk(HillAI.ATTACK, target);
	}
}
