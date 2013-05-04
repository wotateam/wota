package de.wota.ai;

import java.util.List;

import de.wota.Action;
import de.wota.Message;
import de.wota.gameobjects.Ant;
import de.wota.gameobjects.Sugar;
import de.wota.gameobjects.SugarObject;

/**
 * Contains AI PI common to ants and the ant hill, i.e. perception.
 *  
 * @author Daniel
 * 
 */
public abstract class BaseAI {
	public List<Ant> visibleAnts;
	public List<Sugar> visibleSugar;
	public List<Message> incomingMessages;
	protected Action action = new Action(); // FIXME really protected?

	public abstract void tick();
	
	// TODO add perception
	
	/** CAUTION! THIS METHOD DELETES THE ACTION */
	public Action popAction() {
		Action returnAction = action;
		action = new Action();
		return returnAction;
	}
	
	// TODO Methoden um Konstanten abzufragen
}
