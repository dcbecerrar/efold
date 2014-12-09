package dynamics;


import java.util.List;

import dynamics.Constants.Dir;







public class Sample {
	private static final long serialVersionUID = 1L;
	public Integer[][] structure;
	public double weight;
	public List<Integer> template;
	public List<Dir> pairing;
	public int numStrands;
	public Double probability;
	public int length;
		
	public Sample(List<Integer> t, List<Dir> d, Integer[][] s, int l,double z, double p) {
		this.structure = s;
		this.template = t;
		this.pairing = d;
		weight = z;
		numStrands = t.size();
		probability = p;
		length = l;
	}
	
	public Integer[] neighbors(){
		int lStart, lEnd, lInc, rStart, rEnd, rInc;
		Integer[] neighbors = new Integer[length];
		for(int aux_tInd = 0 ; aux_tInd < numStrands-1; aux_tInd++){
			Dir dir = pairing.get(aux_tInd);
			if(dir == Dir.NONE)
				continue;
			lStart = structure[aux_tInd][0];
			lEnd = structure[aux_tInd][1];
			lInc = 1;
			if(dir == Dir.PARA){
				rStart = structure[aux_tInd+1][0];
				rEnd = structure[aux_tInd+1][1];
				rInc = 1;
			}else{
				rStart = structure[aux_tInd+1][1];
				rEnd = structure[aux_tInd+1][0];
				rInc = -1;
			}
			for(int l=lStart, r=rStart; l!=lEnd+lInc && r!=rEnd+rInc ; l+=lInc, r+=rInc){
					neighbors[l] = r;
			}
		}
		return neighbors;
	}	
	
}
