package wota.ai.bvb;

import wota.gameobjects.*;

public class Mao extends AntAI {

int dir=0;	
	@Override
	public void tick() throws Exception {
		if(dir==0 && audibleMessages.size()>0){
			dir=audibleMessages.get(0).content;
		}
		if(visibleEnemies().size()>0){
			moveToward(closest(visibleEnemies()));	
		}else{
			moveInDirection(dir);
		}
		attack(closest(visibleEnemies()));

	}

}
