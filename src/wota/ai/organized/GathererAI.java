package wota.ai.organized;

import java.util.Iterator;

import wota.gamemaster.SimulationParameters;
import wota.gameobjects.Ant;
import wota.gameobjects.AntAI;
import wota.gameobjects.Caste;
import wota.gameobjects.Hill;
import wota.gameobjects.Message;
import wota.gameobjects.Sugar;
import wota.utility.SeededRandomizer;


public class GathererAI extends AntAI {
	// TODO it seems to be a double-edged sword that these gatherers are telling each other 
	// where they should go next, this is a problem when the sugar source is depleted and they 
	// do not realize this. Maybe this is not worth it when the queen starts talking, too.
	
	private boolean havePickedUpSugar = false;
	private double direction = SeededRandomizer.getInt(360);
	private boolean wasToldSugarDirection = false;
	
	@Override
	public void tick() throws Exception {
		if (audibleHillMessage.content >= HillAI.SUGAR_DIRECTION_START &&
				audibleHillMessage.content < HillAI.SUGAR_DIRECTION_START + 360 &&
				! wasToldSugarDirection) {
			direction = audibleHillMessage.content - HillAI.SUGAR_DIRECTION_START;
			wasToldSugarDirection = true;
		} /*else if (message.sender.caste == Caste.Gatherer) {
			direction = message.content;
			wasToldSugarDirection = true;
		}*/
		
		if (self.sugarCarry > 0 || havePickedUpSugar) { 
			// sugar is dropped automatically if inside hill 
			// keep moving home even if the ant is not carrying sugar, 
			// havePickedUpSugar will be set to false once the ant sees its queen
			moveHome();
			talk(Math.round((float) direction));
		} else if (visibleSugar.size() > 0) {
			Sugar sugar = closest(visibleSugar);
			if (vectorTo(sugar).length() < sugar.radius) {
				pickUpSugar(sugar);
				direction = 180 + vectorToHome().angle();
				havePickedUpSugar = true;
				wasToldSugarDirection = false; // next time, the sugar might not be around
			} else {
				moveToward(sugar);
			}
		} else {
			if (wasToldSugarDirection) {
				moveInDirection(direction);
			} else {
				direction = SeededRandomizer.getInt(360);
				wasToldSugarDirection = true; // told myself where to go
				moveInDirection(direction);
			}
		}
		
		
		for (Hill hill : visibleHills) {
			if (hill.playerID == self.playerID) {
				// by now, we should have talked about the sugar source we picked up sugar from
				// don't keep talking about it, it might be gone now.
				havePickedUpSugar = false;
			}
		}
		
	}
}
