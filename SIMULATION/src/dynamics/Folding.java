package dynamics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


import dynamics.Constants.*;
import dynamics.SampleDistance;
import dynamics.Cluster;
import dynamics.Sample;

public class Folding {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	

	}
	public static boolean areNeighbors(Cluster c1, Cluster c2, SampleDistance sd, double threshold){						
		if(Math.min(c1.template.size(),c2.template.size()) == 0 && Math.max(c1.template.size(),c2.template.size()) <= 2)
			return true;
		else
			return areNeighbors(c1.template, c1.pairing, c2.template, c2.pairing) && !disjoint(c1.samples, c2.samples, sd, threshold);
	}
	public static double[] areNeighborsDistance(Cluster c1, Cluster c2, SampleDistance sd, double threshold){				
		double[] ND = new double[2];
		ND[0]=0;
		ND[1]=0;
		if(Math.min(c1.template.size(),c2.template.size()) == 0 && Math.max(c1.template.size(),c2.template.size()) <= 2){
			ND[0]=1;
			ND[1]=1;
			return ND;
		}else{
			if(areNeighbors(c1.template, c1.pairing, c2.template, c2.pairing)){
				ND[0]=1;
				ND[1]=disjointDistance(c1.samples, c2.samples, sd, threshold);
				return ND;
			}
		}
		return ND;
	}
	public static boolean areNeighbors(List<Integer> t1, List<Dir> p1, List<Integer> t2, List<Dir> p2){
		
		if(Math.min(t1.size(),t2.size()) == 0 && Math.max(t1.size(),t2.size()) <= 2)
			return true;

		
		if(t1.size() == t2.size()){
			if(!(t1.equals(t2) && p1.equals(p2)))
				if(t1.equals(t2) && (p1.contains(Dir.NONE) ^ p2.contains(Dir.NONE))){
					for(int i=0 ; i<p1.size(); i++){
						Dir d1 = p1.get(i);
						Dir d2 = p2.get(i);
						
						if(d1 != Dir.NONE && d2 != Dir.NONE && d1 != d2)
							return false;
					}
					return true;
				}
			return false;
		}			
		else {
			List<Integer> biggerT = null, smallerT = null;
			List<Dir> biggerP = null, smallerP = null;
			
			if(t1.size() > t2.size()){
				biggerT = t1;
				biggerP = p1;
				smallerT = t2;
				smallerP = p2;
			}
			else{
				biggerT = t2;
				biggerP = p2;
				smallerT = t1;
				smallerP = p1;
			} 
			
			if(!differByOne(biggerT, biggerP, smallerT, smallerP)){
				if(!isContained(smallerT, smallerP, biggerT, biggerP))
					return false;
			}
		}
		
		return true;
	}
	public static boolean differByOne(List<Integer> biggerT, List<Dir> biggerP, List<Integer> smallerT, List<Dir> smallerP) {		
		return (smallerT.equals(removeFirst(biggerT)) && smallerP.equals(biggerP.subList(1, biggerP.size()))) || (smallerT.equals(removeLast(biggerT)) && smallerP.equals(biggerP.subList(0, biggerP.size()-1)));
	}
	static List<Integer> removeFirst(List<Integer> template){
		List<Integer> t = new ArrayList<Integer>(template);
		Integer removed = t.remove(0);
		for(Integer i = removed + 1; i <= template.size(); i++){
			t.set(t.indexOf(i),i-1);
		}
		return t;
	}
	static List<Integer> removeLast(List<Integer> template){
		List<Integer> t = new ArrayList<Integer>(template);
		int last = t.size() - 1;
		Integer removed = t.remove(last);
		for(Integer i = removed + 1; i <= template.size(); i++){
			t.set(t.indexOf(i),i-1);
		}
		return t;
	}
	private static boolean isContained(List<Integer> smallerT, List<Dir> smallerP, List<Integer> biggerT, List<Dir> biggerP) {
		List<List<Integer>> subTemps = new ArrayList<List<Integer>>();
		List<List<Dir>> subPairs = new ArrayList<List<Dir>>();
		
		List<Integer> subT = new ArrayList<Integer>();
		List<Dir> subP = new ArrayList<Dir>();
		
		subT.add(biggerT.get(0));
		for(int i=1; i<biggerT.size(); i++){
			Dir d = biggerP.get(i-1);
			
			if(d == Dir.NONE){
				subTemps.add(subT);
				subPairs.add(subP);
				subT = new ArrayList<Integer>();
				subP = new ArrayList<Dir>();
			}
			else{
				subP.add(d);
			}
			
			Integer s = biggerT.get(i);
			subT.add(s);					
		}
		subTemps.add(subT);
		subPairs.add(subP);
		
		List<Integer> rSmallerT = new ArrayList<Integer>(smallerT);
		List<Dir> rSmallerP = new ArrayList<Dir>(smallerP);
		Collections.reverse(rSmallerT);
		Collections.reverse(rSmallerP);
		
		for(int i=0; i<subTemps.size(); i++){
			subT = normalizeTemplate(subTemps.get(i));
			subP = subPairs.get(i);
			
			if((smallerT.equals(subT) && smallerP.equals(subP)) || (rSmallerT.equals(subT) && rSmallerP.equals(subP)))
				return true;
		}
		return false;
	}
	public static List<Integer> normalizeTemplate(List<Integer> template){
		List<Integer> normalized = new ArrayList<Integer>(template);
		List<Integer> sorted = new ArrayList<Integer>(template);
		Collections.sort(sorted);
		for(int i=0; i<sorted.size(); i++){
			Integer s = sorted.get(i);
			normalized.set(template.indexOf(s), i+1);
		}
		return normalized;
	}
	public static boolean disjoint(Collection<Sample> set1, Collection<Sample> set2, SampleDistance df, double threshold){
		for(Sample s1 : set1){
			for(Sample s2 : set2){
				if(df.distance(s1, s2) <= threshold)
					return false;
			}
		}
		return true;
	}
	public static double disjointDistance(Collection<Sample> set1, Collection<Sample> set2, SampleDistance df, double threshold){
		double min = Double.MAX_VALUE;
		for(Sample s1 : set1){
			for(Sample s2 : set2){
				if(df.distance(s1, s2) <= min)
					min = df.distance(s1, s2);
			}
		}
		return min;
	}
}
