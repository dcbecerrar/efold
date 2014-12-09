package foldpath;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import foldpath.Landscape;
import metrics.SampleDistance;
import util.Util;
import Jama.Matrix;
import foldpath.Cluster;

public class Simulation {
	public static void print(Matrix m){
		for(double[] row : m.getArray()){
			for(double element : row){
				System.out.print(element + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	
	/**
	 * Computes the matrix exponential using eigen value decomposition
	 * @param m - a non-singular matrix to be exponentiated
	 * @return exp(m)
	 */
	public static Matrix exp(Matrix m){
		Matrix v = m.eig().getV();
		Matrix d = m.eig().getD();
		Matrix vinv = v.inverse();
		
		for(int i=0; i < d.getRowDimension(); i++){
			d.set(i, i, Math.exp(d.get(i, i)));
		}
		
		return v.times(d).times(vinv);
	}
	
	/**
	 * Returns the qth order Pade approximation of exp(A)
	 * @param A - the matrix to be exponentiated
	 * @param q - the order of the Pade approximation
	 * @return exp(A)
	 */
	public static Matrix exp(Matrix A, int q){
		double nA = A.normInf();
		Matrix I = Matrix.identity(A.getRowDimension(), A.getColumnDimension());
		if(nA == 0.0)
			return I;
		double val = Math.log(nA)/Math.log(2.0);
		int e = (int) Math.floor(val);
		int j = Math.max(0,e+q);
		A = A.times(1.0/Math.pow(2.0, j));
		
		Matrix X = A;
		double c = 0.5;
		Matrix N = I.plus(A.times(c));
		Matrix D = I.minus(A.times(c));
		
		for(int k=2; k<=q; k++){
			c = c * (q-k+1) / (k*(2*q-k+1));
			X = X.times(A);
			Matrix cX = X.times(c);
			N = N.plus(cX);
			if(k%2==1)
				D = D.plus(cX);
			else
				D = D.minus(cX);
		}
		Matrix F = D.solve(N);
		for(int k=0; k<j; k++){
			F = F.times(F);
		}
		
		return F;		
	}
	
	/**
	 * Simulates the folding dynamics for a given transition matrix and starting condition
	 * @param initial - The initial density of states
	 * @param rates - The matrix of transition rates between pairs of states
	 * @param minT - The initial time computed
	 * @param maxT - The final time computed
	 * @param granularity - The log10(step-size) between calculations
	 * @return an array containing the densities of each state between the initial and final time at the given granularity
	 */
	public static double[][] foldingDynamics(double[] initial, double[][] rates, double minT, double maxT, double granularity){
		System.out.println("Starting folding Dynamics");
		Matrix initialM = new Matrix(initial, initial.length);
		Matrix ratesM = new Matrix(rates);
		
		int numSteps = (int) - (Math.log10(minT/maxT)/granularity) + 1;
		double[][] pStates = new double[numSteps][initial.length + 1];
		
		int step = 0;
		
		for(double logTime = Math.log10(minT); step < numSteps; step++){
			System.out.println("Step .... "+step);
			logTime = Math.log10(minT)+(granularity*step);
			pStates[step][0] = logTime;
			Matrix m = ratesM.times(Math.pow(10,logTime));
			
			double[] probability = exp(m, 7).times(initialM).transpose().getArray()[0];
			
			
			for(int state = 0; state < probability.length; state++){
				pStates[step][state+1] = probability[state];
			}
			
		}
		System.out.println("Ending folding Dynamics");
		return pStates;
	}
	
	/**
	 * Constructs the transition matrix using Kawasaki rules
	 * @param clusters - The states of 
	 * @param sd - The distance metric used to decide if two clusters are neighbors
	 * @param Beta - A temperature-like scaling factor
	 * @return A transition matrix respecting the delicate balance
	 */
	public static double[][] transitionMatrix(List<Cluster> clusters, SampleDistance sd, double threshold, double Beta){
		int nClusters = clusters.size();
		double[][] tMatrix = new double[nClusters][nClusters];
		double[] colSums = new double[nClusters];
		
		for(int i=0; i< nClusters; i++){
			for(int j=0; j< clusters.size(); j++){
				Cluster c1 = clusters.get(i);
				Cluster c2 = clusters.get(j);
				if(Landscape_tfolder.areNeighbors(c1, c2, sd, threshold)){
					tMatrix[i][j] = Math.pow(c1.weight/c2.weight,1/(2*Beta));
					colSums[j] += tMatrix[i][j];
				}
			}
		}
		
		for(int i=0; i< nClusters; i++)
			tMatrix[i][i] -= colSums[i];
		
		return tMatrix;
	}
	
	public static double[][] transitionMatrix(List<Double> weights, boolean[][] adjacencyMatrix, double Beta){
		int nStates = weights.size();
		double[][] tMatrix = new double[nStates][nStates];
		double[] colSums = new double[nStates];
		
		for(int i=0; i< nStates; i++){
			for(int j=0; j< nStates; j++){
				if(adjacencyMatrix[i][j]){
					Double w1 = weights.get(i);
					Double w2 = weights.get(j);
					tMatrix[i][j] = Math.pow(w1/w2,1/(2*Beta));
					colSums[j] += tMatrix[i][j];
				}
			}
		}
		
		for(int i=0; i< nStates; i++)
			tMatrix[i][i] -= colSums[i];
		
		return tMatrix;
	}
	
	public static double[][] foldingDynamics(List<Cluster> clusters, SampleDistance sd, double threshold, double Beta, double minT, double maxT, double granularity){
		System.out.println("Starting transition matrix");
		double[][] rates = transitionMatrix(clusters, sd, threshold, Beta);
		System.out.println("Ending transition matrix");
	//	System.out.println(Landscape.clusterLabels(clusters));
	//	for(double[] r : rates)
	//	System.out.println(Util.list(ArrayUtils.toObject(r)));
		double[] initial = new double[clusters.size()];
		initial[0] = 1;
		
		return foldingDynamics(initial, rates, minT, maxT, granularity);	
	}
	
	
	public static void main(String[] args) {
		double[] initial = new double[]{1.0,0.0,0.0,0.0,0.0};
		double[][] rates = transitionMatrix(Util.list(0.0001,0.01,3.0,3.0001,300.0), new boolean[][]{{true,false,true,false,false},{false,false,false,false,false},{true,false,true,false,true},{false,false,false,false,false},{false,false,true,false,true}}, 1.0);
		
		double minT = .0000001;
		double maxT=1000000;
		double granularity=.01;
		
		double[][] dynamics = foldingDynamics(initial, rates, minT, maxT, granularity);
		
		Map<String,Double[][]> dynamicsData = new TreeMap<String, Double[][]>();
		for(int cluster = 1; cluster < dynamics[0].length; cluster++){
			String label = "..."+cluster;
			Double[][] data = new Double[dynamics.length][2];
			for(int timeStep = 0; timeStep < dynamics.length; timeStep++){
				data[timeStep][0] = dynamics[timeStep][0];
				data[timeStep][1] = dynamics[timeStep][cluster];
			}
			dynamicsData.put(label, data);
		}
		
	//	ChartUtils.showChart(ChartUtils.createChart(dynamicsData, "duh", "x", "y"));
	}
	
	
}

