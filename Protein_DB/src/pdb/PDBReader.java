package pdb;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;


import util.Tuple;
import util.Util;
import util.Vector;

public class PDBReader {
	
	public static Hashtable<String, String> PFamPDB;
	public static Hashtable<String, String> PFamBrowser;
	public static Hashtable<String, String> PDBChain;
	public static Hashtable<String, String> PDBSequence;
	public static Hashtable<String, String> PDBFasta;
	public static Hashtable<String, String> PDBUniProt;
	public static Hashtable<String, String> UniProtSeqID;
	public static Hashtable<String, String> UniProtSeq;
	
	
	public static void main(String[] args) throws IOException {
		//Set916();
		//SetEvFold();
		StudyCase();
	}
	
	public static void StudyCase() throws IOException{
		String pathFiles = "/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/eFold/experimentation/DB/";
		String pathBetaSheetStudyCase = pathFiles + "betasheetPDBStudyCase.txt";
		String pathBetaSheetStudyCasePDB = pathFiles + "/StudyCase";
		//String pathPFamPDB = pathFiles + "pdb_pfam_mapping.txt";
		String pathPFamPDB = pathFiles + "pdbmap"; 
		String pathPFamBrowser = pathFiles + "pfam_family_browser";
		String pathAnnotation = pathFiles + "StudyCaseAnnotation.csv";
		String pathEvFoldfasta = pathFiles + "StudyCasefasta.txt";
		String pathEvFoldUniprot = pathFiles + "StudyCaseuniprot.fasta";
		String mapping, topology, PFamB, chain;
		double[] stat_strands = new double[6];//max_str, min_str, avg_str, max_gap, min_gap, avg_gap
		
		int num_aa, minRes;
		//write_BetaSheet916(pathOriginalBetaSheet916,pathBetaSheet916);
        File directory = new File(pathBetaSheetStudyCasePDB);
        
        //get all the files from a directory
        File[] fList = directory.listFiles();
        PFamPDB = new Hashtable<String,String>();
        PFamBrowser = new Hashtable<String,String>();
        PDBChain = new Hashtable<String,String>();
        PDBSequence =  new Hashtable<String,String>();
        PDBFasta =  new Hashtable<String,String>();
        PDBUniProt =  new Hashtable<String,String>();
        UniProtSeqID =  new Hashtable<String,String>();
        UniProtSeq = new Hashtable<String,String>();
        
        mappingPFamPDB_con(pathPFamPDB);
        PFamBrowser(pathPFamBrowser);
        PDBChain(pathBetaSheetStudyCase);
        PDBFasta(pathEvFoldfasta);
        UniProtSeqID(pathEvFoldUniprot);
        
        for (File file : fList){
            System.out.println("Processing .........  "+  file.getName());
            topology = "";
            Tuple<List<Integer>,List<List<Integer>>> out_readPDB = new Tuple<List<Integer>,List<List<Integer>>>(null, null);
            List<Integer> Pairing = new LinkedList<Integer>();
            List<List<Integer>> Strand = new LinkedList<List<Integer>>();
            if(PDBChain.containsKey(file.getName().replace(".pdb", ""))){
            	chain = PDBChain.get(file.getName().replace(".pdb", ""));
            }else{
            	chain= "A";
            }
            out_readPDB = readPDB(file.getAbsolutePath(), chain.charAt(0));
            Pairing = out_readPDB.key();
            Strand = out_readPDB.value();
            num_aa = Pairing.remove(Pairing.size()-1);
            minRes = Pairing.remove(Pairing.size()-1);
            filterCopies(Strand, Pairing);
            topology = getTopology(Strand.get(0),Pairing);
            stat_strands = stat_strands(Strand);
            if(PFamPDB.containsKey(file.getName().replace(".pdb", "")+chain)){
            	mapping = PFamPDB.get(file.getName().replace(".pdb", "")+chain);
            }else{
            	System.out.println("ERROR: Not in PFamPDB");
            	continue;
            }
            if(PFamBrowser.containsKey(mapping.split(";")[0])){
            	PFamB = PFamBrowser.get(mapping.split(";")[0]);
            }else{
            	System.out.println("ERROR: Not in PFamBrowser");
            	continue;
            }
            String seq916 = PDBFasta.get(file.getName().replace(".pdb", "")+chain);
            String seqFasta = PDBFasta.get(file.getName().replace(".pdb", "")+chain);
            String seqId = UniProtSeqID.get(mapping.split(";")[3]);
            String seqUniprot = UniProtSeq.get(mapping.split(";")[3]); 
            int index, size_index, index2, size_index2;
            if(seqUniprot.contains(seq916)){
            	index = seqUniprot.indexOf(seq916)+1;
            	size_index = seq916.length();
            	index2 = index;
            	size_index2 = 0;
            }else{
            	String aux = findIndex(seq916, seqUniprot);
            	index = Integer.valueOf(aux.split(";")[0])+1;
            	size_index = Integer.valueOf(aux.split(";")[1]);
            	if(seq916.length()<=seqUniprot.length()){
            		aux = findIndex2(seq916, seqUniprot);
            		index2 = Integer.valueOf(aux.split(";")[0])+1;
            		size_index2 = Integer.valueOf(aux.split(";")[1]);
            	}else{
            		index2=-5;
            		size_index2 = -5;
            	}
            }
            String resp = file.getName().replace(".pdb", "")+";"+ chain +";"+ num_aa +";"+ seqFasta.length() +";"+seq916.length() +";"+ index +";"+ size_index +";"+ index2 +";"+ size_index2 +";"+ minRes +";"+mapping.split(";")[2]+";"+mapping.split(";")[1]+";"+PFamB.split(";")[0]+";"+PFamB.split(";")[1]+";"+ mapping.split(";")[0]+";"+ mapping.split(";")[3]+";" +seqId+";" +mapping.split(";")[4]+";"+Strand.get(0).size()+";"+ stat_strands[0] +";"+ stat_strands[1] +";"+ stat_strands[2] +";"+ stat_strands[3] +";"+ stat_strands[4] +";"+ stat_strands[5] +";"+topology+";"+seq916+";"+seqFasta + ";" + seqUniprot;
            writeAnnotation(pathAnnotation,resp);
            System.out.println(resp);
        }
	}
	
