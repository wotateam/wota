package de.wota.utility;

import java.util.Random;

public class SeededRandomizer {
	private static long seed;
	private static Random random = new Random();

	public static void resetSeed(long seed) {
		SeededRandomizer.seed = seed;
		random.setSeed(seed);
	}

	public static int nextInt() {
		return random.nextInt();
	}

	public static int nextInt(int n) {
		return random.nextInt(n);
	}
	
	public static double nextDouble()
	{
		return random.nextDouble();
	}
	
	public static long getSeed()
	{
		return seed;
	}
}
