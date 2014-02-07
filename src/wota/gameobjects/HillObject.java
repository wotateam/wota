package wota.gameobjects;

import java.util.List;

import wota.gameobjects.Hill;
import wota.utility.Vector;

public class HillObject extends GameObject {
	private Hill hill;
	protected final HillAI hillAI;
	private GameWorld.Player player;
	private double storedFood;
	final Caste caste;
	
	public HillObject(Vector position, GameWorld.Player player, Class<? extends HillAI> hillAIClass,
						Parameters parameters) {
		super(position, parameters);
		this.player = player;
		this.storedFood = parameters.STARTING_FOOD;
		HillAI hillAI = null;
		try {
			hillAI = hillAIClass.newInstance(); 
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not create HillAI -> exit");
			System.exit(1);
		}
		hillAI.setParameters(parameters);
		this.hillAI = hillAI;
		this.hillAI.setHillObject(this);
		
		caste = Caste.Hill;
	}
	
	public GameWorld.Player getPlayer() {
		return player;
	}

	public void createHill() {
		hill = new Hill(this);
		this.hillAI.setHill(hill);
	}
	
	public Hill getHill() {
		return hill;
	}

	public double getStoredFood() {
		return storedFood;
	}

	public void changeStoredFoodBy(int deltaFood) {
		storedFood += deltaFood;
	}
	
	public List<AntOrder> getAntOrders() {
		return hillAI.popAntOrders();
	}
	
	/** calls hillAI.tick() and handles exceptions */
	public void tick(List<Ant> visibleAnts, List<AntCorpse> visibleCorpses, List<Sugar> visibleSugar, 
			List<Hill> visibleHills, List<AntMessage> incomingAntMessages, HillMessage incomingHillMessage) {
		hillAI.visibleAnts = visibleAnts;
		hillAI.visibleCorpses = visibleCorpses;
		hillAI.visibleSugar = visibleSugar;
		hillAI.visibleHills = visibleHills;
		hillAI.audibleAntMessages = incomingAntMessages;
		hillAI.audibleHillMessage = incomingHillMessage;
		hillAI.setPosition(getPosition());
		
		try {
			hillAI.tick();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
