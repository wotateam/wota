package de.wota.ai;

import de.wota.ai.solitary.SoldierAI;
import de.wota.gamemaster.AIInformation;
import de.wota.gameobjects.Caste;
import de.wota.utility.SeededRandomizer;

@AIInformation(creator = "WotA-Team", name = "AggressiveQueen")
public class QueenAI extends de.wota.gameobjects.QueenAI {
	
	@Override
	public void tick() {
		createAnt(Caste.Soldier, SoldierAI.class);
	}
}
