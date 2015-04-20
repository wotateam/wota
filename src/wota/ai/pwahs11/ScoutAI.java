package wota.ai.pwahs11;
import java.util.LinkedList;

import wota.gamemaster.AIInformation;
import wota.gameobjects.*;
import wota.utility.Vector;

@AIInformation(creator = "pwahs", name = "pwahs11")
public class ScoutAI extends TalkingAntAI {
	
	Vector last_position = null;
	boolean head_home = false;
	
	@Override
	public void tick() throws Exception {
		super.tick();
		if (head_home) {
			moveHome();
			if (vectorToHome().length() <= HillAI.EPS){
				head_home = false;
			}
		}
		if (!head_home && last_position != null) {
			moveToward(last_position);
			if (Vector.subtract(last_position, self.position).length() <= HillAI.EPS) {
				last_position = null;
			}
		} 
		if (!head_home && last_position == null) {
			moveInDirection(dir);			
		}		
	}
	
	@Override
	public void first_steps(){
		if (time == 1){		//was just born, figure out time and directions:
			//find time:
			if (audibleHillMessage != null){
				if (audibleHillMessage.content < 0) {
					initialTime = audibleHillMessage.content - HillAI.OFFSET_TIME;
				}
			}
			//compute direction, assumes square arena
			dir = Math.asin(self.caste.SIGHT_RANGE / parameters.SIZE_X) * 360 / Math.PI
					+ (self.id * 90) % 360;
			//initialize hills:
			for(int i = 0 ; i < HillAI.NR_HILLS ; ++i) {
				hills[i] = new LinkedList<SnapshotMessagePair>();
				indices[i] = 0;
			}
			//add my hill:
			for(Hill h: visibleHills){
				if (h.playerID == self.playerID){
					hills[HillAI.HILL_IND].add(new SnapshotMessagePair(HillAI.HILL, visibleHills.get(0)));					
				}
			}
			nr_hill = 0;		
		}
	}

	@Override
	public void foundsugar(Snapshot sugar) {
		if (last_position == null) {
			double angle = dir/180 * Math.PI;
			Vector dir_vector = new Vector(Math.cos(angle), Math.sin(angle));
			last_position = Vector.add(self.position, dir_vector.scale(2*self.caste.SIGHT_RANGE));
			while (last_position.x < 0) last_position.x += parameters.SIZE_X;
			while (last_position.y < 0) last_position.y += parameters.SIZE_Y;
			while (last_position.x > parameters.SIZE_X) last_position.x -= parameters.SIZE_X;
			while (last_position.y > parameters.SIZE_Y) last_position.y -= parameters.SIZE_Y;
			
		}
		head_home = true;
	}
	
	@Override
	public void shout() {
		if (!head_home && last_position==null) {
			super.shout();
		} else {
			int sind = HillAI.SUGAR_IND;
			indices[sind]++;
			if (indices[sind] >= hills[sind].size()) indices[sind] = 0;
			SnapshotMessagePair sm = hills[sind].get(indices[sind]);
			talk(sm.m.encode(),sm.s);
		}
	}
	
	@Override
	public void nosugar(Snapshot s) {
		//do nothing
	}
}
