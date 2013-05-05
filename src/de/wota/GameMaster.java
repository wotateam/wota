package de.wota;

import de.wota.gameobjects.GameWorld;

/**
 * Der Spielleiter. Verwaltet alle Ants und ruft tick auf.
 * @author pascal
 *
 */
public class GameMaster {
	
	private final int N_PLAYER;
	private GameWorld gameWorld;
	//private Ant[] players;
	/*
	public void tick() {
		gameWorld.tick();
	}*/
	
	public GameMaster(int nPlayer) {
		N_PLAYER = nPlayer;
	}
	
}
