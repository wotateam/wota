package wota.ai.clonewarriors;

import java.util.Random;

import wota.ai.*;
import wota.gameobjects.Ant;
import wota.gameobjects.AntAI;
import wota.gameobjects.Hill;
import wota.gameobjects.Parameters;
import wota.gameobjects.Sugar;
import wota.utility.SeededRandomizer;


//@AIInformation(creator = "Pascal", name = "Clone Warriors")
public class GathererAI extends AntAI {

	private double lastDir = 0;
	private double health = -1;
	boolean lostHealth = false;
	
	final double RUN_AWAY_FACTOR = 3; // factor of which Ants must be overpowered to run away
	
	@Override
	public void tick() {
		
		update_health();
		
		if (lastDir == 0)
			lastDir = SeededRandomizer.getInt(360);
		
		gather_sugar();
		
		run_away();
		
	}
	
	private void update_health() {
		lostHealth = health > self.health;
		health = self.health;
	}
	
	private void gather_sugar() {
		if (self.sugarCarry == 0) {
			if (visibleSugar.size() == 0)
				moveInDirection(lastDir);
			else { // sugar is visible, determine closest
				Sugar closest = null;
				double closestDistance = Double.MAX_VALUE;
				for (Sugar sugar : visibleSugar) {
					if (vectorTo(sugar).length() < closestDistance) {
						closestDistance = vectorTo(sugar).length();
						closest = sugar;
					}
				}
				// if reachable grab it!
				if (closestDistance < closest.radius) { 
					pickUpSugar(closest);
				}
				else { // go to next
					moveToward(closest);
				}
			}
		}
		
		else { // sugarCarry > 0
			moveHome();
			lastDir = getLastMovementDirection() + 180;
		}
	}

	private void run_away() {
		if (RUN_AWAY_FACTOR*visibleEnemies().size() <= visibleFriends().size() + 0.5) {
			return;
		}
		Ant enemy = closest(visibleEnemies());
		double distance = vectorTo(enemy).length();
		if (distance < parameters.ATTACK_RANGE) {
			moveInDirection(vectorTo(enemy).scale(-1).angle());
		}
		if (lostHealth) {
			dropSugar();
			System.out.println("drop!");
		}
	}

}
