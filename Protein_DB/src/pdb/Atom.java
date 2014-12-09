package pdb;

import util.Vector;

public class Atom {
	double x,y,z;
	int serial;
	String name;
	
	public Atom(int Serial, String atomName, double X, double Y, double Z) {
		x=X;
		y=Y;
		z=Z;
		serial = Serial;
		name = atomName;
	}
	
	public String toString(){
		return name + " " + x + " " + y + " " + z;
	}
	
	public double squaredDistance(Atom other){
		return Vector.squaredDistance(x, y, z, other.x, other.y, other.z);
	}
	
	public double distance(Atom other){
		return Vector.distance(x, y, z, other.x, other.y, other.z);
	}
	
	public double[] vectorTo(Atom other){
		return Vector.vectorTo(x, y, z, other.x, other.y, other.z);
	}
}
