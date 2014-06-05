package wota.ai.loadingpleasewait;

import wota.gameobjects.Caste;
import wota.utility.SeededRandomizer;

/**
 * This ant finds sugar.
 */
public class ScoutAILPW extends AntAILPW {
	
	private boolean foundSugar;
	private boolean checkSugar;
	
	@Override
	public void tick(){
		if (getRandomDirection() == -1) {
			setRandomDirection((int) (SeededRandomizer.nextDouble() * 360));
			checkSugar = SeededRandomizer.nextDouble() < 0.4;
		}
		
		if(closest(visibleSugar) != null)
			setTarget(closest(visibleSugar));
		
		if(canPickUpSugar())
			foundSugar = true;
		
		//if ant is at the hill go back out again
		if(vectorToHome().length() < Caste.Hill.HEARING_RANGE - 10)
			foundSugar = false;
		
		if(getTarget() != null && getTarget().amount < AntAILPW.VERY_LOW_SUGAR)
			setTarget(null);
		
		//move back to hill when sugar has been found
		if(foundSugar)
			moveHome();
		else if(checkSugar)
			super.tick();
		else if(closest(visibleSugar) != null)
			moveToward(closest(visibleSugar));
		else
			moveInDirection(getRandomDirection());
		
		if(getTarget() != null)
			talk(getTarget().amount,getTarget());
	}

}
