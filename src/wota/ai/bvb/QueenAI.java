package wota.ai.bvb;

import wota.gamemaster.AIInformation;
import wota.gameobjects.Caste;

@AIInformation(creator = "David und Simon", name = "BVB")
public class QueenAI extends wota.gameobjects.QueenAI {
	int j=0;
	int n=1;
	@Override
	public void tick() throws Exception {
			for(int i=0;i<10;i++){
				createAnt(Caste.Soldier,Mao.class);	
			}
			n++;
		talk(36*n+1);	
	}
}
