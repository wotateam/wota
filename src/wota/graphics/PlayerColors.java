package wota.graphics;

import java.awt.Color;

import wota.utility.Modulo;

/**
 * Helper class to generate Colors of Players.
 */
public class PlayerColors {

	private static final Color[] playerColors = { Color.RED, Color.BLUE, Color.GREEN,
	Color.CYAN, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.YELLOW };

	public static Color get(int i) {
		if (i < playerColors.length) {
			return playerColors[i];
		}
		else {
			return new Color(Modulo.mod(115*i, 256), Modulo.mod(73*i, 256), Modulo.mod(192*i, 256));
		}
	}

}
