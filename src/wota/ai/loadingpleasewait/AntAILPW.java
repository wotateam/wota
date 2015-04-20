/**
 * 
 */
package wota.ai.loadingpleasewait;

import java.util.ArrayList;
import java.util.LinkedList;

import wota.gameobjects.Ant;
import wota.gameobjects.AntAI;
import wota.gameobjects.AntCorpse;
import wota.gameobjects.AntMessage;
import wota.gameobjects.Caste;
import wota.gameobjects.Snapshot;
import wota.gameobjects.Sugar;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;

/**
 * This AI will go to the nearest Sugar and either guard or gather.
 */
public class AntAILPW extends AntAI {

	public static final int MIN_SUGAR = 30;
	public static final int VERY_LOW_SUGAR = 60;
	public static final int LOW_SUGAR = 90;

	public static final int NO_SUGAR = 0;
	public static final int BACKUP_CALL = -1;

	private ArrayList<Vector> emptySugarPositions = new ArrayList<Vector>();
	private Sugar emptySugar;
	private ArrayList<Sugar> sugarSites = new ArrayList<Sugar>();

	private Sugar target;
	private int randomDirection = -1;
	private boolean needBackup;
	private boolean returningHome;
	private boolean homeUnderAttack;
	private boolean avoidingHome;

	private Vector lastLocation;
	private int sittingDuration;
	private boolean stuck;
	private LinkedList<Ant> enemyCarriers = new LinkedList<Ant>();

	@Override
	public void tick() {

		if (getRandomDirection() == -1)
			setRandomDirection((int) (SeededRandomizer.nextDouble() * 360));

		enemyCarriers.clear();
		for (Ant enemy : visibleEnemies())
			if (enemy.sugarCarry > 0)
				enemyCarriers.add(enemy);
		if (!visibleEnemies().isEmpty() && self.sugarCarry == 0
				&& (self.caste.equals(Caste.Soldier) || self.health > 10 || avoidingHome)) {
			if (!enemyCarriers.isEmpty())
				attack(closest(enemyCarriers));
			else
				attack(closest(visibleEnemies()));

		}

		listen();

		if (closest(visibleSugar) != null && !emptySugarPositions.contains(closest(visibleSugar).getPosition())
				&& closest(visibleSugar).amount > MIN_SUGAR)
			setTarget(closest(visibleSugar));

		if (vectorToHome().length() < 20 && isReturningHome()) {
			setReturningHome(false);
			setRandomDirection(SeededRandomizer.nextInt(360));
		}

		if (vectorToHome().length() < 60 && enemySoldierCount() > 0)
			setHomeUnderAttack(true);

		if (preventGettingStuck())
			return;

		if (getTarget() != null
				&& (vectorTo(getTarget()).length() < getTarget().radius && visibleSugar.isEmpty() || emptySugarPositions
						.contains(getTarget().getPosition()))) {
			emptySugar = getTarget();
			sugarSites.remove(getTarget());
			if (!emptySugarPositions.contains(getTarget().getPosition()))
				emptySugarPositions.add(getTarget().getPosition());
			setTarget(null);
		}

		if (isSitting() && !avoidingHome && enemySoldierCount() == 0 && inMiddleOfRoute()) {
			// prevent sitting in one place and not moving
			assert (visibleSugar.isEmpty()) : "Sitting near sugar";
			setReturningHome(true);
			setStuck(true);
			moveHome();
		} else if (avoidingHome || enemySoldierCount() > 1 && visibleFriends().isEmpty() && emptySugarPositions.isEmpty()) {
			// don't bring an enemy home
			if (!visibleFriends().isEmpty() && vectorTo(closest(visibleFriends())).length() > 40)
				moveInDirection(vectorTo(closest(visibleFriends())).angle() - 180);
			else
				avoidHome();
			needBackup = true;
			avoidingHome = enemySoldierCount() > 0;
		} else if (self.health < 25 && !self.caste.equals(Caste.Soldier) && isHomeUnderAttack() && vectorToHome().length() < 65) {
			// wait before it is safe to go into the hill
			if (visibleEnemies().isEmpty())
				moveInDirection(vectorToHome().angle() - 180);
			else
				moveInDirection(vectorTo(closest(visibleEnemies())).angle() - 180);
		} else if (!self.caste.equals(Caste.Soldier) && inMiddleOfRoute() && inDanger() && self.health < 3) {
			if (vectorTo(closest(visibleEnemies())).length() < 25)
				dropSugar();
			avoidEnemy();
		} else if (visibleSugar.size() == 0 && getTarget() == null && (self.sugarCarry == 0 || isReturningHome())) {

			// find sugar scouts will travel farther than other castes
			if (vectorToHome().length() > 550 || isReturningHome()) {
				moveHome();
				setReturningHome(true);
			} else {
				moveInDirection(getRandomDirection());
			}

		} else {

			// set target to null after target is low on sugar to find another
			if (getTarget() != null && getTarget().amount < MIN_SUGAR)
				setTarget(null);
			else if (self.sugarCarry == 0 && getTarget() == null)
				setTarget(closest(visibleSugar));

			if (canPickUpSugar()) {
				if (self.caste.equals((Caste.Gatherer)))
					pickUpSugar(getTarget());
				else if (self.caste.equals(Caste.Soldier) && getRandomDirection() < 270)
					setReturningHome(true);
				needBackup = enemySoldierCount() > 1;
			}

			if (self.sugarCarry > 0 || isReturningHome())
				moveHome();
			else if (getTarget() == null)
				moveInDirection(getRandomDirection());
			else
				moveToward(getTarget());
		}

		// communicate
		if (inDanger()) {
			talk(AntAILPW.BACKUP_CALL, closest(visibleEnemies()));
		} else if (getTarget() != null) {
			if (needBackup)
				talk(AntAILPW.BACKUP_CALL, getTarget());
			else
				talk(getTarget().amount, getTarget());
		} else if (emptySugar != null)
			talk(NO_SUGAR, emptySugar);

		setLastLocation(self.getPosition());
	}

