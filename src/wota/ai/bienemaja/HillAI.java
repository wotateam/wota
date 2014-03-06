/**
 * 
 */
package wota.ai.bienemaja;

import wota.gamemaster.AIInformation;
import wota.gameobjects.Caste;
import wota.utility.SeededRandomizer;

/**
 *  Give your information about this HillAI here.
 */
@AIInformation(creator = "Simon", name = "Bienenkoenigin")
public class HillAI extends MyHillAI {

	/*
	 * your Hill is not able to move but can
	 * communicate and create new ants. 
	 * 
	 * You can create new ants with				createAnt(caste, antAIClass)		
	 * e.g. if you want a gatherer and the AI
	 * you want use is called SuperGathererAI	createAnt(Caste.Gatherer, SuperGathererAI.class)
	 * 
	 */
	@Override
	public void tick() throws Exception {
		
		/* 
		 * try to create an Ant using the TemplateAI in every tick
		 * if you don't have enough food to create the ant your call
		 * will be ignored
		 */
		double food=self.food;
		dowhatcanbedone();
		if(time==1){
			createAnt(Caste.Scout, Huepfer.class);
			scout+=1;
		}
		if(gatherer>10*scout && food>=parameters.ANT_COST && scout<5){
			food-=parameters.ANT_COST;
			scout+=1;
			createAnt(Caste.Scout, Huepfer.class);
		}
		while(food>=parameters.ANT_COST){
			//createAnt(Caste.Soldier, Thekla.class);
			if(random.getDouble()<0.5 || gatherer<60){//0.4*acceptance(time)+0.3){
				createAnt(Caste.Gatherer, BieneMaja.class);
				food-=parameters.ANT_COST;
				gatherer+=1;
			}else{
				createAnt(Caste.Soldier, Thekla.class);
				soldier+=1;
				food-=parameters.ANT_COST;
			}
		
		say(0);
	}

}
	
	
}
