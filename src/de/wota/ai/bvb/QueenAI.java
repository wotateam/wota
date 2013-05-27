package de.wota.ai.bvb;

import de.wota.gameobjects.Caste;

public class QueenAI extends de.wota.gameobjects.QueenAI {

	@Override
	public void tick() throws Exception {
		createAnt(Caste.Gatherer,GathererAI.class);
		
	}

}
