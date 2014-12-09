package pdb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import util.SampleDistance;
import util.ContactDistance;

import util.Sample;

import util.Constants.Dir;
import util.Cluster;

public class Pathways {
	public static List<Cluster> clusters;
	public static List<String> topologies = new LinkedList<String>();
	public static SampleDistance transMetric;
	public static SampleDistance clustMetric;
	public static Double transitionThreshold;
	public static String clusterMetricParameters="2";
	public static int [][]edges;
	public static int [][]summaryEdges;
	public static int []completePaths;
	public static int []summaryCompletePaths;
	public static int allPaths=0;
	public static int []nuclei_residues;
	public static int total_nuclei=0;
	public static List<String> paths;
	public static int size;
	public static String sequence;
	
	
	public static void main(String[] args) {
		transMetric = new ContactDistance(2);
		clustMetric = new ContactDistance(2);
		String path = "/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/eFold/experimentation/Files/StudyCase/Sets0/";
		readTopologies(path+"Topologies_1UBQ");
		interestingPaths();
		read_files(path);
	}
	
	public static void interestingPaths(){
		paths = new LinkedList<String>();
		//1EM7
//		paths.add("1;4;5");
//		paths.add("1;2;5");
//		paths.add("0;2;5");
//		paths.add("1;3;5");
//		paths.add("0;3;5");
		//1UBQ
		paths.add("1;5;6;10");
		paths.add("1;2;6;10");
		paths.add("0;2;6;10");
		paths.add("1;3;6;10");
		paths.add("0;3;6;10");
		paths.add("1;4;7;10");
		paths.add("1;2;8;10");
		paths.add("0;2;8;10");
		paths.add("1;4;9;10");
		//PF00018
//		paths.add("0;1;3;6");
//		paths.add("0;2;4;6");
//		paths.add("0;1;5;6");
		//PF00240
//		paths.add("1;4;5");
//		paths.add("1;2;5");
//		paths.add("0;2;5");
//		paths.add("1;3;5");
//		paths.add("0;3;5");
		//PF01423
//		paths.add("0;1;3;5");
//		paths.add("0;1;4;5");
//		paths.add("0;2;4;5");
		//PF00014
//		paths.add("0;1");
//		paths.add("0;2");
//		paths.add("0;3");
//		paths.add("0;4");
	}
	
	public static void read_files(String path){
		String pathFiles = path;
		String NameFiles = pathFiles + "NameFiles";
		String writeFile="";
		String nameFile = "", nameFileAux;
		File directoryPdb;
		String FolderPdb;
		List<String> Names = new LinkedList<String>();
		String toWrite="";
		int totalRuns = 0;
		Names = Read_NameFiles(NameFiles);
		for(int i=0; i<Names.size(); i++){
			allPaths=0;
			totalRuns = 0;
			total_nuclei=0;
			edges = new int[topologies.size()][topologies.size()];
			summaryEdges = new int[topologies.size()][topologies.size()];
			completePaths = new int[paths.size()];
			summaryCompletePaths = new int[paths.size()];
			read_csv(path+"Sets0",Names.get(i));
			nuclei_residues = new int[size];
			writeFile = pathFiles + "Pathways_"+ Names.get(i)+".txt";
			FolderPdb = pathFiles+Names.get(i);
	        directoryPdb = new File(FolderPdb);
	        File[] fileList = directoryPdb.listFiles();
			for (File file : fileList){
				nameFile = file.getName();
				if(nameFile.contains("clustersALL_"+Names.get(i))){
					nameFileAux = nameFile.replace("ALL", "");
					clusters = new LinkedList<Cluster>();
					transitionThreshold=readTransitionThreshold(FolderPdb+"/"+nameFileAux); 
					readCluster(FolderPdb+"/"+nameFile);
					compute_missing_topologies(nameFile,path);
					totalRuns++;
					resetedges();
					resetcompletePaths();
					stablishEdges();
					UpdateSummaryEdges();
					checkPathways();
					UpdateSummaryCompletePaths();
					checkAllPaths();
					
				}	
			}
			toWrite = preparePrint(nameFile, totalRuns); 
			writePathwaysStatistics(writeFile,toWrite);
		}

	}
	
