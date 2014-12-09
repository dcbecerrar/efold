package dynamics;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dynamics.SampleDistance;
import dynamics.Constants.Dir;

import dynamics.Util;

import dynamics.Sample;


public class ContactDistance implements SampleDistance {

	int bound;

	public ContactDistance(String b) {
		bound = new Integer(b);
	}
	
	public ContactDistance(int b) {
		bound = b;
	}

	public void setBound(int b){
		bound = b;
	}

	public double distance(Sample s1, Sample s2) {
		return contactDistance(s1, s2, bound);
	}

	public static double contactOrder(Sample s){
		return contactOrder(contacts(s), s.length);	
	}

	public static double contactOrder(Collection<Set<Integer>> contacts, int length){
		double CO = 0;
		for(Set<Integer> contact : contacts){
			Integer[] c = contact.toArray(new Integer[0]);
			CO += Math.abs(c[0] - c[1]);
		}
		return CO/(length*contacts.size());		
	}


	public static Set<Set<Integer>> contacts(Sample s){
		Set<Set<Integer>> contacts = new HashSet<Set<Integer>>();
		Integer[][] aSample = s.structure;
		List<Dir> dirOrder = s.pairing;
		List<Integer> perOrden = s.template;
		int lStart, lEnd, lInc, rStart, rEnd, rInc;
		for(int aux_tInd=0; aux_tInd<aSample.length-1; aux_tInd++){
			//I deleted ttInd and twotInd
			Dir dir = dirOrder.get(aux_tInd);
			
			if(dir == Dir.NONE){
				continue;
			}
			lStart = aSample[aux_tInd][0];
			lEnd = aSample[aux_tInd][1];
			lInc = 1;
			if(dir == Dir.PARA){
				rStart = aSample[aux_tInd+1][0];
				rEnd = aSample[aux_tInd+1][1];
				rInc = 1;
			}else{
				rStart = aSample[aux_tInd+1][1];
				rEnd = aSample[aux_tInd+1][0];
				rInc = -1;
			}
			for(int l=lStart, r=rStart; l!=lEnd+lInc && r!=rEnd+rInc ; l+=lInc, r+=rInc){

					contacts.add(new HashSet<Integer>(Util.list(l,r)));
			
			}
		}
		return contacts;
	}
	
	public static Set<Set<Integer>> contacts(Integer[][] aSample, List<Integer> perOrden, List<Dir> dirOrder){
		Set<Set<Integer>> contacts = new HashSet<Set<Integer>>();
		int lStart, lEnd, lInc, rStart, rEnd, rInc;
		for(int aux_tInd=0; aux_tInd<aSample.length-1; aux_tInd++){
			Dir dir = dirOrder.get(aux_tInd);
			if(dir == Dir.NONE){
				continue;
			}
			lStart = aSample[aux_tInd][0];
			lEnd = aSample[aux_tInd][1];
			lInc = 1;
			if(dir == Dir.PARA){
				rStart = aSample[aux_tInd+1][0];
				rEnd = aSample[aux_tInd+1][1];
				rInc = 1;
			}else{
				rStart = aSample[aux_tInd+1][1];
				rEnd = aSample[aux_tInd+1][0];
				rInc = -1;
			}
			for(int l=lStart, r=rStart; l!=lEnd+lInc && r!=rEnd+rInc ; l+=lInc, r+=rInc){
					contacts.add(new HashSet<Integer>(Util.list(l,r)));
			}
		}
		return contacts;
	}
	
	
	public static boolean containsContact(Set<Integer> contact, Set<Set<Integer>> contacts, int bound){
		Integer[] cont = contact.toArray(new Integer[0]);
		for(int i = -bound; i <= bound; i++){
			for(int j = -bound; j<= bound; j++){
				Set<Integer> c = new HashSet<Integer>(Util.list(cont[0]+i, cont[1]+j));
				if(contacts.contains(c))
					return true;
			}
		}
		return false;
	}

	public static Integer minDistance(Set<Integer> contact, Set<Set<Integer>> contacts, int bound){
		Integer[] cont = contact.toArray(new Integer[0]);
		Integer minDist = (2*bound) + 1; 
		for(int i = -bound; i <= bound; i++){
			for(int j = -bound; j<= bound; j++){
				
				//Error => Provisional fix
				if(cont.length>1 && cont[0]+i != cont[1]+j){
				
				Set<Integer> c = new HashSet<Integer>(Util.list(cont[0]+i, cont[1]+j));
				if(contacts.contains(c)){
					minDist = Math.min(Math.abs(i) + Math.abs(j), minDist);
				}
				
				//End of provisional fix
				}
			}
		}
		return minDist;
	}

	public static double contactDistance(Set<Set<Integer>> contacts1, Set<Set<Integer>> contacts2, int bound) {
		double distance = 0;
				//System.out.println(contacts1);
				//System.out.println(contacts2);
		Set<Set<Integer>> difference1 = new HashSet<Set<Integer>>(contacts1);
		difference1.removeAll(contacts2);
		for(Set<Integer> contact : difference1){
			/* if the distance should just be 1 or zero depending on whether it is within the bound or not
			 * if(!containsContact(contact, contacts2, bound))
			 * distance++;
			 */
			distance += minDistance(contact, contacts2, bound);
		}
		//		System.out.println(distance);
		Set<Set<Integer>> difference2 = new HashSet<Set<Integer>>(contacts2);
		difference2.removeAll(contacts1);
		for(Set<Integer> contact : difference2){
			distance += minDistance(contact, contacts1, bound);
		}
		int maxDist = (2*bound) + 1;
		//		System.out.println(distance);
		return distance/(maxDist*(contacts1.size()+contacts2.size()));
	}

	public static double contactDistance(Sample s1, Sample s2, int bound){
		/*System.out.println("s1");
		System.out.println(s1.template + " " + s1.pairing);
		for(int i=0; i<s1.structure.length; i++){
			System.out.print(s1.structure[i][0] + "  " +s1.structure[i][1]+"  -  ");
		}
		System.out.println();
		System.out.println("s2");
		System.out.println(s2.template + " " + s2.pairing);
		for(int i=0; i<s2.structure.length; i++){
			System.out.print(s2.structure[i][0] + "  "+s2.structure[i][1]+"  -  ");
		}
		System.out.println();
		*/
		return contactDistance(contacts(s1), contacts(s2), bound);
	}

}

