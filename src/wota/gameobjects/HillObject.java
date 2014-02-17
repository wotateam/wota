package wota.gameobjects;

import java.util.LinkedList;
import java.util.List;

import wota.gameobjects.Hill;
import wota.utility.Vector;

public class HillObject extends GameObject {
	private Hill hill;
	protected final HillAI hillAI;
	private GameWorld.Player player;
	private double storedFood;
	final Caste caste;
	private List<AntOrder> antOrders = new LinkedList<AntOrder>();
	private HillMessage message;
	
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
		return antOrders;
	}
	
	public HillMessage getMessage() {
		return message;
	}
	
	/** calls hillAI.tick(), saves the message and the ant orders and handles exceptions  */
	public void tick(List<Ant> visibleAnts, 
			         List<AntCorpse> visibleCorpses, 
			         List<AntMessage> incomingAntMessages) {
		hillAI.visibleAnts = visibleAnts;
		hillAI.visibleCorpses = visibleCorpses;
		hillAI.audibleAntMessages = incomingAntMessages;
		hillAI.setPosition(getPosition());
		
		try {
			hillAI.tick();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		antOrders = hillAI.popAntOrders();
		message = hillAI.popMessage();
	}
	
}
