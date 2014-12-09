package dynamics;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import dynamics.Constants.*;


public class Util {
	public static <T> List<T> list(T ...elements){
		return Arrays.asList(elements);
	}
	
	public static String clusterLabelShort(List<Integer> template, List<Dir> pairing){
		List<Object> zipped = new LinkedList<Object>(template);
		if(pairing.size() == 0)
			zipped.add("Unfolded");
		else
			for(int j=pairing.size(); j>0; j--){
				zipped.add(j, pairing.get(j-1).toString().charAt(0));
			}	
		return StringUtils.join(zipped, "");
	}
	
	public static List<Color> getPalette(int number){
		List<Color> palette = new ArrayList<Color>();
		for(float i=0; i<number; i++){
			palette.add(Color.getHSBColor(i/number, 1f, 1f));
		}
		return palette;
	}
	
	public static <S,T> Map<S, T> map(List<S> keys, List<T> values) {
		Map<S,T> m = new HashMap<S, T>();
		for(int i=0; i<keys.size(); i++)
			m.put(keys.get(i), values.get(i));
		
		return m;
	}
	
}