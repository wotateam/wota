package wota.gamemaster;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import wota.gameobjects.AI;

public class AISecurity {
	public static boolean checkAI(Class<? extends AI> ai) {
		Field[] fields = ai.getDeclaredFields();

		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())
					&& (!Modifier.isFinal(field.getModifiers()) || (!isPrimitive(field) && !field.getType().isEnum()))) {
				System.out.println("Class " + ai + " has non-final static members " + field.getName());
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if the field is of a primitive variable type
	 * 
	 * @param field
	 *            field to check
	 * @return if the type of the given field is primitive
	 */
	public static boolean isPrimitive(Field field) {
		return field.getType().equals(Integer.TYPE) || field.getType().equals(Double.TYPE) || field.getType().equals(Boolean.TYPE)
				|| field.getType().equals(Byte.TYPE) || field.getType().equals(Short.TYPE) || field.getType().equals(Long.TYPE)
				|| field.getType().equals(Character.TYPE) || field.getType().equals(Float.TYPE);
	}
}
