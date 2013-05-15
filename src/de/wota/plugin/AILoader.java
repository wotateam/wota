package de.wota.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;

import de.wota.ai.QueenAI;
import de.wota.gameobjects.Ant;

public class AILoader {
	private String searchpath;

	private final String PLAYER_PACKAGE = "de.wota.ai";

	public AILoader() {
		this("./");
	}

	public AILoader(String path) {
		Policy.setPolicy(new AIPolicy());
		System.setSecurityManager(new SecurityManager());

		searchpath = path;
	}

	// FIXME: This is ugly, but I don't know a way around it
	@SuppressWarnings("unchecked")
	public Class<? extends QueenAI> loadQueen(String className) {
		File queenFile = new File(PLAYER_PACKAGE + className + ".jar");
		File searchFile = new File(searchpath);
		try {
			ClassLoader queenLoader = URLClassLoader.newInstance(new URL[] {
					queenFile.toURI().toURL(), searchFile.toURI().toURL() });

			Class<?> queenAIClass = queenLoader.loadClass(className);

			// Check whether loaded class is a QueenAI
			if (!QueenAI.class.isAssignableFrom(queenAIClass)) {
				System.out.println("Class " + queenAIClass
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

	public static String getAIName(Class<? extends QueenAI> queenAIClass) {
		if (queenAIClass.isAnnotationPresent(de.wota.ai.AIInformation.class)) {
			return queenAIClass.getAnnotation(de.wota.ai.AIInformation.class)
					.name();
		} else {
			return queenAIClass.getSimpleName();
		}
	}

	public static String getAICreator(Class<? extends QueenAI> queenAIClass) {
		if (queenAIClass.isAnnotationPresent(de.wota.ai.AIInformation.class)) {
			return queenAIClass.getAnnotation(de.wota.ai.AIInformation.class)
					.creator();
		} else {
			return "Anonymous";
		}
	}
}
