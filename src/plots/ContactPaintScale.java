package plots;

import java.awt.Color;
import java.awt.Paint;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jfree.chart.renderer.PaintScale;

public class ContactPaintScale implements PaintScale{
	
	HashMap<Double, Color> scale;
	Double[] keys;
	double min,max;
	
	public ContactPaintScale(double Min, double Max, List<Color> colors){
		min = Min;
		max = Max;
		double inc = (Max-Min)/(colors.size()-1);
		double val = Min;
		scale = new HashMap<Double,Color>();
		
		for(Color c: colors){
			scale.put(new Double(val),c);
			val += inc;
		}
		
		keys = scale.keySet().toArray(new Double[]{});
		Arrays.sort(keys);
	}
	
	public double getLowerBound() {
		return min;
	}
	
	public Paint getPaint(double value) {
		Double floor = floor(keys, value);
		Double ceiling = ceiling(keys, value);
		if(floor == ceiling) {
			return scale.get(floor);
		}
		
		double delta = ceiling - floor;
		float rCeilCol = (float) ((value - floor)/delta);
		float rFloorCol = (float) ((ceiling - value)/delta);
		Color Floor = scale.get(floor);
		Color Ceiling  = scale.get(ceiling);
		float[] fRGB = Floor.getRGBComponents(null);
		float[] cRGB = Ceiling.getRGBComponents(null);
		Color mixture = new Color((fRGB[0]*rFloorCol)+(cRGB[0]*rCeilCol),(fRGB[1]*rFloorCol)+(cRGB[1]*rCeilCol),(fRGB[2]*rFloorCol)+(cRGB[2]*rCeilCol),(fRGB[3]*rFloorCol)+(cRGB[3]*rCeilCol));
		return mixture;
	}
	
	public double getUpperBound() {
		return max;
	}
	
	public static Double floor(Double[] items, double key) {
		int location = Arrays.binarySearch(items, key);
		
		if (location >= 0) return items[location];
		
		location = -(location + 1);
		
		if (location == 0) {
			return items[0];
		} else if (location == items.length) {
			return items[items.length - 1];
		}
		
		return items[location - 1];
	}
	
	public static Double ceiling(Double[] items, double key) {
		int location = Arrays.binarySearch(items, key);
		
		if (location >= 0) return items[location];
		
		location = -(location + 1);
		
		if (location == 0) {
			return items[0];
		} else if (location == items.length) {
			return items[items.length - 1];
		}
		
		return items[location];
	}
	
}
