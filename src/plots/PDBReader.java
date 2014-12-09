package plots;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import util.Tuple;
import util.Util;
import util.Vector;

public class PDBReader {
	public static Tuple<Collection<Residue>,List<Integer>> readPDB(String pdbFile, char chain) throws IOException {
		File pdb = new File(pdbFile + ".pdb");
		Map<String,Residue> residues = new HashMap<String,Residue>();
		List<Integer> strands = new LinkedList<Integer>();
		FileInputStream fs = null;
		fs = new FileInputStream(pdb);
		Scanner read = new Scanner(fs);
		int minRes = Integer.MAX_VALUE;
		while(read.hasNext()){
			String fields = read.nextLine();
			String type = fields.length() > 5 ? fields.substring(0, 6).trim() : fields;	
			if(type.equals("TER") && residues.size()>0)
				break;
			if(!(type.equals("ATOM") || type.equals("HETATM") || type.equals("SHEET"))){
				continue;
			}
			if(type.equals("SHEET")){
				if(fields.substring(20, 22).trim().equals(fields.substring(31, 33).trim()) && fields.substring(20, 22).trim().equals(String.valueOf(chain))){
					for(int i = new Integer(fields.substring(22, 26).trim()); i <= new Integer(fields.substring(33, 37).trim()); i++){
						strands.add(i);
					}
				}
			}
			else{
				int serial = Integer.parseInt(fields.substring(6, 11).trim());
				String atomName = fields.substring(12, 16).trim();
				char altLoc = fields.charAt(16);
				String resName = fields.substring(17,20).trim();
				char chainID = fields.charAt(21);
				int resNum = Integer.parseInt(fields.substring(22,26).trim());
				double x = Double.parseDouble(fields.substring(30,38).trim());
				double y = Double.parseDouble(fields.substring(38,46).trim());
				double z = Double.parseDouble(fields.substring(46,54).trim());
				if( chainID != chain || resName.equals("HOH")){
					continue;
				}
				minRes = Math.min(minRes,resNum);
				if(residues.get(resNum+""+chainID) == null){
					residues.put(new String(resNum+""+chainID),new Residue(resName,resNum,chainID));
				}		
				residues.get(new String(resNum+""+chainID)).addAtom(new Atom(serial,atomName,x,y,z));
			}
		}
		fs.close();
		read.close();
		
		List<Integer> s2 = new ArrayList<Integer>(strands.size());
		for(Integer i : strands){
			s2.add(i-minRes+1);
		}
		
		Collection<Residue> res = new ArrayList<Residue>(residues.size());
		for(Residue r : residues.values()){
			r.seqNum = r.seqNum - minRes;
			res.add(r);
		}

		return new Tuple<Collection<Residue>, List<Integer>>(res, s2);
	}

	public static Map<Integer, Residue> readResidues(String pdbFile, char chain) throws IOException {
		File pdb = new File(pdbFile + ".pdb");
		Map<Integer,Residue> residues = new HashMap<Integer,Residue>();
		FileInputStream fs = null;
		fs = new FileInputStream(pdb);
		Scanner read = new Scanner(fs);
		while(read.hasNext()){
			String fields = read.nextLine();
			String type = fields.length() > 5 ? fields.substring(0, 6).trim() : fields;	
			if(type.equals("TER"))
				break;
			if(!(type.equals("ATOM") || type.equals("HETATM") )){
				continue;
			}

			int serial = Integer.parseInt(fields.substring(6, 11).trim());
			String atomName = fields.substring(12, 16).trim();
			char altLoc = fields.charAt(16);
			String resName = fields.substring(17,20).trim();
			char chainID = fields.charAt(21);
			int resNum = Integer.parseInt(fields.substring(22,26).trim());
			double x = Double.parseDouble(fields.substring(30,38).trim());
			double y = Double.parseDouble(fields.substring(38,46).trim());
			double z = Double.parseDouble(fields.substring(46,54).trim());
			if( chainID != chain || resName.equals("HOH")){
				continue;
			}
			if(residues.get(resNum - 1 ) == null){
				residues.put(resNum - 1 ,new Residue(resName,resNum-1,chainID));
			}		
			residues.get(resNum - 1 ).addAtom(new Atom(serial,atomName,x,y,z));

		}
		fs.close();
		read.close();

		return residues;
	}

