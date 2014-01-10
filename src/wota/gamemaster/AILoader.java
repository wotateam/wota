package wota.gamemaster;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;

import wota.gameobjects.HillAI;

/**
 * Handles dynamic loading of player AIs.
 * 
 */
public class AILoader {
	private String searchpath;

	private final String AI_PACKAGE = "wota.ai";
	private final String HILL_AI_CLASS_NAME = "HillAI";

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
	 * Dynamically loads the class object representing the given HillAI
	 * implementation. The class is assumed to be "HillAI" located in the in
	 * the specified subpackage of AI_PACKAGE.
	 * 
	 * @param aiName
	 *            simple name of the class to be loaded (without package
	 *            qualifier)
	 * @return Class object representing an implementation of HillAI
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends HillAI> loadHill(String aiName) {
		File hillFile = new File(aiName + ".jar");
		File searchFile = new File(searchpath);
		try {
			ClassLoader hillLoader = URLClassLoader.newInstance(new URL[] {
					hillFile.toURI().toURL(), searchFile.toURI().toURL() });

			Class<?> hillAIClass = hillLoader.loadClass(AI_PACKAGE + "."
					+ aiName + "." + HILL_AI_CLASS_NAME);

			// Check whether loaded class is a HillAI
			if (!HillAI.class.isAssignableFrom(hillAIClass)) {
				System.out.println("Class " + hillAIClass
						+ " does not extend HillAI");
				return null;
			}

			if (AISecurity.checkAI((Class<? extends HillAI>) hillAIClass))
				return (Class<? extends HillAI>) hillAIClass;
			else
				return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(-1);
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}

	/**
	 * Get the human-readable name of the AI.
	 */
	public static String getAIName(Class<? extends HillAI> hillAIClass) {
		if (hillAIClass
				.isAnnotationPresent(wota.gamemaster.AIInformation.class)) {
			return hillAIClass.getAnnotation(
					wota.gamemaster.AIInformation.class).name();
		} else {
			return hillAIClass.getSimpleName();
		}
	}

	/**
	 * Get the human-readable name of the AI's creator.
	 */
	public static String getAICreator(Class<? extends HillAI> hillAIClass) {
		if (hillAIClass
				.isAnnotationPresent(wota.gamemaster.AIInformation.class)) {
			return hillAIClass.getAnnotation(
					wota.gamemaster.AIInformation.class).creator();
		} else {
			return "Anonymous";
		}
	}
}
