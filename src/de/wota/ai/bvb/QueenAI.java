package de.wota.ai.bvb;

import de.wota.gameobjects.Caste;

public class QueenAI extends de.wota.gameobjects.QueenAI {

	@Override
	public void tick() throws Exception {
		while(true){
			createAnt(Caste.Soldier,Mao.class);
		}
		
		
	}

}