	public static Character chain(String pdbFile) throws IOException {
		File pdb = new File(pdbFile + ".pdb");
		FileInputStream fs = null;
		fs = new FileInputStream(pdb);
		Scanner read = new Scanner(fs);
		Character chain = null;
		while(read.hasNext()){
			String fields = read.nextLine();
			String type = fields.length() > 5 ? fields.substring(0, 6).trim() : fields;	
			if(!(type.equals("DBREF"))){
				continue;
			}
			String molecule = fields.substring(7,12).trim();
			char chainID = fields.charAt(12);
			if(pdbFile.substring(pdbFile.lastIndexOf('/')+1).equals(molecule)){
				chain = chainID;
				break;
			}
		}
		if(chain == null)
			chain = 'A';
		return chain;
	}

	public static Double[] strandAssignments(String pdbFile, Character chn) throws IOException {

		//Character chain = chain(pdbFile);
		Character chain = chn;
		
		if(chain == null)
			chain = 'A';

		Tuple<Collection<Residue>, List<Integer>> residues = readPDB(pdbFile, chain);

		Integer minRes = Integer.MAX_VALUE;
		int maxRes = Integer.MIN_VALUE;
		for(Residue r : residues.key()){
			minRes = Math.min(minRes, r.seqNum);
			maxRes = Math.max(maxRes, r.seqNum);
		}

		Double[] assignments = new Double[maxRes-minRes+1];
		for(Integer i : residues.value()){
			assignments[i-minRes-1] = 1.0;
		}

		return assignments;
	}

	public static void main(String[] args) throws IOException {
		String toDisplay = "./database/folding/2PM1";
		Double[] ass = strandAssignments(toDisplay,'A');
		//System.out.println(ass.value());
//Los dos comentarios salidos los hice en dec 1st.		
//		StrandChart.showChartWithPDB(new double[][]{{}}, ass);
		//	System.out.println(residues);
		Map<List<Integer>, Double> dmap = getPDBContactMap(toDisplay,chain(toDisplay),6.5);
//		ContactChart.showChart( threshold(dmap,1.0),Color.white, Color.black, 300, 300);
		
		/*	Map<String,Integer> contacts = new HashMap<String, Integer>();
		for(Residue r : residues.values()){
			for(int i=0; i < residues.size(); i++){
				if(i == r.seqNum)continue;
		//		System.out.println(Util.list(r.code,r.seqNum)+" "+Util.list(residues.get(i).code,i)+" "+dmap.get(Util.list(r.seqNum,i)));
				if(contacts.get(r.code+residues.get(i).code) == null){
					contacts.put(r.code+residues.get(i).code,0);
				}
				if(dmap.get(Util.list(r.seqNum,i)) < 5){
					contacts.put(r.code+residues.get(i).code, contacts.get(r.code+residues.get(i).code) + 1);
				}
			}

		}
		System.out.println(contacts);
		 */		
		 //System.out.println(residues);
	}

	public static Map<List<Integer>,Double> getPDBContactMap(String PDBFile, char chain, double minDist) throws IOException{
		Tuple<Collection<Residue>, List<Integer>> pdb = readPDB(PDBFile, chain);
		Collection<Residue> residues = pdb.key();
		List<Integer> strands = pdb.value();
		Map<List<Integer>,Double> cmap = contactMap(residues, Math.pow(minDist,2));
		
		return cmap;	
		//return addStrands(filter(cmap, strands),strands);
	}
	
	public static Map<List<Integer>,Double> getPDBContactMap2(String PDBFile, char chain, double minDist) throws IOException{
		Tuple<Collection<Residue>, List<Integer>> pdb = readPDB(PDBFile, chain);
		Collection<Residue> residues = pdb.key();
		List<Integer> strands = pdb.value();
		Map<List<Integer>,Double> cmap = contactMap(residues, Math.pow(minDist,2));
		
		//return cmap;	
		//return addStrands(filter(cmap, strands),strands);
		//I commented the previous return to do not add the strands itself
		return filter(cmap, strands);
	}
	

	public static Map<List<Integer>,Double> filter( Map<List<Integer>,Double> contactMap, List<Integer> strands){
		Map<List<Integer>,Double> filtered = new HashMap<List<Integer>, Double>();
		for(Entry<List<Integer>, Double> e : contactMap.entrySet()){
			if(!(strands.contains(e.getKey().get(0)+1) && strands.contains(e.getKey().get(1)+1))){
				filtered.put(e.getKey(),0.0);
			}
			else{
				filtered.put(e.getKey(), e.getValue());
			}
		}

		return filtered;
	}

