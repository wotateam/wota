package wota.gamemaster;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import wota.gameobjects.AI;

public class AISecurity {
	public static boolean checkAI(Class<? extends AI> ai) {
		Field[] fields = ai.getDeclaredFields();

		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())
					&& !Modifier.isFinal(field.getModifiers())) {
				System.out.println("Class " + ai
						+ " has non-final static members " + field.getName());
				return false;
			}
		}

		return true;
	}
}
