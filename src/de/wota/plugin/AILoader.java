package de.wota.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;

import de.wota.ai.QueenAI;

public class AILoader {
	public AILoader() {
		Policy.setPolicy(new AIPolicy());
		System.setSecurityManager(new SecurityManager());
	}

	// FIXME: This is ugly, but I don't know a way around it
	@SuppressWarnings("unchecked")
	public Class<? extends QueenAI> loadQueen(String className) {
		File queenFile = new File(className + ".jar");
		File searchPath = new File("./");
		try {
			ClassLoader queenLoader = URLClassLoader.newInstance(new URL[] {
					queenFile.toURI().toURL(), searchPath.toURI().toURL() });

			Class<?> queenClass = queenLoader.loadClass(className);

			// Check whether loaded class is a QueenAI
			if (!QueenAI.class.isAssignableFrom(queenClass)) {
				System.out.println("Class " + queenClass
						+ " does not extend QueenAI");
				return null;
			}
			return (Class<? extends QueenAI>) queenLoader.loadClass(className);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getAIName(Class<? extends QueenAI> queenClass) {
		if (queenClass.isAnnotationPresent(de.wota.ai.AIInformation.class))
		{
			return queenClass.getAnnotation(de.wota.ai.AIInformation.class).value();
		} else
		{
			return queenClass.getSimpleName();
		}
	}
}
