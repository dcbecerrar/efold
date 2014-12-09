package experiments;


import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import foldpath.Sample;
import plots.PDBReader;
import plots.ResultChart;
import SS.Constants.*;
import util.Util;

public class Contact_Prediction {

	
	/**
	 * The samples are added to the upper half of the triangle
	 * @param samples - Samples from sampling
	 * @return A stochastic contact map, which can be displayed using the ContactChart utility.
	 */
	public static Map<List<Integer>,Double> contactMap(Collection<Sample> samples){
		Map<List<Integer>,Double> contactMap = new HashMap<List<Integer>,Double>();
		int length = samples.iterator().next().length;
		
		for(int i=0; i<length; i++){
			for(int j=0; j<length; j++){
				contactMap.put(Arrays.asList(new Integer[]{i,j}), 0.0);
			}
		}
		
		for(Sample aSample : samples){	
			Integer[] LHS = aSample.neighbors();
			Double p = aSample.probability;
			for(int i=0; i<length; i++){
				Integer l = LHS[i];
				if(l!=null){
					contactMap.put(Arrays.asList(new Integer[]{l,i}), contactMap.get(Arrays.asList(new Integer[]{l,i})) + p);
					contactMap.put(Arrays.asList(new Integer[]{i,l}), contactMap.get(Arrays.asList(new Integer[]{i,l})) + p);
				}
			}
		}
		return contactMap;	
	}
	
	public static double[][] strandAssignments(Collection<Sample> samples){
		int numStrands = samples.iterator().next().numStrands;
		int length = samples.iterator().next().length;	
		List<Integer> template = samples.iterator().next().template;
		double[][] assignments = new double[numStrands][length];
		double[] sum = new double[numStrands];
		for(Sample aSample : samples){
			for(int i=0; i< numStrands; i++){
				Integer[][] structure = aSample.structure;
				int start = structure[i][0]; 
				int end = structure[i][1];
				for(int j=start; j<end; j++){
					assignments[template.get(i)-1][j] += aSample.probability;				
				}
				sum[template.get(i)-1] += aSample.probability * length;
			}
		}
		for(int i=0; i< numStrands; i++){
			for(int j=0; j<length; j++){
				assignments[template.get(i)-1][j] /= sum[template.get(i)-1];
			}
		}
		return assignments;	
	}
	
