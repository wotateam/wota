package wota.ai;

import wota.ai.solitary.SoldierAI;
import wota.gamemaster.AIInformation;
import wota.gameobjects.Caste;
import wota.utility.SeededRandomizer;

@AIInformation(creator = "WotA-Team", name = "Aggressive")
public class QueenAI extends wota.gameobjects.QueenAI {
	
	@Override
	public void tick() {
		createAnt(Caste.Soldier, SoldierAI.class);
	}
}
