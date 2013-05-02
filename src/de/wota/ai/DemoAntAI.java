package de.wota.ai;
import java.util.List;

public class DemoAntAI extends AntAI {

	@Override
	public void tick(Ant self, List<Ant> visibleAnts) {
		System.out.println("Test: Rennt immer nach rechts");
		moveTo(0);
	}

}
