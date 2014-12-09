package pdb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Ranking {
	public static Hashtable<String,String> topologies; 
	public static Hashtable<String,Double> clusters_filter;
	public static int strand;

	public static void main(String[] args) {
		topologies = new Hashtable<String, String>();
		String path = "/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/eFold/experimentation/Files/StudyCase/Sets0/";
		strand = 5;
		read_csv(path+"Sets0"); 
		read_files(path);
		
	}
	
	public static void read_files(String path){
		String pathFiles = path;
		String NameFiles = pathFiles + "NameFiles";
		String writeFile = pathFiles + "Ranking.txt";
		String nameFile;
		File directoryPdb;
		String FolderPdb;
		List<String> Names = new LinkedList<String>();
		String toWrite;
		String topology;
		Names = Read_NameFiles(NameFiles);
		double total;
		
		switch(strand){
			case 2: 
				total = 2;
				break;
			case 3: 
				total = 12;
				break;
			case 4:
				total = 144;
				break;
			case 5:
				total = 2160;
				break;
			default:
				total = 0;
				break;
		}		
		
	
		for(int i=0; i<Names.size(); i++){
			
			double Best_result;
			double Average_result, Average_result_all, cont_average, cont_average_all;
			double []Average_alpha_results=new double[11];
			double Average_alpha_results_aux;
			double []cont_average_alpha=new double[11];
			int alpha;
			double proportion_best=0d;
			
			Best_result = Integer.MAX_VALUE;
			Average_alpha_results_aux = Integer.MAX_VALUE;
			Average_result = 0;
			cont_average = 0;
			cont_average_all = 0;
			FolderPdb = pathFiles+Names.get(i);
	        directoryPdb = new File(FolderPdb);
	        File[] fileList = directoryPdb.listFiles();
	        int[] ranking = new int[2];
			for (File file : fileList){
				nameFile = file.getName();
				if(nameFile.contains("clusters_"+Names.get(i))){
					read_cluster(FolderPdb+"/"+nameFile);
					topology = topologies.get(Names.get(i));
					ranking = Ranking_cluster(topology);
					if(ranking[0]==-1){
						System.out.println("Not Found File "+nameFile);
						continue;
					}
					if(ranking[0]<Best_result){
						Best_result = ranking[0];
						proportion_best = Best_result/ranking[1];
					}
					Average_result += ranking[0];
					cont_average ++;
					cont_average_all += ranking[1]; 
					alpha = (int) (Double.valueOf(nameFile.split("_")[3].substring(0,3)) * 10);
					Average_alpha_results[alpha]+=ranking[0];
					cont_average_alpha[alpha] +=1;
				}	
			}
			//Average with respect to the total number of clusters
			Average_result_all = Average_result/cont_average_all;
			//Average with respect to the total number of runs
			Average_result = Average_result/cont_average;
			for(int j=0; j<11; j++){
				if(cont_average_alpha[j]>0){
					Average_alpha_results[j] = Average_alpha_results[j]/cont_average_alpha[j];
					if(Average_alpha_results[j]<Average_alpha_results_aux){
						Average_alpha_results_aux = Average_alpha_results[j];
					}
				}
			}	
			
			
			
			toWrite = Names.get(i)+ "\t";
			toWrite += Best_result + "\t" + proportion_best + "\t" + Average_result + "\t" + Average_result_all +"\t" + Average_alpha_results_aux +"\t" + (Best_result/total); 
			writeAnnotation(writeFile,toWrite);
		}
	}
	
	public static void read_csv(String FilePath){
		BufferedReader br = null;
		
		String sCurrentLine;
		try {
			br = new BufferedReader(new FileReader(FilePath));
			while ((sCurrentLine = br.readLine()) != null) {
					String[] csv_values = sCurrentLine.split(",");
					topologies.put(csv_values[0], csv_values[25]+"#"+ new StringBuilder(csv_values[25]).reverse().toString());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void read_cluster(String FilePath){
		BufferedReader br = null;
		String topology;
		double value;
		String sCurrentLine;
		clusters_filter = new Hashtable<String,Double>();
		try {
			br = new BufferedReader(new FileReader(FilePath));
			while ((sCurrentLine = br.readLine()) != null) {
					if(sCurrentLine.contains(";")){
						continue;
					}
					String[] csv_values = sCurrentLine.split(" ");
					topology = csv_values[1];
					value = Double.valueOf(csv_values[2]);
					if(!clusters_filter.contains(topology)){
						clusters_filter.put(topology, value);
					}else{
						value += clusters_filter.get(topology);
						clusters_filter.put(topology, value);
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
	
	public static int[] Ranking_cluster(String topo){
		Double target_value;
		int comparison=0, none;
		String[] topology = topo.split("#");
		Iterator it = clusters_filter.entrySet().iterator();
		int []counters = new int[2];
		if(!(clusters_filter.containsKey(topology[0])||clusters_filter.containsKey(topology[1]))){
			counters[0] = -1;
			counters[1] = -1;
			return counters;
		}
		if(clusters_filter.containsKey(topology[0])){
			target_value = clusters_filter.get(topology[0]);
		}else{
			target_value = clusters_filter.get(topology[1]);
		}

		counters[0] = 0;
		counters[1] = 0;
		comparison = topology[0].length();
		comparison = comparison - (DetectNones(topology[0])*2);
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			String key = (String) pairs.getKey();
			Double value = (Double) pairs.getValue();
			none = DetectNones(key)*2;
			if((key.length() - none) == comparison){
				counters[1]++;
				if(value>target_value){
					counters[0]++;
				}
			}
		}
		return counters;
	}
	
	public static int DetectNones(String topology){
		int none = 0;
		for(int i=0;i<topology.length();i++){
			if(topology.charAt(i)=='N'){
				none++;
			}
		}
		return none;
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
	
	public static void writeAnnotation(String pdb_write, String Line){
		BufferedReader br = null;
		File file = new File(pdb_write);
		// if file doesnt exists, then create it
		
		try {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(Line+"\n");	
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

}
