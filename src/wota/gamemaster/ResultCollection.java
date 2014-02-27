/**
 * 
 */
package wota.gamemaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Gathers scores in tournaments or batch mode.
 */
public class ResultCollection {
	
	private Map<String, Result> scores;
	
	/**
	 * Assumes that the same ai does not appear twice.
	 */
	public ResultCollection() {
		scores = new HashMap<String, Result>();
	}
	
	@Override
	public String toString() {
		String str = "=======================================\n";
		str = str + String.format("%18s %2s %2s %2s %3s\n", "AI name", "W", "D", "L", "P");
		for (Result result : scores.values()) {
			str = str + result + "\n";
		}
		str = str + "=======================================\n";
		return str;
	}
	
	public void addGame(String winner, String looser) {
		addIfNotExistent(winner);
		addIfNotExistent(looser);
		scores.get(winner).addWin();
		scores.get(looser).addLoss();
	}
			
	private void addIfNotExistent(String name) { 
		if (!scores.containsKey(name)) {
			scores.put(name, new Result(name));
		}
	}
	
	public void addGame(String[] winAIs, String[] drawAIs, String[] lossAIs) {
		if (winAIs != null) {
			for (String ai : winAIs) {
				addIfNotExistent(ai);
				scores.get(ai).addWin();
			}
		}
		if (lossAIs != null) {
			for (String ai : lossAIs) {
				addIfNotExistent(ai);
				scores.get(ai).addLoss();
			}
		}
		if (drawAIs != null) {
			for (String ai : drawAIs) {
				addIfNotExistent(ai);
				scores.get(ai).addDraw();
			}
		}
	}
	
	public class Result {
		private final String name;
		private int wins = 0;
		private int draws = 0;
		private int losses = 0;

		
		public Result(String name) {
			this.name = name;
		}
		
		public void addWin() {
			wins++;
		}
		
		public void addDraw() {
			draws++;
		}
		
		public void addLoss() {
			losses++;
		}

		public double getScore() {
			return wins + draws/2.;
		}
		
		@Override
		public String toString() {
			return String.format("%18s %2d %2d %2d %2.1f", name, wins, draws, losses, getScore());
		}
	}
	
}
