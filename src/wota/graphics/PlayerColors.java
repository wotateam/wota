package wota.graphics;

import java.awt.Color;

import wota.utility.Modulo;

public class PlayerColors {

	private static final Color[] playerColors = { Color.RED, Color.BLUE, Color.GREEN,
	Color.CYAN, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.YELLOW };

	public static Color get(int i) {
		return playerColors[Modulo.mod(i, PlayerColors.playerColors.length)];
	}

}