	public static void compute_missing_topologies(String nameFile, String path){
		String FileDelete = path+"Delete1UBQ.txt";
		FileWriter fw;
		try {
			fw = new FileWriter(FileDelete,true);
			BufferedWriter bw = new BufferedWriter(fw);

		
			
		
		
		boolean presence=true;
		System.out.println(nameFile+":\n");
		for(int i=0;i<topologies.size();i++){		
			presence = false;
			for(int j=0;j<clusters.size();j++){
				if(topologies.get(i).equals(clusterLabelShort(clusters.get(j).template,clusters.get(j).pairing))){
					presence = true;
					break;
				}
			}
			if(presence == false){
				System.out.println(topologies.get(i)+":\n");
				
					bw.write(nameFile+ " ------------> "+topologies.get(i)+"\n");
			}
		}
		
		
		
		bw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void checkAllPaths(){
		//1EM7
//		if(completePaths[1]==1 && completePaths[2]==1 && completePaths[3]==1 && completePaths[4]==1 && completePaths[0]==1){
//			allPaths++;
//		}
		//1UBQ
		if(completePaths[1]==1 && completePaths[2]==1 && completePaths[3]==1 && completePaths[4]==1 && completePaths[0]==1 && completePaths[5]==1 && completePaths[6]==1 && completePaths[7]==1 && completePaths[8]==1 && completePaths[9]==1 & completePaths[10]==1){
			allPaths++;
		}
//		if((completePaths[1]==1 || completePaths[2]==1) && (completePaths[3]==1 || completePaths[4]==1) && (completePaths[0]==1)){
//			allPaths++;
//		}
		//PF00018
//		if((completePaths[0]==1 && completePaths[1]==1)){
//			allPaths++;
//		}
		//PF00240
//		if((completePaths[1]==1 || completePaths[2]==1) && (completePaths[3]==1 || completePaths[4]==1) && (completePaths[0]==1)){
//			allPaths++;
//		}
		//PF01423
//		if((completePaths[0]==1 || completePaths[1]==1) && (completePaths[2]==1)){
//			allPaths++;
//		}
	}
	
	public static String preparePrint(String nameFile, int totalRuns){
		String line="nameFile\n";
		line = "Summary Edges" + "\n";
		for(int i=0;i<topologies.size();i++){
			line+="\t"+topologies.get(i);
		}
		line += "\n";
		for(int i=0;i<topologies.size();i++){
			line+=topologies.get(i);
			for(int j=0;j<topologies.size();j++){
				line+="\t"+ ((double)summaryEdges[i][j]/totalRuns);
			}
			line += "\n";
		}
		line += "Summary Pathways" + "\n";
		for(int i=0;i<paths.size();i++){
			line += paths.get(i)+"\t";
		}
		line += "\n";
		for(int i=0;i<paths.size();i++){
			line += ((double)summaryCompletePaths[i]/totalRuns)+"\t";
		}
		line += "\n";
		line += "All paths";
		line += "\n";
		line += (double)allPaths/totalRuns;
		line += "\n";
		line += "Total Runs: "+ totalRuns;
		line += "\n";
		line += "Total nuclei residues";
		line += "\n";
		line += total_nuclei;
		line += "\n";
		for(int i=0; i<nuclei_residues.length;i++){
			line+=sequence.charAt(i)+"\t";
		}
		line += "\n";
		for(int i=0; i<nuclei_residues.length;i++){
			line+=((double)nuclei_residues[i]/total_nuclei)+"\t";
		}
		return line;
	}

	public static void checkPathways(){
		String[] pth;
		int pos1,pos2;
		boolean complete=true;
		for(int i=0; i<paths.size(); i++){
			pth=paths.get(i).split(";");
			complete = true;
			for(int j=0;j<pth.length-1;j++){
				pos1 = Integer.valueOf(pth[j]);
				pos2 = Integer.valueOf(pth[j+1]);
				if(edges[pos1][pos2]!=1){
					complete=false;
					j=pth.length;
				}
			}
			if(complete){
				completePaths[i]=1;
			}
		}
	}
	
	public static void UpdateSummaryEdges(){
		for(int i=0;i<edges.length;i++){
			for(int j=0;j<edges.length;j++){
				summaryEdges[i][j]+=edges[i][j]; 
			}
		}
	}
	
	public static void resetedges(){
		for(int i=0;i<edges.length;i++){
			for(int j=0;j<edges.length;j++){
				edges[i][j]=0; 
			}
		}
	}
	
	public static void resetcompletePaths(){
		for(int i=0;i<completePaths.length;i++){
				completePaths[i]=0; 
		}
	}
	
	public static void UpdateSummaryCompletePaths(){
		for(int i=0;i<completePaths.length;i++){
			summaryCompletePaths[i]+=completePaths[i]; 
		}
	}

	
	public static void stablishEdges(){
		String topo1, topo1R, topo2, topo2R;
		List<Integer> tp1;
		List<Integer> tp2;
		for(int i=0;i<topologies.size()-1;i++){
			topo1 = topologies.get(i);
			topo1R = new StringBuilder(topo1).reverse().toString();
			tp1 = new LinkedList();
			for(int m=0;m<clusters.size()-1;m++){
				if(clusterLabelShort(clusters.get(m).template, clusters.get(m).pairing).equals(topo1)||clusterLabelShort(clusters.get(m).template, clusters.get(m).pairing).equals(topo1R)){
					tp1.add(m);
				}	
			}
			for(int j=i+1;j<topologies.size();j++){
				topo2 = topologies.get(j);
				topo2R = new StringBuilder(topo2).reverse().toString();
				tp2 = new LinkedList();
				for(int m=0;m<clusters.size()-1;m++){
					if(clusterLabelShort(clusters.get(m).template, clusters.get(m).pairing).equals(topo2)||clusterLabelShort(clusters.get(m).template, clusters.get(m).pairing).equals(topo2R)){
						tp2.add(m);
					}	
				}
				for(int m=0;m<tp1.size();m++){
					for(int n=0;n<tp2.size();n++){
						if(areNeighbors(clusters.get(tp1.get(m)), clusters.get(tp2.get(n)), transMetric, transitionThreshold)){
							edges[i][j] = 1;
							edges[j][i] = 1;
							m = tp1.size();
							n = tp2.size();
						}
					}
				}
			}
		}
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
	
	public static void readTopologies(String path){
		BufferedReader br = null;
		String sCurrentLine;
		try {
			br = new BufferedReader(new FileReader(path));
			while ((sCurrentLine = br.readLine()) != null) {
				topologies.add(sCurrentLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
		}
	}
	
	public static Double readTransitionThreshold(String path){
		BufferedReader br = null;
		Double TT=-1d;
		String sCurrentLine;
		try {
			br = new BufferedReader(new FileReader(path));
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.contains(";")){
					TT = Double.valueOf(sCurrentLine.split(";")[0]);
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
		}
		return TT;
	}
	
	public static void readCluster(String path){
		List<Integer> template = new LinkedList<Integer>();
		List<Dir> pairing = null;
		double w_cluster = 0, w_sample, p_sample;
		BufferedReader br = null;
		int index = 0;
		List<Sample> connectedSet = null; 
		try {

			String sCurrentLine;
			String []splitLine;
			String []residues;
			String []positions;
			String aux;
			br = new BufferedReader(new FileReader(path));
			boolean first = true, topology = true, sample = true;
			clusters.add(new Cluster(new ArrayList<Integer>(), new ArrayList<Dir>(), null, 0.001));
			while ((sCurrentLine = br.readLine()) != null) {
				if(first && sCurrentLine.contains("Unfolded")){
					first = false;
					continue;
				}
				if(!first && sCurrentLine.contains("Cluster")){
					topology = true;
					sample = false;
					if(template.size()!=0){
						Cluster cluster = new Cluster(template, pairing, connectedSet, w_cluster);
						clusters.add(cluster);
					}
					continue;
				}
				if(!first && topology){
					splitLine = sCurrentLine.split(" ");
					template = new LinkedList<Integer>(expandTemplate(splitLine[0]));
					pairing = new LinkedList<Dir>(expandDir(splitLine[0]));
					w_cluster = Double.valueOf(splitLine[1]);
					topology = false;
					sample = true;
					connectedSet = new LinkedList<Sample>();
					continue;
				}
				if(!first && sample){
					splitLine = sCurrentLine.split(" ");
					w_sample = Double.valueOf(splitLine[0]);
					p_sample = Double.valueOf(splitLine[1]);
					topology = false;
					sample = true;
					residues = splitLine[2].split(";");
					Integer [][]theSample = new Integer[residues.length][2];
					for(int i =0; i<residues.length; i++){
						aux = residues[i];
						index = aux.indexOf("(");
						aux = aux.substring(0, index) + aux.substring(index+1);
						index = aux.indexOf(")");
						aux = aux.substring(0, index) + aux.substring(index+1);
						positions = aux.split(",");
						theSample [i][0] = Integer.valueOf(positions[0]);
						theSample [i][1] = Integer.valueOf(positions[1]);
					}
					Sample new_sample = new Sample(template, pairing, theSample, 0, w_sample, p_sample);
					connectedSet.add(new_sample);
					if(clusterLabelShort(template,pairing).equals(topologies.get(topologies.size()-1))){
						total_nuclei++;
						for(int p=0;p<theSample.length;p++){
							for(int q=theSample[p][0];q<=theSample[p][1];q++){
								nuclei_residues[q]++;
							}
						}
					}
				}
			}
			if(template.size()!=0){
				Cluster cluster = new Cluster(template, pairing, connectedSet, w_cluster);
				clusters.add(cluster);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static List<String> Read_NameFiles(String path){
		BufferedReader br = null;
		List<String> Names = new LinkedList<String>(); 
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(path));
			while ((sCurrentLine = br.readLine()) != null) {
//				if(sCurrentLine.length()==4){
					Names.add(sCurrentLine);
//				}			
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return Names;
	}
	
	public static List<Dir> expandDir(String topology){
		List<Dir> pairing = new LinkedList<Dir>();
		for(int i=0;i<topology.length();i++){
			if(i%2!=0){
				pairing.add(MapDir(topology.charAt(i)));
			}
		}
		return pairing;
	}
	
	public static List<Integer> expandTemplate(String topology){
		List<Integer> template = new LinkedList<Integer>();
		for(int i=0;i<topology.length();i++){
			if(i%2==0){
				template.add(Integer.valueOf(topology.substring(i, i+1)));
			}
		}
		return template;
	}
	
	public static Dir MapDir(char dir){
		if(dir=='P'){
			return Dir.PARA;
		}
		if(dir=='A'){
			return Dir.ANTI;
		}
		if(dir=='N'){
			return Dir.NONE;
		}
		return null;
	}
	
	public static boolean areNeighbors(Cluster c1, Cluster c2, SampleDistance sd, double threshold){						
		if(Math.min(c1.template.size(),c2.template.size()) == 0 && Math.max(c1.template.size(),c2.template.size()) <= 2){
			return true;
		}else{
			if(areNeighbors(c1.template, c1.pairing, c2.template, c2.pairing)){
				return  !disjoint(c1.samples, c2.samples, sd, threshold);
			}else{
				return false;
			}
		}	
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
		//return (smallerT.equals(removeFirst(biggerT)) && smallerP.equals(biggerP.subList(1, biggerP.size()))) || (smallerT.equals(removeLast(biggerT)) && smallerP.equals(biggerP.subList(0, biggerP.size()-1)));
		return (smallerT.equals(removeFirst(biggerT)) && smallerP.equals(biggerP.subList(1, biggerP.size()))) || (smallerT.equals(removeLast(biggerT)) && smallerP.equals(biggerP.subList(0, biggerP.size()-1))) || (smallerT.equals(reversed_tmp(removeFirst(biggerT))) && smallerP.equals(reversed_pair(biggerP.subList(1, biggerP.size())))) || (smallerT.equals(reversed_tmp(removeLast(biggerT))) && smallerP.equals(reversed_pair(biggerP.subList(0, biggerP.size()-1))));	
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
	
	static List<Integer> reversed_tmp(List<Integer> template){
		List<Integer> t = new LinkedList<Integer>(template);
		Collections.reverse(t);
		return t;
	}
	
	static List<Dir> reversed_pair(List<Dir> template){
		List<Dir> t = new LinkedList<Dir>(template);
		Collections.reverse(t);
		return t;
	}
	
	public static boolean disjoint(Collection<Sample> set1, Collection<Sample> set2, SampleDistance df, double threshold){
		double distance;
		double thres, maxDist;
		maxDist = (2*Double.valueOf(clusterMetricParameters)) + 1;
		for(Sample s1 : set1){
			for(Sample s2 : set2){
				thres = findThreshold(s1)+findThreshold(s2);
				thres=((thres/(maxDist*thres))*threshold);
				distance = df.distance(s1, s2);
				if(distance <= thres)
					return false;
			}
		}
		return true;
	}
	
	public static int findThreshold(Sample s){
		int strand1, strand2, lgth=0;
		for(int i=0;i<s.structure.length-1;i++){
			strand1 = s.structure[i][1] - s.structure[i][0] +1;
			strand2 = s.structure[i+1][1] - s.structure[i+1][0] +1;
			lgth += Math.min(strand1, strand2);
		}
		return lgth;
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
	
	public static void writePathwaysStatistics(String path, String line){
		BufferedReader br = null;
		File file = new File(path);
		// if file doesnt exists, then create it
		
		try {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(line+"\n");	
		bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void read_csv(String FilePath, String target){
		BufferedReader br = null;
		
		String sCurrentLine;
		try {
			br = new BufferedReader(new FileReader(FilePath));
			while ((sCurrentLine = br.readLine()) != null) {
					String[] csv_values = sCurrentLine.split(",");
					if(csv_values[0].equals(target)){
						size = Integer.valueOf(csv_values[4]);
						sequence = csv_values[26];
					}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
}
