/**
 * 
 */
package wota.ai.pwahs11;

import java.util.LinkedList;
import java.util.List;

import wota.gamemaster.AIInformation;
import wota.gameobjects.Ant;
import wota.gameobjects.AntCorpse;
import wota.gameobjects.Caste;
import wota.gameobjects.Message;
import wota.gameobjects.Snapshot;
import wota.gameobjects.Sugar;


/**
 * 
 */
@AIInformation(creator = "Philipp", name = "pwahs11")
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
	
	public static final int OFFSET_TIME = -100000;

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
	
	public static final int SHOUT_LAST_NOSUGAR = 3; //how many no-sugar hills should be broadcasted
	
	public static final int NEW_LEADER = 10;
	public static final int NEW_TARGET = 11;
	public static final int NEED_NEW_LEADER = 12;
	
	public static final double SUGAR_PROB = 0.6;
	
	public static final int QUEUELENGTH = 1;
	public static final int WAITLENGTH = 5;
	public static final double TIME_FACTOR = 0.01;
	
	public static final int GREEDY_GATHERING = 300;
	public static final int SIEGE_TIME = 10000;
	public static final int END_SIEGE = 12000;
	public static final int SIEGE_LAYERS = 5;
	public static final int SIEGE_DIST = 4;
	
	public static final double COMBAT_RADIUS = 0.8;
	public static final double EPS = 0.005;
	
	public static final int ENOUGH_GATHERER = 150;
	public static final double STARTER_PAIRING = 0;
	
	public static final double MAX_DISTANCE_TO_ATTACKED_HILL = Caste.Soldier.SIGHT_RANGE;
	
	//Array of LinkedList:
	@SuppressWarnings("unchecked")
	LinkedList<SnapshotMessagePair> []hills = (LinkedList<SnapshotMessagePair>[]) new LinkedList[HillAI.NR_HILLS];
		
	int []indices = new int[HillAI.NR_HILLS];
	int nr_hill = 0;
	
	boolean new_ant = false;
	
	@Override
	public void tick() throws Exception {
		time++;
		new_ant = false;
		double myfood = self.food;
		if (time==1){
			//initialize indices:
			for(int i = 0 ; i < HillAI.NR_HILLS ; ++i) {
				hills[i] = new LinkedList<SnapshotMessagePair>();
				indices[i] = 0;
			}			
			//add my hill:
			hills[HillAI.HILL_IND].add(new SnapshotMessagePair(HillAI.HILL, self));
			nr_hill = 0;
			myfood = create_initial_ants(myfood);
		} else {//time > 1:
			listen();		
			have_a_look_around();
			shout();
			SnapshotMessagePair needs_most_protection = null;
			for (SnapshotMessagePair sm: hills[SUGAR_IND]) {
				if (!sm.m.alive) {
					//found a hill that is not guarded:
					if (needs_most_protection == null) {
						needs_most_protection = sm;
					} else {
						if (vectorTo(sm.s).length() > vectorTo(needs_most_protection.s).length()) {
							needs_most_protection = sm;
						}
					}
				}
			}
			if (needs_most_protection != null && !hire_guard(needs_most_protection)) {
				//create_guard:
				if (myfood >= parameters.ANT_COST) {
					myfood -= parameters.ANT_COST;
					createAnt(Caste.Soldier, GuardAI.class); 
					antCount++;
					new_ant = true;
				}
			}
			
			if (myfood >= parameters.ANT_COST) {
				myfood -= parameters.ANT_COST;
				createAnt(Caste.Gatherer, SugarAI.class); 
				antCount++;
				new_ant = true;
			}
		}
		if (new_ant) {
			talk(time + OFFSET_TIME);
			new_ant = false;
		}
	}
	
	private boolean hire_guard(SnapshotMessagePair sm) {
		if (sm == null) return false;
		for (Ant a: visibleAnts) {
			if (a.caste == Caste.Soldier && !has_a_job(a)) {
				sm.m.id = a.id;
				sm.m.alive = true;
				return true;
			}
		}
		return false;
	}

	private boolean has_a_job(Ant a) {
		for(SnapshotMessagePair sm: hills[SUGAR_IND]) {
			if (sm.m.id == a.id) {
				return true;
			}
		}
		for(SnapshotMessagePair sm: hills[HILL_IND]) {
			if (sm.m.id == a.id) {
				return true;
			}
		}
		return false;
	}
	
	private double create_initial_ants(double myfood) {	
		createAnt(Caste.Scout, ScoutAI.class);
		myfood -= parameters.ANT_COST;
		antCount++;
		while (myfood >= parameters.ANT_COST){
			myfood -= parameters.ANT_COST;
			createAnt(Caste.Gatherer, SugarAI.class);
			antCount++;
			new_ant = true;
		}
		return myfood;
	}
	
	public void interpret(Message m){
		MessageContent mc = new MessageContent(m.content);
		SnapshotMessagePair sm = new SnapshotMessagePair(mc,  m.contentSnapshot);
		switch(mc.type){
		case HillAI.SUGAR:
			if (!HillAI.contained(hills[HillAI.NOSUGAR_IND],sm.s)){
				if (!HillAI.update_list(hills[HillAI.SUGAR_IND], sm)) {
					//?
				};
			}
			break;
		case HillAI.NOSUGAR:
			if (HillAI.update_list(hills[HillAI.NOSUGAR_IND], sm)){
				HillAI.delete(hills[HillAI.SUGAR_IND],sm.s);
			}
			break;
		case HillAI.HILL:
			HillAI.update_list(hills[HillAI.HILL_IND], sm);
			break;
		default:
			if (mc.type < 0) {
				time = mc.type - HillAI.OFFSET_TIME;
			}
			break;
		}
		//System.out.printf("%d: %d\n", self.id, m.content);
	}
	
	private void listen(){
		for(Message m: audibleAntMessages){
			interpret(m);
		}
	}
	
	private void have_a_look_around(){
		//look around for corpses:
		for(AntCorpse ac: visibleCorpses) {
			for(SnapshotMessagePair sm: hills[HillAI.SUGAR_IND]) {
				if (sm.m.alive && sm.m.id == ac.id) {
					sm.m.alive = false;
				}
			}
		}
	}
	
	private void shout(){
		do{
			nr_hill = (nr_hill + 1) % HillAI.NR_HILLS;
		}
		while (hills[nr_hill].size() == 0);
		indices[nr_hill]++;
		if (indices[nr_hill] >= hills[nr_hill].size()) indices[nr_hill] = 0;
		if (nr_hill==HillAI.NOSUGAR_IND
				&& indices[nr_hill] < hills[nr_hill].size() - HillAI.SHOUT_LAST_NOSUGAR){
			indices[nr_hill] = hills[nr_hill].size() - HillAI.SHOUT_LAST_NOSUGAR;
		}
		SnapshotMessagePair sm = hills[nr_hill].get(indices[nr_hill]);
		talk(sm.m.encode(),sm.s);
	}
	
	public static boolean contained(List<SnapshotMessagePair> list, Snapshot shot){
		for(SnapshotMessagePair snap: list){
			if (snap.s.hasSameOriginal(shot)) return true;
		}
		return false;
	}
	
	public static boolean contained_in_sugar_list(List<Sugar> list, Snapshot shot) {
		for(Sugar sugar: list){
			if (sugar.hasSameOriginal(shot)) return true;
		}
		return false;
	}
	
	//returns true if change had been made to list
	public static boolean update_list(LinkedList<SnapshotMessagePair> list, SnapshotMessagePair new_el){
		for(SnapshotMessagePair old_el: list) {
			if (old_el.s.hasSameOriginal(new_el.s)) {
				if (old_el.m.alive) {
					if (!new_el.m.alive && old_el.m.id == new_el.m.id) {
						old_el.m = new_el.m;
						return true;
					}
				} else {
					if (new_el.m.alive && old_el.m.id != new_el.m.id) {
						old_el.m = new_el.m;
						return true;
					}
				}
				return false;
			}
		}
		list.addLast(new_el);
		return true;
	}
	
	public static void delete(LinkedList<SnapshotMessagePair> list, Snapshot shot){
		List<SnapshotMessagePair> marked_for_deletion = new LinkedList<SnapshotMessagePair>();
		for(SnapshotMessagePair sm: list){
			if (sm.s.hasSameOriginal(shot)) marked_for_deletion.add(sm);
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
