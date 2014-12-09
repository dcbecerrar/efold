package pdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FirstFoldEvent {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/eFold/experimentation/Files/StudyCase/Sets0/1EM7_A1/";
		printMatrix(path);

	}
	
	public static void printMatrix(String path){
		File directory = new File(path);
        File[] fileList = directory.listFiles();
        List<String> columns = new LinkedList<String>();
        columns.add("1P2");
        columns.add("1A2");
        columns.add("3P1A2");
        columns.add("1P3A2");
        columns.add("3A4P1A2");
        int[][] matrix = new int[55][5]; 
		String nameFile;
		for (File file : fileList){
			nameFile = file.getName();
			if(nameFile.contains("phylogeny")){
				List<String> topologies = new LinkedList<String>();
				topologies = extractPhylogeny(path+"/"+nameFile);
				nameFile = nameFile.replace(".txt", "");
				String[] names = nameFile.split("_");
				int indexRow = (int)(Double.valueOf(names[4])*50)+(Integer.valueOf(names[3])-1); 
				for(int i=0;i<topologies.size();i++){
					int indexColumn = columns.indexOf(topologies.get(i));
					matrix[indexRow][indexColumn]++;
				}
			}
		}
		for(int i=0;i<matrix.length;i++){
			for(int j=0;j<matrix[0].length;j++){
				System.out.print(matrix[i][j]+"\t");
			}
			System.out.println();
		}
	}
	
	public static List<String> extractPhylogeny(String path){
		BufferedReader br = null;
		List<String> Names = new LinkedList<String>();
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(path));
			while ((sCurrentLine = br.readLine()) != null) {
				Names.add(sCurrentLine);		
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

}
