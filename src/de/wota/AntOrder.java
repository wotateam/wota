package de.wota;

import de.wota.gameobjects.AntAI;
import de.wota.gameobjects.caste.Caste;

/**
 * Auftrag für eine neue Ant
 * @author pascal
 *
 */
public class AntOrder {
	private Caste caste;
	private Class<? extends AntAI> antAIClass;
	
	public Caste getCaste() {
		return caste;
	}
	
	public Class<? extends AntAI> getAntAIClass() {
		return antAIClass;
	}
	
	public AntOrder(Caste caste, Class<? extends AntAI> antAIClass) {
		this.caste = caste;
		this.antAIClass = antAIClass;
	}
}
