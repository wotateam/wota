/**
 * 
 */
package wota.ai.pwahs05; /* <-- change this to wota.ai.YOUR_AI_NAME
 							  * make sure your file is in the folder /de/wota/ai/YOUR_AI_NAME
 							  * and has the same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */

import java.util.LinkedList;
import java.util.List;

import wota.gameobjects.*;
import wota.utility.SeededRandomizer;

/**
 * Put a description of you AI here.
 */
public class TalkingAntAI extends AntAI {

	int time = 0;
	int initialTime = 0;
	int travelTime = 0;
	double dir = -1;
	
	List<LinkedList<Snapshot>> hills = new LinkedList<LinkedList<Snapshot>>();
	int []indices = new int[HillAI.NR_HILLS];
	int nr_hill = 0;
	
	public void nosugar(Snapshot s){
		
	}
	
	public void interpret(Message m){
		switch(m.content){
		case HillAI.SUGAR:
			if (!HillAI.contained(hills.get(HillAI.NOSUGAR_IND),m.contentSugar)){
				HillAI.add_if_not_contained(hills.get(HillAI.SUGAR_IND),m.contentSugar);
			}
			break;
		case HillAI.NOSUGAR:
			if (HillAI.add_if_not_contained(hills.get(HillAI.NOSUGAR_IND),m.contentSugar)){
				HillAI.delete(hills.get(HillAI.SUGAR_IND),m.contentSugar);
				nosugar(m.contentSugar);
			}
			break;
		case HillAI.HILL:
			HillAI.add_if_not_contained(hills.get(HillAI.HILL_IND),m.contentHill);
			break;
		}
		//System.out.printf("%d: %d\n", self.id, m.content);
	}
	
	public void listen(){
		for(Message m: audibleAntMessages){
			interpret(m);
		}
		if (audibleHillMessage!=null) interpret(audibleHillMessage);
	}
	
	public void have_a_look_around(){
		for(Hill hill: visibleHills){
			HillAI.add_if_not_contained(hills.get(HillAI.HILL_IND),hill);
		}
		for(Sugar sugar: visibleSugar){
			
			HillAI.add_if_not_contained(hills.get(HillAI.SUGAR_IND), sugar);
		//System.out.printf("(talking) ID=%d, known sugar hills: %d\n", self.id, hills.get(HillAI.SUGAR_IND).size());
		}
		List<Snapshot> sugarhills = hills.get(HillAI.SUGAR_IND);
		for(Snapshot sugar: sugarhills){
			if (vectorBetween(self, sugar).length() < self.caste.SIGHT_RANGE
				&& !HillAI.contained(sugar, visibleSugar)) {
				HillAI.add_if_not_contained(hills.get(HillAI.NOSUGAR_IND), sugar);
				HillAI.delete(hills.get(HillAI.SUGAR_IND), sugar);
				break;
			}
		}
	}
	
	public void shout(){
		do{
			nr_hill = (nr_hill + 1) % HillAI.NR_HILLS;
		}
		while (hills.get(nr_hill).size() == 0);
		indices[nr_hill]++;
		if (indices[nr_hill]>=hills.get(nr_hill).size()) indices[nr_hill] = 0;
		
		if (nr_hill==HillAI.NOSUGAR_IND
				&& indices[nr_hill] < hills.get(nr_hill).size() - HillAI.SHOUT_LAST_NOSUGAR){
			indices[nr_hill] = hills.get(nr_hill).size() - HillAI.SHOUT_LAST_NOSUGAR;
		}
		
		talk(nr_hill+HillAI.HILL_OFFSET,
				hills.get(nr_hill).get(indices[nr_hill]));
	}
	
	public void first_cry(){
		if (time == 1){		//was just born, figure out time and directions:
			
			//find time:
			if (audibleHillMessage != null){
				if (audibleHillMessage.content < 0) {
					initialTime = audibleHillMessage.content - HillAI.initTime;
				}
			}
			if (initialTime==1) {
				//in the beginning, figure out direction:
				int count = 0;
				int total = 0;
				for (Ant a: visibleFriends()) {
					if (a.caste.compareTo(self.caste) == 0) {
						total++;
						if (a.id < self.id) count++;
					}
				}
				
				dir = (360*(count - (count % 2) * HillAI.STARTER_PAIRING))/(total+1);
			}else{//later, just go some random direction:
				dir = SeededRandomizer.nextDouble()*360;
			}
			
			//initialize hills:
			for(int i = 0 ; i < HillAI.NR_HILLS ; ++i) {
				hills.add(new LinkedList<Snapshot>());
				indices[i] = 0;
			}
			
			//add my hill:
			for(Hill h: visibleHills){
				if (h.playerID == self.playerID){
					hills.get(HillAI.HILL_IND).add(visibleHills.get(0));					
				}
			}
			nr_hill = 0;		
		}
	}
	
	public void handle_sugar(){
		
	}
	
	public void other_stuff(){
		
	}
	
	@Override
	public void tick() throws Exception {
		
		time++;
			
		first_cry();
		
		listen();
		
		have_a_look_around();
		
		moveInDirection(dir);
		
		handle_sugar();
		
		if (visibleEnemies().size()>0 && self.caste != Caste.Scout){
			Ant enemy = getWeakest(visibleEnemies());
			if (enemy.sugarCarry>0 && self.sugarCarry == 0){
				moveToward(enemy);
				attack(enemy);
			}
		}
		
		shout();

		other_stuff();
	}

	public Ant getWeakest(List<Ant> enemies) {
		Ant weakest = null;
		double health = Double.MAX_VALUE;
		for (Ant enemy : enemies) {
			double factor = 1;
			if (enemy.sugarCarry > 0) factor = 1.0 / parameters.VULNERABILITY_WHILE_CARRYING;
			if (enemy.health * factor < health) {
				health = enemy.health * factor;
				weakest = enemy;
			}
		}
		return weakest;
	}
	
	public Ant getWeakestReachable(List<Ant> enemies) {
		Ant weakest = null;
		double health = Double.MAX_VALUE;
		for (Ant enemy : enemies) {
			if (vectorBetween(self, enemy).length() > parameters.ATTACK_RANGE)
				continue;
			
			double factor = 1;
			if (enemy.sugarCarry > 0) factor = 1.0 / parameters.VULNERABILITY_WHILE_CARRYING;
			if (enemy.health * factor < health) {
				health = enemy.health * factor;
				weakest = enemy;
			}
		}
		return weakest;
	}
	
	public Ant getClosest(List<Ant> enemies) {
		Ant closest = null;
		double distance = Double.MAX_VALUE;
		for (Ant enemy : enemies) {
			double d = vectorBetween(self,enemy).length();
			if (d < distance && enemy.caste == Caste.Soldier) {
				distance = d;
				closest = enemy;
			}
		}
		if (closest == null){
			for (Ant enemy : enemies) {
				double d = vectorBetween(self,enemy).length();
				if (d < distance) {
					distance = d;
					closest = enemy;
				}
			}
		}
		return closest;
	}
}
