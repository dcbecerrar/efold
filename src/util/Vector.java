package util;

public class Vector {
	
	public static double dotProduct(double[] one, double[] two){
		double sum = 0;
		for(int i=0; i<3; i++){
			sum += one[i]*two[i];	                  
		}
		return sum;
	}
	
	public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2){
		return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2);
	}
	
	public static double squaredSize(double x1, double y1, double z1){
		return dotProduct(new double[] {x1, y1, z1}, new double[] {x1, y1, z1});
	}
	
	public static double squaredSize(double[] vector){
		return dotProduct(vector, vector);
	}
	public static double squaredDistance(double[] one, double[] two){
		return Math.pow(one[0] - two[0], 2) + Math.pow(one[1] - two[1], 2) + Math.pow(one[2] - two[2], 2);
	}

	public static double distance(double[] one, double[] two){
		return Math.sqrt(Math.pow(one[0] - two[0], 2) + Math.pow(one[1] - two[1], 2) + Math.pow(one[2] - two[2], 2));
	}
	
	public static double distance(double x1, double y1, double z1, double x2, double y2, double z2){
		return Math.sqrt(squaredDistance(x1, y1, z1, x2, y2, z2));
	}
	
	public static double[] vectorTo(double x1, double y1, double z1, double x2, double y2, double z2){
		return new double[] { x1 - x2, y1 - y2, z1 - z2 };
	}
	
	public static double[] vectorTo(double[] one, double[] two){
		return new double[] { one[0] - two[0], one[1] - two[1], one[2] - two[2] };
	}
}
