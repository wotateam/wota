package wota.utility;

import java.util.Random;

public class SeededRandomizer {
	private static long seed;
	private static Random random = new Random();

	public static void resetSeed(long seed) {
		SeededRandomizer.seed = seed;
		random.setSeed(seed);
	}

	/* I think this will only lead to confusion
	public static int nextInt() {
		return random.nextInt();
	} */

	/**
	 * returns a random integer number between 0 and n-1
	 * @param n Upper bound for random number
	 * @return a random integer number between 0 and n-1
	 */
	public static int getInt(int n) {
		return random.nextInt(n);
	}
	
	public static long nextLong() {
		return random.nextLong();
	}
	
	/**
	 * @return Returns the next pseudorandom, uniformly distributed double value between 0.0 and 1.0
	 */
	public static double getDouble()
	{
		return random.nextDouble();
	}
	
	public static long getSeed()
	{
		return seed;
	}
}
