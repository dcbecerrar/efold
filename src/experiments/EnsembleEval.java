package experiments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import util.Util;

public class EnsembleEval {

	public static void main(String[] args) {

	}

	public static double inside(Map<List<Integer>,Double> contactMap, Map<List<Integer>,Double> observed){
		double total = 0;
		for(Entry<List<Integer>, Double> e : observed.entrySet()){
			if(contactMap.get(e.getKey()) != null){
				total += e.getValue() * contactMap.get(e.getKey());			
			}
		}
		return total;	
	}

	public static double total(Map<List<Integer>,Double> contactMap){
		double total = 0;
		for(Entry<List<Integer>, Double> e : contactMap.entrySet()){
			total += e.getValue();			
		}
		return total;	
	}

	public static Map<List<Integer>,Double> boundedMap(Map<List<Integer>,Double> contactMap, int bound){
		Map<List<Integer>,Double> bMap = new HashMap<List<Integer>, Double>();

		for(Entry<List<Integer>, Double> e : contactMap.entrySet()){

			List<Integer> key = e.getKey();
			if(e.getValue() > 0){
				for(int i = -bound; i <= bound; i++){
					for(int j = -bound; j <= bound; j++){							
						Integer x = key.get(0)+i;
						Integer y = key.get(1)+j;
						if(contactMap.containsKey(Util.list(x,y))){
							bMap.put(Util.list(x,y),1.0);
						}
					}
				}
			}
			else if(bMap.get(key) == null)
				bMap.put(key,0.0);

		}
		return bMap;	
	}
	
	public static Map<List<Integer>,Double> filter(Map<List<Integer>,Double> map, int distance){
		Map<List<Integer>,Double> filtered = new HashMap<List<Integer>, Double>(map);
		for(Entry<List<Integer>, Double> e : filtered.entrySet()){
			if(Math.abs(e.getKey().get(0) - e.getKey().get(1)) < distance )
				filtered.put(e.getKey(), 0.0);
		}
		return filtered;
	}

	public static double accuracy(Map<List<Integer>,Double> contactMap, Map<List<Integer>,Double> observed, int bound){
		Map<List<Integer>,Double> bMap = boundedMap(observed, bound);
		return (inside(bMap, contactMap)/total(contactMap));	
	}
	
	public static double accuracy(Map<List<Integer>,Double> contactMap, Map<List<Integer>,Double> observed, int bound, int distance){
		Map<List<Integer>,Double> bMap = boundedMap(filter(observed,distance), bound);
		Map<List<Integer>,Double> oMap = filter(contactMap,distance);
		
		return (inside(bMap, oMap)/total(oMap));
	}

	public static double coverage(Map<List<Integer>,Double> contactMap, Map<List<Integer>,Double> observed, int bound){
		Map<List<Integer>,Double> bMap = boundedMap(contactMap, bound);
		return (inside(bMap, observed)/total(observed));	
	}
	
	public static double coverage(Map<List<Integer>,Double> contactMap, Map<List<Integer>,Double> observed, int bound, int distance){
		Map<List<Integer>,Double> bMap = boundedMap(filter(contactMap,distance), bound);
		Map<List<Integer>,Double> oMap = filter(observed,distance);
		
		return (inside(bMap, oMap)/total(oMap));	
	}
	
	public static String accuracyS(Map<List<Integer>,Double> contactMap, Map<List<Integer>,Double> observed, int bound){
		Map<List<Integer>,Double> bMap = boundedMap(observed, bound);
		return (inside(bMap, contactMap)/total(contactMap)) + " (" + (int) (inside(bMap, contactMap)/2)+"/"+(int) (total(contactMap)/2)+")";	
	}

	public static String coverageS(Map<List<Integer>,Double> contactMap, Map<List<Integer>,Double> observed, int bound){
		Map<List<Integer>,Double> bMap = boundedMap(contactMap, bound);
		return (inside(bMap, observed)/total(observed)) + " (" + (int) (inside(bMap, observed)/2)+"/"+(int) (total(observed)/2)+")";	
	}
	
	public static double fMeasure(Map<List<Integer>,Double> contactMap, Map<List<Integer>,Double> observed, int bound){
		double precision = accuracy(contactMap, observed, bound);
		double sensitivity = coverage(contactMap, observed, bound);
		return precision + sensitivity > 0 ? (2*precision*sensitivity)/(precision+sensitivity) : 0;
	}
	
	public static double fMeasure(Map<List<Integer>,Double> contactMap, Map<List<Integer>,Double> observed, int bound, int distance){
		double precision = accuracy(contactMap, observed, bound, distance);
		double sensitivity = coverage(contactMap, observed, bound, distance);
		return precision + sensitivity > 0 ? (2*precision*sensitivity)/(precision+sensitivity) : 0;
	}

}
