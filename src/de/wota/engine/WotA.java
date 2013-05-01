package de.wota.engine;

import java.util.ArrayList;
import java.util.Arrays;

import de.wota.engine.loader.AntLoader;

public class WotA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AntLoader loader = new AntLoader();
		loader.findInstalledAnts();
		loader.loadAnts(new ArrayList<String>(Arrays.asList("DemoAnt")));
		loader.testAnts();
	}

}
