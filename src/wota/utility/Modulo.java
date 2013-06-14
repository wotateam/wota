package wota.utility;

public class Modulo {
	public static int mod(int x, int m) {
		int r = x % m;
		if (r < 0) {
			return r + m;
		} else {
			return r;
		}
	}
	
	public static double mod(double x, double m) {
		double r = x % m;
		if (r < 0) {
			return r + m;
		} else {
			return r;
		}
	}
}
