package de.wota.engine.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import de.wota.ai.AntAI;

public class AntLoader {
	
	private ServiceLoader<AntAI> installedAnts;
	
	private List<Class<AntAI>> loadedAnts;
	
	public void findInstalledAnts()
	{
		installedAnts = ServiceLoader.load(AntAI.class);
	}
	
	public void loadAnts(List<String> names)
	{
		loadedAnts = new ArrayList<Class<AntAI>>();
		
		Iterator<AntAI> ants = installedAnts.iterator();
		AntAI currentAnt;
		
		while (ants.hasNext())
		{
			currentAnt = ants.next();
			
			if (names.contains(currentAnt.getClass().getName()))
			{
				loadedAnts.add((Class<AntAI>) currentAnt.getClass());
			}
		}
	}
	
	public void testAnts()
	{
		for (Class<AntAI> antClass : loadedAnts)
		{
			try {
				antClass.newInstance().tick();
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
