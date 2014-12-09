package plots;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import util.Vector;

public class Residue implements Iterable<Atom>{

	String code;
	List<Atom> atoms;
	int seqNum;
	char chainID;

	public Residue(String resName, int SeqNum, char ChainID ) {
		code = resName;
		atoms = new LinkedList<Atom>();
		seqNum = SeqNum;
		chainID = ChainID;
	}

	public String toString(){
		return String.format("%s%4d%c", code, seqNum, chainID);
	}

	public String rString() {
		return toString();
	}

	public void addAtom(Atom atom) {
		atoms.add(atom);		
	}

	public Iterator<Atom> iterator() {
		return atoms.iterator();
	}

	public double[] vectorTo(Residue o) {
		double[] myCoord = getCAlpha();
		double[] oCoord = o.getCAlpha();
		return Vector.vectorTo(myCoord[0],myCoord[1],myCoord[2],oCoord[0],oCoord[1],oCoord[2]);
	}

	public double[] getCAlpha() {
		Atom CA = null;
		for(Atom a: atoms){
			if(a.name.equals("CA")){
				CA = a;
				break;
			}

		}
		return new double[] { CA.x, CA.y, CA.z };
	}

	public double[] meanPosition(){
		double X=0, Y=0, Z=0;
		int n = 0;
		for(Atom a : atoms){
			X += a.x;
			Y += a.y;
			Z += a.z;
			n++;
		}
		return new double[]{ X/n, Y/n, Z/n};
	}
}
