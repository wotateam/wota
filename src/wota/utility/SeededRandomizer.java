package wota.utility;

import java.util.Random;

public class SeededRandomizer {
	private static long seed;
	private static final Random internalRandom = new Random();
	
	public static final SeededRandomizer random = new SeededRandomizer();
	
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
	public static int nextInt(int n) {
		return internalRandom.nextInt(n);
	}
	
	public static long nextLong() {
		return internalRandom.nextLong();
	}
	
	/**
	 * @return Returns the next pseudorandom, uniformly distributed double value between 0.0 and 1.0
	 */
	public static double nextDouble()
	{
		return internalRandom.nextDouble();
	}
	
	public static long getSeed()
	{
		return seed;
	}
}
