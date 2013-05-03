package de.wota;

import de.wota.gameobjects.Ant;
import de.wota.ai.AntAI;

/**
 * Auftrag für eine neue Ant
 * @author pascal
 *
 */
public class AntOrder {
	private Ant.Caste caste;
	private Class<? extends AntAI> antAIClass;
	
	public Ant.Caste getCaste() {
		return caste;
	}
	
	public Class<? extends AntAI> getAntAIClass() {
		return antAIClass;
	}
	
	public AntOrder(Ant.Caste caste, Class<? extends AntAI> antAIClass) {
		this.caste = caste;
		this.antAIClass = antAIClass;
	}
}
