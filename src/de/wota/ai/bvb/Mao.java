package de.wota.ai.bvb;

import de.wota.gameobjects.*;

public class Mao extends AntAI {

int dir=0;	
	@Override
	public void tick() throws Exception {
		// TODO Auto-generated method stub
		if(dir==0 && audibleMessages.size()>0){
			dir=audibleMessages.get(0).content;
		}
		if(visibleEnemies().size()>0){
			moveToward(closestAnt(visibleEnemies()));	
		}else{
			moveInDirection(dir);
		}
		attack(closestAnt(visibleEnemies()));

	}

}
