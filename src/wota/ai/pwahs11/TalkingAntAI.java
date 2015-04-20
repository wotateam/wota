package wota.ai.pwahs11;
import java.util.LinkedList;
import java.util.List;

import wota.gameobjects.*;
import wota.utility.SeededRandomizer;

class MessageContent {
	int type = -1;
	int id = 0;
	boolean alive = false; 
	void decode(int enc) {
		if (enc < 0) {
			type = enc;
		} else {
			alive = (enc % 2 == 1);
			enc /= 2;
			type = enc % 100;
			enc /= 100;
			id = enc;
		}
	}
	int encode() {
		if (type < 0) return type;
		int enc = id;
		enc = enc * 100 + type;
		enc = enc * 2 + (alive ? 1 : 0);
		return enc;
	}
	//constructors:
	MessageContent(int enc) {
		decode(enc);
	}
	//constructors:
	MessageContent(int type_, int id_, boolean alive_or_dead_) {
		type = type_;
		id = id_;
		alive = alive_or_dead_;
	}
}

class SnapshotMessagePair {
	MessageContent m;
	Snapshot s;
	SnapshotMessagePair(MessageContent m_, Snapshot s_){
		m = m_;
		s = s_;
	}
	//with dummy-MessageContent:
	public SnapshotMessagePair(int type_, Snapshot s_) {
		m = new MessageContent(type_, 0, false);
		s = s_;
	}
}

public abstract class TalkingAntAI extends AntAI {
	int time = 0;
	int initialTime = 0;
	double dir = 0;
	//Array of LinkedList:
	@SuppressWarnings("unchecked")
	LinkedList<SnapshotMessagePair> []hills = (LinkedList<SnapshotMessagePair>[]) new LinkedList[HillAI.NR_HILLS];
		
	int []indices = new int[HillAI.NR_HILLS];
	int nr_hill = 0;

	@Override
	public void tick() throws Exception {
		time++;			
		if (time == 1) first_steps();		
		listen();		
		have_a_look_around();
		shout();
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
				nosugar(sm.s);
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
	
	public void listen(){
		for(Message m: audibleAntMessages){
			interpret(m);
		}
		if (audibleHillMessage!=null) interpret(audibleHillMessage);
	}
	
	public void have_a_look_around(){
		//check hills:
		for(Hill hill: visibleHills){
			HillAI.update_list(hills[HillAI.HILL_IND],
					new SnapshotMessagePair(HillAI.HILL, hill));
		}
		//check visible sugar:
		for(Sugar sugar: visibleSugar){
			if (HillAI.update_list(hills[HillAI.SUGAR_IND], 
					new SnapshotMessagePair(HillAI.SUGAR,  sugar))) {
				foundsugar(sugar);
			}
		//System.out.printf("(talking) ID=%d, known sugar hills: %d\n", self.id, hills.get(HillAI.SUGAR_IND).size());
		}
		//look around for sugar that should be here:
		List<SnapshotMessagePair> sugarhills = hills[HillAI.SUGAR_IND];
		for(SnapshotMessagePair sm: sugarhills){
			Snapshot sugar = sm.s;
			if (vectorBetween(self, sugar).length() < self.caste.SIGHT_RANGE
				&& !HillAI.contained_in_sugar_list(visibleSugar,sugar)) {
				sm.m.type = HillAI.NOSUGAR;
				HillAI.update_list(hills[HillAI.NOSUGAR_IND], sm);
				HillAI.delete(hills[HillAI.SUGAR_IND], sugar);
				nosugar(sugar);
				break;
			}
		}
		//look around for corpses:
		for(AntCorpse ac: visibleCorpses) {
			for(SnapshotMessagePair sm: hills[HillAI.SUGAR_IND]) {
				if (sm.m.alive && sm.m.id == ac.id) {
					sm.m.alive = false;
				}
			}
		}
	}
	
	public void shout(){
		do{
			nr_hill = (nr_hill + 1) % HillAI.NR_HILLS;
		}
		while (hills[nr_hill].size() == 0);
		indices[nr_hill]++;
		if (indices[nr_hill] >= hills[nr_hill].size()) indices[nr_hill] = 0;
		if (nr_hill==HillAI.NOSUGAR_IND
				&& indices[nr_hill] + HillAI.SHOUT_LAST_NOSUGAR < hills[nr_hill].size()){
			indices[nr_hill] = hills[nr_hill].size() - HillAI.SHOUT_LAST_NOSUGAR;
		}
		SnapshotMessagePair sm = hills[nr_hill].get(indices[nr_hill]);
		talk(sm.m.encode(),sm.s);
	}
	
	public void first_steps(){
		if (time == 1){		//was just born, figure out time and directions:
			//find time:
			if (audibleHillMessage != null){
				if (audibleHillMessage.content < 0) {
					initialTime = audibleHillMessage.content - HillAI.OFFSET_TIME;
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
			
			//initialize indices:
			for(int i = 0 ; i < HillAI.NR_HILLS ; ++i) {
				hills[i] = new LinkedList<SnapshotMessagePair>();
				indices[i] = 0;
			}
			//add my hill:
			for(Hill h: visibleHills){
				if (h.playerID == self.playerID){
					SnapshotMessagePair sm = new SnapshotMessagePair(HillAI.HILL, h);
					hills[HillAI.HILL_IND].add(sm);					
				}
			}
			nr_hill = 0;		
		}
	}
	
	public abstract void foundsugar(Snapshot sugar);
	public abstract void nosugar(Snapshot sugar);
	
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
