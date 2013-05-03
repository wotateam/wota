package de.wota;

public class Vector {
	public double x;
	public double y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector add(Vector otherVector) {
		return new Vector(x + otherVector.x, y + otherVector.y);
	}
	
	public static Vector fromPolar(double amplitude, double direction) {
		return new Vector(amplitude*Math.cos(direction/360.*2*Math.PI), amplitude*Math.sin(direction/360.*2*Math.PI));
	}
}
