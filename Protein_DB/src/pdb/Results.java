package pdb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Results {
	public static void main(String[] args) throws IOException {
		String path = "/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/eFold/experimentation/Files/StudyCase/Sets0/";
		StatisticsBest(path);
		StatisticsAverageBest(path);
		StatisticsAverageBestCluster(path);
	}
	
	public static void StatisticsAverageBest(String path){
		String pathFiles = path;
		String NameFiles = pathFiles + "NameFiles";
		String writeFile = pathFiles + "statisticsAverageBest1UBQ.txt";
		File directory = new File(NameFiles);
		List<String> Names = new LinkedList<String>();
		String FolderPdb;
		File[] fList = directory.listFiles();
		File directoryPdb;


		

		String label_template;
		String nameFile;
		String toWrite;
		Names = Read_NameFiles(NameFiles);
		for(int i=0; i<Names.size(); i++){
			
			//Starting Objects........
			double values_aux[][];
			double position;
			double values_one[][][] = new double[11][2][9];
			double values_one_result[][] = new double[2][9];
			String[][] values_label_one = new String[2][9];
			double values_two[][][] = new double[11][2][9];
			double values_two_result[][] = new double[2][9];
			String[][] values_label_two = new String[2][9];
			int[] cont_one = new int[11];
			int[] cont_two = new int[11];
			//.........................
			
			
			FolderPdb = pathFiles+Names.get(i);
	        directoryPdb = new File(FolderPdb);
	        File[] fileList = directoryPdb.listFiles();
			
			for (File file : fileList){
				nameFile = file.getName();
				if(nameFile.contains("summary_one")){
					values_aux = read_summary(file.getAbsolutePath());
					label_template = nameFile.substring(nameFile.indexOf("summary_one_"));
					position = (Double.valueOf(label_template.split("_")[3])*10);
					update_averageBest(values_one[(int)position],values_aux,cont_one,(int)position);
				}
				if(file.getName().contains("summary_two")){
					values_aux = read_summary(file.getAbsolutePath());
					label_template = nameFile.substring(nameFile.indexOf("summary_two_"));
					position = (Double.valueOf(label_template.split("_")[3])*10);
					update_averageBest(values_two[(int)position],values_aux,cont_two,(int)position);
				}
			}
			update_averageDivisionResult(values_one, cont_one);
			update_averageDivisionResult(values_two, cont_two);
			update_averageBestResult(values_one,values_one_result,values_label_one);
			update_averageBestResult(values_two,values_two_result,values_label_two);
			toWrite = Names.get(i);
			toWrite += toWrite(values_one_result, values_label_one, values_two_result, values_label_two);
			writeAnnotation(writeFile,toWrite);
		}
		
	}
	
	public static void StatisticsAverageBestCluster(String path){
		String pathFiles = path;
		String NameFiles = pathFiles + "NameFiles";
		String writeFile = pathFiles + "statisticsAverageBestCluster1UBQ.txt";
		File directory = new File(NameFiles);
		List<String> Names = new LinkedList<String>();
		String FolderPdb;
		File[] fList = directory.listFiles();
		File directoryPdb;


		
		double values_aux[][];
		double position;
		int run;
		String label_template;
		String nameFile;
		String toWrite;
		Names = Read_NameFiles(NameFiles);
		for(int i=0; i<Names.size(); i++){
			
			
			//Starting Objects........
			double values_one[][][][] = new double[5][11][2][9];
			double values_one_aux[][][] = new double[11][2][9];
			double values_one_result[][] = new double[2][9];
			String[][] values_label_one = new String[2][9];
			double values_two[][][][] = new double[5][11][2][9];
			double values_two_aux[][][] = new double[11][2][9]; 
			double values_two_result[][] = new double[2][9];
			String[][] values_label_two = new String[2][9];
			int[][][] cont_one = new int[11][2][9];
			int[][][] cont_two = new int[11][2][9];
			//.........................
			
			
			
			
			FolderPdb = pathFiles+Names.get(i);
	        directoryPdb = new File(FolderPdb);
	        File[] fileList = directoryPdb.listFiles();
			
			for (File file : fileList){
				nameFile = file.getName();
				if(nameFile.contains("summary_one")){
					values_aux = read_summary(file.getAbsolutePath());
					label_template = nameFile.substring(nameFile.indexOf("summary_one_"));
					position = (Double.valueOf(label_template.split("_")[3])*10);
					run = Integer.valueOf(label_template.split("_")[2])-1;
					update_averageBestCluster(values_one[run][(int)position],values_aux);
				}
				if(file.getName().contains("summary_two")){
					values_aux = read_summary(file.getAbsolutePath());
					label_template = nameFile.substring(nameFile.indexOf("summary_two_"));
					position = (Double.valueOf(label_template.split("_")[3])*10);
					run = Integer.valueOf(label_template.split("_")[2])-1;
					update_averageBestCluster(values_two[run][(int)position],values_aux);
				}
			}

			update_averageBestClusterAux(values_one,values_one_aux,cont_one);
			update_averageBestClusterAux(values_two,values_two_aux,cont_two);
			update_averageDivisionResultCluster(values_one_aux, cont_one);
			update_averageDivisionResultCluster(values_two_aux, cont_two);
			update_averageBestResult(values_one_aux,values_one_result,values_label_one);
			update_averageBestResult(values_two_aux,values_two_result,values_label_two);
			toWrite = Names.get(i);
			toWrite += toWrite(values_one_result, values_label_one, values_two_result, values_label_two);
			writeAnnotation(writeFile,toWrite);
		}
		
	}
	
	public static void StatisticsBest(String path){
		String pathFiles = path;
		String NameFiles = pathFiles + "NameFiles";
		String writeFile = pathFiles + "statisticsBest1UBQ.txt";
		File directory = new File(NameFiles);
		List<String> Names = new LinkedList<String>();
		String FolderPdb;
		File[] fList = directory.listFiles();
		File directoryPdb;

		String label_template;
		String nameFile;
		String toWrite;
		Names = Read_NameFiles(NameFiles);
		for(int i=0; i<Names.size(); i++){
			
			
			//Starting Objects........
			double values_one[][] = new double[2][9];
			String[][] values_label_one = new String[2][9];
			double values_two[][] = new double[2][9];
			String[][] values_label_two = new String[2][9];		
			double values_aux[][];
			//.........................
			
			
			FolderPdb = pathFiles+Names.get(i);
	        directoryPdb = new File(FolderPdb);
	        File[] fileList = directoryPdb.listFiles();
			
			for (File file : fileList){
				nameFile = file.getName();
				if(nameFile.contains("summary_one")){
					values_aux = read_summary(file.getAbsolutePath());
					label_template = nameFile.substring(nameFile.indexOf("summary_one_"));
					update_max(values_one,values_aux,values_label_one,label_template);
				}
				if(file.getName().contains("summary_two")){
					values_aux = read_summary(file.getAbsolutePath());
					label_template = nameFile.substring(nameFile.indexOf("summary_two_"));
					update_max(values_two,values_aux,values_label_two,label_template);
				}
			}
			toWrite = Names.get(i);
			toWrite += toWrite(values_one, values_label_one, values_two, values_label_two);
			writeAnnotation(writeFile,toWrite);
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
	
	public static void update_max(double[][] target, double[][] template, String[][] labels, String label_template){
		for(int i=0;i<target.length;i++){
			for(int j=0;j<target[0].length;j++){
				if(template[i][j]>=target[i][j]){
					target[i][j] = template[i][j];
					labels[i][j] = label_template;
				}
			}
		}	
	}
	
	public static void update_averageBest(double[][] target, double[][] template, int[] counter, int position){
		for(int i=0;i<target.length;i++){
			for(int j=0;j<target[0].length;j++){
					if(template[i][j]>=0){
						target[i][j] += template[i][j];
					}
			}
		}
		counter[position]++;
	}
	
	public static void update_averageBestCluster(double[][] target, double[][] template){
		for(int i=0;i<target.length;i++){
			for(int j=0;j<target[0].length;j++){
					if(template[i][j]>=target[i][j]){
						target[i][j] = template[i][j];
					}
			}
		}
	}
	
	public static void update_averageBestClusterAux(double[][][][] target, double[][][] template, int[][][] cont){
		for(int k=0;k<target[0][0].length;k++){
			for(int m=0;m<target[0][0][0].length;m++){
				for(int j=0;j<target[0].length;j++){
					for(int i=0; i<target.length;i++){
						if(target[i][j][k][m]>=0){
							template[j][k][m] += target[i][j][k][m];
							cont[j][k][m]++;
						}
					}
				}
			}
		}
	}
	
	public static void update_averageBestResult(double[][][] target, double[][] template, String[][] labels){
		double max = Double.MIN_VALUE;
		String max_label = "";
		for(int j=0;j<target[0].length;j++){
			for(int k=0;k<target[0][0].length;k++){
				max = Double.MIN_VALUE;
				max_label = "";
				for(int i=0;i<target.length;i++){
					if(target[i][j][k]>max){
						max = target[i][j][k];
						max_label = String.valueOf(i);
					}
				}
				template[j][k] = max;
				labels[j][k] = max_label;
			}
		}
	
	}
	
	public static void update_averageDivisionResult(double[][][] target, int[] divisor){
		for(int i=0;i<target.length;i++){
			for(int j=0;j<target[0].length;j++){
				for(int k=0;k<target[0][0].length;k++){
					target[i][j][k] = target[i][j][k]/divisor[i];
				}
			}
		}	
	}
	
	public static void update_averageDivisionResultCluster(double[][][] target, int[][][] divisor){
		for(int i=0;i<target.length;i++){
			for(int j=0;j<target[0].length;j++){
				for(int k=0;k<target[0][0].length;k++){
					target[i][j][k] = target[i][j][k]/divisor[i][j][k];
				}
			}
		}	
	}
	
	public static String toWrite(double[][] target1, String[][] labels1,double[][] target2, String[][] labels2){
		String toWrite = "";
		for(int i=0;i<target1.length;i++){
			for(int j=0;j<target1[0].length;j++){
				toWrite+=";"+target1[i][j]+";"+labels1[i][j];
			}
		}
		for(int i=0;i<target1.length;i++){
			for(int j=0;j<target1[0].length;j++){
				toWrite+=";"+target2[i][j]+";"+labels2[i][j];
			}
		}
		return toWrite;
	}
	
	public static double[][] read_summary(String path){
		double [][]values = new double [2][9];
		BufferedReader br = null;
		List<String> Names = new LinkedList<String>();
		int cont;
		try {
			String sCurrentLine;
			String []splitLine;
			cont = 0;
			br = new BufferedReader(new FileReader(path));
			while ((sCurrentLine = br.readLine()) != null) {
				if(!sCurrentLine.contains("agreement") && !sCurrentLine.contains("f12")){
					splitLine = sCurrentLine.split("\t");
					if(splitLine.length==9){
						for(int i=0;i<9;i++){
							if(splitLine[i].equals("NaN")){
								values[cont][i] = -1d;
							}else{
								values[cont][i]=Double.valueOf(splitLine[i]);
							}	
						}
						cont ++;
					}
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
		return values;
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
}
