package foldpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import SS.Constants;

public class Energy {
	Double[][] BURIED_PARALLEL, BURIED_ANTIPARALLEL, EXPOSED_PARALLEL, EXPOSED_ANTIPARALLEL, LOOP;
	Double[][][][] STACK_BURIED_PARALLEL, STACK_BURIED_ANTIPARALLEL, STACK_EXPOSED_PARALLEL, STACK_EXPOSED_ANTIPARALLEL;
	double temperature;
	
	public Energy(String buriedParallel, String buriedAntiparallel,String exposedParallel,String exposedAntiparallel, String loop, double loopFactor, double recentering, double antiPara){
		BURIED_PARALLEL = new Double[26][26];
		BURIED_ANTIPARALLEL = new Double[26][26];
		EXPOSED_PARALLEL = new Double[26][26];
		EXPOSED_ANTIPARALLEL = new Double[26][26];
		STACK_BURIED_PARALLEL = new Double[26][26][26][26];
		STACK_BURIED_ANTIPARALLEL = new Double[26][26][26][26];
		STACK_EXPOSED_PARALLEL = new Double[26][26][26][26];
		STACK_EXPOSED_ANTIPARALLEL = new Double[26][26][26][26];
		LOOP = new Double[26][26];
		
		try {
			FileInputStream fs = new FileInputStream(new File(buriedParallel));
			Scanner read = new Scanner(fs);
			while(read.hasNext()){
				String[] fields = read.nextLine().split("\\s+");
				BURIED_PARALLEL[fields[0].charAt(0)-'A'][fields[1].charAt(0)-'A'] = (Double.parseDouble(fields[2]) - recentering)/antiPara;
			}
			fs.close();
			read.close();
			
			fs = new FileInputStream(new File(buriedAntiparallel));
			read = new Scanner(fs);
			while(read.hasNext()){
				String[] fields = read.nextLine().split("\\s+");
				BURIED_ANTIPARALLEL[fields[0].charAt(0)-'A'][fields[1].charAt(0)-'A'] = (Double.parseDouble(fields[2]) * (1.0 - loopFactor) - recentering)*antiPara;			
			}
			fs.close();
			read.close();
			
			fs = new FileInputStream(new File(exposedParallel));
			read = new Scanner(fs);
			while(read.hasNext()){
				String[] fields = read.nextLine().split("\\s+");
				EXPOSED_PARALLEL[fields[0].charAt(0)-'A'][fields[1].charAt(0)-'A'] = (Double.parseDouble(fields[2]) * (1.0 - loopFactor) - recentering)/antiPara;
			}
			fs.close();
			read.close();
			
			fs = new FileInputStream(new File(exposedAntiparallel));
			read = new Scanner(fs);
			while(read.hasNext()){
				String[] fields = read.nextLine().split("\\s+");
				EXPOSED_ANTIPARALLEL[fields[0].charAt(0)-'A'][fields[1].charAt(0)-'A'] = (Double.parseDouble(fields[2]) * (1.0 - loopFactor) - recentering)*antiPara;
			}
			fs.close();
			read.close();
			
			fs = new FileInputStream(new File(loop));
			read = new Scanner(fs);
			while(read.hasNext()){
				String[] fields = read.nextLine().split("\\s+");
				LOOP[fields[0].charAt(0)-'A'][fields[1].charAt(0)-'A'] = Double.parseDouble(fields[2]) * loopFactor - recentering;
			}
			fs.close();
			read.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public double energy(Constants.Environment env, Constants.Dir dir, char ... residues){
		if(residues.length != 2)
			throw new IllegalArgumentException("Two residues must be specified for this energy function");
		int one = (char) (Character.toUpperCase(residues[0])-'A');
		int two = (char) (Character.toUpperCase(residues[1])-'A');
		try {
			switch(env){
				case BURIED:{
					switch(dir){
						case PARA:
							return BURIED_PARALLEL[one][two];				
						case ANTI:
							return BURIED_ANTIPARALLEL[one][two];
					}
				}
				case EXPOSED:{
					switch(dir){
						case PARA:
							return EXPOSED_PARALLEL[one][two];					
						case ANTI:
							return EXPOSED_ANTIPARALLEL[one][two];
					}
				}
				case LOOP:{
					return LOOP[one][two];
				}
			}
			throw new IllegalArgumentException("Invalid environment "+env);
		}
		catch (NullPointerException e) {
			throw new IllegalArgumentException("At least one of '"+residues[0]+"' and '"+residues[1]+"' is not a valid amino acid code");
		}
	}
}
