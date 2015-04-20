package wota.ai.firstopponent;

import wota.gamemaster.AIInformation;
import wota.gameobjects.*;
import wota.utility.SeededRandomizer;

public class GathererAI extends AntAI {

	private static final int DIRECTION_CHANGE_PERIOD = 50;
	private int ticksUntilNextDirectionChange = 0;
	
	@Override
	public void tick() throws Exception {
		if (self.sugarCarry > 0) {
			moveHome();
			ticksUntilNextDirectionChange = 0;
		} else if (visibleSugar.size() > 0) {
			Sugar sugar = visibleSugar.get(0);
			moveToward(sugar);
			pickUpSugar(sugar);
		} else if (ticksUntilNextDirectionChange == 0) {
			moveInDirection(random.nextInt(360));
			ticksUntilNextDirectionChange = DIRECTION_CHANGE_PERIOD;
		} else {
			moveAhead();
			ticksUntilNextDirectionChange--;
		}
		
	}

}
