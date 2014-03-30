/**
 * 
 */
package wota.ai.dichterunddenker;

import wota.gamemaster.AIInformation;
import wota.gameobjects.Caste;
import wota.utility.SeededRandomizer;
/*
 *
 */
@AIInformation(creator = "Simon", name = "JohannWolfgangvonGoethe")
public class HillAI extends wota.gameobjects.HillAI {
	public int counter=0;
	public boolean endgame=false;
	/*
	 * your Queen is not able to move but can
	 * communicate and create new ants. 
	 * 
	 * You can create new ants with				createAnt(caste, antAIClass)		
	 * e.g. if you want a gatherer and the AI
	 * you want use is called SuperGathererAI	createAnt(Caste.Gatherer, SuperGathererAI.class)
	 * 
	 */
	@Override
	public void tick() throws Exception {
		counter++;
		if(counter==500){
			endgame=true;
			counter=0;
		}
		talk(random.nextInt(360));
		/* 
		 * try to create an Ant using the TemplateAI in every tick
		 * if you don't have enough food to create the ant your call
		 * will be ignored
		 */
		if(endgame==false){
			if(random.nextInt(10)<9){
				createAnt(Caste.Gatherer, FamilieMann.class);
			}else{
				if(random.nextDouble()<0.6){
					createAnt(Caste.Soldier, FranzKafka.class);
					createAnt(Caste.Soldier, FranzKafka.class);
				}else{
					createAnt(Caste.Soldier, ErnestHemingway.class);
					createAnt(Caste.Soldier, ErnestHemingway.class);
				}
			
			}
		}else{
			if(counter==55){
				counter=0;
				if(random.nextInt(10)<5){
					createAnt(Caste.Gatherer, FamilieMann.class);
				}else{
					if(random.nextDouble()<0.8){
						createAnt(Caste.Soldier, FranzKafka.class);
						createAnt(Caste.Soldier, FranzKafka.class);
						createAnt(Caste.Soldier, FranzKafka.class);
					}else{
						createAnt(Caste.Soldier, ErnestHemingway.class);
						createAnt(Caste.Soldier, ErnestHemingway.class);
					}
				
				}
				for(int i=0;i<100;i++){
					createAnt(Caste.Gatherer, FamilieMann.class);
				}
			}
			
		}
			
		
	}

}
