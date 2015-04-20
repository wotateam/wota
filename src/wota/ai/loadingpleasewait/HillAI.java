/**
 * 
 */
package wota.ai.loadingpleasewait;

import java.util.ArrayList;
import java.util.LinkedList;

import wota.gamemaster.AIInformation;
import wota.gameobjects.Ant;
import wota.gameobjects.AntMessage;
import wota.gameobjects.Caste;
import wota.gameobjects.Sugar;

/**
 *  This hill AI creates more Gatherers than Soldiers and Scouts and
 *  relays a message about the nearest Sugar
 */
@AIInformation(creator = "LoadingPleaseWait", name = "AILoading")
public class HillAI extends wota.gameobjects.HillAI {

	public static final int TEAM_SIZE = 4;
	public static final int SEARCH = -2;
	
	private ArrayList<Sugar> sugarSites = new ArrayList<Sugar>();
	private ArrayList<Sugar> emptySites = new ArrayList<Sugar>();
	private ArrayList<Ant> guardList = new ArrayList<Ant>();
	private Sugar previousClosestSugar;
	private Sugar secondPreviousClosestSugar;
	private int totalAntsProduced;
	private int ticksSinceBackupSent = 999;
	private boolean backupRequested;
	private boolean producingCommandos;
	private boolean guardDown = true;
	private int maxGuards = 2;


	@Override
	public void tick(){
		
		/* 
		 * try to create an Ant using the Gatherer in every tick
		 * if you don't have enough food to create the ant your call
		 * will be ignored
		 * 
		 * The majority of ants will be gatherers
		 */
		
		int requestedAnts = 0;
		
		if((int)(self.food / parameters.ANT_COST) >= 1 && totalAntsProduced > 15 && guardList.size() <= maxGuards && guardDown){
			createAnt(Caste.Soldier, GuardAntAI.class);
			guardDown = false;
			totalAntsProduced++;
			requestedAnts++;
		}
		if(ticksSinceBackupSent++ > 85 && (int)(self.food / parameters.ANT_COST) >= 1 + requestedAnts && backupRequested){
			createAnt(Caste.Soldier, PatrollAntAI.class);
			backupRequested = false;
			ticksSinceBackupSent = 0;
			totalAntsProduced++;
			requestedAnts++;
		}
		
		if(totalAntsProduced > 180)
			maxGuards = 20;
		else if(totalAntsProduced > 85)
			maxGuards = 15;
		else if(totalAntsProduced > 55)
			maxGuards = 10;
		else if(totalAntsProduced > 30)
			maxGuards = 5;
		
		for(int i = requestedAnts; i < (int) (self.food / parameters.ANT_COST); i++){
			if(totalAntsProduced % 16 == 0 && totalAntsProduced > 0)
				producingCommandos = true;
			
			if(producingCommandos){
				//create a team of commandos
				if((int)(self.food / parameters.ANT_COST) - i >= TEAM_SIZE){
					for(int soldierCount = 0; soldierCount < TEAM_SIZE; soldierCount++)
						createAnt(Caste.Soldier, CommandoAntAI.class);
					totalAntsProduced += TEAM_SIZE;
					i += TEAM_SIZE - 1;
					producingCommandos = false;
				}
			}else if(totalAntsProduced % 4 == 0 && totalAntsProduced > 4){
				createAnt(Caste.Soldier, PatrollAntAI.class);
			}else if(totalAntsProduced % 61 == 0)
				createAnt(Caste.Scout, ScoutAILPW.class);
			else
				createAnt(Caste.Gatherer, AntAILPW.class);
			if(!producingCommandos)
				totalAntsProduced++;
		}
		
		Sugar closestSugar = null;
		
		guardList.clear();
		
		//communicate with ants about where the sugar is
		for(AntMessage message : audibleAntMessages){
			if(message.contentSugar != null){
				if(message.content == AntAILPW.BACKUP_CALL 
						&& message.contentSugar.amount > AntAILPW.LOW_SUGAR){
					backupRequested = true;
					addSugarSite(message.contentSugar);
				}else if(message.content > AntAILPW.LOW_SUGAR)
					addSugarSite(message.contentSugar);
				else if(message.content < AntAILPW.LOW_SUGAR)
					removeSugarSite(message.contentSugar);
			}else if(message.contentAnt == null && message.content < GuardAntAI.LOW_HEALTH){
				guardDown = true;
			}else if(message.content == GuardAntAI.UNDER_ATTACK)
				guardDown = true;
			if(message.sender.caste.equals(Caste.Soldier) && message.sender.antAIClassName.equals("GuardAntAI"))
				guardList.add(message.sender);
		}
		
		LinkedList<Sugar> removeList = new LinkedList<Sugar>();
		for(Sugar emptySite : emptySites){
			for(Sugar site : sugarSites){
				if(emptySite != null && site != null && emptySite.getPosition().isSameVectorAs(site.getPosition()))
					removeList.add(site);
			}
		}
		sugarSites.removeAll(removeList);
		
		closestSugar = closest(sugarSites);
		
		if(closestSugar != null && totalAntsProduced > 40 && sugarSites.size() > 2 && (closestSugar.hasSameOriginal(secondPreviousClosestSugar) || closestSugar.hasSameOriginal(previousClosestSugar))){

			//tell ants to go to third closest sugar some of the time
			sugarSites.remove(previousClosestSugar);
			sugarSites.remove(secondPreviousClosestSugar);
			closestSugar = closest(sugarSites);
			addSugarSite(previousClosestSugar);
			addSugarSite(secondPreviousClosestSugar);
			talk(closestSugar.amount, closestSugar);
		} else if(closestSugar != null && sugarSites.size() > 1 && totalAntsProduced > 20 && closestSugar.hasSameOriginal(previousClosestSugar)){

			//tell ants to go to second closest sugar some of the time
			sugarSites.remove(previousClosestSugar);
			closestSugar = closest(sugarSites);
			addSugarSite(previousClosestSugar);
			talk(closestSugar.amount, closestSugar);
		}else if(closestSugar != null)
			talk(closestSugar.amount, closestSugar);
		else{
			talk(SEARCH);
		}
		secondPreviousClosestSugar = previousClosestSugar;
		previousClosestSugar = closestSugar;
	}
	
	/**
	 * add an element to sugarSites if it is not already in the list
	 * 
	 * @param site element to add
	 */
	public synchronized void addSugarSite(Sugar site){
		if(!sugarSites.contains(site) && !emptySites.contains(site) && site != null){
			sugarSites.add(site);
		}
	}
	
	
	/**
	 * remove sugar sites with the same location from the list
	 * 
	 * @param site site to remove
	 */
	public synchronized void removeSugarSite(Sugar site){
		if(site != null){
			emptySites.add(site);
			sugarSites.remove(site);
		}
	}

}