	public static Map<List<Integer>,Double> getThresholdMap(String PDBFile, char chain, double threshold) throws IOException{
		Tuple<Collection<Residue>, List<Integer>> pdb = readPDB(PDBFile, chain);
		Collection<Residue> residues = pdb.key();
		return threshold(contactMap(residues, Math.pow(threshold,2)), 1.0);
	}

	public static Map<List<Integer>,Double> getFilteredThresholdMap(String PDBFile, char chain, double threshold) throws IOException{
		Tuple<Collection<Residue>, List<Integer>> pdb = readPDB(PDBFile, chain);
		Collection<Residue> residues = pdb.key();
		return filter(threshold(contactMap(residues, Math.pow(threshold,2)), 1.0),pdb.value());
	}
	
	public static Map<List<Integer>,Double> distMap(Collection<Residue> residues){
		Map<List<Integer>,Double> contactMap = new HashMap<List<Integer>, Double>();
		for(Residue r1 : residues){
			for(Residue r2 : residues){
				double meanDist = Math.sqrt(meanSquaredDistance(r1, r2));
				contactMap.put(Util.list(r1.seqNum, r2.seqNum), meanDist);
			}
		}

		return contactMap;
	}

	public static Map<List<Integer>,Double> contactMap(Collection<Residue> residues, double min){
		Map<List<Integer>,Double> contactMap = new HashMap<List<Integer>, Double>();
		for(Residue r1 : residues){
			for(Residue r2 : residues){
				if(r2.seqNum < r1.seqNum-2 || r1.seqNum < r2.seqNum-2){
					double meanDist = meanSquaredDistance(r1, r2);
					//			if(meanDist < 49){
					//			contactMap.put(Arrays.asList(new Integer[]{r1.seqNum, r2.seqNum}), minSquaredDistance(r1, r2));
					//			
					//		}
					//		else{
					contactMap.put(Arrays.asList(new Integer[]{r1.seqNum, r2.seqNum}), meanDist);
					//		}
					//	min = Math.min(min,meanSquaredDistance(r1, r2));
					//	System.out.println(meanSquaredDistance(r1, r2));
				}
			}
		}
		for(Entry<List<Integer>, Double> e : contactMap.entrySet()){
			if(e.getValue() < min){
				contactMap.put(e.getKey(), 1.0);
				//System.out.println(e.getKey());
			}
			//		if(e.getValue() < 81)
			//			contactMap.put(e.getKey(), 1.0);
			else
			//	contactMap.put(e.getKey(), min/e.getValue());
					contactMap.put(e.getKey(), 0.0);
		}
		return contactMap;
	}

	public static Map<List<Integer>,Double> threshold(Map<List<Integer>,Double> contactMap, double threshold){
		Map<List<Integer>,Double> thresholded = new HashMap<List<Integer>, Double>();
		for(Entry<List<Integer>,Double> e : contactMap.entrySet()){
			thresholded.put(e.getKey(), e.getValue() >= threshold ? 1.0 : 0.0 );
		}
		return thresholded;
	}

	public static Map<List<Integer>,Double> addStrands(Map<List<Integer>,Double> contactMap, List<Integer> strands){
		for(Integer i : strands){
			contactMap.put(Arrays.asList(new Integer[]{i-1, i-1}), 1.0 );
		}
		return contactMap;
	}

	public static double meanSquaredDistance(Residue one, Residue two){
		return Vector.squaredDistance(one.meanPosition(),two.meanPosition());
	}

	public static double minSquaredDistance(Residue one, Residue two){
		double minDist = Double.MAX_VALUE;
		for(Atom a :  one){
			for(Atom b : two){
				minDist = Math.min(minDist, a.squaredDistance(b));
			}
		}
		return minDist;
	}
	
	public static Map<List<Integer>,Double> read_EvFold(String pdb, int start, int base){
		BufferedReader br = null;
		Map<List<Integer>,Double> DI = new HashMap<List<Integer>,Double>(); 
		try {
 
			String sCurrentLine;
			String []splitLine;
			br = new BufferedReader(new FileReader(pdb));
			boolean first = true;
			while ((sCurrentLine = br.readLine()) != null) {
				if(!first){
					splitLine = sCurrentLine.split(",");
					DI.put(Arrays.asList(new Integer[]{Integer.valueOf(splitLine[0])-base+start, Integer.valueOf(splitLine[2])-base+start}), Double.valueOf(splitLine[5]));
				}else{
					first = false;
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

		return DI;
	}
}