	public static void SetEvFold() throws IOException{
		String pathFiles = "/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/eFold/experimentation/DB/";
		String pathBetaSheetEvFold = pathFiles + "betasheetPDBEvFold.txt";
		String pathBetaSheetEvFoldPDB = pathFiles + "/EvFold";
		//String pathPFamPDB = pathFiles + "pdb_pfam_mapping.txt";
		String pathPFamPDB = pathFiles + "pdbmap"; 
		String pathPFamBrowser = pathFiles + "pfam_family_browser";
		String pathAnnotation = pathFiles + "EvFoldAnnotation.csv";
		String pathEvFoldfasta = pathFiles + "EvFoldfasta.txt";
		String pathEvFoldUniprot = pathFiles + "EvFolduniprot.fasta";
		String mapping, topology, PFamB, chain;
		double[] stat_strands = new double[6];//max_str, min_str, avg_str, max_gap, min_gap, avg_gap
		
		int num_aa, minRes;
		//write_BetaSheet916(pathOriginalBetaSheet916,pathBetaSheet916);
        File directory = new File(pathBetaSheetEvFoldPDB);
        
        //get all the files from a directory
        File[] fList = directory.listFiles();
        PFamPDB = new Hashtable<String,String>();
        PFamBrowser = new Hashtable<String,String>();
        PDBChain = new Hashtable<String,String>();
        PDBSequence =  new Hashtable<String,String>();
        PDBFasta =  new Hashtable<String,String>();
        PDBUniProt =  new Hashtable<String,String>();
        UniProtSeqID =  new Hashtable<String,String>();
        UniProtSeq = new Hashtable<String,String>();
        
        mappingPFamPDB_con(pathPFamPDB);
        PFamBrowser(pathPFamBrowser);
        PDBChain(pathBetaSheetEvFold);
        PDBFasta(pathEvFoldfasta);
        UniProtSeqID(pathEvFoldUniprot);
        
        for (File file : fList){
            System.out.println("Processing .........  "+  file.getName());
            topology = "";
            Tuple<List<Integer>,List<List<Integer>>> out_readPDB = new Tuple<List<Integer>,List<List<Integer>>>(null, null);
            List<Integer> Pairing = new LinkedList<Integer>();
            List<List<Integer>> Strand = new LinkedList<List<Integer>>();
            if(PDBChain.containsKey(file.getName().replace(".pdb", ""))){
            	chain = PDBChain.get(file.getName().replace(".pdb", ""));
            }else{
            	chain= "A";
            }
            out_readPDB = readPDB(file.getAbsolutePath(), chain.charAt(0));
            Pairing = out_readPDB.key();
            Strand = out_readPDB.value();
            num_aa = Pairing.remove(Pairing.size()-1);
            minRes = Pairing.remove(Pairing.size()-1);
            filterCopies(Strand, Pairing);
            topology = getTopology(Strand.get(0),Pairing);
            stat_strands = stat_strands(Strand);
            if(PFamPDB.containsKey(file.getName().replace(".pdb", "")+chain)){
            	mapping = PFamPDB.get(file.getName().replace(".pdb", "")+chain);
            }else{
            	System.out.println("ERROR: Not in PFamPDB");
            	continue;
            }
            if(PFamBrowser.containsKey(mapping.split(";")[0])){
            	PFamB = PFamBrowser.get(mapping.split(";")[0]);
            }else{
            	System.out.println("ERROR: Not in PFamBrowser");
            	continue;
            }
            String seq916 = PDBFasta.get(file.getName().replace(".pdb", "")+chain);
            String seqFasta = PDBFasta.get(file.getName().replace(".pdb", "")+chain);
            String seqId = UniProtSeqID.get(mapping.split(";")[3]);
            String seqUniprot = UniProtSeq.get(mapping.split(";")[3]); 
            int index, size_index, index2, size_index2;
            if(seqUniprot.contains(seq916)){
            	index = seqUniprot.indexOf(seq916)+1;
            	size_index = seq916.length();
            	index2 = index;
            	size_index2 = 0;
            }else{
            	String aux = findIndex(seq916, seqUniprot);
            	index = Integer.valueOf(aux.split(";")[0])+1;
            	size_index = Integer.valueOf(aux.split(";")[1]);
            	if(seq916.length()<=seqUniprot.length()){
            		aux = findIndex2(seq916, seqUniprot);
            		index2 = Integer.valueOf(aux.split(";")[0])+1;
            		size_index2 = Integer.valueOf(aux.split(";")[1]);
            	}else{
            		index2=-5;
            		size_index2 = -5;
            	}
            }
            String resp = file.getName().replace(".pdb", "")+";"+ chain +";"+ num_aa +";"+ seqFasta.length() +";"+seq916.length() +";"+ index +";"+ size_index +";"+ index2 +";"+ size_index2 +";"+ minRes +";"+mapping.split(";")[2]+";"+mapping.split(";")[1]+";"+PFamB.split(";")[0]+";"+PFamB.split(";")[1]+";"+ mapping.split(";")[0]+";"+ mapping.split(";")[3]+";" +seqId+";" +mapping.split(";")[4]+";"+Strand.get(0).size()+";"+ stat_strands[0] +";"+ stat_strands[1] +";"+ stat_strands[2] +";"+ stat_strands[3] +";"+ stat_strands[4] +";"+ stat_strands[5] +";"+topology+";"+seq916+";"+seqFasta + ";" + seqUniprot;
            writeAnnotation(pathAnnotation,resp);
            System.out.println(resp);
        }
        
	}
	
