/**
 * 
 */
package wota.ai.pwahs05;

import java.util.LinkedList;
import java.util.List;


import wota.gameobjects.Sugar;
import wota.gamemaster.AIInformation;
import wota.gameobjects.Ant;
import wota.gameobjects.Caste;
import wota.gameobjects.Message;
import wota.gameobjects.Snapshot;


/**
 * 
 */
@AIInformation(creator = "Philipp", name = "pwahs05")
public class HillAI extends wota.gameobjects.HillAI {

	/*
	 * your Hill is not able to move but can
	 * communicate and create new ants. 
	 * 
	 * You can create new ants with				createAnt(caste, antAIClass)		
	 * e.g. if you want a gatherer and the AI
	 * you want use is called SuperGathererAI	createAnt(Caste.Gatherer, SuperGathererAI.class)
	 * 
	 */
	int time = 0;
	int antCount = 0;
	
	public static final int initTime = -10000;

	public static final int FIGHT = 1;
	public static final int number_fighters = 50;
	
	public static final int SUGAR_IND = 0;
	public static final int NOSUGAR_IND = 1;
	public static final int HILL_IND = 2;
	public static final int NR_HILLS = 3;
	
	public static final int HILL_OFFSET = 3;
	public static final int SUGAR = HILL_OFFSET+SUGAR_IND;
	public static final int NOSUGAR = HILL_OFFSET+NOSUGAR_IND;
	public static final int HILL = HILL_OFFSET+HILL_IND;
	
	public static final int SHOUT_LAST_NOSUGAR = 3;
	
	public static final int NEW_LEADER = 10;
	public static final int NEW_TARGET = 11;
	public static final int NEED_NEW_LEADER = 12;
	
	public static final double SUGAR_PROB = 0.6;
	
	public static final int GUARDLENGTH = 1;
	public static final int QUEUELENGTH = 3;
	public static final int WAITLENGTH = 5;
	
	public static final int STOP_LOOKING = 0;
	
	public static final int SWAT_TEAM_SIZE = 2;
	public static final int SWAT_TEAM_START_AMOUNT = 0;
	
	public static final double COMBAT_RADIUS = 0.8;
	public static final double EPS = 0.005;
	
	public static final int FORWARD = 40;
	public static final int BACKWARD = 70;
	
	public static final int ENOUGH_GATHERER = 150;
	public static final double STARTER_PAIRING = 0;
	
	public static final double MAX_DISTANCE_TO_ATTACKED_HILL = Caste.Soldier.SIGHT_RANGE;
	
	enum SwatStatus{
		WAITING,
		GUARDING,
		PATROLLING,
		ATTACKING,
		ARRIVING,
		ROAMING
	};
	

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
	
	
	
	@Override
	public void tick() throws Exception {
		time++;
		
		boolean new_ant = false;
		
		double myfood = self.food;
		
		if (time==1){
			//initialize hills:
			for(int i = 0 ; i < HillAI.NR_HILLS ; ++i) {
				hills.add(new LinkedList<Snapshot>());
				indices[i] = 0;
			}
			
			//add my hill:
			hills.get(HillAI.HILL_IND).add(self);
			nr_hill = 0;	
			
			createAnt(Caste.Scout, ScoutAI.class);
			myfood -= parameters.ANT_COST;
			antCount++;
			//saving up for SWAT teams:
			myfood -= HillAI.SWAT_TEAM_SIZE * HillAI.SWAT_TEAM_START_AMOUNT * parameters.ANT_COST;
			while (myfood >= parameters.ANT_COST){
				myfood -= parameters.ANT_COST;
				createAnt(Caste.Gatherer, SugarAI.class);
				antCount++;
				new_ant = true;
			}
		} else if (time <= 1+5*HillAI.SWAT_TEAM_START_AMOUNT) {
			if (time%5 == 0 ){
				for (int i = 0; i < HillAI.SWAT_TEAM_SIZE ; i++ ) {
					if (myfood >= parameters.ANT_COST){
						myfood -= parameters.ANT_COST;
						createAnt(Caste.Soldier, SoldierAI.class);
						new_ant = true;
						antCount++;
					}
				}			
			}
		} else {//time > 1:
			/*
			boolean fight = false;
			for(Message m: audibleAntMessages){
				if (m.content==HillAI.FIGHT){
					fight = true;
				}
			}*/
			
			while(myfood >= parameters.ANT_COST){
				if ((antCount%10==3 && antCount>20) || antCount >= ENOUGH_GATHERER){
					if (myfood >= HillAI.SWAT_TEAM_SIZE*parameters.ANT_COST){
						for(int i=0; i < HillAI.SWAT_TEAM_SIZE;i++){
							myfood-=parameters.ANT_COST;
							if (antCount%5!=0) createAnt(Caste.Soldier, SoldierAI.class);
							else createAnt(Caste.Gatherer, SugarAI.class);
							antCount++;
							new_ant = true;
						}
					} else break;
				}else{
					myfood -= parameters.ANT_COST;
					createAnt(Caste.Gatherer, SugarAI.class);
					antCount++;
					new_ant = true;
				}
			}
		}
		
		listen();

		if (new_ant) talk(time + HillAI.initTime);
		else {
			shout();
		}
		
	}
	
	public static boolean contained(List<Snapshot> list, Snapshot shot){
		for(Snapshot snap: list){
			if (snap.hasSameOriginal(shot)) return true;
		}
		return false;
	}
	
	public static boolean contained(Snapshot shot, List<Sugar> list){
		for(Snapshot snap: list){
			if (snap.hasSameOriginal(shot)) return true;
		}
		return false;
	}
	
	public static boolean add_if_not_contained(LinkedList<Snapshot> list, Snapshot shot){
		if (!contained(list,shot)){
			list.addLast(shot);
			return true;
		}
		return false;
	}
	
	public static void delete(LinkedList<Snapshot> list, Snapshot shot){
		List<Snapshot> marked_for_deletion = new LinkedList<Snapshot>();
		for(Snapshot snap: list){
			if (snap.hasSameOriginal(shot)) marked_for_deletion.add(snap);
		}
		list.removeAll(marked_for_deletion);
	}
	
	public Ant getWeakest(List<Ant> enemies) {
		Ant weakest = null;
		double health = Double.MAX_VALUE;
		for (Ant enemy : enemies) {
			double factor = 1;
			if (enemy.sugarCarry > 0) factor = parameters.VULNERABILITY_WHILE_CARRYING;
			if (enemy.health * factor < health) {
				health = enemy.health * factor;
				weakest = enemy;
			}
		}
		return weakest;
	}

}
