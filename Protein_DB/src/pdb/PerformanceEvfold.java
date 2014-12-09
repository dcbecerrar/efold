package pdb;

import java.awt.Color;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import util.Util;



public class PerformanceEvfold {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String pathFiles = "/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/eFold/experimentation/Files/StudyCase/Sets0/";
		String pathSet = pathFiles + "Sets0";
		String NameFiles = pathFiles + "NameFiles";
		String contactsPath = pathFiles + "Contacts/";
		String directoryPdb = pathFiles + "Pdb/";
		String writeFile = pathFiles + "statisticsEvFold.txt";
		List<String> Names = new LinkedList<String>();
		Names = Read_NameFiles(NameFiles);
		for(int i=0; i<Names.size(); i++){
			contact_prediction(writeFile, Names.get(i), directoryPdb+Names.get(i), pathSet, contactsPath+Names.get(i));
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
	
	public static char read_csv(String FilePath, String target){
		BufferedReader br = null;
		String sCurrentLine;
		char chain='-';
		try {
			br = new BufferedReader(new FileReader(FilePath));
			while ((sCurrentLine = br.readLine()) != null) {
					String[] csv_values = sCurrentLine.split(",");
					if(csv_values[0].equals(target)){
						chain = csv_values[1].charAt(0);
						return chain;
					}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chain;
	}
	
	
	public static Map<List<Integer>,Double>  mapEvFold(String FilePath){
		BufferedReader br = null;
		String sCurrentLine;
		int x,y;
		double value;
		Map<List<Integer>,Double> contMapEvFold = new HashMap<List<Integer>,Double>(); 
		try {
			br = new BufferedReader(new FileReader(FilePath));
			while ((sCurrentLine = br.readLine()) != null) {
					String[] csv_values = sCurrentLine.split(";");
					x = Integer.valueOf(csv_values[0]);
					y = Integer.valueOf(csv_values[1]);
					value = Double.valueOf(csv_values[2]);
					contMapEvFold.put(Arrays.asList(new Integer[]{x,y}), value);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contMapEvFold;
	}
	
	public static void contact_prediction(String filePath, String target, String pdb, String csvPath, String contact){
		try {
			Character chain = read_csv(csvPath, target);
			//if( chain == null)
				//chain = 'A';
			Map<List<Integer>,Double> pdbMap = PDBReader.getPDBContactMap2(pdb,chain, 8.0);	
			Map<List<Integer>,Double> pdbThresh = PDBReader.getPDBContactMap(pdb, chain, 8.0);
			Map<List<Integer>,Double> contMapEvFold = mapEvFold(contact);
			String Line=target+"\t";
			writeTextualSummary(filePath, pdbMap, contMapEvFold, false,Line);//I changed the order pdbTresh and pdbMap
			Line="";
			writeTextualSummary(filePath, pdbThresh, contMapEvFold, true,Line);


		} catch (IOException e) {
			e.printStackTrace();
		} catch (HeadlessException e){
			e.printStackTrace();
		}


	}
	public static void writeTextualSummary(String path, Map<List<Integer>, Double> pdbThresh, Map<List<Integer>, Double> contMap, boolean two, String Line) throws IOException {

		double accuracy, accuracy_2;
		double accuracy12, accuracy12_2;
		double accuracy24, accuracy24_2;
		double coverage, coverage_2;
		double coverage12, coverage12_2;
		double coverage24, coverage24_2;
		double fMeasure, fMeasure_2;
		double fMeasure12, fMeasure12_2;
		double fMeasure24, fMeasure24_2;


		accuracy = accuracy(contMap, pdbThresh,0);
		accuracy12 = accuracy(contMap, pdbThresh,0, 12);
		accuracy24 = accuracy(contMap, pdbThresh,0, 24);
		coverage = coverage(contMap, pdbThresh, 0);
		coverage12 = coverage(contMap, pdbThresh, 0, 12);
		coverage24 = coverage(contMap, pdbThresh, 0, 24);
		fMeasure = fMeasure(contMap, pdbThresh, 0);
		fMeasure12 = fMeasure(contMap, pdbThresh, 0, 12);
		fMeasure24 = fMeasure(contMap, pdbThresh, 0, 24);
		accuracy_2 = accuracy(contMap, pdbThresh,2);
		accuracy12_2 = accuracy(contMap, pdbThresh,2,12);
		accuracy24_2 = accuracy(contMap, pdbThresh,2, 24);
		coverage_2 = coverage(contMap, pdbThresh,2);
		coverage12_2 = coverage(contMap, pdbThresh,2,12);
		coverage24_2 = coverage(contMap, pdbThresh,2,24);
		fMeasure_2 = fMeasure(contMap, pdbThresh, 2);
		fMeasure12_2 = fMeasure(contMap, pdbThresh, 2, 12);
		fMeasure24_2 = fMeasure(contMap, pdbThresh, 2, 24);
		
		

		Line+=fMeasure+"\t"+coverage+"\t"+accuracy+"\t"+fMeasure12+"\t"+coverage12+"\t"+accuracy12+"\t"+fMeasure24+"\t"+coverage24+"\t"+accuracy24+"\t";
		//Line+=fMeasure+"\t"+accuracy+"\t"+coverage+"\t"+fMeasure12+"\t"+accuracy12+"\t"+coverage12+"\t"+fMeasure24+"\t"+accuracy24+"\t"+coverage24+"\t";
		Line+=fMeasure_2+"\t"+coverage_2+"\t"+accuracy_2+"\t"+fMeasure12_2+"\t"+coverage12_2+"\t"+accuracy12_2+"\t"+fMeasure24_2+"\t"+coverage24_2+"\t"+accuracy24_2;
		//Line+=fMeasure_2+"\t"+accuracy_2+"\t"+coverage_2+"\t"+fMeasure12_2+"\t"+accuracy12_2+"\t"+coverage12_2+"\t"+fMeasure24_2+"\t"+accuracy24_2+"\t"+coverage24_2;
		if(!two){
			Line+="\t";
		}else{
			Line+="\n";
		}
		
		BufferedReader br = null;
		File file = new File(path);
		// if file doesnt exists, then create it
		
		try {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(Line);	
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
	
	public static double inside(Map<List<Integer>,Double> contactMap, Map<List<Integer>,Double> observed){
		double total = 0;
		for(Entry<List<Integer>, Double> e : observed.entrySet()){
			if(contactMap.get(e.getKey()) != null){
				total += e.getValue() * contactMap.get(e.getKey());			
			}
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
	public static double total(Map<List<Integer>,Double> contactMap){
		double total = 0;
		for(Entry<List<Integer>, Double> e : contactMap.entrySet()){
			total += e.getValue();			
		}
		return total;	
	}
}
