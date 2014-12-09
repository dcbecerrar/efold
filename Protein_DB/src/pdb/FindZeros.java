package pdb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import pdb.PDBReader;
import util.Util;

public class FindZeros {
	public static Hashtable<String, String> PDBChain;
	
	public static void main(String[] args) throws IOException {
		PDBChain = new Hashtable<String,String>();
		String pathFiles = "/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/eFold/experimentation/DB/";
		String path = "/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/eFold/experimentation/Files/916/Sets5/";
		String pathPDB = pathFiles + "916/";
		String pathBetaSheetStudyCase = pathFiles + "betasheetPDBStudyCase.txt";
		String pathBetaSheetEvFold = pathFiles + "betasheetPDBEvFold.txt";
		String pathBetaSheet916 = pathFiles + "betasheetPDB916.txt";
		PDBChain(pathBetaSheetStudyCase);
		PDBChain(pathBetaSheetEvFold);
		PDBChain(pathBetaSheet916);
		Zeros(path, pathPDB);
	}
	
	public static void Zeros(String path, String pathPDB) throws IOException{
		String pathFiles = path;
		String NameFiles = pathFiles + "NameFiles";
		String writeFile = pathFiles + "Zeros.txt";
		List<String> Names = new LinkedList<String>();
		String pdb;
		char chain;

		String toWrite;
		Names = Read_NameFiles(NameFiles);
		for(int i=0; i<Names.size(); i++){
			pdb = pathPDB+Names.get(i);
			chain = PDBChain.get(Names.get(i)).charAt(0);
			Map<List<Integer>,Double> pdbMap = PDBReader.getPDBContactMap2(pdb,chain, 8.0);	
			Map<List<Integer>,Double> pdbThresh = PDBReader.getPDBContactMap(pdb, chain, 8.0);
			toWrite = Names.get(i)+ ":"+ "\t";
			toWrite += "ONE:\t";
			toWrite+= writeStatistics(statistics(pdbMap));
			toWrite += "TWO:\t";
			toWrite+= writeStatistics(statistics(pdbThresh));
			writeAnnotation(writeFile,toWrite);
		}	

	}
	
	public static String writeStatistics(boolean[] values){
	String toWrite = "";	
		for(int i=0;i<values.length;i++){
			if(values[i]){
				toWrite += CaseValues(i) +"\t";
			}
		}
		return toWrite;
	}
	
	public static String CaseValues(int index){
		String option;
		switch(index){
		case 0:
			option = "a0";
			break;
		case 1:
			option = "a12";
			break;
		case 2:
			option = "a24";
			break;
		case 3:
			option = "c0";
			break;
		case 4:
			option = "c12";
			break;
		case 5:
			option = "c24";
			break;
		case 6:
			option = "f0";
			break;
		case 7:
			option = "f12";
			break;
		case 8:
			option = "f24";
			break;
		default: 
			option = "error";
			break;
		}
		return option;
	}
	
	public static boolean[] statistics(Map<List<Integer>,Double> pdbThresh){
		boolean []statistics = new boolean[9];
		statistics[0] = accuracy(pdbThresh);
		statistics[1] = accuracy(pdbThresh,0, 12);
		statistics[2] = accuracy(pdbThresh,0, 24);
		statistics[3] = coverage(pdbThresh, 0);
		statistics[4] = coverage(pdbThresh, 0, 12);
		statistics[5] = coverage(pdbThresh, 0, 24);
		statistics[6] = fMeasure(pdbThresh, 0);
		statistics[7] = fMeasure(pdbThresh, 0, 12);
		statistics[8] = fMeasure(pdbThresh, 0, 24);
		return statistics;
	}
	
	public static boolean accuracy(Map<List<Integer>,Double> contactMap){
		if (total(contactMap)>0){
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean accuracy(Map<List<Integer>,Double> contactMap, int bound, int distance){
		Map<List<Integer>,Double> oMap = filter(contactMap,distance);
		if (total(oMap)>0){
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean coverage(Map<List<Integer>,Double> contactMap, int bound){
		Map<List<Integer>,Double> bMap = boundedMap(contactMap, bound);
		if (total(bMap)>0){
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean coverage(Map<List<Integer>,Double> contactMap, int bound, int distance){
		Map<List<Integer>,Double> bMap = boundedMap(filter(contactMap,distance), bound);
		if (total(bMap)>0){
			return false;
		}else{
			return true;
		}	
	}
	
	public static boolean fMeasure(Map<List<Integer>,Double> contactMap, int bound){
		boolean precision = accuracy(contactMap);
		boolean sensitivity = coverage(contactMap, bound);
		if(precision && sensitivity){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean fMeasure(Map<List<Integer>,Double> contactMap, int bound, int distance){
		boolean precision = accuracy(contactMap, bound, distance);
		boolean sensitivity = coverage(contactMap, bound, distance);
		if(precision && sensitivity){
			return true;
		}else{
			return false;
		}
	}
	
	public static double total(Map<List<Integer>,Double> contactMap){
		double total = 0;
		for(Entry<List<Integer>, Double> e : contactMap.entrySet()){
			total += e.getValue();			
		}
		return total;	
	}
	
	public static Map<List<Integer>,Double> filter(Map<List<Integer>,Double> map, int distance){
		Map<List<Integer>,Double> filtered = new HashMap<List<Integer>, Double>(map);
		for(Entry<List<Integer>, Double> e : filtered.entrySet()){
			if(Math.abs(e.getKey().get(0) - e.getKey().get(1)) < distance )
				filtered.put(e.getKey(), 0.0);
		}
		return filtered;
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
	
	public static void PDBChain(String pathPDBList){
		BufferedReader br = null;
		String PDB, Chain, sCurrentLine;
		try {
			br = new BufferedReader(new FileReader(pathPDBList));
			while ((sCurrentLine = br.readLine()) != null) {
				PDB = sCurrentLine.substring(0,sCurrentLine.length()-1);
				Chain = sCurrentLine.substring(sCurrentLine.length()-1, sCurrentLine.length());
				PDBChain.put(PDB, Chain);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<String> Read_NameFiles(String path){
		BufferedReader br = null;
		List<String> Names = new LinkedList<String>(); 
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(path));
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.length()==4){
					Names.add(sCurrentLine);
				}			
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
