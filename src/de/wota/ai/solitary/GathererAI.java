package de.wota.ai.solitary;

import java.util.Random;

import de.wota.ai.*;
import de.wota.gameobjects.AntAI;
import de.wota.gameobjects.LeftoverParameters;
import de.wota.gameobjects.Hill;
import de.wota.gameobjects.Sugar;
import de.wota.utility.SeededRandomizer;

//@AIInformation(creator = "WotA-Team", name = "SolitaryAI")
public class GathererAI extends AntAI {

	private double lastDir = 0;
	
	@Override
	public void tick() {
		
		if (lastDir == 0)
			lastDir = SeededRandomizer.getInt(360);
		
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

}