	/**
	 * @return if the ant is in a position to pick up sugar
	 */
	public boolean canPickUpSugar() {
		return getTarget() != null && vectorTo(getTarget()).length() < getTarget().radius && self.sugarCarry == 0;
	}

	/**
	 * @return if the ant is not near the home or the target
	 */
	public boolean inMiddleOfRoute() {
		return vectorToHome().length() > 100 && (getTarget() == null || vectorTo(getTarget()).length() > 100);
	}

	/**
	 * @return if the ant is in danger due to enemy soldiers and lack of friends to fight them
	 */
	public boolean inDanger() {
		int friendSoldierCount = 0;
		for(Ant friend : visibleFriends())
			if(friend.caste.equals(Caste.Soldier))
				friendSoldierCount++;
		return (enemySoldierCount() > friendSoldierCount) || (enemySoldierCount() * 20 > self.health);
	}

	/**
	 * @return true if this ant is the closest friendly ant to the closest visible enemy
	 */
	public boolean closestToEnemy() {
		for (Ant friend : visibleFriends()) {
			if (vectorBetween(friend, closest(visibleEnemies())).length() < vectorTo(closest(visibleEnemies())).length())
				return false;
		}
		return true;
	}

	/**
	 * @return angle from ant to home
	 */
	public double angleToHome() {
		return self.getPosition().angle() - vectorToHome().angle();
	}

	/**
	 * @param snapshot
	 * @return angle from ant to snapshot
	 */
	public double angleTo(Snapshot snapshot) {
		return self.getPosition().angle() - vectorTo(snapshot).angle();
	}

	/**
	 * Sets target to the contentSugar of the scouts message and update the target
	 */
	protected void listen() {
		sugarSites.clear();
		setHomeUnderAttack(false);

		if (getTarget() != null && !sugarSites.contains(getTarget()) && !emptySugarPositions.contains(getTarget().getPosition()))
			sugarSites.add(getTarget());

		for (AntMessage message : audibleAntMessages) {
			if (message.contentSugar != null) {
				if (message.content > VERY_LOW_SUGAR && !sugarSites.contains(message.contentSugar)
						&& !emptySugarPositions.contains(message.contentSugar.getPosition()))
					sugarSites.add(message.contentSugar);
				else if (message.content == NO_SUGAR) {
					if (!emptySugarPositions.contains(message.contentSugar.getPosition()))
						emptySugarPositions.add(message.contentSugar.getPosition());
					if (getTarget() != null && getTarget().getPosition().isSameVectorAs(message.contentSugar.getPosition())) {
						setTarget(null);
					}
				} else if (message.content == AntAILPW.BACKUP_CALL && self.caste.equals(Caste.Soldier))
					setTarget(message.contentSugar);
			} else if (message.content == GuardAntAI.UNDER_ATTACK) {
				setHomeUnderAttack(true);
			}
		}

		LinkedList<Sugar> removeList = new LinkedList<Sugar>();
		for (Sugar site : sugarSites)
			if (emptySugarPositions.contains(site.getPosition()))
				removeList.add(site);
		sugarSites.removeAll(removeList);

		if (audibleHillMessage != null && isInView(audibleHillMessage.sender)) {
			if (audibleHillMessage.contentSugar != null && self.sugarCarry == 0
					&& !emptySugarPositions.contains(audibleHillMessage.contentSugar.getPosition()))
				setTarget(audibleHillMessage.contentSugar);
		} else if (getTarget() == null) {
			setTarget(closest(sugarSites));
		}
	}

