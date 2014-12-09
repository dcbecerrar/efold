package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import foldpath.Cluster;
import foldpath.Landscape_tfolder;
import foldpath.Sample;
import SS.Constants;
import SS.Constants.Dir;

public class Print {
	public static void print_template(List<Integer> template){
		for(int i=0; i< template.size(); i++){
			System.out.print(template.get(i)+ " ");
		}
		System.out.println();
	}

	public static void printl_inputs(List<List<Integer>> test){
	double cont=0, ag=0d;	
	Iterator<List<Integer>> iter = test.iterator();
		while (iter.hasNext()){
			Iterator<Integer> siter = iter.next().iterator();
			while(siter.hasNext()){
				System.out.print(siter.next() + " ..... ");
			}
			System.out.println();
			cont++;
		}
		System.out.println("Size = "+ cont);
	}
	public static void print_pairings(List<Hashtable<List<Dir>,Integer>> pairing){
		Iterator<Hashtable<List<Constants.Dir>,Integer>> iter = pairing.iterator();
		int ii=0;
		while (iter.hasNext()){
			Hashtable<List<Constants.Dir>,Integer> map = iter.next();
			Set<List<Dir>> keys = map.keySet();
			for(Iterator i = keys.iterator(); i.hasNext();){
			       List<Dir> key = (List<Dir>) i.next();
			      // print_interactions(key);
			       int value =  map.get(key);
			       System.out.println(key + " = " + value);
			}
			System.out.println("==================================" + ii +"==================================" );
			ii++;
		}
	}
	public static void print_interactions(List<Dir> pair){
		for(int i=0; i< pair.size(); i++){
			System.out.print(pair.get(i)+ " ");
		}
		System.out.println();
	}
	
	public static void print_phylogeny(List<List<Integer>> Phylo,  String fileName, List<Cluster> clusters){
		try { 
			String content = "";
			List<Integer> template_c, gen;
			List<Dir> pairing_c;
			String label_aux="";
			for(int i=0; i<Phylo.size();i++){
				gen = Phylo.get(i);
				
				Cluster c = clusters.get(gen.get(0));
				template_c = c.template;
				pairing_c = c.pairing;
				if(template_c.size()==0){
					continue;
				}
				if(i!=0){
					label_aux +="\n";
				}
				label_aux += Landscape_tfolder.clusterLabelShort(template_c,pairing_c);
			}
			content = label_aux;
			File file = new File(fileName);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void print_clusters(List<Cluster> clusters, String fileName, String first){
		try { 
			String content = "";
			content = first + "\n";
			int cont = 0;
			for(Cluster c: clusters){
				content+= cont + " " + Landscape_tfolder.clusterLabelShort(c.template, c.pairing)+ " " + c.weight +"\n";
				cont ++;
			}
			
 
			File file = new File(fileName);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void print_dynamics_matrix(double[][] matrix, String fileName){
		try { 
			String content = "";			
			File file = new File(fileName);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i = 0; i< matrix.length; i++){
				content="";
				for(int j = 0; j< matrix[0].length; j++){
					content += String.valueOf(matrix[i][j]) + " ";
				}
				content += "\n";
				bw.write(content);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void print_a_s_f_matrix(double[][] matrix, String fileName){
		try { 
			String content = "";			
			File file = new File(fileName);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i = 0; i< matrix.length; i++){
				content="";
				for(int j = 0; j< matrix[0].length; j++){
					content += String.valueOf(matrix[i][j]) + " ";
				}
				content += "\n";
				bw.write(content);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void print_densities(HashMap<List<Integer>,Double> m, String fileName, int numStrands){
		try { 
			String content = "";			
			File file = new File(fileName);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			
			for(int i=1; i<4; i++){
				for(int j=1; j<numStrands; j++){
					content += m.get(Util.list(j,(i*2)-1)) + " ";
				}
				content +="\n";
			}
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void print_clusters_all(List<Cluster> clusters, String fileName){
		int cont=0;
		try { 
			String infoCluster="", infoSample="", infoStructure="";
			Collection<Sample> samples;
			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(Cluster c: clusters){
				cont++;
				bw.write("Cluster "+ cont +"\n");
				infoCluster= Landscape_tfolder.clusterLabelShort(c.template, c.pairing)+ " " + c.weight +"\n";
				bw.write(infoCluster);
				samples = c.get_samples();
				if(samples!= null){
					for(Sample s: samples){
						infoStructure = "";
						for(int i=0;i<s.structure.length;i++){
							infoStructure += "("+s.structure[i][0]+ "," + s.structure[i][1]+ ")" + ";";
						}
						infoSample = s.weight + " " + s.probability + " " +  infoStructure + "\n";
						bw.write(infoSample);
					}
				}
			}
			bw.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void print_probability_templates(HashMap<List<Integer>,Double> m_PARA,HashMap<List<Integer>,Double> m_ANTI,HashMap<List<Integer>,Double> m_NONE, String fileName, int numStrands){
		try { 
			String content = "";			
			File file = new File(fileName);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write("PARA\n");
			for(int i=1; i<= numStrands; i++){
				for(int j=1; j<=numStrands; j++){
					content += m_PARA.get(Util.list(i,j))+ " ";
				}
				content += "\n";
			}
			bw.write(content);
			content ="";
			
			bw.write("ANTI\n");
			for(int i=1; i<= numStrands; i++){
				for(int j=1; j<=numStrands; j++){
					content += m_ANTI.get(Util.list(i,j))+ " ";
				}
				content += "\n";
			}
			bw.write(content);
			content ="";
			
			
			bw.write("NONE\n");
			for(int i=1; i<= numStrands; i++){
				for(int j=1; j<=numStrands; j++){
					content += m_NONE.get(Util.list(i,j))+ " ";
				}
				content += "\n";
			}
			bw.write(content);
			content ="";
			
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void print_evfold_predictions(String fileName, String content){

		try { 
			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content +"\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
