package wota.utility;

import java.util.Random;

public class SeededRandomizer {
	private static long seed;
	private static Random internalRandom = new Random();
	
	public static SeededRandomizer random = new SeededRandomizer();
	
	public static void resetSeed(long seed) {
		SeededRandomizer.seed = seed;
		internalRandom.setSeed(seed);
	}

	/* I think this will only lead to confusion
	public static int nextInt() {
		return internalRandom.nextInt();
	} */

	/**
	 * returns a internalRandom integer number between 0 and n-1
	 * @param n Upper bound for internalRandom number
	 * @return a internalRandom integer number between 0 and n-1
	 */
	public static int getInt(int n) {
		return internalRandom.nextInt(n);
	}
	
	public static long nextLong() {
		return internalRandom.nextLong();
	}
	
	/**
	 * @return Returns the next pseudorandom, uniformly distributed double value between 0.0 and 1.0
	 */
	public static double getDouble()
	{
		return internalRandom.nextDouble();
	}
	
	public static long getSeed()
	{
		return seed;
	}
}