	/**
	 * Ant moves around and away from the hill
	 */
	protected void avoidHome() {
		moveInDirection(angleToHome() - 90);
	}
	
	/**
	 * Move around enemy soldier
	 */
	protected void avoidEnemy() {
		needBackup = true;
		// move around enemy
		moveInDirection(angleTo(closest(visibleEnemies())) - 170);
	}

	/**
	 * @return the target
	 */
	protected Sugar getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	protected void setTarget(Sugar target) {
		this.target = target;
	}

	/**
	 * @return the randomDirection
	 */
	protected int getRandomDirection() {
		return randomDirection;
	}

	/**
	 * @param randomDirection
	 *            the randomDirection to set
	 */
	protected void setRandomDirection(int randomDirection) {
		this.randomDirection = randomDirection;
	}

	/**
	 * @return the returningHome
	 */
	protected boolean isReturningHome() {
		return returningHome;
	}

	/**
	 * @param returningHome
	 *            the returningHome to set
	 */
	protected void setReturningHome(boolean returningHome) {
		this.returningHome = returningHome;
	}

	/**
	 * @return the homeUnderAttack
	 */
	protected boolean isHomeUnderAttack() {
		return homeUnderAttack;
	}

	/**
	 * @param homeUnderAttack
	 *            the homeUnderAttack to set
	 */
	protected void setHomeUnderAttack(boolean homeUnderAttack) {
		this.homeUnderAttack = homeUnderAttack;
	}

	/**
	 * @return the lastLocation
	 */
	protected Vector getLastLocation() {
		return lastLocation;
	}

	/**
	 * @param lastLocation
	 *            the lastLocation to set
	 */
	protected void setLastLocation(Vector lastLocation) {
		this.lastLocation = lastLocation;
	}

	/**
	 * @return the sittingDuration
	 */
	protected int getSittingDuration() {
		return sittingDuration;
	}

	/**
	 * @param sittingDuration
	 *            the sittingDuration to set
	 */
	protected void setSittingDuration(int sittingDuration) {
		this.sittingDuration = sittingDuration;
	}

	/**
	 * @return if the ant has remained in the same spot for over 100 ticks
	 */
	protected boolean isSitting() {
		if (getLastLocation() != null && Vector.subtract(getLastLocation(), self.getPosition()).length() < 15)
			setSittingDuration(getSittingDuration() + 1);
		else
			setSittingDuration(0);

		return getSittingDuration() > 65;
	}

	/**
	 * Makes the ant move when it is stuck
	 * 
	 * @return wether or not the ant is stuck
	 */
	protected boolean preventGettingStuck() {
		if (!isStuck() || avoidingHome)
			return false;

		if (vectorToHome().length() < 60)
			setStuck(false);
		else
			moveHome();
		return isStuck();
	}

	/**
	 * @return the stuck
	 */
	protected boolean isStuck() {
		return stuck;
	}

	/**
	 * @param stuck
	 *            the stuck to set
	 */
	protected void setStuck(boolean stuck) {
		this.stuck = stuck;
	}

	/**
	 * @return number of corpses from friend ants
	 */
	protected int friendCorpseCount() {
		int output = 0;
		for (AntCorpse corpse : visibleCorpses)
			if (corpse.playerID == self.playerID)
				output++;
		return output;
	}

	/**
	 * @return number of visible enemy soldiers
	 */
	protected int enemySoldierCount() {
		int output = 0;
		for (Ant enemy : visibleEnemies())
			if (enemy.caste.equals(Caste.Soldier))
				output++;
		return output;
	}

}
