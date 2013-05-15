package de.wota.gamemaster;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;

import de.wota.gameobjects.Ant;
import de.wota.gameobjects.QueenAI;

/**
 * Handles dynamic loading of player AIs.
 * 
 */
public class AILoader {
	private String searchpath;

	private final String AI_PACKAGE = "de.wota.ai.";

	/**
	 * Creates a new AILoader using the directory of the executable as
	 * searchpath.
	 */
	public AILoader() {
		this("./");
	}

	/**
	 * Creates a new AILoader using the given directory or .jar file as
	 * searchpath.
	 * 
	 * @param path
	 *            directory or .jar file where player AIs should be located
	 */
	public AILoader(String path) {
		Policy.setPolicy(new AIPolicy());
		System.setSecurityManager(new SecurityManager());

		searchpath = path;
	}

	// FIXME: This is ugly, but I don't know a way around it
	/**
	 * Dynamically loads the class object representing the given QueenAI
	 * implementation. The class is assumed to be located in the "ai" package.
	 * 
	 * @param className
	 *            simple name of the class to be loaded (without package
	 *            qualifier)
	 * @return Class object representing an implementation of QueenAI
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends QueenAI> loadQueen(String className) {
		File queenFile = new File(className + ".jar");
		File searchFile = new File(searchpath);
		try {
			ClassLoader queenLoader = URLClassLoader.newInstance(new URL[] {
					queenFile.toURI().toURL(), searchFile.toURI().toURL() });

			Class<?> queenAIClass = queenLoader.loadClass(AI_PACKAGE
					+ className);

			// Check whether loaded class is a QueenAI
			if (!QueenAI.class.isAssignableFrom(queenAIClass)) {
				System.out.println("Class " + queenAIClass
						+ " does not extend QueenAI");
				return null;
			}
			return (Class<? extends QueenAI>) queenAIClass;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the human-readable name of the AI.
	 * @param queenAIClass player AI
	 * @return name of the player AI
	 */
	public static String getAIName(Class<? extends QueenAI> queenAIClass) {
		if (queenAIClass
				.isAnnotationPresent(de.wota.gamemaster.AIInformation.class)) {
			return queenAIClass.getAnnotation(
					de.wota.gamemaster.AIInformation.class).name();
		} else {
			return queenAIClass.getSimpleName();
		}
	}

	/**
	 * Get the human-readable name of the  AI's creator.
	 * @param queenAIClass player AI
	 * @return name of the AI's creator
	 */
	public static String getAICreator(Class<? extends QueenAI> queenAIClass) {
		if (queenAIClass
				.isAnnotationPresent(de.wota.gamemaster.AIInformation.class)) {
			return queenAIClass.getAnnotation(
					de.wota.gamemaster.AIInformation.class).creator();
		} else {
			return "Anonymous";
		}
	}
}
