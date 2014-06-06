package wota.ai.loadingpleasewait;

import wota.gameobjects.AntAI;
import wota.gameobjects.Caste;

/**
 *  This ant simply guards the hill
 */
public class GuardAntAI extends AntAI {

	public static final int LOW_HEALTH = 1;
	public static final int UNDER_ATTACK = -3;
	
	private double angle;
	
	@Override
	public void tick() {
		attack(closest(visibleEnemies()));
		
		if(vectorToHome().length() > 45)
			moveHome();
		else if(!visibleEnemies().isEmpty())
			moveToward(closest(visibleEnemies()));
		else
			orbitHome();
		
		//call for help
		if(visibleEnemies().size() > 4 && visibleEnemies().get(0).caste.equals(Caste.Soldier))
			talk(UNDER_ATTACK);
		else
			talk((int) self.health);
	}
	
	/**
	 * Move around the hill at a distance of 45
	 */
	public void orbitHome(){
		moveInDirection(angle);
		if(angle >= 360)
			angle = 0;
		else
			angle += 20;
	}

}
