package de.wota;

public class Vector {
	public double x;
	public double y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double length() {
		return Math.sqrt(x*x + y*y);
	}
	
	public static Vector add(Vector p1, Vector p2) {
		return new Vector(p1.x + p2.x, p1.y + p2.y);
	}
	
	public static Vector scale(double a, Vector p) {
		return new Vector(p.x * a, p.y * a);
	}
	
	public static Vector fromPolar(double amplitude, double direction) {
		return new Vector(amplitude*Math.cos(direction/360.*2*Math.PI), amplitude*Math.sin(direction/360.*2*Math.PI));
	}
}
