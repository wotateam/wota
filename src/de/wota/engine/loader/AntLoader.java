package de.wota.engine.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import de.wota.Ant;

public class AntLoader {
	
	private ServiceLoader<Ant> installedAnts;
	
	private List<Class<Ant>> loadedAnts;
	
	public void findInstalledAnts()
	{
		installedAnts = ServiceLoader.load(Ant.class);
	}
	
	public void loadAnts(List<String> names)
	{
		loadedAnts = new ArrayList<Class<Ant>>();
		
		Iterator<Ant> ants = installedAnts.iterator();
		Ant currentAnt;
		
		while (ants.hasNext())
		{
			currentAnt = ants.next();
			
			if (names.contains(currentAnt.getClass().getName()))
			{
				loadedAnts.add((Class<Ant>) currentAnt.getClass());
			}
		}
	}
	
	public void testAnts()
	{
		for (Class<Ant> antClass : loadedAnts)
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