	public static void Set916() throws IOException{
		String pathFiles = "/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/eFold/experimentation/DB/";
		String pathBetaSheet916 = pathFiles + "betasheetPDB916.txt";
		String pathOriginalBetaSheet916 = pathFiles + "betasheet.dat.txt";
		String pathBetaSheet916PDB = pathFiles + "/916";
		//String pathPFamPDB = pathFiles + "pdb_pfam_mapping.txt";
		String pathPFamPDB = pathFiles + "pdbmap"; 
		String pathPFamBrowser = pathFiles + "pfam_family_browser";
		String pathAnnotation = pathFiles + "916Annotation.csv";
		String path916fasta = pathFiles + "916fasta.txt";
		String path916Uniprot = pathFiles + "916uniprot.fasta";
		String mapping, topology, PFamB, chain;
		double[] stat_strands = new double[6];//max_str, min_str, avg_str, max_gap, min_gap, avg_gap

		int num_aa, minRes;
		//write_BetaSheet916(pathOriginalBetaSheet916,pathBetaSheet916);
        File directory = new File(pathBetaSheet916PDB);
        
        //get all the files from a directory
        File[] fList = directory.listFiles();
        PFamPDB = new Hashtable<String,String>();
        PFamBrowser = new Hashtable<String,String>();
        PDBChain = new Hashtable<String,String>();
        PDBSequence =  new Hashtable<String,String>();
        PDBFasta =  new Hashtable<String,String>();
        PDBUniProt =  new Hashtable<String,String>();
        UniProtSeqID =  new Hashtable<String,String>();
        UniProtSeq = new Hashtable<String,String>();
        
        mappingPFamPDB_con(pathPFamPDB);
        PFamBrowser(pathPFamBrowser);
        PDBChain(pathBetaSheet916);
        PDBSequence(pathOriginalBetaSheet916);
        PDBFasta(path916fasta);
        UniProtSeqID(path916Uniprot);
        for (File file : fList){
            System.out.println("Processing .........  "+  file.getName());
            topology = "";
            Tuple<List<Integer>,List<List<Integer>>> out_readPDB = new Tuple<List<Integer>,List<List<Integer>>>(null, null);
            List<Integer> Pairing = new LinkedList<Integer>();
            List<List<Integer>> Strand = new LinkedList<List<Integer>>();
            if(PDBChain.containsKey(file.getName().replace(".pdb", ""))){
            	chain = PDBChain.get(file.getName().replace(".pdb", ""));
            }else{
            	chain= "A";
            }
            out_readPDB = readPDB(file.getAbsolutePath(), chain.charAt(0));
            Pairing = out_readPDB.key();
            Strand = out_readPDB.value();
            num_aa = Pairing.remove(Pairing.size()-1);
            minRes = Pairing.remove(Pairing.size()-1);
            filterCopies(Strand, Pairing);
            topology = getTopology(Strand.get(0),Pairing);
            stat_strands = stat_strands(Strand);
            if(PFamPDB.containsKey(file.getName().replace(".pdb", "")+chain)){
            	mapping = PFamPDB.get(file.getName().replace(".pdb", "")+chain);
            }else{
            	System.out.println("ERROR: Not in PFamPDB");
            	continue;
            }
            if(PFamBrowser.containsKey(mapping.split(";")[0])){
            	PFamB = PFamBrowser.get(mapping.split(";")[0]);
            }else{
            	System.out.println("ERROR: Not in PFamBrowser");
            	continue;
            }
            String seq916 = PDBSequence.get(file.getName().replace(".pdb", ""));
            String seqFasta = PDBFasta.get(file.getName().replace(".pdb", "")+chain);
            String seqId = UniProtSeqID.get(mapping.split(";")[3]);
            String seqUniprot = UniProtSeq.get(mapping.split(";")[3]); 
            int index, size_index, index2, size_index2;
            if(seqUniprot.contains(seq916)){
            	index = seqUniprot.indexOf(seq916)+1;
            	size_index = seq916.length();
            	index2 = index;
            	size_index2 = 0;
            }else{
            	String aux = findIndex(seq916, seqUniprot);
            	index = Integer.valueOf(aux.split(";")[0])+1;
            	size_index = Integer.valueOf(aux.split(";")[1]);
            	if(seq916.length()<=seqUniprot.length()){
            		aux = findIndex2(seq916, seqUniprot);
            		index2 = Integer.valueOf(aux.split(";")[0])+1;
            		size_index2 = Integer.valueOf(aux.split(";")[1]);
            	}else{
            		index2=-5;
            		size_index2 = -5;
            	}
            }
            String resp = file.getName().replace(".pdb", "")+";"+ chain +";"+ num_aa +";"+ seqFasta.length() +";"+seq916.length() +";"+ index +";"+ size_index +";"+ index2 +";"+ size_index2 +";"+ minRes +";"+mapping.split(";")[2]+";"+mapping.split(";")[1]+";"+PFamB.split(";")[0]+";"+PFamB.split(";")[1]+";"+ mapping.split(";")[0]+";"+ mapping.split(";")[3]+";" +seqId+";" +mapping.split(";")[4]+";"+Strand.get(0).size()+";"+ stat_strands[0] +";"+ stat_strands[1] +";"+ stat_strands[2] +";"+ stat_strands[3] +";"+ stat_strands[4] +";"+ stat_strands[5] +";"+topology+";"+seq916+";"+seqFasta + ";" + seqUniprot;
            writeAnnotation(pathAnnotation,resp);
            System.out.println(resp);
        }
	}
	
