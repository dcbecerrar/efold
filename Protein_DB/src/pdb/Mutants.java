package pdb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;



public class Mutants {
	public static String seq916;
	public static String seqFasta;
	public static String mutant;
	public static List<String> sequences = new LinkedList<String>();
	public static List<String> ids = new LinkedList<String>();
	public static double [][]identities;
	
	public static void main(String[] args) {
		
		String path = "/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/eFold/experimentation/Files/StudyCase/Sets0/";
		read_csv(path); 
		
	}
	
	public static void read_csv(String FilePath){
		BufferedReader br = null;
		String sCurrentLine;
		try {
			br = new BufferedReader(new FileReader(FilePath+"Sets0_mutant"));
			while ((sCurrentLine = br.readLine()) != null) {
					String[] csv_values = sCurrentLine.split(",");
					seq916 = csv_values[26];
					seqFasta = csv_values[27];
					mutant = csv_values[0];
					//read_write_contacts(FilePath+"Contacts/1EM7",FilePath+"Contacts/"+mutant);
					ids.add(mutant);
					sequences.add(seq916);
			}
			identities = new double[ids.size()][ids.size()];
			identity_matrix();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void identity_matrix(){
        for(int i=0;i<ids.size();i++){
        	for(int j=i; j<sequences.size();j++){
        		identities[i][j] = computeIdentity(sequences.get(i),sequences.get(j));
        	}
        }
        printIdentity();
	}
	
	public static void printIdentity(){
		for(int i=0;i<ids.size();i++){
			System.out.print("\t"+ ids.get(i));
		}
		System.out.println();
        for(int i=0;i<identities.length;i++){
        	System.out.print(ids.get(i));
        	for(int j=i; j<identities[0].length;j++){
        		System.out.print("\t"+ identities[i][j]);
        	}
        	System.out.println();
        }
        
	}
	
	public static double computeIdentity(String S1, String S2){
		double count = 0;
		for(int i=0;i<S1.length();i++){
			if(S1.charAt(i)==S2.charAt(i)){
				count++;
			}
		}
		return count/S1.length();
	}
	
	public static void read_write_contacts(String FilePath, String FileWrite){
		BufferedReader br = null;
		File file = new File(FileWrite);
		// if file doesnt exists, then create it
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			String sCurrentLine;
			br = new BufferedReader(new FileReader(FilePath));
			while ((sCurrentLine = br.readLine()) != null) {
				String[] evfold_values = sCurrentLine.split(";");
				int val1 = Integer.valueOf(evfold_values[0]);
				int val2 = Integer.valueOf(evfold_values[1]);
				if(seq916.charAt(val1-1)==seqFasta.charAt(val1-1) && seq916.charAt(val2-1)==seqFasta.charAt(val2-1)){
					bw.write(sCurrentLine+"\n");	
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