	public static void writeTextualSummary(String path, Collection<Sample> samples, Map<List<Integer>, Double> pdbMap, Map<List<Integer>, Double> pdbThresh, Map<List<Integer>, Double> contMap, int clust, String version, double[][] summary, double[][] summary_count, String pdb, Character chain) throws IOException {
		int granularity = 200;
		double[] accuracy = new double[granularity+1];
		double[] accuracy12 = new double[granularity+1];
		double[] accuracy24 = new double[granularity+1];
		double[] coverage = new double[granularity+1];
		double[] coverage12 = new double[granularity+1];
		double[] coverage24 = new double[granularity+1];
		double[] fMeasure = new double[granularity+1];
		double[] fMeasure12 = new double[granularity+1];
		double[] fMeasure24 = new double[granularity+1];
		int fMax = 0;
		int fMax12 = 0;
		int fMax24 = 0;
		double totalP = 0;
		for(Sample s : samples){
			totalP += s.probability;
		}
		
		for(int i=1; i< granularity; i ++){
			accuracy[i-1] = EnsembleEval.accuracy(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh,0);
			accuracy12[i-1] = EnsembleEval.accuracy(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh,0, 12);
			accuracy24[i-1] = EnsembleEval.accuracy(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh,0, 24);
			coverage[i-1] = EnsembleEval.coverage(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh, 0);
			coverage12[i-1] = EnsembleEval.coverage(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh, 0, 12);
			coverage24[i-1] = EnsembleEval.coverage(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh, 0, 24);
			fMeasure[i-1] = EnsembleEval.fMeasure(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh, 0);
			fMeasure12[i-1] = EnsembleEval.fMeasure(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh, 0, 12);
			fMeasure24[i-1] = EnsembleEval.fMeasure(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh, 0, 24);
			fMax = fMeasure[i-1] >= fMeasure[fMax] ? i-1 : fMax;
			fMax12 = fMeasure12[i-1] >= fMeasure12[fMax12] ? i-1 : fMax12;
			fMax24 = fMeasure24[i-1] >= fMeasure24[fMax24] ? i-1 : fMax24;
		}
		
		PrintStream fw = new PrintStream(path+"/summary"+version+"_"+ clust);
		
		fw.println("Exact agreement");
		fw.printf("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n","f","c","a","f12","c12","a12","f24","c24","a24");
		fw.printf("%1.4f\t%1.4f\t%1.4f\t%1.4f\t%1.4f\t%1.4f\t%1.4f\t%1.4f\t%1.4f\n",fMeasure[fMax],accuracy[fMax],coverage[fMax],fMeasure12[fMax12],accuracy12[fMax12],coverage12[fMax12],fMeasure24[fMax24],accuracy24[fMax24],coverage24[fMax24]);
		
		
		if(!Double.isNaN(fMeasure[fMax])){
			summary[0][0]+=fMeasure[fMax];
			summary_count[0][0]+=1;
		}
		if(!Double.isNaN(accuracy[fMax])){
			summary[0][1]+=accuracy[fMax];
			summary_count[0][1]+=1;
		}
		if(!Double.isNaN(coverage[fMax])){
			summary[0][2]+=coverage[fMax];
			summary_count[0][2]+=1;
		}
		if(!Double.isNaN(fMeasure12[fMax12])){
			summary[0][3]+=fMeasure12[fMax12];
			summary_count[0][3]+=1;
		}
		if(!Double.isNaN(accuracy12[fMax12])){
			summary[0][4]+=accuracy12[fMax12];
			summary_count[0][4]+=1;
		}
		if(!Double.isNaN(coverage12[fMax12])){
			summary[0][5]+=coverage12[fMax12];
			summary_count[0][5]+=1;
		}
		if(!Double.isNaN(fMeasure24[fMax24])){
			summary[0][6]+=fMeasure24[fMax24];
			summary_count[0][6]+=1;
		}
		if(!Double.isNaN(accuracy24[fMax24])){
			summary[0][7]+=accuracy24[fMax24];
			summary_count[0][7]+=1;
		}
		if(!Double.isNaN(coverage24[fMax24])){
			summary[0][8]+=coverage24[fMax24];
			summary_count[0][8]+=1;
		}
		
		
		
		
		
		
		
		
		
		
		fw.println("+-2 agreement");
		
		accuracy = new double[granularity+1];
		accuracy12 = new double[granularity+1];
		accuracy24 = new double[granularity+1];
		coverage = new double[granularity+1];
		coverage12 = new double[granularity+1];
		coverage24 = new double[granularity+1];
		fMeasure = new double[granularity+1];
		fMeasure12 = new double[granularity+1];
		fMeasure24 = new double[granularity+1];
		fMax = 0;
		fMax12 = 0;
		fMax24 = 0;
		for(int i=1; i<= granularity; i ++){
			accuracy[i-1] = EnsembleEval.accuracy(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh,2);
			accuracy12[i-1] = EnsembleEval.accuracy(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh,2, 12);
			accuracy24[i-1] = EnsembleEval.accuracy(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh,2, 24);
			coverage[i-1] = EnsembleEval.coverage(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh, 2);
			coverage12[i-1] = EnsembleEval.coverage(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh, 2, 12);
			coverage24[i-1] = EnsembleEval.coverage(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh, 2, 24);
			fMeasure[i-1] = EnsembleEval.fMeasure(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh, 2);
			fMeasure12[i-1] = EnsembleEval.fMeasure(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh, 2, 12);
			fMeasure24[i-1] = EnsembleEval.fMeasure(PDBReader.threshold(contMap, i * totalP / granularity), pdbThresh, 2, 24);
			fMax = fMeasure[i-1] >= fMeasure[fMax] ? i-1 : fMax;
			fMax12 = fMeasure12[i-1] >= fMeasure12[fMax12] ? i-1 : fMax12;
			fMax24 = fMeasure24[i-1] >= fMeasure24[fMax24] ? i-1 : fMax24;
		}
	
		fw.printf("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n","f","c","a","f12","c12","a12","f24","c24","a24");
		fw.printf("%1.4f\t%1.4f\t%1.4f\t%1.4f\t%1.4f\t%1.4f\t%1.4f\t%1.4f\t%1.4f\n",fMeasure[fMax],accuracy[fMax],coverage[fMax],fMeasure12[fMax12],accuracy12[fMax12],coverage12[fMax12],fMeasure24[fMax24],accuracy24[fMax24],coverage24[fMax24]);
		
		fw.close();

		if(!Double.isNaN(fMeasure[fMax])){
			summary[1][0]+=fMeasure[fMax];
			summary_count[1][0]+=1;
		}
		if(!Double.isNaN(accuracy[fMax])){
			summary[1][1]+=accuracy[fMax];
			summary_count[1][1]+=1;
		}
		if(!Double.isNaN(coverage[fMax])){
			summary[1][2]+=coverage[fMax];
			summary_count[1][2]+=1;
		}
		if(!Double.isNaN(fMeasure12[fMax12])){
			summary[1][3]+=fMeasure12[fMax12];
			summary_count[1][3]+=1;
		}
		if(!Double.isNaN(accuracy12[fMax12])){
			summary[1][4]+=accuracy12[fMax12];
			summary_count[1][4]+=1;
		}
		if(!Double.isNaN(coverage12[fMax12])){
			summary[1][5]+=coverage12[fMax12];
			summary_count[1][5]+=1;
		}
		if(!Double.isNaN(fMeasure24[fMax24])){
			summary[1][6]+=fMeasure24[fMax24];
			summary_count[1][6]+=1;
		}
		if(!Double.isNaN(accuracy24[fMax24])){
			summary[1][7]+=accuracy24[fMax24];
			summary_count[1][7]+=1;
		}
		if(!Double.isNaN(coverage24[fMax24])){
			summary[1][8]+=coverage24[fMax24];
			summary_count[1][8]+=1;
		}
		
		fw = new PrintStream(path+"/accuracy"+version+"_"+clust);
		fw.println("accuracy coverage f-measure");
		for(int i=0; i < accuracy.length ; i++){
			fw.printf("%f %f %f\n", accuracy[i], coverage[i], fMeasure[i]);
		}
		fw.close();
		
		fw = new PrintStream(path+"/strands"+version+"_"+clust);
		double[][] sd = strandAssignments(samples);
		Double[] sa = PDBReader.strandAssignments(pdb, chain);
		for(int i=0; i < sd.length ; i++){
			fw.printf("%d ", i+1);
		}
		fw.println("pdb");
		int correction = 0;

		if(sa.length > sd[0].length){
			correction = sa.length - sd[0].length;
		}
		for(int i=0; i < sa.length - correction ; i++){
			for(int j=0; j<sd.length; j++){
				fw.printf("%f ", sd[j][i]);
			}
			fw.printf("%f\n", sa[i]==null? 0 : sa[i] );
		}
		fw.close();
		
		ResultChart.saveChart(accuracy, coverage, fMeasure, path +"/accuracy"+version+"_"+clust+".png", 600, 300, "Predictive Performance", "Threshold", "Value");
		
	}
	
}