	public static Tuple<List<Integer>,List<List<Integer>>> readPDB(String pdbFile, char chain) throws IOException {
		File pdb = new File(pdbFile);
		Map<String,Residue> residues = new HashMap<String,Residue>();
		List<Integer> strands_start = new LinkedList<Integer>();
		List<Integer> strands_end = new LinkedList<Integer>();
		List<Integer> pairing = new LinkedList<Integer>();
		int cont_length=0;
		FileInputStream fs = null;
		fs = new FileInputStream(pdb);
		Scanner read = new Scanner(fs);
		int minRes = Integer.MAX_VALUE;
		while(read.hasNext()){
			String fields = read.nextLine();
			String type = fields.length() > 5 ? fields.substring(0, 6).trim() : fields;	
			if(type.equals("TER") && residues.size()>0){
				break;
			}
			if(!(type.equals("ATOM") || type.equals("HETATM") || type.equals("SHEET"))){
				continue;
			}
			if(type.equals("SHEET")){
				//for(int i = new Integer(fields.substring(22, 26).trim()); i <= new Integer(fields.substring(33, 37).trim()); i++){
				//	strands.add(i);
				//}20,22  31,33
				if(fields.substring(20, 22).trim().equals(fields.substring(31, 33).trim()) && fields.substring(20, 22).trim().equals(String.valueOf(chain))){
					strands_start.add(new Integer(fields.substring(22, 26).trim()));
					strands_end.add(new Integer(fields.substring(33, 37).trim()));
					if(isInteger(fields.substring(38, 41).trim())){
						pairing.add(new Integer(fields.substring(38, 41).trim()));
					}else{
						pairing.add(new Integer(0));
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
		
		List<Integer> s2 = new ArrayList<Integer>(strands_start.size());
		for(Integer i : strands_start){
			s2.add(i-minRes+1);
		}
		List<Integer> s3 = new ArrayList<Integer>(strands_end.size());
		for(Integer i : strands_end){
			s3.add(i-minRes+1);
		}
		List<List<Integer>> strnd = new LinkedList<List<Integer>>();
		strnd.add(s2);
		strnd.add(s3);
		
		Collection<Residue> res = new ArrayList<Residue>(residues.size());
		for(Residue r : residues.values()){
			r.seqNum = r.seqNum - minRes;
			res.add(r);
		}
		
		pairing.add(minRes);
		pairing.add(residues.size());
		return new Tuple<List<Integer>, List<List<Integer>>>(pairing, strnd);
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
	
	public static String getTopology(List<Integer> Strand,List<Integer> Pairing){
		int value1, value2, pos;
		String pair="", topology="";
        for(int i=0; i<Strand.size();i++){
        	value1 = Strand.get(i);
        	pos = 0;
        	for(int j=0; j<Strand.size(); j++){
        		value2 = Strand.get(j);
        		if(value2<value1){
        			pos++; 
        		}
        	}
        	pos = pos +1;
        	pair ="";
        	if(i!=0){
        		if(Pairing.get(i)==0){
        			pair="N";
        		}
        		if(Pairing.get(i)==1){
        			pair="P";
        		}
        		if(Pairing.get(i)==-1){
        			pair="A";
        		}
        	}
        	topology+=pair+pos;
        }
        return topology;
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
	
	public static void mappingPFamPDB (String pathMapping){
		BufferedReader br = null;
		String sCurrentLine;
		String []splitLine;
		String concat_key ="", concat_value ="";
		int range1, range2;
		try {
			br = new BufferedReader(new FileReader(pathMapping));
			while ((sCurrentLine = br.readLine()) != null) {
				if(!sCurrentLine.contains("PDB_ID")){
					splitLine = sCurrentLine.split("\t");
					concat_key = splitLine[0]+splitLine[1];
					if(isInteger(splitLine[2]) && isInteger(splitLine[3])){
						if(!PFamPDB.containsKey(concat_key)){
							concat_value = splitLine[4].substring(0, splitLine[4].indexOf("."))+";"+splitLine[2]+";"+splitLine[3];
							PFamPDB.put(concat_key, concat_value);
						}else{
							range1 = Integer.valueOf(PFamPDB.get(concat_key).split(";")[2]) - Integer.valueOf(PFamPDB.get(concat_key).split(";")[1]);
							range2 = Integer.valueOf(splitLine[3]) - Integer.valueOf(splitLine[2]);
							if(range2>range1){
								concat_value = splitLine[4].substring(0, splitLine[4].indexOf("."))+";"+splitLine[2]+";"+splitLine[3];
								PFamPDB.put(concat_key, concat_value);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void mappingPFamPDB_con (String pathMapping){
		BufferedReader br = null;
		String sCurrentLine;
		String []splitLine;
		String concat_key ="", concat_value ="";
		int range1, range2;
		try {
			br = new BufferedReader(new FileReader(pathMapping));
			while ((sCurrentLine = br.readLine()) != null) {
				splitLine = sCurrentLine.split(";");
				concat_key = splitLine[0].trim()+splitLine[1].trim();
				if(isInteger(splitLine[2].split("-")[0].trim()) && isInteger(splitLine[2].split("-")[1].trim())){
					if(!PFamPDB.containsKey(concat_key)){
						concat_value = splitLine[4].trim()+";"+splitLine[2].split("-")[1].trim()+";"+splitLine[2].split("-")[0].trim()+";"+splitLine[5].trim()+";"+splitLine[6].trim();
						PFamPDB.put(concat_key, concat_value);
					}else{
						range1 = Integer.valueOf(PFamPDB.get(concat_key).split(";")[2].trim()) - Integer.valueOf(PFamPDB.get(concat_key).split(";")[1].trim());
						range2 = Integer.valueOf(splitLine[2].split("-")[1].trim()) - Integer.valueOf(splitLine[2].split("-")[0].trim());
						if(range2>range1){
							concat_value = splitLine[4].trim()+";"+splitLine[2].split("-")[1].trim()+";"+splitLine[2].split("-")[0].trim()+";"+splitLine[5].trim()+";"+splitLine[6].trim();
							PFamPDB.put(concat_key, concat_value);
						}
					}
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void UniProtSeqID(String pathMapping){
		BufferedReader br = null;
		String sCurrentLine;
		String []splitLine;
		String concat_key ="", concat_value ="";
		try {
			br = new BufferedReader(new FileReader(pathMapping));
			concat_value="";
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.charAt(0)=='>'){
					if(concat_value!="" && !UniProtSeq.containsKey(concat_key)){
						UniProtSeq.put(concat_key, concat_value);
					}
					splitLine = sCurrentLine.split("\\|");
					concat_key = splitLine[1].trim();
					concat_value = splitLine[2].split(" ")[0].trim();
						if(!UniProtSeqID.containsKey(concat_key)){
							UniProtSeqID.put(concat_key, concat_value);
						}
					concat_value = "";	
				}else{
					concat_value += sCurrentLine;
				}
			}
			if(concat_value!="" && !UniProtSeq.containsKey(concat_key)){
				UniProtSeq.put(concat_key, concat_value);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void PFamBrowser (String pathMapping){
		BufferedReader br = null;
		String sCurrentLine;
		String []splitLine;
		String concat_key ="", concat_value ="";
		try {
			br = new BufferedReader(new FileReader(pathMapping));
			while ((sCurrentLine = br.readLine()) != null) {
				splitLine = sCurrentLine.split("\t");
				concat_key = splitLine[1];
				if(!PFamBrowser.containsKey(concat_key)){
					concat_value = splitLine[3]+";"+splitLine[4];
					PFamBrowser.put(concat_key, concat_value);
				}else{
					System.out.println("WEIRD = Duplicate PFam family .. "+ concat_key);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	
	public static void write_BetaSheet916(String pdb_read, String pdb_write){
		BufferedReader br = null;
		int cont = 0;
		File file = new File(pdb_write);
		// if file doesnt exists, then create it
		
		try {
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		
			String sCurrentLine;
			String []splitLine;
			br = new BufferedReader(new FileReader(pdb_read));
			while ((sCurrentLine = br.readLine()) != null) {
				if(!sCurrentLine.contains("#") && sCurrentLine.length()==5){
					bw.write(sCurrentLine+"\n");
					cont ++;
				}
			}
			System.out.println(cont);
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
	
	public static void PDBSequence(String pdb_read){
		BufferedReader br = null;
		int cont = 0;
			String PDB = null, Sequence;
			String sCurrentLine;
			try {
				br = new BufferedReader(new FileReader(pdb_read));
				while ((sCurrentLine = br.readLine()) != null) {
					if(!sCurrentLine.contains("#") && sCurrentLine.length()==5){
						cont = 0;
						PDB = sCurrentLine.substring(0,sCurrentLine.length()-1);
					}else{
						cont ++;
					}
					if(cont==2 && PDB != null){
						Sequence = sCurrentLine.replace("\t", "");
						PDBSequence.put(PDB, Sequence);
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
	
	public static void PDBFasta(String pdb_read){
		BufferedReader br = null;
		String PDB = null, Sequence="";
		String sCurrentLine;
		try {
			br = new BufferedReader(new FileReader(pdb_read));
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.contains(">")){
					PDB = sCurrentLine.substring(1,7).replace(":", "");
					Sequence = "";
				}else{
					Sequence += sCurrentLine;
					PDBFasta.put(PDB, Sequence);
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
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	public static void filterCopies(List<List<Integer>> strands, List<Integer> pairing){
		int index;
		for(int i=0; i<strands.get(0).size();i++){
			while(strands.get(0).subList(i+1, strands.get(0).size()).contains(strands.get(0).get(i))){
				index = strands.get(0).lastIndexOf(strands.get(0).get(i));
				strands.get(0).remove(index);
				strands.get(1).remove(index);
				pairing.remove(index);
			}
		}
	}
	
	public static double[] stat_strands(List<List<Integer>> strnd){
	double min_str= Double.MAX_VALUE, max_str = Double.MIN_VALUE, prom_str = 0d;
	double min_gap= Double.MAX_VALUE, max_gap = Double.MIN_VALUE, prom_gap = 0d;
	double min_gap_closer = Double.MAX_VALUE;
	int cont=0;
	double[] stat = new double[6];
	double val = 0d;
		for(int i=0; i<strnd.get(0).size();i++){
			val = strnd.get(1).get(i)-strnd.get(0).get(i);
			cont++;
			if(val<min_str){
				min_str = val;
			}
			if(val>max_str){
				max_str = val;
			}
			prom_str += (val+1);
			min_gap_closer = Double.MAX_VALUE;
			for(int j=0; j<strnd.get(1).size();j++){
				if(i!=j){
					val = strnd.get(0).get(i)-strnd.get(1).get(j);
					if(val>0){
						if(val<min_gap_closer){
							min_gap_closer = val;
						}
					}	
				}	
			}
			if(min_gap_closer != Double.MAX_VALUE){
				prom_gap += (min_gap_closer-1);
				if(min_gap_closer>max_gap){
					max_gap = min_gap_closer;
				}
				if(min_gap_closer<min_gap){
					min_gap = min_gap_closer;
				}
			}
		}	
		prom_str = prom_str/cont;
		prom_gap = prom_gap/(cont-1);
		stat[0] = max_str+1;
		stat[1] = min_str+1;
		stat[2] = prom_str;
		stat[3] = max_gap-1;
		stat[4] = min_gap-1;
		stat[5] = prom_gap;
		return stat;
	}
	public static String findIndex(String seq916, String seqUniprot){
		String seq916_start = seq916;
		String seq916_end = seq916;
		int index_start=0, index_end=0, cont_start=0, cont_end=0;
		while(!seqUniprot.contains(seq916_start) && seq916_start.length()>0){
			seq916_start = seq916_start.substring(1);
			cont_start++;
		}
		index_start = seqUniprot.indexOf(seq916_start) - cont_start;
		while(!seqUniprot.contains(seq916_end) && seq916_end.length()>0){
			seq916_end = seq916_end.substring(0,seq916_end.length()-1);
			cont_end++;
		}
		index_end = seqUniprot.indexOf(seq916_end);
		if(seq916_start.length()>=seq916_end.length()){
			return index_start + ";" + seq916_start.length();
		}else{
			return index_end + ";" + seq916_end.length();
		}
	}
	
	public static String findIndex2(String seq916, String seqUniprot){
		String seq916_start = seq916;
		String seq916_end = seq916;
		int index_start=0, index_end=0, cont_start=0, cont_start_total=0, cont_end=0, cont_end_total=0;
		
		while(!seqUniprot.contains(seq916_start)){
			cont_start=0;
			while(!seqUniprot.contains(seq916_start)){
				seq916_start = seq916_start.substring(1);
				cont_start++;
			}
			if(seqUniprot.indexOf(seq916_start)-cont_start<0 || seq916_start.length()<=1){
				cont_start_total = seq916.length();
				break;
			}
			cont_start_total++;
			index_start = seqUniprot.indexOf(seq916_start);
			seq916_start = String.valueOf(seqUniprot.charAt(index_start-1)) + seq916_start;   
			seq916_start = seq916.substring(0,cont_start-1)+seq916_start;
		}
		index_start = seqUniprot.indexOf(seq916_start);
		
		
		while(!seqUniprot.contains(seq916_end)){
			cont_end=0;
			while(!seqUniprot.contains(seq916_end)){
				seq916_end = seq916_end.substring(0,seq916_end.length()-1);
				cont_end++;
			}
			cont_end_total++;
			if(seqUniprot.indexOf(seq916_end)+seq916.length()>seqUniprot.length() || seq916_end.length()<=1){
				cont_end_total = seq916.length();
				break;
			}
			index_end = seqUniprot.indexOf(seq916_end) + seq916_end.length();
			seq916_end = seq916_end + String.valueOf(seqUniprot.charAt(index_end));   
			seq916_end = seq916_end + seq916.substring(seq916_end.length());
		}
		index_end = seqUniprot.indexOf(seq916_end);
		
		if(cont_start_total<cont_end_total){
			if(seq916_start.length()==seq916.length()){
				return index_start + ";" + cont_start_total;
			}else{
				return "-1;-1";
			}			
		}else{
			if(seq916_end.length()==seq916.length()){
				return index_end + ";" + cont_end_total;
			}else{
				return "-1;-1";
			}
		}
	}
	
	public static Map<List<Integer>,Double> getPDBContactMap(String PDBFile, char chain, double minDist) throws IOException{
		Tuple<Collection<Residue>, List<Integer>> pdb = readPDB_previous(PDBFile, chain);
		Collection<Residue> residues = pdb.key();
		List<Integer> strands = pdb.value();
		Map<List<Integer>,Double> cmap = contactMap(residues, Math.pow(minDist,2));
		
		return cmap;	
		//return addStrands(filter(cmap, strands),strands);
	}
	
	public static Map<List<Integer>,Double> getPDBContactMap2(String PDBFile, char chain, double minDist) throws IOException{
		Tuple<Collection<Residue>, List<Integer>> pdb = readPDB_previous(PDBFile, chain);
		Collection<Residue> residues = pdb.key();
		List<Integer> strands = pdb.value();
		Map<List<Integer>,Double> cmap = contactMap(residues, Math.pow(minDist,2));
		
		//return cmap;	
		//return addStrands(filter(cmap, strands),strands);
		//I commented the previous return to do not add the strands itself
		return filter(cmap, strands);
	}
	
	public static Tuple<Collection<Residue>,List<Integer>> readPDB_previous(String pdbFile, char chain) throws IOException {
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
}
