package de.wota.engine.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import de.wota.Ant;

public class AntLoader {
	private static ServiceLoader installedAnts;
	
	private static List<Class> loadedAnts;
	
	static void findInstalledAnts()
	{
		installedAnts = ServiceLoader.load(Ant.class);
	}
	
	static void loadAnts(List<String> names)
	{
		loadedAnts = new ArrayList<Class>();
		
		Iterator<Ant> ants = installedAnts.iterator();
		Ant currentAnt;
		
		while (ants.hasNext())
		{
			currentAnt = ants.next();
			
			if (names.contains(currentAnt.getClass().getName()))
			{
				loadedAnts.add(currentAnt.getClass());
			}
		}
	}
	
	static void testAnts()
	{
		for (Class antClass : loadedAnts)
		{
			try {
				((Ant) antClass.newInstance()).tick();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
