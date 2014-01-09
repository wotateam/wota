package wota.gameobjects;


/**
 * Order for a new Ant
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
