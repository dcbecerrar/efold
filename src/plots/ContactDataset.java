package plots;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.data.general.HeatMapDataset;

public class ContactDataset implements HeatMapDataset{
	
	Map<List<Integer>,Double> data;
	int xMin;
	int xMax;
	int yMin;
	int yMax;
	double zMin;
	double zMax;
	public ContactDataset (Map<List<Integer>,Double> Data){
		xMin = Integer.MAX_VALUE;
		yMin = Integer.MAX_VALUE;
		xMax = Integer.MIN_VALUE;
		yMax = Integer.MIN_VALUE;
		zMin = 0;
		zMax = Double.MIN_VALUE;
		data = Data;
		for(Entry<List<Integer>,Double> e : data.entrySet()){
			List<Integer> coord = e.getKey();
			xMax = Math.max(coord.get(0),xMax);
			xMin = Math.min(coord.get(0),xMin);
			yMax = Math.max(coord.get(1),yMax);
			yMin = Math.min(coord.get(1),yMin);
			zMin = Math.min(e.getValue(), zMin);
			zMax = Math.max(e.getValue(), zMax);
		}
	}
	
	public ContactDataset (Map<List<Integer>,Double> Data, boolean heat){
		xMin = Integer.MAX_VALUE;
		yMin = Integer.MAX_VALUE;
		xMax = Integer.MIN_VALUE;
		yMax = Integer.MIN_VALUE;
		zMin = 0;
		zMax = Double.MIN_VALUE;
		data = Data;
		for(Entry<List<Integer>,Double> e : data.entrySet()){
			List<Integer> coord = e.getKey();
			xMax = Math.max(coord.get(0),xMax);
			xMin = Math.min(coord.get(0),xMin);
			yMax = Math.max(coord.get(1),yMax);
			yMin = Math.min(coord.get(1),yMin);
			zMin = 0;
			zMax = 1;
		}
	}
	
	public double getMinZ(){
		return zMin;
	}
	
	public double getMaxZ(){
		return zMax;
	}
	
	public double getMaximumXValue(){
		return xMax;
	}
	public double getMaximumYValue(){
		return yMax;
	}
	public double getMinimumXValue(){
		return xMin;
	}
	public double getMinimumYValue(){
		return yMin;
	}
	public int	getXSampleCount(){
		return xMax-xMin+1;
	}
	public double getXValue(int xIndex){
		return 0;
	}
	public int	getYSampleCount(){
		return yMax-yMin+1;
	}
	public double getYValue(int yIndex){
		return 0;
	}
	
	public Number getZ(int xIndex, int yIndex){
		return data.get(Arrays.asList(new Integer[]{xIndex+xMin,yIndex+yMin}));
	}
	
	public double getZValue(int xIndex, int yIndex){
		Double val = data.get(Arrays.asList(new Integer[]{xIndex+xMin,yIndex+yMin}));
		if(val == null)
			return 0;
		else
			return val;
	}
}