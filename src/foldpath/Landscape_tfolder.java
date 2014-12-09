package foldpath;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

import SS.Constants.Environment;

import SS.Constants.Dir;

import plots.ContactChart;
import plots.PDBReader;
import plots.StrandChart;

import edu.uci.ics.jung.visualization.NetworkFigure;
import edu.uci.ics.jung.visualization.VisualizationSaver;
import evolutionary.EvFold;
import experiments.Contact_Prediction;
import foldpath.Cluster;
import foldpath.Sample;
import foldpath.Simulation;
import cluster.SampleCluster;

import metrics.ContactDistance;
import metrics.SampleDistance;



import SS.Constants;
import SS.Constants.*;



import util.*;
import weka.core.pmml.Function;

public class Landscape_tfolder {
	static List<Hashtable<List<Constants.Dir>,Integer>> total_pairings_hash;
	public static List<List<List<Constants.Dir>>> total_pairings;
	public static List<Template_tfolder> list_templates;
	public static Arguments params;
	public static Energy ef;
	public static int seq_length;
	public static int allow_space_border;
	public static int allow_space_center;
	public static int runs;
	public static int num_runs;
	public static boolean salida;
	public static List<HashMap<List<Integer>,Double>> list_m_true;
	public static List<HashMap<List<Integer>,Double>> list_m_false;
	public static HashMap<List<Integer>,Double> mdensity;
	public static HashMap<List<Integer>,Double> mdensity_count;
	public static double[][] summary_one;
	public static double[][] summary_two;
	public static double[][] summary_one_count;
	public static double[][] summary_two_count;
	public static List<List<Integer>> phylogeny = new LinkedList<List<Integer>>();
	

	public static void main(String[] args) throws IOException {	
		// TODO Auto-generated method stub
		
		double alpha, max_alpha, initial_alpha;
		int initial_runs, failed_runs;
		List<String> set = new LinkedList<String>();
		double initial_clusterThreshold;
		double initial_transitionThreshold;
		
		
		
		// With Parameters
		
		alpha = Double.valueOf(args[0]);
		max_alpha =	Double.valueOf(args[1]);
		runs = Integer.valueOf(args[2]);
		initial_runs = Integer.valueOf(args[2]);
		num_runs = Integer.valueOf(args[3]);
		initial_alpha=alpha;
		System.out.println("PARAMETERS => "+ "alpha= "+alpha +"     max_alpha= "+max_alpha+"     runs= "+ runs + "     num_runs= "+num_runs);
		
		
		 //Without Parameters
		
//		alpha=0.7;
//		max_alpha=0.7;
//		runs = 1;
//		num_runs = 3;
//		initial_runs=1;
//		initial_alpha=0.7;
//		System.out.println("PARAMETERS => "+ "alpha= "+alpha +"     max_alpha= "+max_alpha+"     runs= "+ runs + "     num_runs= "+num_runs);
		
		 
		
		Arguments opts = new Arguments(args);
		params = opts;
		
		//test_clusters();
		

		initial_clusterThreshold = params.clusterThreshold;
		initial_transitionThreshold = params.transitionThreshold;
		
		set = read_csv(params.pathFiles+"Sets1");
		
		
		for(String input:set){
			update_parameters(input);
			seq_length = params.sequence.length();
			allow_space_border = params.minG+params.minL;
			allow_space_center = 2*params.minG+params.minL;
			params.clusterThreshold=initial_clusterThreshold;
			params.transitionThreshold = initial_transitionThreshold;
			alpha=initial_alpha;
			failed_runs = 0;
			while(alpha <= max_alpha){
				params.clusterThreshold=initial_clusterThreshold;
				params.transitionThreshold = initial_transitionThreshold;
				failed_runs=0;
				params.alpha = alpha;
				runs = initial_runs;
				list_m_true = new LinkedList<HashMap<List<Integer>,Double>>();
				list_m_false = new LinkedList<HashMap<List<Integer>,Double>>();
				list_m_true.add(new HashMap<List<Integer>,Double>());
				list_m_true.add(new HashMap<List<Integer>,Double>());
				list_m_true.add(new HashMap<List<Integer>,Double>());
				list_m_false.add(new HashMap<List<Integer>,Double>());
				list_m_false.add(new HashMap<List<Integer>,Double>());
				list_m_false.add(new HashMap<List<Integer>,Double>());
				mdensity=new HashMap<List<Integer>,Double>();
				mdensity_count=new HashMap<List<Integer>,Double>();
				summary_one = new double[2][9];
				summary_two = new double[2][9];
				summary_one_count = new double[2][9];
				summary_two_count = new double[2][9];

				while(runs<=num_runs){	
					salida = false;
					total_pairings_hash = new LinkedList<Hashtable<List<Constants.Dir>, Integer>>();
					total_pairings = new LinkedList<List<List<Constants.Dir>>>();
					list_templates = new LinkedList<Template_tfolder>();
					phylogeny = new LinkedList<List<Integer>>();

					System.out.println(params.protein + "  #  "+ params.clusterThreshold + "  #  "+ params.transitionThreshold);
					generator();
					if(phylogeny.size()>0 || params.numStrands==2){
						runs++;
						failed_runs=0;
						params.clusterThreshold=initial_clusterThreshold;
						params.transitionThreshold = initial_transitionThreshold;
					}else{
						failed_runs++;
						//4 and 8 for Sets3 and Sets4
						if(failed_runs%2==0){
							params.transitionThreshold += 0.25;
						}
						if(failed_runs%3==0){
							params.clusterThreshold += 0.1;
						}
						if(params.transitionThreshold>=5.05){
							runs++;
						}
						
					}
					for(int i=0; i<list_templates.size();i++){
						list_templates.get(i).unset_Zs();
					}
					total_pairings_hash.clear();
					total_pairings.clear();
					phylogeny.clear();
					list_templates.clear();
					list_templates = null;
					total_pairings_hash=null;
					total_pairings=null;
					phylogeny=null;
					list_templates=null;
					//System.gc();
				}
				if(list_m_true.get(0).size()>0 || list_m_true.get(1).size()>0 || list_m_true.get(2).size()>0){
					summary_interactions_template(params.protein);
				}
				if(mdensity.size()>0){
					summary_density(params.protein);
				}
				summary_report();
				Print.print_a_s_f_matrix(summary_one, params.pathFiles+params.protein+"/SummaryReportOne_"+params.protein+"_"+params.alpha+".txt");
				Print.print_a_s_f_matrix(summary_two, params.pathFiles+params.protein+"/SummaryReportTwo_"+params.protein+"_"+params.alpha+".txt");

				alpha += 0.1;
				
				
				list_m_true.clear();
				list_m_false.clear();
				list_m_true = null;
				list_m_false = null;
				mdensity.clear();
				mdensity=null;
				mdensity_count.clear();
				mdensity_count=null;
				summary_one = null;
				summary_two = null;
				summary_one_count = null;
				summary_two_count = null;			
			}
		}
//		for(String input:set){
//			update_parameters(input);
//			System.out.println("PROTEIN.... "+ params.protein);
//			generator_test();
//		}
	}
	

	public static void update_parameters(String csv){
		String[] csv_values = csv.split(",");
		params.protein = csv_values[0];
		params.chain = csv_values[1];
		params.Pfam = params.pathFiles+"Fasta/"+csv_values[14]+".fa";
		params.evFoldIndex = Integer.valueOf(csv_values[5]);
		params.PDB = params.pathFiles+"Pdb/"+params.protein;
		//params.Seq_ID = csv_values[12]+"/"+csv_values[13];
		params.Seq_ID = csv_values[16];
		params.Pfam_range = csv_values[17];
		params.numStrands = Integer.valueOf(csv_values[18]);
		params.maxL = Integer.valueOf(csv_values[19]);
		params.minL = Integer.valueOf(csv_values[20]);
		params.minG = Integer.valueOf(csv_values[23]);
		params.target_topology = csv_values[25];
		params.target_topology_reverse = new StringBuilder(csv_values[25]).reverse().toString();
		params.sequence = csv_values[26];
		ef = new Energy(params.pathFiles+"BURIED PARALLEL",params.pathFiles+"BURIED ANTIPARALLEL",params.pathFiles+"EXPOSED PARALLEL",params.pathFiles+"EXPOSED ANTIPARALLEL",params.pathFiles+"LOOP",params.coilWeight, params.recentering,params.antiPara);
		if(params.loops){
			params.loopEnergy = loopEnergy(0,params.sequence.length()-1,SS.Constants.Environment.LOOP);
		}
	}
	
	public static List<String> read_csv(String FilePath){
		List<String> input = new LinkedList<String>();
		BufferedReader br = null;
		String sCurrentLine;
		try {
			br = new BufferedReader(new FileReader(FilePath));
			while ((sCurrentLine = br.readLine()) != null) {
					input.add(sCurrentLine);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return input;
	}
	
	public static void generator_test() {
		// ****************  evFold **************** //
		EvFold experiments = new EvFold(params.sequence.length()+1);
		experiments.evolutionary_constraints(params);
	}
	
	public static void generator() throws IOException{
		List<Cluster> clusters = new LinkedList<Cluster>();
		List<Cluster> clusters_aux = new LinkedList<Cluster>();
		List<List<Integer>> phylogeny_aux=null;
		Hashtable<String,String> hierarchy = new Hashtable<String,String>();
		//ef = new Energy(params.pathFiles+"BURIED PARALLEL",params.pathFiles+"BURIED ANTIPARALLEL",params.pathFiles+"EXPOSED PARALLEL",params.pathFiles+"EXPOSED ANTIPARALLEL",params.pathFiles+"LOOP",params.coilWeight, params.recentering,params.antiPara);
		List<Integer> Test = new LinkedList<Integer>();
		Map<Cluster,Color> colors = null;
		double[][] dynamics;
		boolean filter=false;
		Test.add(1);
		Test.add(2);
		allPairings(enumeratePairings(params.numStrands-1, params.numStrands-1), params.numStrands-1);
		create_hash_pairing();
		//Print.print_pairings(total_pairings_hash);
		
		
		// ****************  evFold **************** //
		EvFold experiments = new EvFold(params.sequence.length()+1);
		//experiments.evolutionary_constraints(params);
		experiments.evolutionary_constraints_aux(params);
		//experiments(experiments);
		int []hel=new int[experiments.Data.length]; 
		hel=helix_module(experiments);

		
		
		// ****************  DFS  **************** //
		//adjacents_DFS(Test,null,params.numStrands,false,clusters,hierarchy,experiments.Data);
		
		
		// ****************  BFS **************** //
		Test.add(-1);
		Test.add(0);
		List<List<Integer>> nodes = new LinkedList<List<Integer>>();
		nodes.add(Test);
		adjacents_BFS(nodes, params.numStrands, clusters, hierarchy, experiments.Contacts);
		

		
		
		if(phylogeny.size()>0 || params.numStrands==2){
		

			
		// ****************  Storing Clusters **************** //
		System.out.println("Storing Clusters");
		Print.print_clusters_all(clusters, params.pathFiles+params.protein+"/clustersALL_"+params.protein+"_"+runs+"_"+params.alpha+".txt");	
			
		// ****************  SIMULATION **************** //
		System.out.println("Starting Dynamics");
		if(clusters.size()>params.limitClusters){
			filter = true;
			phylogeny_aux=filter_clusters(clusters,clusters_aux);
			System.out.println("clusters_aux.size = "+ clusters_aux.size());
			dynamics =  Simulation.foldingDynamics(clusters_aux, params.transMetric, params.transitionThreshold, params.temp, params.startTime, params.endTime, params.granularity);
			colors = Util.map(clusters_aux, ChartUtils.getPalette(clusters_aux.size()));
			Print.print_clusters(clusters_aux, params.pathFiles+params.protein+"/clusters_aux_"+params.protein+"_"+runs+"_"+params.alpha+".txt",params.transitionThreshold +";"+params.clusterThreshold);
			// ****************  Dynamics Report **************** //
			System.out.println("Creating dynamics figure");
			JFreeChart dynamicsChart = createFoldingDynamicsChart(clusters_aux, dynamics, colors);	
			ChartUtils.saveChart(dynamicsChart, params.pathFiles+params.protein+"/dynam_"+params.protein+"_"+runs+"_"+params.alpha+".png", 600, 600);
		}else{
			dynamics =  Simulation.foldingDynamics(clusters, params.transMetric, params.transitionThreshold, params.temp, params.startTime, params.endTime, params.granularity);
			colors = Util.map(clusters, ChartUtils.getPalette(clusters.size()));
			// ****************  Dynamics Report **************** //
			System.out.println("Creating dynamics figure");
			JFreeChart dynamicsChart = createFoldingDynamicsChart(clusters, dynamics, colors);	
			ChartUtils.saveChart(dynamicsChart, params.pathFiles+params.protein+"/dynam_"+params.protein+"_"+runs+"_"+params.alpha+".png", 600, 600);
		}
		
		// **************** Storing Phylogeny **************** //
		System.out.println("Storing Phylogeny");	
		if(filter){
			Print.print_phylogeny(phylogeny_aux, params.pathFiles+params.protein+"/phylogeny_"+params.protein+"_"+runs+"_"+params.alpha+".txt", clusters_aux);
		}else{
			Print.print_phylogeny(phylogeny, params.pathFiles+params.protein+"/phylogeny_"+params.protein+"_"+runs+"_"+params.alpha+".txt", clusters);
		}
		
		

		
		// ****************  Network Report **************** //
		System.out.println("Creating network figure");
		colors = Util.map(clusters, ChartUtils.getPalette(clusters.size()));
		NetworkFigure figure = new NetworkFigure(clusters, params.transMetric, params.transitionThreshold, colors);
		VisualizationSaver.save(figure.viewer, params.pathFiles+params.protein+"/dynam_"+params.protein+"_"+runs+"_"+params.alpha+".svg");	
		
		// ****************  Contact Prediction Report   **************** //
		System.out.println("Creating contact prediction report");
		contact_prediction(clusters, params, hel,params.protein, experiments);
		
		// ****************  Interaction Templates   **************** //
		System.out.println("Creating Interaction Templates report");
		interactions_template(clusters,params.protein,false);
		interactions_template(clusters,params.protein,true);
		if(phylogeny.size()!=0){
			if(filter){
				interactions_density(clusters_aux,dynamics,params.protein,phylogeny_aux,filter);
			}else{
				interactions_density(clusters,dynamics,params.protein,phylogeny,filter);
			}
		}
		
		// ****************  Printing Clusters and dynamics matrix   **************** //
		Print.print_clusters(clusters, params.pathFiles+params.protein+"/clusters_"+params.protein+"_"+runs+"_"+params.alpha+".txt",params.transitionThreshold +";"+params.clusterThreshold);
		Print.print_dynamics_matrix(dynamics, params.pathFiles+params.protein+"/Files_matrixdynamics_"+params.protein+"_"+runs+"_"+params.alpha+".txt");
		
		colors.clear();
		colors = null;
		dynamics=null;
		}
		experiments = null;
		clusters.clear();
		clusters=null;
		clusters_aux.clear();
		clusters_aux=null;

		
	}
	
	public static void generator_experimentation(){
		ef = new Energy("BURIED PARALLEL","BURIED ANTIPARALLEL","EXPOSED PARALLEL","EXPOSED ANTIPARALLEL","LOOP",params.coilWeight, params.recentering,params.antiPara);
		List<Integer> Test = new LinkedList<Integer>();
		Test.add(1);
		Test.add(2);
		for(int i=0;i<params.numStrands-1;i++){
			total_pairings_hash.add(i,new Hashtable<List<Constants.Dir>, Integer>());
		}
		allPairings(enumeratePairings(params.numStrands-1, params.numStrands-1), params.numStrands-1);
		
		
		///EXPERIMENTATION.......
		
		String[] alphabet= {"A","C","D","E","F","G","H","I","K","L","M","N","P","Q","R","S","T","V","W","Y"};
		long[] time = new long[7];
		int cont =0;
		int length = 140;
		String sequence=null;
		params.maxL = 5;
		params.minL = 5;
		params.minG = 5;
		int attempts = 1;
		long startTime=0, stopTime=0, elapsedTime=0;
		
		while(length<=140){
			System.out.println("*************      "+ length+"      *************");
			for(int i=0;i<attempts;i++){
				sequence = create_sequence(alphabet,length);
				params.sequence = sequence;
				seq_length = params.sequence.length();
				startTime = System.currentTimeMillis();
				adjacents_DFS(Test,null,params.numStrands,false,null,null,null);
				stopTime = System.currentTimeMillis();
				elapsedTime = (stopTime - startTime)/1000;
				System.out.println(elapsedTime+ "   ");
				
			}
			time[cont]=elapsedTime;
			length +=10;
			cont++;
		}	
		
	}
	
	
	public static void experiments(EvFold evolutionary) throws IOException{
		//CODE FOR THE 1EM7 EXPERIMENT.......
		
		if(params.protein=="1EM7"){
			System.out.println("Analysing Protein: "+params.protein);
			String pdb = params.pathFiles+"1EM7";
			//String pdb ="/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/RECOMB_2013/1EM7";
			Character chain = PDBReader.chain(pdb);
			if( chain == null)
				chain = 'A';
			Map<List<Integer>,Double> pdbMap = PDBReader.getPDBContactMap(pdb,chain, 8.0);
			for(int i=0;i<=params.sequence.length()-1;i++){
				for(int j=i+1;j<=params.sequence.length();j++){
					if(pdbMap.get(Arrays.asList(new Integer[]{i, j}))!=null && pdbMap.get(Arrays.asList(new Integer[]{i, j}))>=1d){
						evolutionary.Data[i][j]=1d;
						evolutionary.Data[j][i]=1d;
					}else{
						evolutionary.Data[i][j]=0d;
						evolutionary.Data[j][i]=0d;
					}

				}
			}

			//Perfect Prediction
			evolutionary.Data[3][18] = evolutionary.Data[4][17] = evolutionary.Data[5][16] = evolutionary.Data[6][15] = evolutionary.Data[7][14]=1d; 
			evolutionary.Data[18][3] = evolutionary.Data[17][4] = evolutionary.Data[16][5] = evolutionary.Data[15][6] = evolutionary.Data[14][7]=1d;

			evolutionary.Data[4][51] = evolutionary.Data[5][52] = evolutionary.Data[6][53] = evolutionary.Data[7][54] = evolutionary.Data[8][55]=1d; 
			evolutionary.Data[51][4] = evolutionary.Data[52][5] = evolutionary.Data[53][6] = evolutionary.Data[54][7] = evolutionary.Data[55][8]=1d;

			evolutionary.Data[46][51] = evolutionary.Data[45][52] = evolutionary.Data[44][53] = evolutionary.Data[43][54] = evolutionary.Data[42][55]=1d; 
			evolutionary.Data[51][46] = evolutionary.Data[52][45] = evolutionary.Data[53][44] = evolutionary.Data[54][43] = evolutionary.Data[55][42]=1d;

			//Neighbors
			evolutionary.Data[3][17] = evolutionary.Data[4][16] = evolutionary.Data[5][15] = evolutionary.Data[6][14] = evolutionary.Data[7][13]=0.75d; 
			evolutionary.Data[17][3] = evolutionary.Data[16][4] = evolutionary.Data[15][5] = evolutionary.Data[14][6] = evolutionary.Data[13][7]=0.75d;
			evolutionary.Data[3][19] = evolutionary.Data[4][18] = evolutionary.Data[5][17] = evolutionary.Data[6][16] = evolutionary.Data[7][15]=0.75d; 
			evolutionary.Data[19][3] = evolutionary.Data[18][4] = evolutionary.Data[17][5] = evolutionary.Data[16][6] = evolutionary.Data[15][7]=0.75d;
			evolutionary.Data[2][18] = evolutionary.Data[8][14] =0.75d; 
			evolutionary.Data[18][2] = evolutionary.Data[14][8] =0.75d;

			evolutionary.Data[4][50] = evolutionary.Data[5][51] = evolutionary.Data[6][52] = evolutionary.Data[7][53] = evolutionary.Data[8][54]=0.75d; 
			evolutionary.Data[50][4] = evolutionary.Data[51][5] = evolutionary.Data[52][6] = evolutionary.Data[53][7] = evolutionary.Data[54][8]=0.75d;
			evolutionary.Data[4][52] = evolutionary.Data[5][53] = evolutionary.Data[6][54] = evolutionary.Data[7][55] = evolutionary.Data[8][56]=0.75d; 
			evolutionary.Data[52][4] = evolutionary.Data[53][5] = evolutionary.Data[54][6] = evolutionary.Data[55][7] = evolutionary.Data[56][8]=0.75d;
			evolutionary.Data[3][51] = evolutionary.Data[9][55] =0.75d; 
			evolutionary.Data[51][3] = evolutionary.Data[55][9] =0.75d;

			evolutionary.Data[46][50] = evolutionary.Data[45][51] = evolutionary.Data[44][52] = evolutionary.Data[43][53] = evolutionary.Data[42][54]=0.75d; 
			evolutionary.Data[50][46] = evolutionary.Data[51][45] = evolutionary.Data[52][44] = evolutionary.Data[53][43] = evolutionary.Data[54][42]=0.75d;
			evolutionary.Data[46][52] = evolutionary.Data[45][53] = evolutionary.Data[44][54] = evolutionary.Data[43][55] = evolutionary.Data[42][56]=0.75d; 
			evolutionary.Data[52][46] = evolutionary.Data[53][45] = evolutionary.Data[54][44] = evolutionary.Data[55][43] = evolutionary.Data[56][42]=0.75d;
			evolutionary.Data[45][51] = evolutionary.Data[43][55] =0.75d;
			evolutionary.Data[51][45] = evolutionary.Data[55][43] =0.75d;


		}else{
		
			int counter = 0;
			String DI_file = params.pathFiles+params.DI_file;
			Map<List<Integer>,Double> Ev_Fold = PDBReader.read_EvFold(DI_file, params.evFoldStart, params.evFoldBase); 
			System.out.println("Analysing Protein: "+params.protein);
			for(int i=0;i<=params.sequence.length()-1;i++){
				for(int j=i+1;j<=params.sequence.length();j++){
					if(Ev_Fold.get(Arrays.asList(new Integer[]{i, j}))!=null && Ev_Fold.get(Arrays.asList(new Integer[]{i, j})) >= (params.ev_threshold)){
						//evolutionary.Data[i][j]=Ev_Fold.get(Arrays.asList(new Integer[]{i, j}));
						//evolutionary.Data[j][i]=Ev_Fold.get(Arrays.asList(new Integer[]{i, j}));
						evolutionary.Data[i][j] = 1d;
						evolutionary.Data[j][i] = 1d;
						counter ++;
					}else{
						evolutionary.Data[i][j]=0d;
						evolutionary.Data[j][i]=0d;
					}

				}
			}
			System.out.println("Counter..."+ counter);
		}
		
		
		
	}
	
	
	public static String create_sequence(String[] alphabet, int length){
		String seq="";
		int random;
		Random rand = new Random();
		for(int i=0;i<length;i++){
			random =  rand.nextInt(20) ;
			seq+=alphabet[random];
		}
		return seq;
	}
	
	public static void create_hash_pairing(){

		for(int i=0;i<params.numStrands-1;i++){
			Hashtable<List<Constants.Dir>, Integer> hash_pairing = new Hashtable<List<Constants.Dir>, Integer>();
			for(int j=0;j<total_pairings.get(i).size();j++){
				hash_pairing.put(total_pairings.get(i).get(j),Integer.valueOf(j+1));
			}
			total_pairings_hash.add(i,hash_pairing);
		}
		
	}
	
	public static List<Integer> adjacents_DFS(List<Integer> node, Template_tfolder parent, int deep, boolean reverse, List<Cluster> clusters, Hashtable<String,String> hierarchy, double[][] evFold){
		//Print.print_template(node);
   		System.out.println("***************          " + node.toString()+"          ***************");
		Template_tfolder temp = new Template_tfolder(node,reverse);
		list_templates.add(temp);
		temp.set_itself(list_templates.size());
		if(parent != null){
			temp.set_parent(parent.get_itself());
		}
		temp.set_Zs(total_pairings_hash.get(node.size()-2).size(),params.numStrands);
		List<List<Integer>> children = new LinkedList<List<Integer>>(); 
		
		//Template_tfolder temp, Template_tfolder parent, List<Cluster> previous, List<Cluster> clusters
		strands(temp, parent, clusters, hierarchy, evFold);
		temp.unset_Z();
		if(node.size()>=deep){
			System.gc();
			return null;
		}else{
			children=offsprings(node);
			for(int i = 0; i < children.size();i++){
				if(i==children.size()-1){
					adjacents_DFS(children.get(i),temp,deep, true, clusters, hierarchy, evFold);
				}else{
					adjacents_DFS(children.get(i),temp,deep, false, clusters, hierarchy, evFold);
				}
			}
			temp.unset_Zs();
			temp = null;
			System.gc();
		}
		return null;
	}
	
	public static void adjacents_BFS(List<List<Integer>> nodes, int deep, List<Cluster> clusters, Hashtable<String,String> hierarchy, double[][] evFold){
		//Print.print_template(node);
		Template_tfolder parent = null;
		boolean reverse = false;
		int aux;
		while(nodes.size()!=0){
			List<Integer> node = nodes.get(0);
			aux = node.get(node.size()-1);
			if(aux == -1){
				reverse = true;
			}else{
				reverse = false;
			}
			node.remove(node.size()-1);
			aux = node.get(node.size()-1);
			if(aux!=-1){
				parent = list_templates.get(aux-1);
			}
			node.remove(node.size()-1);
			System.out.println("***************          " + node.toString()+"          ***************");
			Template_tfolder temp = new Template_tfolder(node,reverse);
			list_templates.add(temp);
			temp.set_itself(list_templates.size());
			if(parent != null){
				temp.set_parent(parent.get_itself());
			}
			temp.set_Zs(total_pairings_hash.get(node.size()-2).size(),params.numStrands);
			List<List<Integer>> children = new LinkedList<List<Integer>>(); 
			strands(temp, parent, clusters, hierarchy, evFold);
			temp.unset_Z();
			if(node.size()>=deep){
				//System.gc();
			}else{
				children=offsprings(node);
				for(int i = 0; i < children.size();i++){
					children.get(i).add(temp.get_itself());
					if(i==children.size()-1){
						children.get(i).add(-1);
						nodes.add(children.get(i));
					}else{
						children.get(i).add(0);
						nodes.add(children.get(i));
					}
				}
			}
			nodes.remove(0);
		}
	}
	
	public static List<List<Integer>> offsprings(List<Integer> parent){
		List<List<Integer>> Perm = new LinkedList<List<Integer>>();
		int value=parent.size()+1;
		int aux;
		for(int j=0;j<=parent.size();j++){
			List<Integer> Permutation = new LinkedList<Integer>();
			for(int i=0;i<parent.size();i++){
				aux = parent.get(i);
				if(aux >= value){
					Permutation.add(i,aux+1);
				}else{
					Permutation.add(i,aux);
				}
			}
			Permutation.add(value);
			Perm.add(Permutation);
			if(value == 2){
				break;
			}
			value--;
		}
		List<Integer> Permutation = new LinkedList<Integer>();
		int cont=0;
		for(int j=parent.size()-1;j>=0;j--){
			Permutation.add(cont,Perm.get(Perm.size()-1).get(j));
			cont++;
		}
		Permutation.add(value);
		Perm.add(Permutation);
		return Perm;
	}
	
	public static List<List<Constants.Dir>> enumeratePairings(int number, int size){
		List<List<Constants.Dir>> pairings = new LinkedList<List<Constants.Dir>>();
		if(number > 0){
			for(Constants.Dir d : Constants.Dir.values()){
				for(List<Constants.Dir> pairing : enumeratePairings(number - 1, size)){
					pairing.add(0,d);
					if (!((pairing.size()==1 && Dir.NONE==pairing.get(0)) || (pairing.size()==size && Dir.NONE==pairing.get(0)) 
							|| (pairing.size()>=2 && pairing.get(0)==Dir.NONE && pairing.get(1)==Dir.NONE))){
							if(pairing.contains(Dir.NONE)){
								pairings.add(0,pairing);
							}else{
								pairings.add(pairing); 
							}
							//if(number==size){
							//	total_pairings_hash.get(size-1).put(pairing,total_pairings_hash.get(size-1).size()+1);
							//}			
					}
				}
			}
		}
		else{
			pairings.add(new LinkedList<Constants.Dir>());

		}
		return pairings;
	}
	public static List<List<List<Constants.Dir>>> allPairings(List<List<Constants.Dir>> pairings, int str){	
		//List<List<List<Constants.Dir>>> total_pairings = new LinkedList<List<List<Constants.Dir>>>();
		List<List<Constants.Dir>> aux = new LinkedList<List<Constants.Dir>>();
		int size, i=0, total=1;
		total_pairings.add(0, pairings);
		size=total_pairings.get(0).size();
		for(int j=0;j<str-1;j++){
			while (i<size){
				if((i%2==0) && (total_pairings.get(0).get(i).get(total_pairings.get(0).get(i).size()-2) != Constants.Dir.NONE)){
					aux.add(total_pairings.get(0).get(i).subList(0,total_pairings.get(0).get(i).size()-1));
					//total_pairings_hash.get(str-2-j).put(total_pairings.get(0).get(i).subList(0,total_pairings.get(0).get(i).size()-1),total);
					//total++;
				}
				i++;
			}
			i=0;
			total=1;
			total_pairings.add(0,aux);
			size = total_pairings.get(0).size();
			aux = null;
			aux = new LinkedList<List<Constants.Dir>>();
		}
		return total_pairings;
	}
	
	public static void clear_areas(int child_pos,int total_children, Template_tfolder temp){
		if(child_pos==total_children-1){
			
		}else{
			if(child_pos!=total_children-2){
				
			}else{
				if(child_pos==0){
					
				}
			}
		}
	}
	
	public static void strands(Template_tfolder temp, Template_tfolder parent, List<Cluster> clusters, Hashtable<String,String> hierarchy, double[][] evFold){
		if(temp.get_template().size()==2){
			strands_window(temp, clusters, hierarchy, evFold);
		}else{
			strands_area(temp, parent, clusters, hierarchy, evFold);
		}
	}
	
	public static void strands_area(Template_tfolder node, Template_tfolder parent, List<Cluster> clusters, Hashtable<String,String> hierarchy, double[][] evFold ){
		int parent_inlaw_template = -1;
		int parent_pairing = -1;
		String query = "", query_aux="";
		Template_tfolder parent_inlaw = null;
		List<Cluster> current_cluster = new LinkedList<Cluster>();
		int new_value = 0;
		boolean border=false, error=true;
		List<List<Hashtable<List<Integer>,List<Double>>>> Tables_parent = null; 
		List<Integer> key;
		Enumeration<List<Integer>> parent_keys = null;
		int size_parent = parent.get_template().size();
		int area = -1, area_aux = -1, position=0;
		List<Integer> temp = new LinkedList<Integer>(node.get_template()); 
		int pos=temp.size();
		List<Dir> new_key;
		List<Integer> query_template_aux;
		List<Dir> query_pair_aux= null;
		// ****************  Preprocessing for the sampling method **************** //
		int []deleted = new int[pos];
		int []deleted_border = new int[pos];
		int []deleted_firstborder = new int[pos];
		int []deleted_right = new int[pos];
		int []deleted_left = new int[pos];
		int [][]neighbors = new int[pos][4];
		int [][]family = null;
		

		
		if(!params.BFS){
			List<Integer> original_template = new LinkedList<Integer>(compute_real_template(node));
			compute_deleted(original_template, deleted);
			compute_deleted_border(original_template, deleted_border);
			compute_deleted_right(original_template, deleted_right);
			compute_deleted_left(original_template,deleted_left);
		}
		

		
		if(salida==false){
		
		double scale = 0d;	
			
		for(int num_pair=0; num_pair < total_pairings_hash.get(pos-2).size(); num_pair++){
			parent_inlaw_template = -1;
			new_key = total_pairings.get(pos-2).get(num_pair);
			

			
			
			
			
			
			
			
			
			
			// ****************  To check if the template is accessible **************** //
			boolean isAccessible = false;
			boolean uncle = false;
			boolean first_border = false;
			boolean reverse = false;
			int none = 0, option=0;
			query="";
			String []found_parent = new String[3];
			int[]ans = new int[4]; 
			
			
			found_parent = finding_parent(hierarchy,node.get_template(),new_key,node.get_reverse()).split("#");
			if(found_parent.length==3){
				query_template_aux = new LinkedList<Integer>(Util.String_template(found_parent[0])); 
				query_pair_aux = new LinkedList<Dir>(Util.String_pair(found_parent[1]));
				query = found_parent[0]+ "#" +found_parent[1];
				option = Integer.valueOf(found_parent[2]);
			}
			ans = finding_option(option,node.get_reverse());
			
			if(ans[0]<1){
				System.out.println("Inaccessible template: "+temp+" "+new_key);
				if(clusterLabelShort(temp,new_key).equals(params.target_topology) || clusterLabelShort(temp,new_key).equals(params.target_topology_reverse)){
					salida = true;
					break;
				}
				continue;
			}	
			
			if(ans[1]==0){
				reverse = false;
			}else{
				reverse = true;
			}
			if(ans[2]==0){
				uncle = false;
			}else{
				uncle = true;
				parent_inlaw_template = Integer.valueOf(hierarchy.get(query).split("#")[0]);
				parent_inlaw = list_templates.get(parent_inlaw_template-1);
			}
			none = ans[3];
			
			
		
			
			
			// ****************  Preprocessing for compute all the possible templates **************** //
			
			int pair = total_pairings_hash.get(pos-3).get(query_pair_aux)-1;
			if(!uncle){	
				new_value = node.get_template().get(node.get_template().size()-1);
				if(new_value>size_parent){
					area=size_parent-1;
					area_aux = area-1;
					border = true;
					parent_keys = parent.get_Zs().get(area).get(pair).get(area-1).keys();
				}else{
					area=new_value-2;
					area_aux = area+1;
					border=false;
					parent_keys = parent.get_Zs().get(area).get(pair).get(area).keys();
				}
				Tables_parent=parent.get_Zs().get(area);
			}else{
				new_value = node.get_template().get(0);
				if(new_value>size_parent){
					area=size_parent-1;
					area_aux = area-1;
					border=true;
					parent_keys = parent_inlaw.get_Zs().get(area).get(pair).get(area-1).keys();
					Tables_parent=parent_inlaw.get_Zs().get(area);
				}else{
					area=new_value-2;
					border=false;
					if(area == -1){
						area = 0;
						first_border = true;
						parent_keys = parent_inlaw.get_Zs().get(pos-1).get(pair).get(area).keys();
						Tables_parent=parent_inlaw.get_Zs().get(pos-1);
					}else{
						parent_keys = parent_inlaw.get_Zs().get(area).get(pair).get(area).keys();
						Tables_parent=parent_inlaw.get_Zs().get(area);
					}
					area_aux = area+1;
				}
				
			}
			parent_pairing = pair;

			
			
			// ****************  Processing all the possible templates **************** //
			while (parent_keys.hasMoreElements()){
				error = false;
				key = parent_keys.nextElement();	
				strands_window_area_final(key, border, node, Tables_parent, area, reverse, new_key, uncle, first_border, none, evFold);
			}
			
			
			
			
			//node.get_Z().get(total_pairings_hash.get(pos-2).get(new_key)-1).clear();
			
			// ****************  To compute the sampling **************** //
			//Here I have to ask if error = false. => It guarantees that there is at least one configuration to sample
			
			pair = total_pairings_hash.get(pos-2).get(new_key)-1;
			node.get_ZSum().add(pair, compute_ZSum(node.get_Z().get(pair), node.get_mapZSum().get(pair),evFold,temp,new_key));
			List<Sample> samples;
			if(params.DSampling){
				samples = Direct_Sampling(node, pair);
			}else{
				if(params.BFS){
					family = new int[temp.size()-2][5];
					Family(hierarchy,node,pair,family);
					List<Integer> original_template = new LinkedList<Integer>(compute_real_template_BFS(node,family));
					compute_deleted(original_template, deleted);
					compute_deleted_border(original_template, deleted_border);
					compute_deleted_firstborder(original_template,deleted_firstborder);
					compute_deleted_right(original_template, deleted_right);
					compute_deleted_left(original_template,deleted_left);
					compute_neighbors(original_template,node.get_template(),neighbors);
					samples = Sampling_BFS(node, pair, deleted, deleted_right, deleted_left, deleted_border, deleted_firstborder, family, evFold, original_template, neighbors);
				}else{
					samples = Sampling(node, pair, deleted, deleted_right, deleted_left, deleted_border);
				}
			}
			
			List<Collection<Sample>> connectedSets = largestConnectedSets(samples, (SampleDistance) params.clustMetric, params.clusterThreshold, params.maxClusters);
			current_cluster = new LinkedList<Cluster>();
			for(Collection<Sample> connectedSet : connectedSets){
				double w = 0;
				for(Sample s : connectedSet){
					w += s.weight;
				}
				Cluster cluster = new Cluster(temp, new_key, connectedSet, w);
				isAccessible = false;
				Iterator<Cluster> pIterator = clusters.iterator();
				while(!isAccessible && pIterator.hasNext()){
					Cluster prev = pIterator.next();
					isAccessible |= areNeighbors(cluster, prev, params.transMetric, params.transitionThreshold);
				}
				if(!isAccessible){
					System.out.println("Inaccessible template + Strands: "+temp+" "+ new_key);
					continue;
				}else{
					System.out.println("Accessed template: "+temp+" "+ new_key + "    "+ w);
				}
				current_cluster.add(cluster);	
			}
			
			if(current_cluster.size()!=0){
				
				// ****************  To compute the  **************** //

				
				if(clusterLabelShort(temp,new_key).equals(params.target_topology) || clusterLabelShort(temp,new_key).equals(params.target_topology_reverse)){
					genealogy(current_cluster.size(),family,clusters);
				}
				double w_topology=0d;
				for(Cluster c: current_cluster){
					w_topology +=c.weight;
				}
				
				hierarchy.put(temp.toString() + "#" + new_key.toString(),node.get_itself()+"#"+ num_pair+"#"+w_topology);
				clusters.addAll(current_cluster);
				// ****************  To clean the memory of Z **************** //
				node.unset_Z(pair);	
				//salida=false;
			}else{
				// ****************  To clean the memory of Z **************** //
				node.unset_Z(pair);
				// ****************  To clean the memory of Zs **************** //
				if(temp.size()<params.numStrands){
					node.unset_Zpartitions(temp.size()+1, pair);
				}
				if(clusterLabelShort(temp,new_key).equals(params.target_topology) || clusterLabelShort(temp,new_key).equals(params.target_topology_reverse)){
					salida = true;
					break;
				}
				continue;
			}
			
			
			


			
			// ****************  To compute all the possible siblings - strands **************** //
			int area_parent;
			if(size_parent+2<=params.numStrands && size_parent > 2){	
				for(int j=0;j<size_parent;j++){
					//Check the value of area_aux, is it the only value that I have to check???????
					if(j!=area_aux && j!=area){
						System.out.println("**************  SIBLINGS  **************");
						if(j<area_aux){
							area_parent = j;
						}else{
							area_parent = j-1;
						}
						if(!uncle){
							parent_keys = parent.get_Zs().get(area).get(parent_pairing).get(area_parent).keys();
						}else{
							if(new_value==1){
								parent_keys = parent_inlaw.get_Zs().get(pos-1).get(parent_pairing).get(area_parent).keys();
							}else{
								parent_keys = parent_inlaw.get_Zs().get(area).get(parent_pairing).get(area_parent).keys();
							}
						}
						while (parent_keys.hasMoreElements()){
							key = parent_keys.nextElement();
							//System.out.println(key.toString());
							//Aca debo mirar que ya no lo haya metido en la tabla, con el cambio de tener los nuevos valores lo puedo hacer ............
							//Revisar si uncle y first_border deben estar tal cual, I am not using first_border
							//Revisar si debo ingresar la variable NONE
							strands_window_area_final_sibling(key, border, node, Tables_parent, area_parent, j, reverse, new_key, uncle, first_border, none, evFold);
						}
					}
				}
			}
		}
		
		
		
		}//Borrarlo

		
		
		
		// ****************  To clean the memory of areas **************** //
		
		//Think in the cleaning for BFS => Idea => We can use the list of parents, once a level of the tree is done, we can delete all the nodes in the tree that we know that it wont be further used
		if(!params.BFS){
			if(!border){
				if(new_value!=2 || node.get_reverse()){
					parent.unset_Zsarea(area,total_pairings_hash.get(size_parent-2).size());
				}
			}
		}

	
	}
	
	public static void strands_window(Template_tfolder temp, List<Cluster> clusters, Hashtable<String,String> hierarchy, double[][] evFold){
		int max_value_i1, max_value_j1, min_value_i1, min_value_j1,  max_value_i2, max_value_j2, min_value_i2, min_value_j2, maxEnd;
		SS.Constants.Environment []enviro = new Constants.Environment[]{Constants.Environment.BURIED, Constants.Environment.EXPOSED};		
		double value, loop=0d;
		boolean baseCase = true;
		List<Dir> pairing;
		List<Integer>key_check;
		List<Integer>template = temp.get_template();
		List<Double> new_value;
		min_value_i1 = 0;
		maxEnd = params.sequence.length();
		max_value_i1 = maxEnd-(2*params.minL+params.minG)+1;
		max_value_i2 = maxEnd-(params.minL)+1;
		List<List<Hashtable<List<Integer>,List<Double>>>> Tables_1 = null;
		List<List<Hashtable<List<Integer>,List<Double>>>> Tables_2 = null;
		List<List<Hashtable<List<Integer>,List<Double>>>> Tables_3 = null;
		if(params.numStrands>2){
			baseCase = false;
			Tables_1 = temp.get_Zs().get(0);
			Tables_2 = temp.get_Zs().get(1);
			Tables_3 = temp.get_Zs().get(2);
		}
		List<Cluster> current_cluster = new LinkedList<Cluster>();
		
		Integer [][]structure = new Integer[2][2];
		
		clusters.add(new Cluster(new ArrayList<Integer>(), new ArrayList<Dir>(), null, 0.001));
		
		for(int pair = 0; pair < total_pairings_hash.get(0).size(); pair ++){
			pairing = total_pairings.get(0).get(pair);
			for(int i1=min_value_i1; i1<max_value_i1; i1++){
				min_value_j1=i1+params.minL-1;
				max_value_j1=Math.min(min_value_j1+(params.maxL-params.minL)+1, maxEnd-(params.minL+params.minG)+1);
				for(int j1=min_value_j1; j1<max_value_j1; j1++){
					min_value_i2 = j1+(params.minG)+1;
					for(int i2=min_value_i2; i2<max_value_i2;i2++){
						min_value_j2=i2+params.minL-1;
						max_value_j2 = Math.min(min_value_j2+(params.maxL-params.minL)+1, maxEnd);
						for(int j2=min_value_j2; j2<max_value_j2;j2++){
							if(params.loops){
								loop = loopEnergy(i1,j1,SS.Constants.Environment.LOOP);
								loop += loopEnergy(i2,j2,SS.Constants.Environment.LOOP);
								loop = params.loopEnergy - loop;
							}
							for(int f=1; f<= enviro.length;f++){
								value = energy_pairings(i1,j1,i2,j2,Util.map_environment(f),total_pairings.get(0).get(pair).get(0));
								
								if(params.loops){
									value+=loop;
								}
								
								//Energy as a contact measure 
								//value = zScore(value,params.temp);
								
								//Energy weighted by the alpha parameter
								structure[0][0] = i1;
								structure[0][1] = j1;
								structure[1][0] = i2;
								structure[1][1] = j2;
								value = (params.alpha * value) + ((1-params.alpha) * (evolutionary_contribution(evFold,ContactDistance.contacts(structure, template, pairing),params.evFold_threshold,value) - 1));
								value = zScore(value,params.temp);
								
								
								
								
								key_check=new LinkedList<Integer>(Util.list(i1,j1,i2,j2));
								new_value= new LinkedList<Double>(Util.list(value,(double)i1,(double)j1,(double)i2,(double)j2));
								check_contains(temp.get_Z().get(pair),key_check,value,new_value);
								if(!baseCase){
									if(i2-j1>allow_space_center){
										check_contains(Tables_1.get(pair).get(0),key_check,value,new_value);
									}	
									if(seq_length-j2>allow_space_border){
										check_contains(Tables_2.get(pair).get(0),key_check,value,new_value);
									}
									if(i1>=allow_space_border){
										check_contains(Tables_3.get(pair).get(0),key_check,value,new_value);
									}
								}
							}
						}
					}
				}
			}
			temp.get_ZSum().add(pair,compute_ZSum(temp.get_Z().get(pair),temp.get_mapZSum().get(pair), evFold, template, pairing));
			List<Sample> samples = Sampling(temp, pair);
			List<Collection<Sample>> connectedSets = largestConnectedSets(samples, (SampleDistance) params.clustMetric, params.clusterThreshold, Math.max(params.maxClusters,params.numStrands));
			current_cluster = new LinkedList<Cluster>();
			for(Collection<Sample> connectedSet : connectedSets){
				double w = 0;
				for(Sample s : connectedSet){
					w += s.weight;
				}
				Cluster cluster = new Cluster(temp.get_template(), total_pairings.get(0).get(pair), connectedSet, w);
				current_cluster.add(cluster);		
			}
			double w_topology=0;
			for(Cluster c: current_cluster){
				w_topology +=c.weight;
			}
			hierarchy.put(temp.get_template().toString()+ "#" +total_pairings.get(0).get(pair).toString(), temp.get_itself()+"#"+pair+"#"+w_topology);
			clusters.addAll(current_cluster);
			//To free memory.....
			temp.unset_Z(pair);
		}

		
	}
	
	public static int strands_window_area_final(List<Integer> key, boolean border, Template_tfolder node, List<List<Hashtable<List<Integer>,List<Double>>>> Tables_parent, int area, boolean reverse, List<Dir> new_key, boolean uncle, boolean first_border, int none, double[][] evFold){
		int min_value_i3, max_value_i3, min_value_j3, max_value_j3, maxEnd, pos=0, pair=0;
		double value=0d, final_value=0d, loop=0d;
		List<Integer> temp = new LinkedList<Integer>(node.get_template()); 
		List<Double> values;
		List<Double> new_value; 
		List<Integer> key_check;
		
		
		Integer [][] structure = new Integer[temp.size()][2];
		int aux;

		pos = 0;
		pos=temp.size();

		//List<Dir> new_key;
		SS.Constants.Environment []enviro = new Constants.Environment[]{Constants.Environment.BURIED, Constants.Environment.EXPOSED};
		maxEnd = maxEnd(key, border, uncle, first_border);
		min_value_i3 = min_value_i3(key, border, uncle, first_border);
		max_value_i3 = max_value_i3(key, border, uncle, first_border);
//		Enumeration<List<Dir>> enumKey = total_pairings_hash.get(pos-2).keys();
//		while(enumKey.hasMoreElements()){
//			new_key = enumKey.nextElement();
			//util.Print.print_interactions(new_key);
			pair = total_pairings_hash.get(pos-2).get(new_key);
			values = previous_pairing(new_key,temp,area,Tables_parent,key,reverse,border,uncle,none);
			if(max_value_i3-min_value_i3<1){
				return 0;
			}
			for(int i3=min_value_i3; i3<max_value_i3; i3++){
				min_value_j3=min_value_j3(i3);
				max_value_j3=max_value_j3(min_value_j3,maxEnd);
				for(int j3=min_value_j3; j3<max_value_j3; j3++){
					if(params.loops){
						loop=loopEnergy(i3,j3,SS.Constants.Environment.LOOP);
					}
					for(int f=1; f<= enviro.length;f++){
						if(!uncle){
							if(reverse){
								value = energy_pairings(values.get(1).intValue(),values.get(2).intValue(),i3,j3,Util.map_environment(f),new_key.get(new_key.size()-1));
							}else{
								value = energy_pairings(values.get(values.size()-2).intValue(),values.get(values.size()-1).intValue(),i3,j3,Util.map_environment(f),new_key.get(new_key.size()-1));
							}
						}else{
								value = energy_pairings(values.get(1).intValue(),values.get(2).intValue(),i3,j3,Util.map_environment(f),new_key.get(0));
						}
						
						
						final_value = energy(values.get(0),params.temp)+value;
						
						if(params.loops){
							final_value = final_value-loop;
						}
						
						//Energy as a contact function
						//final_value = zScore(final_value,params.temp);
						//new_value = prepare_new_value(final_value,values,i3,j3,reverse,uncle);
						
						
						//Energy weighted by the alpha parameter
						new_value = prepare_new_value(final_value,values,i3,j3,reverse,uncle);
						for(int i=1;i<new_value.size();i++){
							aux = new_value.get(i).intValue();
							structure[(i-1)/2][(i+1)%2] = aux;
						}
						final_value = (params.alpha * final_value) + ((1-params.alpha) * (evolutionary_contribution(evFold,ContactDistance.contacts(structure, temp, new_key),params.evFold_threshold,final_value) - 1));
						final_value = zScore(final_value,params.temp);
						new_value.set(0, final_value);
						
						
						
						

						if(border){

							key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(3),i3,j3));
							check_contains(node.get_Z().get(pair-1), key_check, final_value, new_value);
							//key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),key.get(2),j3));
							//new_check_contains(node,0,pair-1,key_check,final_value, new_value);
							if(pos!=params.numStrands){
								if(seq_length-j3>allow_space_border){
									update_areas_border(node, pos-1, pair, area, key, final_value, new_value,i3,j3);
								}
								if(i3-key.get(3)>allow_space_center){
									update_areas_border(node, pos-2, pair, area, key, final_value, new_value,i3,j3);
								}
								if(key.get(2)-key.get(1)>allow_space_center){
									update_areas_border(node, pos-3, pair, area, key, final_value, new_value,i3,j3);
								}
								if(key.get(0)>=allow_space_border){
									update_areas_border(node, pos, pair, area, key, final_value, new_value,i3,j3);
								}
							}

						}else{
							if(!first_border){
								key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),i3,key.get(3)));
								check_contains(node.get_Z().get(pair-1),key_check,final_value,new_value);
								//key_check = new LinkedList<Integer> (Util.list(key.get(0),j3,key.get(2),key.get(3)));
								//new_check_contains(node,area+1,pair-1,key_check,final_value,new_value);				
								if(pos!=params.numStrands){
									if(seq_length-key.get(3)>allow_space_border){
										update_areas_center(node, pos-1, pair, area, key, final_value, new_value,i3,j3);
									}
									if(i3-key.get(1)>allow_space_center){
										update_areas_center(node, area, pair, area, key, final_value, new_value,i3,j3);
									}
									if(key.get(2)-j3>allow_space_center){
										update_areas_center(node, area+1, pair, area ,key, final_value, new_value,i3,j3);
									}
									if(key.get(0)>=allow_space_border){
										update_areas_center(node, pos, pair, area, key, final_value, new_value,i3,j3);
									}
								}
							}else{
								key_check = new LinkedList<Integer>(Util.list(i3,j3,key.get(0),key.get(3)));
								check_contains(node.get_Z().get(pair-1),key_check,final_value,new_value);
								//key_check = new LinkedList<Integer> (Util.list(key.get(0),j3,key.get(2),key.get(3)));
								//new_check_contains(node,area+1,pair-1,key_check,final_value,new_value);				
								if(pos!=params.numStrands){
									if(seq_length-key.get(3)>allow_space_border){
										update_areas_firstborder(node, pos-1, pair, area, key, final_value, new_value,i3,j3);
									}
									if(key.get(2)-key.get(1)>allow_space_center){
										update_areas_firstborder(node, 1, pair, area, key, final_value, new_value,i3,j3);
									}
									if(key.get(0)-j3>allow_space_center){
										update_areas_firstborder(node, 0, pair, area ,key, final_value, new_value,i3,j3);
									}
									if(i3>=allow_space_border){
										update_areas_firstborder(node, pos, pair, area, key, final_value, new_value,i3,j3);
									}
								}
							}
						}
					}
				}
			}
//		}
		return 1;
	}
	
	public static int strands_window_area_final_sibling(List<Integer> key, boolean border, Template_tfolder node, List<List<Hashtable<List<Integer>,List<Double>>>> Tables_parent, int area, int area_sibling, boolean reverse, List<Dir> new_key, boolean uncle, boolean first_border, int none, double[][] evFold){
		int min_value_i3, max_value_i3, min_value_j3, max_value_j3, maxEnd, pos=0, pair;
		int new_strand;
		double value=0d, final_value=0d;
		List<Integer> temp = new LinkedList<Integer>(node.get_template()); 
		List<Double> values = new LinkedList<Double>();
		List<Double> new_value; 
		Integer [][] structure = new Integer[temp.size()][2];
		int aux;
		
		
		
		
		pos = 0;
		pos=temp.size();
		if(!uncle){
			new_strand=temp.get(pos-1);
		}else{
			new_strand=temp.get(0);
		}
		//List<Dir> new_key;
		SS.Constants.Environment []enviro = new Constants.Environment[]{Constants.Environment.BURIED, Constants.Environment.EXPOSED};
		maxEnd = maxEnd_sibling(key, border, area, new_strand, pos, uncle, first_border);
		min_value_i3 = min_value_i3_sibling(key, border, area, new_strand, uncle, first_border); 
		max_value_i3 = max_value_i3_sibling(key, border, area, new_strand, pos, uncle, first_border);
		
		if(min_value_i3>=max_value_i3){
			return 0;
		}
		
		//Enumeration<List<Dir>> enumKey = total_pairings_hash.get(pos-2).keys();
		//while(enumKey.hasMoreElements()){
		//	new_key = enumKey.nextElement();
			//util.Print.print_interactions(new_key);
			pair = total_pairings_hash.get(pos-2).get(new_key);
			
			//previous_pairing and previous_pairing_sibling seem to be the same => Check if I can delete this last function
			values = previous_pairing_sibling(new_key,temp,area,Tables_parent,key,reverse,border,uncle,none);
			for(int i3=min_value_i3; i3<max_value_i3; i3++){
				min_value_j3=min_value_j3_sibling(i3);
				max_value_j3=max_value_j3_sibling(min_value_j3,maxEnd);
				for(int j3=min_value_j3; j3<max_value_j3; j3++){
					
					for(int f=1; f<= enviro.length;f++){
						
						
						if(!uncle){
							if(reverse){
								value = energy_pairings(values.get(1).intValue(),values.get(2).intValue(),i3,j3,Util.map_environment(f),new_key.get(new_key.size()-1));
							}else{
								value = energy_pairings(values.get(values.size()-2).intValue(),values.get(values.size()-1).intValue(),i3,j3,Util.map_environment(f),new_key.get(new_key.size()-1));
							}
						}else{
							value = energy_pairings(values.get(1).intValue(),values.get(2).intValue(),i3,j3,Util.map_environment(f),new_key.get(0));	
						}
						
						
						
						final_value = energy(values.get(0),params.temp)+value;
						
						
						
						//Energy as a contact function
						//final_value = zScore(final_value,params.temp);
						//=> Check if I have to update the new value with uncle, do I have to code a different function?
						//new_value = prepare_new_value(final_value,values,i3,j3,reverse,uncle);
						
						
						
						//Energy weighted by the alpha parameter
						new_value = prepare_new_value(final_value,values,i3,j3,reverse,uncle);
						for(int i=1;i<new_value.size();i++){
							aux = new_value.get(i).intValue();
							structure[(i-1)/2][(i+1)%2] = aux;
						}
						final_value = (params.alpha * final_value) + ((1-params.alpha) * (evolutionary_contribution(evFold,ContactDistance.contacts(structure, temp, new_key),params.evFold_threshold,final_value) + 1));
						final_value = zScore(final_value,params.temp);
						new_value.set(0, final_value);


						
						
						
						
						if(border){
							if(pos-1!=params.numStrands){
								if(seq_length-j3>allow_space_border){
									update_areas_border_sibling(node, pos-1, pair, area_sibling, key, final_value, new_value,i3,j3);
								}
								if(i3-key.get(3)>allow_space_center){
									update_areas_border_sibling(node, pos-2, pair, area_sibling, key, final_value, new_value,i3,j3);
								}							
								if(key.get(2)-key.get(1)>allow_space_center){
									update_areas_border_sibling(node, area_sibling, pair, area_sibling, key, final_value, new_value,i3,j3);
								}
								if(key.get(0)>=allow_space_border){
									update_areas_border_sibling(node, pos, pair, area_sibling, key, final_value, new_value,i3,j3);
								}									
							}
						}else{
							if(!first_border){				
								if(pos-1!=params.numStrands){
									if(seq_length-key.get(3)>allow_space_border){
										update_areas_center_sibling(node, pos-1, pair, area_sibling, key, final_value, new_value,i3,j3,new_strand);
									}

									if(key.get(2)-key.get(1)>allow_space_center){
										update_areas_center_sibling(node, area_sibling, pair, area_sibling, key, final_value, new_value,i3,j3,new_strand);
									}
									
									if(key.get(0)>=allow_space_border){
										update_areas_center_sibling(node, pos, pair, area_sibling, key, final_value, new_value, i3, j3,new_strand);
									}
								}							
							}else{
								if(pos-1!=params.numStrands){
									if(seq_length-key.get(3)>allow_space_border){
										update_areas_firstborder_sibling(node, pos-1, pair, area_sibling, key, final_value, new_value,i3,j3);
									}
									if(key.get(2)-key.get(1)>allow_space_center){
										update_areas_firstborder_sibling(node, area_sibling, pair, area_sibling, key, final_value, new_value,i3,j3);
									}
									if(i3>=allow_space_border){
										update_areas_firstborder_sibling(node, pos, pair, area_sibling, key, final_value, new_value,i3,j3);
									}		

								}
							}
						}
					}
				}
			}
		//}
		return 1;
	}
	
	public static double compute_ZSum(Hashtable<List<Integer>,List<Double>> Zs, NavigableMap<Double, List<Integer>> map, double[][] evFold, List<Integer> template, List<Dir> pairing){
		Enumeration<List<Integer>> partition = Zs.keys();
		double ZSum_value=0d, weight=0d;
		Integer [][] structure = new Integer[template.size()][2];
		List<Double> values;
		List<Integer> key;
		int aux;
		map.put(0d,  new LinkedList<Integer>(Util.list(0,0,0,0)));
		while(partition.hasMoreElements()){
			key = new LinkedList<Integer>(partition.nextElement());
			values = new LinkedList<Double>(Zs.get(key));
			
			/* Normal contact energy
			for(int i=1;i<values.size();i++){
				aux = values.get(i).intValue();
				key.add(aux);
				structure[(i-1)/2][(i+1)%2] = aux;
			}
			
			weight = evolutionary_contribution(evFold,ContactDistance.contacts(structure, template, pairing),0) + 1;
			if(weight >1){
				weight *= Math.pow(10,template.size());
			}*/
			
			//Energy using parameter alpha
			for(int i=1;i<values.size();i++){
				aux = values.get(i).intValue();
				key.add(aux);
			}
			weight = 1;
			ZSum_value += values.get(0) * weight;
			map.put(ZSum_value, key);
		}
		return ZSum_value;
	}
	
	
	public static List<Sample> Sampling(Template_tfolder node ,int pair, int []deleted, int []deleted_right, int []deleted_left, int[]deleted_border){
		double key, weight, ZSum, minZSum, weight_aux;
		int size_template, new_strand, previous_strand, new_area, area, parent, original_size_template, original_parent;
		int []aux; 
		parent = node.get_parent(); 
		original_parent = parent;
		NavigableMap<Double, List<Integer>> map = node.get_mapZSum().get(pair);
		ZSum = node.get_ZSum().get(pair);
		Map.Entry<Double, List<Integer>> values_map;
		List<Sample> Samples = new LinkedList<Sample>();
		List<Integer> new_template = new LinkedList<Integer>(node.get_template()); 
		List<Integer> original_template = new LinkedList<Integer>(new_template);
		size_template = new_template.size();
		original_size_template = size_template;
		List<Dir> new_pairing = new LinkedList<Dir>(total_pairings.get(size_template-2).get(pair));
		List<Dir> original_pairing = new LinkedList<Dir>(new_pairing);
		List<Integer> values;
		int[] previous_area;
		//int []deleted_end = new int[size_template];
		//Send this two functions outside the function..................................
		int i = 0;
		while (i<Math.min(map.size(),params.numSamples)){
			size_template = original_size_template;
			Integer[][] theSample = new Integer[size_template][2];			
			parent = original_parent;
			new_template = original_template;
			new_pairing = original_pairing;
			//minZSum = map.firstKey();
			//key=minZSum+(Math.random() * (ZSum-minZSum));
			key = Math.random() * ZSum;
			values_map=map.ceilingEntry(key);
			values = new LinkedList<Integer>(values_map.getValue());
			weight_aux = map.floorKey(key);
			weight = values_map.getKey()-weight_aux;
			theSample[size_template-1][0] = values.get(4);
			theSample[size_template-1][1] =	values.get(5);
			area=calculate_insertion(new_template.get(size_template-1),size_template-1);
			for(int j=original_size_template-2;j>=1;j--){
				new_strand = new_template.get(size_template-1);
				previous_strand = new_template.get(size_template -2);
				new_area = calculate_insertion(new_strand,size_template-1);
				if(new_area==size_template-2){
					previous_area = get_previous_area(size_template,new_strand,values,new_area-1,area,deleted_right[size_template-1],deleted_left[size_template-1], deleted_border[size_template-1],0,0,null,null);
					area=new_area-1;
				}else{
					previous_area = get_previous_area(size_template,new_strand,values,new_area,area,deleted_right[size_template-1],deleted_left[size_template-1], deleted_border[size_template-1],0,0,null,null);
					area=new_area;
				}
				new_template = new LinkedList<Integer>(list_templates.get(parent-1).get_template());
				new_pairing = new LinkedList<Dir>(calculate_new_pairing(new_pairing));
				size_template = new_template.size();
				values = new LinkedList<Integer>(calculate_new_values(previous_area,new_area,parent, total_pairings_hash.get(size_template-2).get(new_pairing)-1,size_template,deleted[size_template]));
				theSample[j][0] = values.get(4);
				theSample[j][1] = values.get(5);
				parent = list_templates.get(parent-1).get_parent();


			}
			theSample[0][0] = values.get(0);
			theSample[0][1] = values.get(1);
			aux = Samples_contains(Samples,theSample,weight/ZSum);
			if(aux[0] != 1){
				Sample new_sample = new Sample(original_template, original_pairing, theSample, params.sequence.length(), weight, weight/ZSum);
				Samples.add(aux[1],new_sample);
				i++;
			}
		}
		return Samples;
	}
	
	public static void Family(Hashtable<String,String> hierarchy, Template_tfolder node, int pair, int[][] family){
		int parent, size;
		List<Integer> query_template_aux=null;
		List<Dir> query_pair_aux=null;
		String query ="";
		List<Integer> new_template = new LinkedList<Integer>(node.get_template());
		List<Dir> new_pairing = total_pairings.get(new_template.size()-2).get(pair);
 		size = new_template.size()-3;
 		parent = node.get_itself();
		for(int i=size; i>=0; i--){
			query="";
			String []found_parent = new String[3]; 
			int []ans = new int[3];
			found_parent =finding_parent(hierarchy,new_template,new_pairing,list_templates.get(parent-1).get_reverse()).split("#");
			if(found_parent.length==3){
				query_template_aux = new LinkedList<Integer>(Util.String_template(found_parent[0])); 
				query_pair_aux = new LinkedList<Dir>(Util.String_pair(found_parent[1]));
				query = found_parent[0]+ "#" +found_parent[1];
			}
			ans=finding_option(Integer.valueOf(found_parent[2]),list_templates.get(parent-1).get_reverse());
			
			family[i][0] = Integer.valueOf(hierarchy.get(query).split("#")[0]);
			family[i][1] = Integer.valueOf(hierarchy.get(query).split("#")[1]);
			family[i][2] = ans[2];
			family[i][3] = ans[3];
			family[i][4] = Integer.valueOf(found_parent[2]);
			
			parent = family[i][0];
			new_template = new LinkedList<Integer>(query_template_aux);
			new_pairing = new LinkedList<Dir>(query_pair_aux);
		}
	}
		
	public static String finding_parent(Hashtable<String,String> hierarchy, List<Integer> input_template, List<Dir> input_pairing, boolean input_reverse){
		
		List<Integer> query_template_aux;
		List<Dir> query_pair_aux;
		String query_aux="", query="";
		int position, option=0;
		double weight_topology=0d, max_topology=0d;
		query_template_aux = new LinkedList<Integer>(normalizeTemplate(input_template.subList(0, input_template.size()-1)));
		query_pair_aux = new LinkedList<Dir>(input_pairing.subList(0, input_pairing.size()-1));
		
		if(!input_reverse){
			query_aux = query_template_aux.toString() + "#" + query_pair_aux.toString();
			position = query_pair_aux.size()-1;
			if(hierarchy.containsKey(query_aux)){
				weight_topology = Double.valueOf(hierarchy.get(query_aux).split("#")[2]);
				max_topology=weight_topology;
				query=query_aux;
				option = 1;
			}
		}else{
			Collections.reverse(query_template_aux);
			Collections.reverse(query_pair_aux);
			query_aux = query_template_aux.toString() + "#" + query_pair_aux.toString();
			position = 0;
			if(hierarchy.containsKey(query_aux)){
				weight_topology = Double.valueOf(hierarchy.get(query_aux).split("#")[2]);
				max_topology=weight_topology;
				query=query_aux;
				option = 2;
			}
		}
		
		if(query_pair_aux.get(position) == Dir.NONE){
			query_pair_aux.set(position, Dir.PARA);
			query_aux = query_template_aux.toString() + "#" + query_pair_aux.toString();
			if(hierarchy.containsKey(query_aux)){
				weight_topology = Double.valueOf(hierarchy.get(query_aux).split("#")[2]);
				if(weight_topology>max_topology){
					max_topology=weight_topology;
					query=query_aux;
					option = 3;
				}
			}
			query_pair_aux.set(position, Dir.ANTI);
			query_aux = query_template_aux.toString() + "#" + query_pair_aux.toString();
			if(hierarchy.containsKey(query_aux)){
				weight_topology = Double.valueOf(hierarchy.get(query_aux).split("#")[2]);
				if(weight_topology>max_topology){
					max_topology = weight_topology;
					query=query_aux;
					option = 4;
				}
			}
		}
		
		query_template_aux = new LinkedList<Integer>(removeFirst(input_template));
		query_pair_aux = new LinkedList<Dir>(input_pairing.subList(1, input_pairing.size()));
		query_aux = query_template_aux.toString() + "#" + query_pair_aux.toString();
		if(hierarchy.containsKey(query_aux)){
			weight_topology = Double.valueOf(hierarchy.get(query_aux).split("#")[2]);
			if(weight_topology>max_topology){
				max_topology = weight_topology;
				query=query_aux;
				option = 5;
			}
		}
		
		position = 0;
		if(query_pair_aux.get(position) == Dir.NONE){
			query_pair_aux.set(position, Dir.PARA);
			query_aux = query_template_aux.toString() + "#" + query_pair_aux.toString();
			if(hierarchy.containsKey(query_aux)){
				weight_topology = Double.valueOf(hierarchy.get(query_aux).split("#")[2]);
				if(weight_topology>max_topology){
					max_topology = weight_topology;
					query=query_aux;
					option = 6;
				}
			}

			query_pair_aux.set(position, Dir.ANTI);
			query_aux = query_template_aux.toString() + "#" + query_pair_aux.toString();
			if(hierarchy.containsKey(query_aux)){
				weight_topology = Double.valueOf(hierarchy.get(query_aux).split("#")[2]);
				if(weight_topology>max_topology){
					max_topology = weight_topology;
					query=query_aux;
					option = 7;
				}
			}
		}
		
		if(option<5){
			query_template_aux = new LinkedList<Integer>(removeFirst(input_template));
			query_pair_aux = new LinkedList<Dir>(input_pairing.subList(1, input_pairing.size()));
			Collections.reverse(query_template_aux);
			Collections.reverse(query_pair_aux);
			query_aux = query_template_aux.toString() + "#" + query_pair_aux.toString();
			if(hierarchy.containsKey(query_aux)){
				weight_topology = Double.valueOf(hierarchy.get(query_aux).split("#")[2]);
				if(weight_topology>max_topology){
					max_topology = weight_topology;
					query=query_aux;
					option = 8;
				}
			}
			position = query_pair_aux.size()-1;		
			if(query_pair_aux.get(position) == Dir.NONE){
				query_pair_aux.set(position, Dir.PARA);
				query_aux = query_template_aux.toString() + "#" + query_pair_aux.toString();
				if(hierarchy.containsKey(query_aux)){
					weight_topology = Double.valueOf(hierarchy.get(query_aux).split("#")[2]);
					if(weight_topology>max_topology){
						max_topology = weight_topology;
						query=query_aux;
						option = 9;
					}
				}
				query_pair_aux.set(position, Dir.ANTI);
				query_aux = query_template_aux.toString() + "#" + query_pair_aux.toString();
				if(hierarchy.containsKey(query_aux)){
					weight_topology = Double.valueOf(hierarchy.get(query_aux).split("#")[2]);
					if(weight_topology>max_topology){
						max_topology = weight_topology;
						query=query_aux;
						option = 10;
					}
				}

			}
		}
		query += "#" +option;
		return query;
	}
		
	public static int[] finding_option(int option, boolean rev){
		int isAccessible=0, uncle=-1, none=-1, reverse=-1;
		int answer[] = new int[4];
		switch(option){
		case 0:
			isAccessible = 0;
			break;
		case 1:
			uncle = 0;
			none = 0;
			reverse = 0;
			isAccessible = 1;
			break;
		case 2:
			uncle = 0;
			none = 0;
			reverse = 1;
			isAccessible = 1;
			break;
		case 3:
			uncle = 0;
			none = 1;
			if(rev){
				reverse = 1;
			}else{
				reverse = 0;
			}
			isAccessible = 1;
			break;
		case 4:
			uncle = 0;
			none = 2;
			if(rev){
				reverse = 1;
			}else{
				reverse = 0;
			}
			isAccessible = 1;
			break;
		case 5:
			uncle = 1;
			none = 0;
			reverse = 0;
			isAccessible = 1;
			break;
		case 6:
			uncle = 1;
			none = 1;
			reverse = 0;
			isAccessible = 1;
			break;
		case 7:
			uncle = 1;
			none = 2;
			reverse = 0;
			isAccessible = 1;
			break;
		case 8:
			uncle = 1;
			none = 0;
			reverse = 1;
			isAccessible = 1;
			break;
		case 9:
			uncle = 1;
			none = 1;
			reverse = 1;
			isAccessible = 1;
			break;
		case 10:
			uncle = 1;
			none =2;
			reverse = 1;
			isAccessible = 1;
			break;
		default:
			System.out.println("ERROR finding parents");
			isAccessible = 0;
			break;
		}
		answer[0] = isAccessible;
		answer[1] = reverse;
		answer[2] = uncle;
		answer[3] = none;
		return answer;
	}
	
	public static List<Sample> Sampling_BFS(Template_tfolder node ,int pair, int []deleted, int []deleted_right, int []deleted_left, int[]deleted_border, int[]deleted_firstborder, int family[][], double[][] evFold, List<Integer> real_template, int[][] neighbors){
		double key, weight, ZSum, minZSum, weight_aux;
		int size_template, new_strand, new_area, area, parent, original_size_template, original_parent, previous_node, index, limit;
		int []aux;
		double []aux_prob= new double[2];
		NavigableMap<Double, List<Integer>> map = node.get_mapZSum().get(pair);
		ZSum = node.get_ZSum().get(pair);
		Map.Entry<Double, List<Integer>> values_map;
		List<Sample> Samples = new LinkedList<Sample>();
		List<Integer> new_template = new LinkedList<Integer>(node.get_template()); 
		List<Integer> original_template = new LinkedList<Integer>(new_template);
		size_template = new_template.size();
		original_size_template = size_template;
		previous_node = node.get_itself();
		List<Dir> new_pairing = new LinkedList<Dir>(total_pairings.get(size_template-2).get(pair));
		List<Dir> original_pairing = new LinkedList<Dir>(new_pairing);
		List<Integer> values;
		int[] previous_area;
		Random rand = new Random(System.currentTimeMillis());

		parent = family[size_template-3][0]; 
		original_parent = parent;
		
		
		//int []deleted_end = new int[size_template];
		//Send this two functions outside the function..................................
		int i = 0;
		int cnt=0;
		while (i<=Math.min(map.size()/4,params.numSamples)&& map.size()>0){
			previous_node = node.get_itself();
			weight = 1d;

		
			size_template = original_size_template;
			Integer[][] theSample = new Integer[size_template][2];			
			parent = original_parent;
			new_template = original_template;
			new_pairing = original_pairing;
			//minZSum = map.firstKey();
			//key=minZSum+(Math.random() * (ZSum-minZSum));
			//key = Math.random() * ZSum;
			key = rand.nextDouble() * ZSum;
			values_map=map.ceilingEntry(key);
			values = new LinkedList<Integer>(values_map.getValue());
			weight_aux = map.floorKey(key);
			weight *= (values_map.getKey()-weight_aux)/ZSum;
			
			index = original_template.indexOf(real_template.get(original_size_template-1));
			if(family[size_template-3][2]==0){
				theSample[index][0] = values.get(values.size()-2);
				theSample[index][1] =	values.get(values.size()-1);
				area=calculate_insertion(new_template.get(size_template-1),size_template-1);
			}else{
				theSample[index][0] = values.get(4);
				theSample[index][1] = values.get(5);
				area=calculate_insertion(new_template.get(0),size_template-1);
			}
			
			for(int j=original_size_template-2;j>=1;j--){
				if(family[size_template-3][2]==0){
					new_strand = new_template.get(size_template-1);
				}else{
					new_strand = new_template.get(0); 
				}
				new_area = calculate_insertion(new_strand,size_template-1);
				if(new_area==size_template-2){
					previous_area = get_previous_area(size_template,new_strand,values,new_area-1,area,deleted_right[size_template-1],deleted_left[size_template-1], deleted_border[size_template-1], deleted_firstborder[size_template-1],family[size_template-3][2],theSample, neighbors[j+1]);
					area=new_area-1;
				}else{
					previous_area = get_previous_area(size_template,new_strand,values,new_area,area,deleted_right[size_template-1],deleted_left[size_template-1], deleted_border[size_template-1],deleted_firstborder[size_template-1],family[size_template-3][2],theSample, neighbors[j+1]);
					area=new_area;
				}

				new_template = new LinkedList<Integer>(list_templates.get(parent-1).get_template());
				new_pairing = new LinkedList<Dir>(total_pairings.get(size_template-3).get(family[size_template-3][1]));
				size_template = new_template.size();
				values = new LinkedList<Integer>(calculate_new_values_BFS(previous_area,new_area,parent, total_pairings_hash.get(size_template-2).get(new_pairing)-1,size_template,deleted[size_template],family,new_strand,family[size_template-2][3],list_templates.get(previous_node-1).get_reverse(),aux_prob, evFold,theSample, neighbors[j+1]));
				weight *= aux_prob[0]/aux_prob[1];
				
	
				
				
				index = original_template.indexOf(real_template.get(j));
				theSample[index][0] = values.get(values.size()-2);
				theSample[index][1] = values.get(values.size()-1);
			
				
				if(size_template-3>=0){
					previous_node = parent;
					parent = family[size_template-3][0];
				}	
			}
			
			index = original_template.indexOf(real_template.get(0));	
			theSample[index][0] = values.get(0);
			theSample[index][1] = values.get(1);
			
			aux = Samples_contains(Samples,theSample,weight);
			if(aux[0] != 1){
				Sample new_sample = new Sample(original_template, original_pairing, theSample, params.sequence.length(), weight, weight/ZSum);
				Samples.add(aux[1],new_sample);
				i++;
				cnt=0;
			}else{
				cnt++;
				if(cnt>Math.min(map.size()/4,params.numSamples)){
					break;
				}
			}
		}
		return Samples;
	}
	
	public static List<Sample> Direct_Sampling(Template_tfolder node ,int pair){
		double key, weight, ZSum, minZSum, weight_aux;
		int size_template;
		int []aux; 

		NavigableMap<Double, List<Integer>> map = node.get_mapZSum().get(pair);
		ZSum = node.get_ZSum().get(pair);
		Map.Entry<Double, List<Integer>> values_map;
		List<Sample> Samples = new LinkedList<Sample>();
		List<Integer> new_template = new LinkedList<Integer>(node.get_template()); 
		size_template = new_template.size();
		List<Dir> new_pairing = new LinkedList<Dir>(total_pairings.get(size_template-2).get(pair));
		List<Integer> values;
		//minZSum = map.firstKey();
		int i = 0;
		while (i<Math.min(map.size(),params.numSamples)){
			Integer[][] theSample = new Integer[size_template][2];
			//key=minZSum+(Math.random() * (ZSum-minZSum));
			key=(Math.random() * ZSum);
			values_map=map.ceilingEntry(key);
			values = new LinkedList<Integer>(values_map.getValue());
			weight_aux = map.floorKey(key);
			weight = values_map.getKey()-weight_aux;
			for(int j=0; j< size_template;j++){
				theSample[j][0] = values.get(4+(2*j));
				theSample[j][1] = values.get(4+(2*j)+1);
			}
			aux = Samples_contains(Samples,theSample,weight/ZSum);
			if(aux[0] != 1){
				Sample new_sample = new Sample(new_template, new_pairing, theSample, params.sequence.length(), weight/ZSum, weight/ZSum);
				Samples.add(aux[1],new_sample);
				i++;
			}
		}
		return Samples;
	}
	
	public static List<Sample> Sampling(Template_tfolder node ,int pair){
		double key, ZSum =0d, weight, weight_aux;
		int []aux;
		int i=0; 
		int limit;
		Random rand = new Random(System.currentTimeMillis());
		Map.Entry<Double, List<Integer>> values_map;
		List<Sample> Samples = new LinkedList<Sample>();
		List<Integer> values;
		NavigableMap<Double, List<Integer>> map = node.get_mapZSum().get(pair);
		ZSum = node.get_ZSum().get(pair);
		List<Dir> new_pairing = new LinkedList<Dir>(total_pairings.get(0).get(pair));
		List<Integer> new_template = new LinkedList<Integer>(node.get_template()); 
		int cnt=0;
		while (i<=Math.min(map.size()/4, params.numSamples) && map.size()>0){
			Integer[][] theSample = new Integer[2][2];	
			//key= Math.random() * ZSum;
			key = rand.nextDouble() * ZSum;
			values_map=map.ceilingEntry(key);
			weight_aux = map.floorKey(key);
			weight = (values_map.getKey()-weight_aux)/ZSum;
			values = new LinkedList<Integer>(values_map.getValue());
			theSample[0][0] = values.get(0);
			theSample[0][1]	= values.get(1);	
			theSample[1][0] = values.get(2);
			theSample[1][1] =	values.get(3);
			aux = Samples_contains(Samples,theSample,weight);
			if(aux[0] != 1){ 
				Sample new_sample = new Sample(new_template, new_pairing, theSample, params.sequence.length(), weight, weight/ZSum);
				Samples.add(aux[1],new_sample);
				i++;
				cnt=0;
			}else{
				cnt++;
				if(cnt>Math.min(map.size()/4, params.numSamples)){
					break;
				}
			}
		}
		return Samples;
	}
	
	public static int[] Samples_contains(List<Sample> Samples, Integer[][] template, double prob){
		int []resp = new int [2];
		int i=0;
		resp[0]=0;
		resp[1]=0;
		for(Sample sam:Samples){
			resp[0]=1;
			for(int j =0; j< Math.min(sam.length, template.length); j++){
				if(template[j][0]!=sam.structure[j][0] || template[j][1]!=sam.structure[j][1] ){
					resp[0]=0;
					break;
				}
			}
			if(resp[0]==0){
				if(sam.probability > prob){
					resp[1] = i+1;
				}
			}else{
				break;
			}
			i++;
		}
		return resp;
	}
	
	public static List<Integer> compute_real_template(Template_tfolder node){
		List<Integer> real_template = new LinkedList<Integer>(node.get_template());
		int original_size, current_template;
		original_size=real_template.size();
		boolean []reverse = new boolean[original_size];
		reverse[0]=reverse[1]=false;
		current_template = node.get_itself();
		for(int i=original_size-1;i>=2;i--){
			if(list_templates.get(current_template-1).get_reverse()){
				reverse[i]=true;
			}else{
				reverse[i]=false;
			}
			current_template= list_templates.get(current_template-1).get_parent();
			if(reverse[i]){
				Collections.reverse(real_template.subList(0, i));
			}
		}
		return real_template;
	}
	
	
	public static List<Integer> compute_real_template_BFS(Template_tfolder node, int[][] family){
		List<Integer> real_template = new LinkedList<Integer>(node.get_template());
		int original_size, current_template, aux;
		original_size=real_template.size();
		boolean []reverse = new boolean[original_size];
		reverse[0]=reverse[1]=false;
		current_template = node.get_itself();
		for(int i=original_size-1;i>=2;i--){
			if(list_templates.get(current_template-1).get_reverse()){
				reverse[i]=true;
			}else{
				reverse[i]=false;
			}
			current_template= family[i-2][0];
			if(family[i-2][2]>0){
				aux=real_template.get(0);
				for(int j=0;j<i;j++){
					real_template.set(j, real_template.get(j+1));
				}
				real_template.set(i, aux);
				if(family[i-2][4]<8){
					reverse[i]=false;
				}else{
					reverse[i]=true;
				}
			}
			if(reverse[i]){
				Collections.reverse(real_template.subList(0, i));
			}
		}
		return real_template;
	}
	
	public static double evolutionary_contribution(double[][] evFold_contact, Set<Set<Integer>> eFold_contact, double threshold, double scale){
	int i1=0, j1=0, mod;
	double contribution = 0d;
		for(Set<Integer> set_ite:eFold_contact){
			mod=0;
			for(Integer opt: set_ite){
				if(mod==0){
					i1=opt;
				}else{
					j1=opt;
				}
				mod++;
			}
			if(evFold_contact[i1+1][j1+1]>threshold){
				contribution+=evFold_contact[i1+1][j1+1];
			}
		}
		return (contribution*scale)/eFold_contact.size();
	}
	
	public static void compute_deleted_border(List<Integer> template, int[] deleted_border){
		int size = template.size();
		int i, k, max_less=0, value_i, value_k;
		boolean border=true;
		deleted_border[0]=0;
		deleted_border[size-1]=0;
		for(i=1;i<=size-2;i++){
			max_less=0;
			value_i=template.get(i);
			for(k=0;k<=i-1;k++){
				value_k = template.get(k);
				if(value_k<value_i && value_k>max_less){
					max_less = value_k;
				}
				if(value_k>value_i){
					border=false;
					break;
				}
			}

			if(border){
				deleted_border[i]=value_i-max_less-1;
			}else{
				deleted_border[i]=0;
			}
		}	
	}
	
	public static void compute_deleted_firstborder(List<Integer> template, int[] deleted_border){
		int size = template.size();
		int i, k, min_greater=0, value_i, value_k;
		boolean border=true;
		deleted_border[0]=0;
		deleted_border[size-1]=0;
		for(i=1;i<=size-2;i++){
			value_i=template.get(i);
			min_greater=500;
			for(k=0;k<=i-1;k++){
				value_k=template.get(k);
				if(value_k>value_i && value_k<min_greater){
					min_greater = value_k;
				}
				if(value_k<value_i){
					border=false;
					break;
				}
			}

			if(border){
				deleted_border[i]=min_greater-value_i-1;
			}else{
				deleted_border[i]=0;
			}
		}	
	}
	
	public static void compute_deleted(List<Integer> template, int[] deleted){
		int size = template.size();
		int i, j, k, cont, max_less=0, min_greater=500, max_less_aux=0, min_greater_aux=500, value_k, value_i;
		boolean flag = false;
		deleted[0]=0;
		deleted[size-1]=0;
		deleted[1]=0;
		for(i=2;i<=size-2;i++){
			flag = false;
			cont=0;
			min_greater=500;
			min_greater_aux=500;
			max_less=0;
			max_less_aux=0;
			value_i=template.get(i);
			for(k=0;k<=i-1;k++){
				value_k = template.get(k);
				if(value_k<value_i){
					if(value_k>max_less){
						max_less_aux = max_less;
						max_less = value_k;
					}else{
						if(value_k>max_less_aux){
							max_less_aux =value_k;
						}
					}
				}
				
				
				/*
				if(template.get(k)<template.get(i) && template.get(k)>max_less_aux && template.get(k)<max_less){//Check example 1 4 3 5 2, it is necessary to recognize the strand 3.
					max_less_aux = template.get(k);
				}*/
				
				if(value_k>value_i){
					if(value_k<min_greater){
						min_greater_aux = min_greater;
						min_greater = value_k;
					}else{
						if(value_k<min_greater_aux){
							min_greater_aux = value_k;
						}
					}
				}
			}

			
			
			if(min_greater>=500){
				min_greater=max_less;
				max_less=max_less_aux;
				flag = true;
				//min_greater = template.get(i);
			}
			
			if(max_less==0 && !flag){
				max_less = min_greater;
				min_greater = min_greater_aux;
			}
				
			for(j=i+1;j<=size-1;j++){
				//if(template.get(i)>template.get(j) && template.get(j)>max_less && template.get(j)<min_greater){
				if(template.get(j)>max_less && template.get(j)<min_greater){
					cont++;
				}
			}
			deleted[i]=cont;
		}	
	}
	
	
	public static void compute_deleted_right(List<Integer> template, int[] deleted_right){
		int size = template.size();
		int i, j, k, cont, cont_aux, max_less=0, min =500, value_k, value_i, value_j;
		deleted_right[0]=0;
		deleted_right[size-1]=0;
		for(i=1;i<=size-2;i++){
			value_i=template.get(i);
			cont=0;
			cont_aux=0;
			max_less=0;
			min=500;
			for(k=0;k<=i-1;k++){
				value_k=template.get(k);
				if(value_k<value_i && value_k>max_less){
					max_less = value_k;
				}
				if(value_k<min){
					min = value_k;
				}
			}
			for(j=i+1;j<=size-1;j++){
				value_j=template.get(j);
				if(value_i>value_j && value_j>min){
					cont++;
					if(value_j>max_less){
						cont_aux++;
					}
				}
			}
			if(cont_aux==value_i-max_less-1 && cont_aux!=0){
				deleted_right[i]=0;
			}else{
				deleted_right[i]=cont-cont_aux;
			}
		}
	}
	
	public static void compute_deleted_left(List<Integer> template, int[] deleted_left){
		int size = template.size(), value_i, value_j, value_k;
		int i, j, k, cont, max=0, min_greater=500, min_greater_aux = 500, min = 500;
		deleted_left[0]=0;
		deleted_left[size-1]=0;
		for(i=1;i<=size-2;i++){
			value_i=template.get(i);
			cont=0;
			max=0;
			min=500;
			min_greater=500;
			min_greater_aux=500;
			for(k=0;k<=i-1;k++){
				value_k=template.get(k);
				if(value_k>value_i && value_k>max){
					max = value_k;
				}
				if(value_k<min){
					min = value_k;
				}
				if(value_k>value_i){
					if(value_k<min_greater){
						min_greater = value_k;
					}else{
						if(value_k<min_greater_aux){
							min_greater_aux = value_k;
						}
					}
				}
			}
			if(min_greater == min){
				min_greater = min_greater_aux;
			}
			for(j=i+1;j<=size-1;j++){
				value_j=template.get(j);
				if(value_i<value_j && value_j<max && value_j>min_greater){
					cont++;
				}
			}
			if(cont==max-value_i-1){
				deleted_left[i]=0;
			}else{
				deleted_left[i]=cont;
			}
		}
	}
	
	public static void compute_neighbors(List<Integer> real_template, List<Integer> node_template, int[][] neighbors){
		int i, j, k, min_greater_before=500, max_less_before=0, min_less_after=500, max_greater_after=0, value_i, value_j, value_k, left=0, right=0;
		int size=node_template.size();
		neighbors [0][0] = -1;
		neighbors [0][1] = -1;
		neighbors [1][0] = -1;
		neighbors [1][1] = -1;
		neighbors [size-1][0] = -1;
		neighbors [size-1][1] = -1;
		for(i=2;i<=size-2;i++){
			value_i=real_template.get(i);
			min_greater_before=500;
			max_less_before=0;
			min_less_after=500;
			max_greater_after=0;
			left=0;
			right=0;
			for(k=0;k<=i-1;k++){
				value_k=real_template.get(k);
				if(value_k>value_i && value_k<min_greater_before){
					min_greater_before = value_k;
				}
				if(value_k<value_i && value_k>max_less_before){
					max_less_before = value_k;
				}
				if(value_k>value_i){
					right++;
				}else{
					left++;
				}
			}	
			for(j=i+1;j<=size-1;j++){
				value_j=real_template.get(j);
				if(value_j>value_i && value_j>max_greater_after && value_j<min_greater_before){
					max_greater_after=value_j;
				}
				if(value_j<value_i && value_j<min_less_after && value_j>max_less_before){
					min_less_after=value_j;
				}
			}
			if(min_less_after!=500){
				neighbors[i][0]=node_template.indexOf(min_less_after);
			}else{
				neighbors[i][0]=-1;
			}
			
			if(max_greater_after!=0){
				neighbors[i][1]=node_template.indexOf(max_greater_after);
			}else{
				neighbors[i][1]=-1;
			}
			neighbors[i][2]=left;
			neighbors[i][3]=right;
		}
	}
	
	public static List<Dir> calculate_new_pairing(List<Dir> pairing){
		List<Dir> new_pairing = new LinkedList<Dir>(pairing.subList(0, pairing.size()-1));
		if(new_pairing.get(new_pairing.size()-1).equals(Dir.NONE)){
			new_pairing.set(new_pairing.size()-1, Dir.PARA);
		}
		return new_pairing;
		//missing the function to check if the last value is 'NONE'	
	}
	
	public static List<Dir> calculate_new_pairing_sampling(List<Dir> pairing, boolean uncle){
		List<Dir> new_pairing;
		if(uncle){
			new_pairing = new LinkedList<Dir>(pairing.subList(1, pairing.size()));
			if(new_pairing.get(0).equals(Dir.NONE)){
				new_pairing.set(0, Dir.PARA);
			}
		}else{
			new_pairing = new LinkedList<Dir>(pairing.subList(0, pairing.size()-1));
			if(new_pairing.get(new_pairing.size()-1).equals(Dir.NONE)){
				new_pairing.set(new_pairing.size()-1, Dir.PARA);
			}
		}

		return new_pairing;
		//missing the function to check if the last value is 'NONE'	
	}
	
	public static List<Integer> calculate_new_values(int[] previous_area, int area, int parent, int pairing, int size_template, int deleted){
		int max_left_gap, min_right_gap, min_distance;
		double ZSum_value=0d, key_map;
		boolean border;
		Template_tfolder parent_node = list_templates.get(parent-1);
		Hashtable<List<Integer>,List<Double>> Tables_parent;
		if(area==size_template-1){
			border = true;
			Tables_parent = parent_node.get_Zs().get(area).get(pairing).get(area-1);
		}else{
			border = false;
			Tables_parent = parent_node.get_Zs().get(area).get(pairing).get(area);
		}
		NavigableMap<Double, List<Integer>> map = new TreeMap<Double,List<Integer>>();
		List<Double> values;
		List<Integer> key;
		
		if(border){
		min_distance= (deleted*(params.minG+params.minL))+params.minG;	
			if(area==1){
				max_left_gap = previous_area[0]+params.maxL-1;
			}else{
				max_left_gap = previous_area[2]-min_distance;
			}
				min_right_gap = previous_area[3]-params.maxL+1;
		}else{
			min_distance=(deleted*(params.minG+params.minL))+(2*params.minG+params.minL);
			if(area==0){
				max_left_gap = previous_area[0]+params.maxL-1;
			}else{
				max_left_gap = previous_area[2]-min_distance;
			}
			if(area==size_template-2){
				min_right_gap = previous_area[3]-params.maxL+1;
			}else{
				min_right_gap = previous_area[1]+min_distance;
			}	
		}
		for(int i=previous_area[1]-1;i<=max_left_gap;i++){
			for(int j=min_right_gap;j<=previous_area[2]+1;j++){
				if(((j-i)<=min_distance)){
					continue;
				}
				key = new LinkedList<Integer>(Util.list(previous_area[0],i,j,previous_area[3]));
				values = new LinkedList<Double>(Tables_parent.get(key));
				key.add(values.get(3).intValue());
				key.add(values.get(4).intValue());
				ZSum_value += values.get(0);
				map.put(ZSum_value, key);		
			}
		}
		key_map=map.firstKey()+(Math.random() * (ZSum_value-map.firstKey()));
		return map.ceilingEntry(key_map).getValue();
	}
	
	public static List<Integer> calculate_new_values_BFS(int[] previous_area, int area, int parent, int pairing, int size_template, int deleted, int[][] family, int initial_border,int none, boolean reverse, double[] aux_prob, double[][] evFold, Integer[][] theSample, int[] neighbors){
		
		int max_left_gap, min_right_gap, min_distance;
		double ZSum_value=0d, key_map, aux_energy=0d, final_value=0d, weight=0d;
		int uncle, parent_uncle, new_strand, value;

		
		
		uncle = family[size_template-2][2];
		if(size_template-3>=0){
			parent_uncle = family[size_template-3][2];
		}else{
			parent_uncle = 0;
		}

				
		
		
		
		boolean border;
		Dir aux_none = null;
		Template_tfolder parent_node = list_templates.get(parent-1);
		List<Integer> template = new LinkedList<Integer>(parent_node.get_template());
		List<Dir> pair = new LinkedList<Dir>(total_pairings.get(size_template-2).get(pairing));
		Hashtable<List<Integer>,List<Double>> Tables_parent;
		Random rand = new Random(System.currentTimeMillis());
		//Integer [][] structure = new Integer[template.size()][2];
		
		
		if(uncle==0){
			if(area==size_template-1){
				border = true;
				Tables_parent = parent_node.get_Zs().get(area).get(pairing).get(area-1);
			}else{
				border = false;
				Tables_parent = parent_node.get_Zs().get(area).get(pairing).get(area);
			}
		}else{
			if(area==size_template-1){
				border = true;
				Tables_parent = parent_node.get_Zs().get(area).get(pairing).get(area-1);
			}else{
				border = false;
				if(initial_border==1){
					Tables_parent = parent_node.get_Zs().get(size_template).get(pairing).get(area);
				}else{
					Tables_parent = parent_node.get_Zs().get(area).get(pairing).get(area);
				}
			}
		}

		
		NavigableMap<Double, List<Integer>> map = new TreeMap<Double,List<Integer>>();
		List<Double> values;
		List<Integer> key;
		
		
		
		if(uncle == 0){
			if(border){
				min_distance= (deleted*(params.minG+params.minL))+params.minG;	
				if(area==1){
					max_left_gap = previous_area[0]+params.maxL-1;
				}else{
					max_left_gap = previous_area[2]-min_distance;
				}
				min_right_gap = previous_area[3]-params.maxL+1;
			}else{
				min_distance=(deleted*(params.minG+params.minL))+(2*params.minG+params.minL);
				if(area==0){
					max_left_gap = previous_area[0]+params.maxL-1;
				}else{
					max_left_gap = previous_area[2]-min_distance;
					if(neighbors[0]!=-1){
						max_left_gap = Math.min(max_left_gap, theSample[neighbors[0]][0]-params.minG-1);
						value = (neighbors[2]*params.minL) + ((neighbors[2]-1)*params.minG)-1;
						if(max_left_gap<previous_area[1]-1 && (previous_area[1]-previous_area[0])>value){
							previous_area[1]=max_left_gap+1;
						}
					}	
				}
				if(area==size_template-2){
					min_right_gap = previous_area[3]-params.maxL+1;
				}else{
					min_right_gap = previous_area[1]+min_distance;
					if(neighbors[1]!=-1){
						min_right_gap=Math.max(min_right_gap, theSample[neighbors[1]][1]+params.minG+1);
						value = (neighbors[3]*params.minL) + ((neighbors[3]-1)*params.minG)-1;
						if(previous_area[2]+1<min_right_gap && (previous_area[3]-previous_area[2])>value){
							previous_area[2]=min_right_gap-1;
						}
					}
				}	
			}
		
	
			
			
		}else{
			if(initial_border==1){
				min_distance= (deleted*(params.minG+params.minL))+params.minG;
				max_left_gap = previous_area[0]+params.maxL-1;
				if(area==size_template-2){
					min_right_gap = previous_area[3]-params.maxL+1;
					min_distance = params.minG;
				}else{
					min_right_gap = previous_area[1]+min_distance;
				}
			}else{
				if(border){
					min_distance= (deleted*(params.minG+params.minL))+params.minG;	
					if(area==1){
						max_left_gap = previous_area[0]+params.maxL-1;
					}else{
						max_left_gap = previous_area[2]-min_distance;
					}
					min_right_gap = previous_area[3]-params.maxL+1;
				}else{
					min_distance=(deleted*(params.minG+params.minL))+(2*params.minG+params.minL);
					if(area==0){
						max_left_gap = previous_area[0]+params.maxL-1;
					}else{
						max_left_gap = previous_area[2]-min_distance;
						if(neighbors[0]!=-1){
							max_left_gap = Math.min(max_left_gap, theSample[neighbors[0]][0]-params.minG-1);
							value = (neighbors[2]*params.minL) + ((neighbors[2]-1)*params.minG)-1;
							if((max_left_gap<previous_area[1]-1) && ((previous_area[1]-previous_area[0])>value)){
								previous_area[1]=max_left_gap+1;
							}
						}	
					}
					if(area==size_template-2){
						min_right_gap = previous_area[3]-params.maxL+1;
					}else{
						min_right_gap = previous_area[1]+min_distance;
						value = (neighbors[3]*params.minL) + ((neighbors[3]-1)*params.minG)-1;
						if(previous_area[2]+1<min_right_gap && (previous_area[3]-previous_area[2])>value){
							previous_area[2]=min_right_gap-1;
						}
					}	
				}
			}
		}
		
		if(none>0){
			if(none == 1){
				aux_none = Dir.PARA;
			}else{
				aux_none = Dir.ANTI;
			}
		}
		
		
		for(int i=previous_area[1]-1;i<=max_left_gap;i++){
			for(int j=min_right_gap;j<=previous_area[2]+1;j++){
				if(((j-i)<=min_distance)){
					continue;
				}
				
				//Check if none will also work with uncle...........
				key = new LinkedList<Integer>(Util.list(previous_area[0],i,j,previous_area[3]));
				if(Tables_parent.containsKey(key)){
					values = new LinkedList<Double>(Tables_parent.get(key));
				}else{
					continue;
				}
				
				
				
				/* Normal contact energy ....
				for(int k=1;k<values.size();k++){
					int aux = values.get(k).intValue();
					structure[((k-1)/2)][(k+1)%2] = aux;
				}
				weight = evolutionary_contribution(evFold,ContactDistance.contacts(structure, template, pair),0) + 1;
				if(weight >1){
					weight *= Math.pow(10,template.size()+1);
				}
				*/
				
				//Using the variable alpha
				weight = 1;
				
				
				if(none>0){
					if(reverse || uncle > 0){
						aux_energy = energy_pairings(values.get(1).intValue(), values.get(2).intValue(),values.get(3).intValue(), values.get(4).intValue(), Util.map_environment(1), aux_none);
					}else{
						aux_energy = energy_pairings(values.get(values.size()-4).intValue(), values.get(values.size()-3).intValue(), values.get(values.size()-2).intValue(), values.get(values.size()-1).intValue(), Util.map_environment(1), aux_none);
					}
					final_value = energy(values.get(0),params.temp)-aux_energy;
					final_value = zScore(final_value,params.temp);
					ZSum_value += final_value;
				}else{
					ZSum_value += values.get(0);
				}
				
				

				if(parent_uncle==0){
					key.add(values.get(values.size()-2).intValue());
					key.add(values.get(values.size()-1).intValue());
					map.put(ZSum_value, key);
				}else{
					key.add(values.get(1).intValue());
					key.add(values.get(2).intValue());
					map.put(ZSum_value, key);
				}
			}
		}
		//key_map=map.firstKey()+(Math.random() * (ZSum_value-map.firstKey()));
		key_map = rand.nextDouble() * ZSum_value;				
		aux_prob[0] = map.ceilingEntry(key_map).getKey();
		aux_prob[1] = ZSum_value;
		//aux_prob[1] = list_templates.get(parent-1).get_ZSum().get(pairing);
		return map.ceilingEntry(key_map).getValue();
	}
	
	public static boolean check_previous_insertions(Integer[][] theSample, int value_i, int value_j, int[] neighbors){
		
		if(neighbors[0]!=-1){
			if(theSample[neighbors[0]][1]+params.minG>=value_j){
				return false;
			}
		}
		if(neighbors[1]!=-1){
			if(value_j+params.minG>=theSample[neighbors[1]][0]){
				return false;
			}
		}
		return true;
	}
	
	public static int calculate_insertion(int new_value, int size_parent){
		if(new_value != 1){
			if(new_value>size_parent){
				return size_parent-1;
			}else{
				return new_value-2;
			}
		}else{
			return 0;
		}
	}
	
	public static int[] get_previous_area(int size_template, int new_strand, List<Integer> values, int new_area, int area, int deleted_right, int deleted_left, int deleted_border, int deleted_firstborder, int uncle, Integer[][] structure, int[] neighbors){
		int []big_area= new int[4];
		
		// This function can be compacted, there is duplication of code
		
		if(uncle == 0){
			if(new_strand == size_template){
				if(new_area+1==area){
					big_area[0]=values.get(0);
					big_area[3]=values.get(1);
					big_area[1]=max_gap_left(new_area,big_area[0]);
					big_area[2]=max_gap_right(new_area,big_area[3],size_template-1);
				}else{
					if(new_area==area){
						big_area[0]=values.get(0);
						big_area[3]=values.get(3)-((params.minL+params.minG)*(deleted_border+1));
						if(neighbors[0]!=-1 && deleted_border>0){
							big_area[3]=Math.min(big_area[3], structure[neighbors[0]][0]-params.minG-1);
						}
						big_area[1]=max_gap_left(new_area,big_area[0]);
						big_area[2]=max_gap_right(new_area,big_area[3],size_template-1);
					}else{
						big_area[0]=values.get(0);
						big_area[3]=values.get(3)-((params.minL+params.minG)*(deleted_border+1));
						if(neighbors[0]!=-1 && deleted_border>0){
							big_area[3]=Math.min(big_area[3], structure[neighbors[0]][0]-params.minG-1);
						}
						big_area[1]=max_gap_left(new_area,big_area[0],deleted_right);
						big_area[2]=max_gap_right(new_area,big_area[3],size_template-1);
					}
				}	
			}else{
				if(new_area==area){
					big_area[0]=values.get(0);
					big_area[3]=values.get(3);
					big_area[1]=values.get(1)+1;		
					big_area[2]=max_gap_right(new_area,big_area[3],size_template-1);
				}else{
					big_area[0]=values.get(0);
					big_area[3]=values.get(3);
					big_area[1]=max_gap_left(new_area,big_area[0],deleted_right);
					big_area[2]=max_gap_right(new_area,big_area[3],size_template-1,deleted_left);
				}
			}
		}else{
			if(new_strand == size_template){
				if(new_area+1==area){
					big_area[0]=values.get(0);
					big_area[3]=values.get(1);
					big_area[1]=max_gap_left(new_area,big_area[0]);
					big_area[2]=max_gap_right(new_area,big_area[3],size_template-1);
				}else{
					if(new_area==area){
						big_area[0]=values.get(0);
						big_area[3]=values.get(3)-((params.minL+params.minG)*(deleted_border+1));
						if(neighbors[0]!=-1 && deleted_border>0){
							big_area[3]=Math.min(big_area[3], structure[neighbors[0]][0]-params.minG-1);
						}
						big_area[1]=max_gap_left(new_area,big_area[0]);
						big_area[2]=max_gap_right(new_area,big_area[3],size_template-1);
					}else{
						big_area[0]=values.get(0);
						big_area[3]=values.get(3)-((params.minL+params.minG)*(deleted_border+1));
						if(neighbors[0]!=-1 && deleted_border>0){
							big_area[3]=Math.min(big_area[3], structure[neighbors[0]][0]-params.minG-1);
						}
						big_area[1]=max_gap_left(new_area,big_area[0],deleted_right);
						big_area[2]=max_gap_right(new_area,big_area[3],size_template-1);
					}
				}
				
			}else{
				if(new_strand==1){
					if(new_area==area){
						big_area[0]=values.get(2);
						big_area[3]=values.get(3);
						big_area[1]=max_gap_left(new_area,big_area[0]);
						big_area[2]=max_gap_right(new_area,big_area[3],size_template-1);
					}else{
						big_area[0]=values.get(0)+((params.minL+params.minG)*(deleted_firstborder+1));
						if(neighbors[1]!=-1 && deleted_firstborder>0){
							big_area[0]=Math.max(big_area[0], structure[neighbors[1]][1]+params.minG);
						}	
						big_area[3]=values.get(3);
						big_area[1]=max_gap_left(new_area,big_area[0]);
						big_area[2]=max_gap_right(new_area,big_area[3],size_template-1,deleted_left);							
					}
					
				}else{
					if(new_area==area){
						big_area[0]=values.get(0);
						big_area[3]=values.get(3);
						big_area[1]=values.get(1)+1;		
						big_area[2]=max_gap_right(new_area,big_area[3],size_template-1);
					}else{
						big_area[0]=values.get(0);
						big_area[3]=values.get(3);
						big_area[1]=max_gap_left(new_area,big_area[0],deleted_right);
						big_area[2]=max_gap_right(new_area,big_area[3],size_template-1,deleted_left);	
					}
				}
			}

		}
		return big_area;
	}
	
	public static double zScore(double energy, double beta){
		return Math.exp(-energy/beta);
	}
	
	public static double energy(double zScore, double beta){
		return Math.log(zScore)*-beta;
	}
	
	public static double energy_pairings(int i1, int j1, int i2, int j2, SS.Constants.Environment env, SS.Constants.Dir dir){
		double pairing_ener = 0d;
		int min;
		min = Math.min((int)j1-i1,(int)j2-i2);
		for(int i=0;i<=min;i++){				
			switch(dir){
			case PARA:
				pairing_ener+=ef.energy(env, dir,params.sequence.charAt((int)i1+i),params.sequence.charAt((int)i2+i));
				break;
			case ANTI:
				pairing_ener+=ef.energy(env, dir,params.sequence.charAt((int)i1+i),params.sequence.charAt((int)j2-i));
				break;
			case NONE:
				pairing_ener=0;
				return pairing_ener;	
			}
		}
		return pairing_ener;
	}
	
	public static double loopEnergy(int lStart, int lEnd, SS.Constants.Environment env){
		double loop_energy = 0;
		for(int i= lStart; i+1 <= lEnd; i++){
			loop_energy += ef.energy(env, null, params.sequence.charAt(i), params.sequence.charAt(i+1));
		}
		return loop_energy;
	}
	
	public static boolean check_contains(Hashtable<List<Integer>,List<Double>> Table, List<Integer> key, double value, List<Double> new_value){
		double val=0d;
		if(Table.containsKey(key)){
			val = Table.get(key).get(0);
			if(value > val){		
				Table.put(key,new_value);
				return true;
			}
		}else{
			Table.put(key,new_value);
			return true;
		}
		return false;
	}
	
	public static int max_gap_right(int area, int i3, int size_parent){
		return i3 - ((size_parent-(area+1))*params.minL + (size_parent-(area+2))*params.minG);
	}
	
	public static int max_gap_right(int area, int i3, int size_parent, int del_left){
		return i3 - ((size_parent-(area+1))*params.minL + (size_parent-(area+2))*params.minG) - del_left*(params.minL+params.minG);
	}
	
	public static int max_gap_left(int area, int i1){
		return i1 + ((area+1)*params.minL)+(area*params.minG);
	}
	
	public static int max_gap_left(int area, int i1, int del_right){
		return i1 + ((area+1)*params.minL)+(area*params.minG)+del_right*(params.minL+params.minG);
	}
	
	public static int maxEnd(List<Integer> key, boolean border, boolean uncle, boolean first_border){
		int maxEnd;
		if(!uncle){
			if(border){
				maxEnd = params.sequence.length()-1;
			}else{
				maxEnd = key.get(2)-params.minG;
			}
		}else{
			if(first_border){
				maxEnd = key.get(0);
			}else{
				if(border){
					maxEnd = params.sequence.length()-1;
				}else{
					maxEnd = key.get(2)-params.minG;
				}
				
			}
		}
		return maxEnd;		
	}
	
	public static int maxEnd_sibling(List<Integer> key, boolean border, int area, int new_strand, int size_template, boolean uncle, boolean first_border){
		int maxEnd;
		if(!uncle){
			if(border){
				maxEnd = params.sequence.length()-1;
			}else{
				if(area+1>=new_strand){
					maxEnd = key.get(1) - ((area+1)-new_strand+1)*(params.minG+params.minL)+1;
				}else{
					maxEnd = key.get(3) -((size_template-new_strand)*(params.minG+params.minL))+1;
				}
			}
		}else{
			if(first_border){
				maxEnd = key.get(0);
			}else{
				if(border){
					maxEnd = params.sequence.length()-1;
				}else{
					if(area+1>=new_strand){
						maxEnd = key.get(1) - ((area+1)-new_strand+1)*(params.minG+params.minL)+1;
					}else{
						maxEnd = key.get(3) -((size_template-new_strand)*(params.minG+params.minL))+1;
					}
				}
			}
		}
		return maxEnd;
	}
	
	public static int min_value_i3(List<Integer> key, boolean border, boolean uncle, boolean first_border){
		int min_value_i3;
		if(!uncle){
			if(border){
				min_value_i3 = key.get(3)+params.minG+1;
			}else{
				min_value_i3 = key.get(1)+params.minG+1;
			}
		}else{
			if(first_border){
				min_value_i3 = 0;
			}else{
				if(border){
					min_value_i3 = key.get(3)+params.minG+1;
				}else{
					min_value_i3 = key.get(1)+params.minG+1;
				}
			}
		}
		return min_value_i3; 
	}
	
	public static int min_value_i3_sibling(List<Integer> key, boolean border, int area, int new_strand, boolean uncle, boolean first_border){
		int min_value_i3;
		if(!uncle){
			if(border){
				min_value_i3 = key.get(3)+params.minG+1;
			}else{
				if(area+1>=new_strand){
					min_value_i3 = key.get(0) + (new_strand-1)*(params.minG+params.minL);
				}else{
					min_value_i3 = key.get(2)+(new_strand-area-2)*(params.minG+params.minL);
				}
			}
		}else{
			if(first_border){
				min_value_i3 = 0;
			}else{
				if(border){
					min_value_i3 = key.get(3)+params.minG+1;
				}else{
					if(area+1>=new_strand){
						min_value_i3 = key.get(0) + (new_strand-1)*(params.minG+params.minL);
					}else{
						min_value_i3 = key.get(2)+(new_strand-area-2)*(params.minG+params.minL);
					}
				}
			}
		}
		return min_value_i3;
	}
	
	public static int max_value_i3(List<Integer> key, boolean border, boolean uncle, boolean first_border){
		int max_value_i3;
		if(!uncle){
			if(border){
				max_value_i3 = params.sequence.length()-params.minL+1;
			}else{
				max_value_i3 = key.get(2)-params.minL-params.minG+1;
			}
		}else{
			if(first_border){
				max_value_i3 = key.get(0)-params.minL-params.minG+1;
			}else{
				if(border){
					max_value_i3 = params.sequence.length()-params.minL+1;
				}else{
					max_value_i3 = 	key.get(2)-params.minL-params.minG+1;
				}
			}
		}
		return max_value_i3;
	}
	
	public static int max_value_i3_sibling(List<Integer> key, boolean border, int area, int new_strand, int size_template, boolean uncle, boolean first_border){
		int max_value_i3;
		if(!uncle){
			if(border){
				max_value_i3 = params.sequence.length()-params.minL+1;
			}else{
				if(area+1>=new_strand){
					max_value_i3 = key.get(1) - (((area+1)-new_strand+1)*(params.minG+params.minL)+params.minG)+1;
				}else{
					max_value_i3 = key.get(3) - ((size_template-new_strand)*(params.minG+params.minL)+params.minG)+1;
				}
			}
		}else{
			if(first_border){
				max_value_i3 = key.get(0)-params.minL-params.minG+1;
			}else{
				if(border){
					max_value_i3 = params.sequence.length()-params.minL+1;
				}else{
					if(area+1>=new_strand){
						max_value_i3 = key.get(1) - (((area+1)-new_strand+1)*(params.minG+params.minL)+params.minG)+1;
					}else{
						max_value_i3 = key.get(3) - ((size_template-new_strand)*(params.minG+params.minL)+params.minG)+1;
					}
				}
			}
		}
		return max_value_i3;
	}
	
	public static int min_value_j3(int i3){
		int min_value_j3;
		min_value_j3=i3+params.minL-1;
		return min_value_j3;
	}
	
	public static int min_value_j3_sibling(int i3){
		int min_value_j3;
		min_value_j3=i3+params.minL-1;
		return min_value_j3;
	}
	
	public static int max_value_j3(int min_value_j3,int maxEnd){
		int max_value_j3;
		max_value_j3=Math.min(min_value_j3+(params.maxL-params.minL)+1, maxEnd);
		return max_value_j3;
	}
	
	public static int max_value_j3_sibling(int min_value_j3, int maxEnd){
		int max_value_j3;
		max_value_j3=Math.min(min_value_j3+(params.maxL-params.minL)+1, maxEnd);
		return max_value_j3;
	}
	
	public static List<Double> previous_pairing(List<Dir> Pair, List<Integer> template, int area, List<List<Hashtable<List<Integer>,List<Double>>>> Tables_parent, List<Integer> key, boolean reverse, boolean border, boolean uncle, int none){

		int index;
		List<Double> aux_values;
		double aux_energy=0d, final_value=0d;
		Dir aux_none = null;

		List<Dir> previous_pair;
		
		if(!uncle){
			previous_pair= new LinkedList<Dir>(Pair.subList(0,Pair.size()-1));
		}else{
			previous_pair= new LinkedList<Dir>(Pair.subList(1,Pair.size()));
		}
		
		//if(previous_pair.get(previous_pair.size()-1)==Dir.NONE){
		if(none>0){
			if(none == 1){
				aux_none = Dir.PARA;
			}else{
				aux_none = Dir.ANTI;
			}
			if(!uncle){
				previous_pair.set(previous_pair.size()-1, aux_none);
			}else{
				previous_pair.set(0, aux_none);
			}
		}
		if(reverse){
			Collections.reverse(previous_pair);
		}
		index = total_pairings_hash.get(previous_pair.size()-1).get(previous_pair);
		if(border){
			if(none>0){
				aux_values = new LinkedList<Double>(Tables_parent.get(index-1).get(area-1).get(key));
			}else{
				return Tables_parent.get(index-1).get(area-1).get(key);
			}
		}else{
			if(none>0){
				aux_values = new LinkedList<Double>(Tables_parent.get(index-1).get(area).get(key));
			}else{
				return Tables_parent.get(index-1).get(area).get(key);
			}
		}
		//It is a case to compute the compensation of the NONE interaction.
		//Check this part with the new order
		
		
	
		if(reverse || uncle){
			aux_energy = energy_pairings(aux_values.get(1).intValue(), aux_values.get(2).intValue(),aux_values.get(3).intValue(), aux_values.get(4).intValue(), Util.map_environment(1), aux_none);
		}else{
			aux_energy = energy_pairings(aux_values.get(aux_values.size()-4).intValue(), aux_values.get(aux_values.size()-3).intValue(), aux_values.get(aux_values.size()-2).intValue(), aux_values.get(aux_values.size()-1).intValue(), Util.map_environment(1), aux_none);
		}
		final_value = energy(aux_values.get(0),params.temp)-aux_energy;
		final_value = zScore(final_value,params.temp);
		aux_values.set(0, final_value);
		
		return aux_values;			
	}
	
	public static List<Double> previous_pairing_sibling(List<Dir> Pair, List<Integer> template, int area, List<List<Hashtable<List<Integer>,List<Double>>>> Tables_parent, List<Integer> key, boolean reverse, boolean border, boolean uncle, int none){

		int index;
		List<Double> aux_values;
		double aux_energy=0d, final_value=0d;
		Dir aux_none = null;

		List<Dir> previous_pair;
		
		if(!uncle){
			previous_pair= new LinkedList<Dir>(Pair.subList(0,Pair.size()-1));
		}else{
			previous_pair= new LinkedList<Dir>(Pair.subList(1,Pair.size()));
		}
		
		//if(previous_pair.get(previous_pair.size()-1)==Dir.NONE){
		if(none>0){
			if(none == 1){
				aux_none = Dir.PARA;
			}else{
				aux_none = Dir.ANTI;
			}
			if(!uncle){
				previous_pair.set(previous_pair.size()-1, aux_none);
			}else{
				previous_pair.set(0, aux_none);
			}
		}
		if(reverse){
			Collections.reverse(previous_pair);
		}
		index = total_pairings_hash.get(previous_pair.size()-1).get(previous_pair);

		if(none>0){
			aux_values = new LinkedList<Double>(Tables_parent.get(index-1).get(area).get(key));
		}else{
			return Tables_parent.get(index-1).get(area).get(key);
		}




		//It is a case to compute the compensation of the NONE interaction.
		//Check this part with the new order
		
		if(reverse || uncle){
			aux_energy = energy_pairings(aux_values.get(1).intValue(), aux_values.get(2).intValue(),aux_values.get(3).intValue(), aux_values.get(4).intValue(), Util.map_environment(1), aux_none);
		}else{
			aux_energy = energy_pairings(aux_values.get(aux_values.size()-4).intValue(), aux_values.get(aux_values.size()-3).intValue(), aux_values.get(aux_values.size()-2).intValue(), aux_values.get(aux_values.size()-1).intValue(), Util.map_environment(1), aux_none);
		}
		final_value = energy(aux_values.get(0),params.temp)-aux_energy;
		final_value = zScore(final_value,params.temp);
		aux_values.set(0, final_value);
		
		return aux_values;
				
	}
	
	public static void update_areas_border(Template_tfolder node, int pos, int pair, int area, List<Integer> key, double value, List<Double> new_value, int i3, int j3){
		List<Integer> key_check;
		key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(3),i3,j3));
		check_contains(node.get_Zs().get(pos).get(pair-1).get(area),key_check, value,new_value);
		key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),key.get(2),j3));
		check_contains(node.get_Zs().get(pos).get(pair-1).get(area-1),key_check,value,new_value);
	}
	
	public static void update_areas_border_sibling(Template_tfolder node, int pos, int pair, int area, List<Integer> key, double value, List<Double> new_value, int i3, int j3){
		List<Integer> key_check;
		key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),key.get(2),j3));
		check_contains(node.get_Zs().get(pos).get(pair-1).get(area),key_check,value,new_value);
	}
	
	public static void update_areas_firstborder(Template_tfolder node, int pos, int pair, int area, List<Integer> key, double value, List<Double> new_value, int i3, int j3){
		List<Integer> key_check;
		key_check = new LinkedList<Integer>(Util.list(i3,j3,key.get(0),key.get(3)));
		check_contains(node.get_Zs().get(pos).get(pair-1).get(area),key_check, value,new_value);
		key_check = new LinkedList<Integer>(Util.list(i3,key.get(1),key.get(2),key.get(3)));
		check_contains(node.get_Zs().get(pos).get(pair-1).get(area+1),key_check,value,new_value);
	}
	
	public static void update_areas_firstborder_sibling(Template_tfolder node, int pos, int pair, int area, List<Integer> key, double value, List<Double> new_value, int i3, int j3){
		List<Integer> key_check;
		key_check = new LinkedList<Integer>(Util.list(i3,key.get(1),key.get(2),key.get(3)));
		check_contains(node.get_Zs().get(pos).get(pair-1).get(area),key_check, value,new_value);
	}
	
	public static void update_areas_center(Template_tfolder node, int pos, int pair, int area, List<Integer> key, double value, List<Double> new_value, int i3, int j3){
		List<Integer> key_check;
		key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),i3,key.get(3)));
		check_contains(node.get_Zs().get(pos).get(pair-1).get(area),key_check,value,new_value);
		key_check = new LinkedList<Integer> (Util.list(key.get(0),j3,key.get(2),key.get(3)));
		check_contains(node.get_Zs().get(pos).get(pair-1).get(area+1),key_check,value,new_value);
	}
	
	public static void update_areas_center_sibling(Template_tfolder node, int pos, int pair, int area, List<Integer> key, double value, List<Double> new_value, int i3, int j3, int new_strand){
		//Check the difference with update_areas_center => I think we do not need those ifs
		check_contains(node.get_Zs().get(pos).get(pair-1).get(area),key,value,new_value);
	}
	
	public static List<Double> prepare_new_value(double value, List<Double> prev_value,double i3,double j3, boolean reverse, boolean uncle){
		/*List<Double> new_value = new LinkedList<Double>(prev_value);
		new_value.set(0,value);
		new_value.set(3, (double)i3);
		new_value.set(4, (double)j3);
		return new_value;
		*/
		List<Double> new_value = new LinkedList<Double>(prev_value);
		if(!uncle){
			if(reverse){
				new_value.remove(0);
				reverse(new_value);
				new_value.add(0,value);
			}else{
				new_value.set(0,value);
			}
			new_value.add(new_value.size(), (double)i3);
			new_value.add(new_value.size(), (double)j3);
		}else{
			if(reverse){
				new_value.remove(0);
				reverse(new_value);
				new_value.add(0,value);
			}else{
				new_value.set(0, value);
			}
			new_value.add(1,(double)i3);
			new_value.add(2,(double)j3);
		}
		return new_value;
	}
	
	public static void reverse(List<Double> new_value){
		double aux;
		for(int i=0; i<new_value.size()/4;i++){
			aux = new_value.get(2*i);
			new_value.set(2*i, new_value.get(new_value.size()-(2*(i+1))));
			new_value.set(new_value.size()-(2*(i+1)), aux);
			aux = new_value.get((2*i)+1);
			new_value.set(2*i+1, new_value.get(new_value.size()-((2*(i+1))-1)));
			new_value.set(new_value.size()-((2*(i+1))-1), aux);
		}
		
	}
	
	public static List<Collection<Sample>> largestConnectedSets(Collection<Sample> samples, SampleDistance df, double threshold, int number){
		List<Set<Sample>> connectedSets = connectedSets(samples, df, threshold);
		List<Collection<Sample>> largestSets = new LinkedList<Collection<Sample>>();
		for(int i = 0 ; i < number && connectedSets.size() > 0 ; i++){
			int maxInd = connectedSets.indexOf(
					Collections.max(connectedSets,new Comparator<Set<Sample>>() {
						public int compare(Set<Sample> l1, Set<Sample> l2) {
							double delta = SampleCluster.weight(l1) - SampleCluster.weight(l2);
							if(delta == 0)
								return 0;
							else{
								if(delta<0)
									return -1;
								else
									return 1;
							}
						}
					}));
			largestSets.add( connectedSets.remove(maxInd));
		}
		return largestSets;
	}
	
	public static List<Set<Sample>> connectedSets(Collection<Sample> samples, SampleDistance df, double threshold){
		List<Set<Sample>> connectedSet = new LinkedList<Set<Sample>>();
		
		for(Sample s : samples){
			connectedSet.add(new HashSet<Sample>(Util.list(s)));
		}
		
		while(true){
			int merges = 0;
			for(int i = 0; i < connectedSet.size() && connectedSet.size() > 1; i++){
				for(int j = i+1; j < connectedSet.size(); ){
					if(!disjoint_connectedSets(connectedSet.get(i), connectedSet.get(j), df, threshold)){
						connectedSet.get(i).addAll(connectedSet.remove(j));
						merges++;
						j=i+1;
					}
					else{
						j++;
					}
				}
				//TODO check if it is not the biggest weight (I already did that sorting the list)
				if(connectedSet.get(i).size() == 1){
					connectedSet.remove(i);
					i = i-1;
				}	
			}
			
			if(merges == 0)
				break;
			
		}
		
		return connectedSet;	
	}
	
	public static boolean disjoint_connectedSets(Collection<Sample> set1, Collection<Sample> set2, SampleDistance df, double threshold){
		double distance;
		double thres, maxDist;
		maxDist = (2*Double.valueOf(params.clusterMetricParameters)) + 1;
		for(Sample s1 : set1){
			for(Sample s2 : set2){
				thres = findThreshold(s1)+findThreshold(s2);
				thres=((thres/(maxDist*thres))*threshold);
				distance = df.distance(s1, s2);
				if(distance <= thres){
					return false;
				}else{
					if(distance > thres*3){
						return true;
					}
				}
			}
		}
		return true;
	}
	
	public static boolean disjoint(Collection<Sample> set1, Collection<Sample> set2, SampleDistance df, double threshold){
		double distance;
		double thres, maxDist;
		maxDist = (2*Double.valueOf(params.clusterMetricParameters)) + 1;
		for(Sample s1 : set1){
			for(Sample s2 : set2){
				thres = findThreshold(s1)+findThreshold(s2);
				thres=((thres/(maxDist*thres))*threshold);
				distance = df.distance(s1, s2);
				if(distance <= thres)
					return false;
			}
		}
		return true;
	}
	
	public static boolean areNeighbors(Cluster c1, Cluster c2, SampleDistance sd, double threshold){						
		if(Math.min(c1.template.size(),c2.template.size()) == 0 && Math.max(c1.template.size(),c2.template.size()) <= 2){
			return true;
		}else{
			if(areNeighbors(c1.template, c1.pairing, c2.template, c2.pairing)){
				return  !disjoint(c1.samples, c2.samples, sd, threshold);
			}else{
				return false;
			}
		}	
	}
	
	
	public static boolean areNeighbors(List<Integer> t1, List<Dir> p1, List<Integer> t2, List<Dir> p2){
		
		if(Math.min(t1.size(),t2.size()) == 0 && Math.max(t1.size(),t2.size()) <= 2)
			return true;

		
		if(t1.size() == t2.size()){
			if(!(t1.equals(t2) && p1.equals(p2)))
				if(t1.equals(t2) && (p1.contains(Dir.NONE) ^ p2.contains(Dir.NONE))){
					for(int i=0 ; i<p1.size(); i++){
						Dir d1 = p1.get(i);
						Dir d2 = p2.get(i);
						
						if(d1 != Dir.NONE && d2 != Dir.NONE && d1 != d2)
							return false;
					}
					return true;
				}
			return false;
		}			
		else {
			List<Integer> biggerT = null, smallerT = null;
			List<Dir> biggerP = null, smallerP = null;
			
			if(t1.size() > t2.size()){
				biggerT = t1;
				biggerP = p1;
				smallerT = t2;
				smallerP = p2;
			}
			else{
				biggerT = t2;
				biggerP = p2;
				smallerT = t1;
				smallerP = p1;
			} 
			
			if(!differByOne(biggerT, biggerP, smallerT, smallerP)){
				if(!isContained(smallerT, smallerP, biggerT, biggerP))
					return false;
			}
		}
		
		return true;
	}
	
	public static int findThreshold(Sample s){
		int strand1, strand2, lgth=0;
		for(int i=0;i<s.structure.length-1;i++){
			strand1 = s.structure[i][1] - s.structure[i][0] +1;
			strand2 = s.structure[i+1][1] - s.structure[i+1][0] +1;
			lgth += Math.min(strand1, strand2);
		}
		return lgth;
	}
	
	public static boolean differByOne(List<Integer> biggerT, List<Dir> biggerP, List<Integer> smallerT, List<Dir> smallerP) {		
		//return (smallerT.equals(removeFirst(biggerT)) && smallerP.equals(biggerP.subList(1, biggerP.size()))) || (smallerT.equals(removeLast(biggerT)) && smallerP.equals(biggerP.subList(0, biggerP.size()-1)));
		return (smallerT.equals(removeFirst(biggerT)) && smallerP.equals(biggerP.subList(1, biggerP.size()))) || (smallerT.equals(removeLast(biggerT)) && smallerP.equals(biggerP.subList(0, biggerP.size()-1))) || (smallerT.equals(reversed_tmp(removeFirst(biggerT))) && smallerP.equals(reversed_pair(biggerP.subList(1, biggerP.size())))) || (smallerT.equals(reversed_tmp(removeLast(biggerT))) && smallerP.equals(reversed_pair(biggerP.subList(0, biggerP.size()-1))));	
	}
	
	static List<Integer> reversed_tmp(List<Integer> template){
		List<Integer> t = new LinkedList<Integer>(template);
		Collections.reverse(t);
		return t;
	}
	
	static List<Dir> reversed_pair(List<Dir> template){
		List<Dir> t = new LinkedList<Dir>(template);
		Collections.reverse(t);
		return t;
	}
	
	static List<Integer> removeFirst(List<Integer> template){
		List<Integer> t = new ArrayList<Integer>(template);
		Integer removed = t.remove(0);
		for(Integer i = removed + 1; i <= template.size(); i++){
			t.set(t.indexOf(i),i-1);
		}
		return t;
	}
	
	static List<Integer> removeLast(List<Integer> template){
		List<Integer> t = new ArrayList<Integer>(template);
		int last = t.size() - 1;
		Integer removed = t.remove(last);
		for(Integer i = removed + 1; i <= template.size(); i++){
			t.set(t.indexOf(i),i-1);
		}
		return t;
	}
	
	private static boolean isContained(List<Integer> smallerT, List<Dir> smallerP, List<Integer> biggerT, List<Dir> biggerP) {
		List<List<Integer>> subTemps = new ArrayList<List<Integer>>();
		List<List<Dir>> subPairs = new ArrayList<List<Dir>>();
		
		List<Integer> subT = new ArrayList<Integer>();
		List<Dir> subP = new ArrayList<Dir>();
		
		subT.add(biggerT.get(0));
		for(int i=1; i<biggerT.size(); i++){
			Dir d = biggerP.get(i-1);
			
			if(d == Dir.NONE){
				subTemps.add(subT);
				subPairs.add(subP);
				subT = new ArrayList<Integer>();
				subP = new ArrayList<Dir>();
			}
			else{
				subP.add(d);
			}
			
			Integer s = biggerT.get(i);
			subT.add(s);					
		}
		subTemps.add(subT);
		subPairs.add(subP);
		
		List<Integer> rSmallerT = new ArrayList<Integer>(smallerT);
		List<Dir> rSmallerP = new ArrayList<Dir>(smallerP);
		Collections.reverse(rSmallerT);
		Collections.reverse(rSmallerP);
		
		for(int i=0; i<subTemps.size(); i++){
			subT = normalizeTemplate(subTemps.get(i));
			subP = subPairs.get(i);
			
			if((smallerT.equals(subT) && smallerP.equals(subP)) || (rSmallerT.equals(subT) && rSmallerP.equals(subP)))
				return true;
		}
		return false;
	}
	
	public static List<Integer> normalizeTemplate(List<Integer> template){
		List<Integer> normalized = new ArrayList<Integer>(template);
		List<Integer> sorted = new ArrayList<Integer>(template);
		Collections.sort(sorted);
		for(int i=0; i<sorted.size(); i++){
			Integer s = sorted.get(i);
			normalized.set(template.indexOf(s), i+1);
		}
		return normalized;
	}
	
	
	
	// Esta funcion me toca moverla.......................
	public static JFreeChart createFoldingDynamicsChart(List<Cluster> centroids, double[][] dynamics, Map<Cluster,Color> colors){
		final List<String> labels = clusterLabels(centroids);
		
		List<Double[][]> datas = new ArrayList<Double[][]>();
		List<Color> seriesColor = new ArrayList<Color>();
		for(int cluster = 1; cluster < dynamics[0].length; cluster++){
			Double[][] data = new Double[dynamics.length][2];
			for(int timeStep = 0; timeStep < dynamics.length; timeStep++){
				data[timeStep][0] = dynamics[timeStep][0];
				data[timeStep][1] = dynamics[timeStep][cluster];
			}
			seriesColor.add(colors.get(centroids.get(cluster - 1)));
			datas.add(data);
		}
		JFreeChart c = ChartUtils.createChart(labels, datas, seriesColor, "Folding Dynamics", "Log(Time)", "State Density");
		c.getPlot().setBackgroundAlpha(0);
		((XYPlot)c.getPlot()).getRenderer().setToolTipGenerator(new XYToolTipGenerator() {
			
			public String generateToolTip(XYDataset arg0, int arg1, int arg2) {
				return labels.get(arg1);
			}
		});
		
		//	c.getLegend().setItemFont(new Font("Serif", Font.PLAIN, 4));
		
		return c;
	}
	
	
	
	public static List<String> clusterLabels(List<Cluster> centroids){		
		Object last = null;
		int clustNum = 1;
		List<String> labels = new ArrayList<String>(centroids.size());
		String query;
		List<List<Integer>> labels_aux = new LinkedList<List<Integer>>();
		
		for(int i = 0 ; i < centroids.size(); i++){			
			Sample s = centroids.get(i);
			List<Integer> template = s.template;
			List<Dir> pairing = s.pairing;
			Object cur = Util.list(template,pairing);
			
			if(cur.equals(last))
				clustNum++;
			else
				clustNum = 1;	
			
			
			//Yo le cambie esta parte, le puse el cluster y el if
			//labels.add(clusterLabel(template, pairing));// + " cluster " + clustNum);
			query = String.valueOf(labels.size());
			if(!labels.contains(query)){	
					labels.add(query);
					//labels.add(clusterLabel(template, pairing));// + " cluster " + clustNum);
			}
			
			last = cur;
		}
		
		return labels;		
	}
	
	
	public static String clusterLabel(List<Integer> template, List<Dir> pairing){
		List<Object> zipped = new LinkedList<Object>(template);
		if(pairing.size() == 0)
			zipped.add("Unfolded");
		else
			for(int j=pairing.size(); j>0; j--){
				zipped.add(j, pairing.get(j-1));
			}	
		return StringUtils.join(zipped, "-");
	}
	
	public static String clusterLabelShort(List<Integer> template, List<Dir> pairing){
		List<Object> zipped = new LinkedList<Object>(template);
		if(pairing.size() == 0)
			zipped.add("Unfolded");
		else
			for(int j=pairing.size(); j>0; j--){
				zipped.add(j, pairing.get(j-1).toString().charAt(0));
			}	
		return StringUtils.join(zipped, "");
	}
	
	public static void contact_prediction(List<Cluster> clusters, Arguments params, int[] helices, String name_file, EvFold evfold){
		int clust = 0;
		
		String pdb = params.PDB;
		for(Cluster c : clusters){
			clust++;
			//if(c.template.equals(Util.list(5,1,2,3,4)) && c.pairing.equals(Util.list(Dir.ANTI,Dir.ANTI,Dir.ANTI,Dir.ANTI))){
			//if(c.template.equals(Util.list(3,4,1,2)) && c.pairing.equals(Util.list(Dir.ANTI,Dir.PARA,Dir.ANTI))){
			//if(c.template.equals(Util.list(1,3,2)) && c.pairing.equals(Util.list(Dir.ANTI,Dir.ANTI))){
			if(clusterLabelShort(c.template,c.pairing).equals(params.target_topology) || clusterLabelShort(c.template,c.pairing).equals(params.target_topology_reverse)){
				try {
					System.out.println("INSIDE :) .........");
					//Character chain = PDBReader.chain(pdb);
					Character chain = params.chain.charAt(0);
					if( chain == null)
						chain = 'A';
					Map<List<Integer>,Double> pdbMap = PDBReader.getPDBContactMap2(pdb,chain, 8.0);	
					Map<List<Integer>,Double> pdbThresh = PDBReader.getPDBContactMap(pdb, chain, 8.0);
					Map<List<Integer>,Double> contMap = Contact_Prediction.contactMap(c.samples);
					
					
					Map<List<Integer>,Double> contMapEvFold =new HashMap<List<Integer>,Double>();
					//Start Module of helices
					Map<List<Integer>,Double> contMapHelix =new HashMap<List<Integer>,Double>();
	

					for(int i=0;i<helices.length;i++){
						if(helices[i]==1 && helices[i+3]==1){
							contMapHelix.put(Arrays.asList(new Integer[]{i,i+3}), 1d);
							contMapHelix.put(Arrays.asList(new Integer[]{i+3,i}), 1d);
						}
					}
					
					/*
					for(int i=0;i<evfold.Data.length;i++){
						for(int j=0; j<evfold.Data[0].length;j++){
							if(evfold.Data[i][j] > 0.1){
								contMapEvFold.put(Arrays.asList(new Integer[]{i,j}), 1d);
							}
						}
					}
					*/

					
					//End Modulee of helices

					StrandChart.saveChartWithPDB(Contact_Prediction.strandAssignments(c.samples),PDBReader.strandAssignments(pdb,params.chain.charAt(0)), params.pathFiles+params.protein + "/strands_" + runs +"_"+params.alpha+"_"+clust+".png", 600, 300);
					//ContactChart.saveChart(contMap, Color.green, contMapHelix, Color.blue, pdbThresh, Color.red, contMapEvFold, Color.gray, params.sequence, Color.black, 300, 300, pdb+"/contacts_"+runs+"_"+params.alpha +"_"+clust+".png");
					ContactChart.saveChart(contMap, Color.green, contMapHelix, Color.blue, pdbThresh, Color.red, params.sequence, Color.black, 300, 300, params.pathFiles+params.protein+"/contacts_"+runs+"_"+params.alpha +"_"+clust+".png");
					//ContactChart.saveChart(contMapHelix, Color.blue, pdbThresh, Color.red, params.sequence, Color.black, 300, 300, pdb+"_aggg"+ clust+"_contacts.png");
					Contact_Prediction.writeTextualSummary(params.pathFiles+params.protein, c.samples, pdbThresh, pdbMap, contMap, clust, "_one_"+runs+"_"+params.alpha, summary_one, summary_one_count, params.PDB, params.chain.charAt(0));//I changed the order pdbTresh and pdbMap
					Contact_Prediction.writeTextualSummary(params.pathFiles+params.protein, c.samples, pdbMap, pdbThresh, contMap, clust, "_two_"+runs+"_"+params.alpha, summary_two, summary_two_count, params.PDB, params.chain.charAt(0));
					
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (HeadlessException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public static int[] helix_module(EvFold evol){
		int sum = 0;
		int []helix = new int[evol.Contacts.length];
		int []pred_helix = new int[evol.Contacts.length];
		for(int i=1;i<evol.Contacts.length-3;i++){
			sum =0;
			for(int j=i+3;j<i+6 && j<evol.Contacts.length;j++){
				if(evol.Contacts[i][j]>0){
					sum++;
				}
			}
			if (sum>1){
				helix[i]=1;
			}
		}
		for(int i=evol.Contacts.length-1;i>=3;i--){
			sum =0;
			for(int j=i-3;j>i-6 && j>=0;j--){
				if(evol.Contacts[j][i]>0){
					sum++;
				}
			}
			if (sum>1){
				helix[i]+=1;
			}
		}
		for(int i=1;i<evol.Contacts.length-1;i++){
			if(helix[i-1]+helix[i+1]+helix[i]>=5){
				pred_helix[i]=1;
			}else{
				pred_helix[i]=0;
			}
		}
		
		for(int i=0;i<evol.Contacts.length;i++){
			//System.out.println(" i="+i + " "+ pred_helix[i]+ " "+ helix[i]);
		}
		return pred_helix;
	}
	
	public static void interactions_template(List<Cluster> clusters, String fileName, boolean option){
		List<Integer> template;
		List<Dir> pairing;
		List<Integer> key = new LinkedList<Integer>();
		String label, scale_seq;
		String path = params.pathFiles+params.protein;
		HashMap<List<Integer>,Double> m_PARA = new HashMap<List<Integer>,Double>();
		HashMap<List<Integer>,Double> m_ANTI = new HashMap<List<Integer>,Double>();
		HashMap<List<Integer>,Double> m_NONE = new HashMap<List<Integer>,Double>();
		HashMap<List<Integer>,Double> m_SCALE = new HashMap<List<Integer>,Double>();
		HashMap<List<Integer>,Double> m = new HashMap<List<Integer>,Double>();
		double w, prob_aux, prob_sum, prob_aux_aux;
		Dir value_pair;
		int value_i, value_j;
				
		for(Cluster c : clusters){
			w=0;
			template = c.template;
			pairing = c.pairing;
			if(template.size()==0){
				continue;
			}
			if(template.size()!=params.numStrands && option==true){
				continue;
			}
			
			w = c.weight;

						
			for(int i=0; i< pairing.size(); i++){
				value_i=Math.max(template.get(i),template.get(i+1));
				value_j=Math.min(template.get(i),template.get(i+1));
				value_pair=pairing.get(i);
				key = Util.list(value_i, value_j);
				if(value_pair == Dir.PARA){
					if(m_PARA.containsKey(key)){
						prob_aux = m_PARA.get(key);
						prob_aux += w;
					}else{
						prob_aux = w;
					}
					m_PARA.put(key, prob_aux);
				}else{
					if(value_pair == Dir.ANTI){
						if(m_ANTI.containsKey(key)){
							prob_aux = m_ANTI.get(key);
							prob_aux+= w;
						}else{
							prob_aux = w;	
						}
						m_ANTI.put(key, prob_aux);
					}else{
						if(m_NONE.containsKey(key)){
							prob_aux = m_NONE.get(key);
							prob_aux += w;
						}else{
							prob_aux = w;
						}
						m_NONE.put(key, prob_aux);
					}
				}
				if(m.containsKey(key)){
					prob_aux = m.get(key);
					prob_aux += w;
				}else{
					prob_aux = w;
				}
				m.put(key,prob_aux);
			}
		}
		
		for(List<Integer> keys : m.keySet()){
			prob_sum = m.get(keys);	
			if(m_PARA.containsKey(keys)){
				prob_aux = m_PARA.get(keys);
				prob_aux = prob_aux / prob_sum;
				m_PARA.put(keys, prob_aux);
				if(!option){
					if(list_m_true.get(0).containsKey(keys)){
						prob_aux_aux = list_m_true.get(0).get(keys); 
					}else{
						prob_aux_aux = 0;
					}
					list_m_true.get(0).put(keys, (prob_aux/num_runs)+prob_aux_aux);
				}else{
					if(list_m_false.get(0).containsKey(keys)){
						prob_aux_aux = list_m_false.get(0).get(keys); 
					}else{
						prob_aux_aux = 0;
					}
					list_m_false.get(0).put(keys, (prob_aux/num_runs)+prob_aux_aux);
				}
			}

			if(m_ANTI.containsKey(keys)){
				prob_aux = m_ANTI.get(keys);
				prob_aux = prob_aux / prob_sum;
				m_ANTI.put(keys, prob_aux);
				if(!option){
					if(list_m_true.get(1).containsKey(keys)){
						prob_aux_aux = list_m_true.get(1).get(keys); 
					}else{
						prob_aux_aux = 0;
					}
					list_m_true.get(1).put(keys, (prob_aux/num_runs)+prob_aux_aux);
				}else{
					if(list_m_false.get(1).containsKey(keys)){
						prob_aux_aux = list_m_false.get(1).get(keys); 
					}else{
						prob_aux_aux = 0;
					}
					list_m_false.get(1).put(keys, (prob_aux/num_runs)+prob_aux_aux);
				}
			}

			if(m_NONE.containsKey(keys)){
				prob_aux = m_NONE.get(keys);
				prob_aux = prob_aux / prob_sum;
				m_NONE.put(keys, prob_aux);
				if(!option){
					if(list_m_true.get(2).containsKey(keys)){
						prob_aux_aux = list_m_true.get(2).get(keys); 
					}else{
						prob_aux_aux = 0;
					}
					list_m_true.get(2).put(keys, (prob_aux/num_runs)+prob_aux_aux);
				}else{
					if(list_m_false.get(2).containsKey(keys)){
						prob_aux_aux = list_m_false.get(2).get(keys); 
					}else{
						prob_aux_aux = 0;
					}
					list_m_false.get(2).put(keys, (prob_aux/num_runs)+prob_aux_aux);
				}
			}
		}
		
		for(int i=1; i<= params.numStrands; i++){
			for(int j=1; j<=params.numStrands; j++){
				key = Util.list(i,j);
				if(!m_PARA.containsKey(key)){
					m_PARA.put(key, 0d);
				}
				if(!m_ANTI.containsKey(key)){
					m_ANTI.put(key, 0d);
				}
				if(!m_NONE.containsKey(key)){
					m_NONE.put(key, 0d);
				}	
			}
		}
		
		
		for(int i=1; i<=4*3; i++){
			m_SCALE.put(Arrays.asList(new Integer[]{i,1}), Double.valueOf(i));
		}
		scale_seq =    "0   .16 .25 .33 .42 .36  .5 .58 .66 .75 .83 .92 1";
		label =  "1    2    3"; 
		if(params.numStrands==2){
		label = "1    2"; 
		}
		if(params.numStrands==3){
		label = "1    2    3"; 
		}
		if(params.numStrands==4){
		label = "1    2    3    4"; 
		}
		if(params.numStrands==5){
		label = "1    2    3    4    5"; 
		}
		
		
		
		BufferedImage img = ContactChart.getChartHeat(m_PARA,m_ANTI,m_NONE,m_SCALE,label, scale_seq,Color.white, Color.red);
		Print.print_probability_templates(m_PARA, m_ANTI, m_NONE, path+"/probabilityTemplate_"+option+"_"+runs+"_"+params.alpha+".txt",params.numStrands);
		try {
			ContactChart.saveHeat(img, path+"/probabilityTemplate_"+option+"_"+runs+"_"+params.alpha+".png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void summary_interactions_template(String fileName){
		List<Integer> key = new LinkedList<Integer>();
		HashMap<List<Integer>,Double> m_SCALE = new HashMap<List<Integer>,Double>();
		String path = params.pathFiles+params.protein;
		boolean option = true;


		for(int i=1; i<= params.numStrands; i++){
			for(int j=1; j<=params.numStrands; j++){
				key = Util.list(i,j);

				if(!list_m_true.get(0).containsKey(key)){
					list_m_true.get(0).put(key, 0d);
				}
				if(!list_m_true.get(1).containsKey(key)){
					list_m_true.get(1).put(key, 0d);
				}
				if(!list_m_true.get(2).containsKey(key)){
					list_m_true.get(2).put(key, 0d);
				}	


				if(!list_m_false.get(0).containsKey(key)){
					list_m_false.get(0).put(key, 0d);
				}
				if(!list_m_false.get(1).containsKey(key)){
					list_m_false.get(1).put(key, 0d);
				}
				if(!list_m_false.get(2).containsKey(key)){
					list_m_false.get(2).put(key, 0d);
				}	
			}
		}

		for(int i=1; i<=4*3; i++){
			m_SCALE.put(Arrays.asList(new Integer[]{i,1}), Double.valueOf(i));
		}
		String scale_seq =    "0   .16 .25 .33 .42 .36  .5 .58 .66 .75 .83 .92 1";
		String label = "1    2    3"; 
		if(params.numStrands==2){
		label = "1    2"; 
		}
		if(params.numStrands==3){
		label = "1    2    3"; 
		}
		if(params.numStrands==4){
		label = "1    2    3    4"; 
		}
		if(params.numStrands==5){
		label = "1    2    3    4    5"; 
		}
		
		if(params.numStrands==6){
		label = "1    2    3    4    5    6"; 
		}
		
		Print.print_probability_templates(list_m_true.get(0),list_m_true.get(1),list_m_true.get(2), path+"/SummaryProbabilityTemplate_"+option+"_"+params.alpha+".txt", params.numStrands);
		BufferedImage img = ContactChart.getChartHeat(list_m_true.get(0),list_m_true.get(1),list_m_true.get(2),m_SCALE,label, scale_seq,Color.white, Color.red);
		try {
			ContactChart.saveHeat(img, path+"/SummaryProbabilityTemplate_"+option+"_"+params.alpha+".png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		option = false;
		Print.print_probability_templates(list_m_false.get(0),list_m_false.get(1),list_m_false.get(2), path+"/SummaryProbabilityTemplate_"+option+"_"+params.alpha+".txt", params.numStrands);
		BufferedImage img1 = ContactChart.getChartHeat(list_m_false.get(0),list_m_false.get(1),list_m_false.get(2),m_SCALE,label, scale_seq,Color.white, Color.red);
		try {
			ContactChart.saveHeat(img1, path+"/SummaryProbabilityTemplate_"+option+"_"+params.alpha+".png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

	}
	
	
	public static void summary_density(String fileName){
		double prob_aux, cont_aux;
		HashMap<List<Integer>,Double> m_SCALE = new HashMap<List<Integer>,Double>();
		String path = params.pathFiles+params.protein;
		
		
		for(int i=1; i<6; i++){
			for(int j=1; j<params.numStrands+1; j++){
				if(i==5 || i==3 || i ==1){
					if(mdensity.containsKey(Util.list(j,i))){
						prob_aux = mdensity.get(Util.list(j,i));
						cont_aux = mdensity_count.get(Util.list(j,i));
						if(cont_aux!=0){
							mdensity.put(Util.list(j,i), prob_aux/cont_aux);
						}
					}else{
						mdensity.put(Util.list(j,i), 0.0);
					}
				}else{
					mdensity.put(Util.list(j,i), 0.0);
				}	
			}
		}
		
		for(int i=1; i<=10; i++){
			m_SCALE.put(Arrays.asList(new Integer[]{i,1}), Double.valueOf(i));
		}
		
		String scale_seq = "0 .2 .4 .6  .8  1";
		//label = "1    2    3    4    5";
		String label = "1    2    3";
		
		if(params.numStrands==3){
		label = "1    2    3"; 
		}
		if(params.numStrands==4){
		label = "1    2    3    4"; 
		}
		if(params.numStrands==5){
		label = "1    2    3    4    5"; 
		}
		
		if(params.numStrands==6){
		label = "1    2    3    4    5    6"; 
		}
		
		Print.print_densities(mdensity,path+"/DensitySummary_"+params.alpha+".txt", params.numStrands);
		BufferedImage img = ContactChart.getChartProb(mdensity,m_SCALE,label,scale_seq,Color.white, Color.red);
		try {
			ContactChart.saveHeat(img, path+"/DensitySummary_"+params.alpha+".png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void summary_report(){
		for(int i=0;i<summary_one.length;i++){
			for(int j=0; j< summary_one[0].length;j++){
				if(summary_one_count[i][j]>0){
					summary_one[i][j]/= summary_one_count[i][j];
				}
				if(summary_two_count[i][j]>0){
					summary_two[i][j]/= summary_two_count[i][j];
				}
			}
		}
	}
	
	public static void interactions_density(List<Cluster> clusters, double[][]dynamics, String fileName, List<List<Integer>> phylogeny_aux, boolean filter){
		
		List<Integer> template_c, template_d, gen;
		List<Dir> pairing_c, pairing_d;
		String label="", label_aux="", scale_seq;
		String path = params.pathFiles+params.protein;
		HashMap<List<Integer>,Double> m_SCALE = new HashMap<List<Integer>,Double>();
		HashMap<List<Integer>,Double> m = new HashMap<List<Integer>,Double>();
		double w, w_max, prob_sum, prob_max=0d, prob_aux_aux=0d;
		double[] prob_cond = new double[phylogeny.size()];
		int[] points = new int[phylogeny.size()];
		Cluster d;
		boolean isAccessible;

		points = density_points(dynamics,clusters,phylogeny_aux,filter);
		
		for(int i=0; i<phylogeny.size();i++){
			prob_sum = 0;
			prob_max = 0;
			if(!filter){
				gen = phylogeny.get(i);
			}else{
				gen = phylogeny_aux.get(i);
			}
			Cluster c = clusters.get(gen.get(0));
			w=0;
			w_max=0;
			template_c = c.template;
			pairing_c = c.pairing;
			if(template_c.size()==0){
				continue;
			}
			if(i!=0){
				label_aux +="\n";
			}
			label_aux += clusterLabelShort(template_c,pairing_c);
			for(int j=0;j<gen.size();j++){
				w += dynamics[points[i]][gen.get(j)];
				if(dynamics[points[i]][gen.get(j)]>w_max){
					w_max=dynamics[points[i]][gen.get(j)];
				}
			}
			
			for(int k=0; k< clusters.size(); k++){
				template_d = clusters.get(k).template;
				if(template_d.size()!=template_c.size()){
					continue;
				}
				prob_sum += dynamics[points[i]][k];
				if(dynamics[points[i]][k]>prob_max){
					prob_max = dynamics[points[i]][k];
				}
			}
			
			if(i == 0){
				prob_cond[i] = prob_sum;
			}
			if(i<phylogeny.size()-1){
				for(int k=0; k< clusters.size(); k++){
					d = clusters.get(k);
					template_d = d.template;
					pairing_d = d.pairing;
					if(template_c.size()>=template_d.size()){
						continue;
					}
					isAccessible = areNeighbors(c, d, params.transMetric, params.transitionThreshold);
					if(isAccessible){
						prob_cond[i+1] += dynamics[points[i+1]][k];  
					}				
				}
			}

			
			
			m.put(Util.list(i+1,1), w/prob_sum);
			m.put(Util.list(i+1,3), w_max/prob_max);
			m.put(Util.list(i+1,5), w/prob_cond[i]);
			
			
			if(mdensity.containsKey(Util.list(i+1,1))){
				prob_aux_aux = mdensity.get(Util.list(i+1,1)); 
			}else{
				prob_aux_aux = 0;
			}
			mdensity.put(Util.list(i+1,1), prob_aux_aux+(w/prob_sum));
			
			if(mdensity.containsKey(Util.list(i+1,3))){
				prob_aux_aux = mdensity.get(Util.list(i+1,3)); 
			}else{
				prob_aux_aux = 0;
			}
			mdensity.put(Util.list(i+1,3), prob_aux_aux+(w_max/prob_max));
			
			if(mdensity.containsKey(Util.list(i+1,5))){
				prob_aux_aux = mdensity.get(Util.list(i+1,5)); 
			}else{
				prob_aux_aux = 0;
			}
			mdensity.put(Util.list(i+1,5), prob_aux_aux+(w/prob_cond[i]));
			
			if(mdensity_count.containsKey(Util.list(i+1,1))){
				prob_aux_aux = mdensity_count.get(Util.list(i+1,1)); 
			}else{
				prob_aux_aux = 0;
			}
			mdensity_count.put(Util.list(i+1,1), prob_aux_aux + 1); 
			
			if(mdensity_count.containsKey(Util.list(i+1,3))){
				prob_aux_aux = mdensity_count.get(Util.list(i+1,3)); 
			}else{
				prob_aux_aux = 0;
			}
			mdensity_count.put(Util.list(i+1,3), prob_aux_aux + 1); 
			
			if(mdensity_count.containsKey(Util.list(i+1,5))){
				prob_aux_aux = mdensity_count.get(Util.list(i+1,5)); 
			}else{
				prob_aux_aux = 0;
			}
			mdensity_count.put(Util.list(i+1,5), prob_aux_aux + 1);
			
		}
		
		for(int i=2; i<6; i++){
			if(i==3 || i==5){
				continue;
			}
			for(int j=1; j<params.numStrands+1; j++){
				m.put(Arrays.asList(new Integer[]{j,i}), 0.0);
			}
		}
		
		for(int i=1; i<=10; i++){
			m_SCALE.put(Arrays.asList(new Integer[]{i,1}), Double.valueOf(i));
		}
		
		scale_seq = "0 .2 .4 .6  .8  1";
		//label = "1    2    3    4    5";
		label = "1    2    3";
		
		if(params.numStrands==3){
		label = "1    2    3"; 
		}
		if(params.numStrands==4){
		label = "1    2    3    4"; 
		}
		if(params.numStrands==5){
		label = "1    2    3    4    5"; 
		}
		
		Print.print_densities(m,path+"/probabilityDensity_"+runs+"_"+params.alpha+".txt", params.numStrands);
		BufferedImage img = ContactChart.getChartProb(m,m_SCALE,label_aux,scale_seq,Color.white, Color.red);
		try {
			ContactChart.saveHeat(img, path+"/probabilityDensity_"+runs+"_"+params.alpha+".png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int[] density_points(double[][] dynamics, List<Cluster> clusters, List<List<Integer>> phylogeny_aux, boolean filter){
		double max = -1, sum = -1;
		int parent, pos=-1;
		int[] points = new int[phylogeny.size()];
		List<Integer> gen;
		
		for(int i=0;i<phylogeny.size();i++){
			max = -1;
			pos = -1;
			sum = 0;
			if(!filter){
				gen=new LinkedList<Integer>(phylogeny.get(i));
			}else{
				gen=new LinkedList<Integer>(phylogeny_aux.get(i));
			}
			for(int k=0;k<dynamics.length;k++){	
				sum=0;
				for(int j=0;j<gen.size();j++){
					parent = gen.get(j);
					sum += dynamics[k][parent]; 
				}
				if(sum > max){
					max = sum;
					pos = k;
				}
			}
			points[i]=pos;
		}
		return points;
	}
	
	public static void genealogy(int current_cluster_size, int[][] family, List<Cluster> clusters){



		int pos_aux=0, size_template;
		List<Integer> gen; 
		List<Integer> new_template;
		List<Dir> new_pairing;

		for(int y=0; y<family.length;y++){
			new_template = new LinkedList<Integer>(list_templates.get(family[y][0]-1).get_template());
			size_template = new_template.size();
			new_pairing = new LinkedList<Dir>(total_pairings.get(size_template-2).get(family[y][1]));
			pos_aux=0;
			gen = new LinkedList<Integer>();
			for(Cluster c: clusters){
				if(new_template.equals(c.template) && new_pairing.equals(c.pairing)){
					gen.add(pos_aux);
				}
				pos_aux++;
			}
			phylogeny.add(gen);		
		}
		gen = new LinkedList<Integer>();
		for(int z=0; z<current_cluster_size; z++){
			gen.add(clusters.size()+z);
		}
		phylogeny.add(gen);

	}
	
	public static List<List<Integer>> filter_clusters(List<Cluster> c, List<Cluster>c_aux){
		List<List<Integer>> phylogeny_aux = new LinkedList<List<Integer>>();
		List<Integer> gen_aux;
		Hashtable<String,String> mapping = new Hashtable<String,String>();
		int count = c.size(), pos = 0, i_aux, size_aux;
		int[] cons_pos = new int [params.numStrands-4];
		int[] cons_pos_j = new int [params.numStrands-4];
		double  weight_aux;
		String key;
		boolean flag=true;
		String[] cons = new String[phylogeny.size()];
		List<Integer> gen = new LinkedList<Integer>();
		
		for(int i=0; i<phylogeny.size();i++){
			gen = phylogeny.get(i);
			cons[i] = Landscape_tfolder.clusterLabelShort(c.get(gen.get(0)).template,c.get(gen.get(0)).pairing);
		}	
		
		for(int i = 0; i < count; i++){
			key = Landscape_tfolder.clusterLabelShort(c.get(i).template, c.get(i).pairing);
			if(!mapping.containsKey(key)){
				size_aux = c_aux.size();
				mapping.put(key, size_aux + "#" +c.get(i).weight);
				c_aux.add(new Cluster(c.get(i).template, c.get(i).pairing, c.get(i).get_samples(), c.get(i).weight));
				if(flag && c.get(i).template.size()==5){
					flag = false;
					pos = size_aux;
				}
				if(!flag){
					if(key.equals(cons[cons.length-1-(params.numStrands-c.get(i).numStrands)])){
						cons_pos[c.get(i).numStrands-5]=size_aux;
					}
				}
			}else{
				i_aux = Integer.valueOf(mapping.get(key).split("#")[0]);
				weight_aux = Double.valueOf(mapping.get(key).split("#")[1])+c.get(i).weight;
				mapping.remove(key);
				mapping.put(key, i_aux + "#" + weight_aux);
				c_aux.get(i_aux).weight = weight_aux;
				c_aux.get(i_aux).samples.addAll(new LinkedList<Sample>(c.get(i).get_samples()));
			}
		}
		

		
		if(c_aux.size()>params.limitClusters){
			double[][] values = new double[params.numStrands-4][c_aux.size()];
			int[][] positions = new int[params.numStrands-4][c_aux.size()];
			int[] sizes = new int[params.numStrands-4];
			flag=true;
			for(int i=pos; i<c_aux.size(); i++){
				values[c_aux.get(i).numStrands-5][sizes[c_aux.get(i).numStrands-5]]=c_aux.get(i).weight;
				positions[c_aux.get(i).numStrands-5][sizes[c_aux.get(i).numStrands-5]]=i;
				if(i==cons_pos[c_aux.get(i).numStrands-5]){
					cons_pos_j[c_aux.get(i).numStrands-5]=i;
				}
		
				sizes[c_aux.get(i).numStrands-5]++;
			}
			double tmp_double, percentage;
			int tmp_int;
			int free_spots, total_spots=0;
			for(int i=0; i<sizes.length; i++){
				total_spots += sizes[i];
			}
			free_spots = params.limitClusters-(pos+1);
			percentage = (double)free_spots/(double)total_spots;
			for(int i=0; i<sizes.length; i++){
				for(int j=0; j<(sizes[i]*percentage);j++){
					for(int k=j+1;k<sizes[i];k++){
						if(values[i][j]<values[i][k]){
							tmp_double = values[i][j];
							values[i][j] = values[i][k];
							values[i][k] = tmp_double;
							tmp_int = positions[i][j];
							positions[i][j] = positions[i][k];
							positions[i][k] = tmp_int;
						}
					}
				}
			}
			for(int i=sizes.length-1; i>=0; i--){
				for(int j=(int)(sizes[i]*percentage); j<sizes[i]-1;j++){
					for(int k=j+1;k<sizes[i];k++){
						if(positions[i][j]<positions[i][k]){
							tmp_double = values[i][j];
							values[i][j] = values[i][k];
							values[i][k] = tmp_double;
							tmp_int = positions[i][j];
							positions[i][j] = positions[i][k];
							positions[i][k] = tmp_int;
						}
					}
					if(positions[i][j]!=cons_pos_j[i]){
						c_aux.remove(positions[i][j]);
					}else{
						System.out.println(Landscape_tfolder.clusterLabelShort(c_aux.get(positions[i][j]).template,c_aux.get(positions[i][j]).pairing));
					}

				}
			}
			System.out.println("Check");
		}
		
		for(int i=0; i<c_aux.size(); i++){
			key = Landscape_tfolder.clusterLabelShort(c_aux.get(i).template, c_aux.get(i).pairing);
			for(int m=0;m<cons.length;m++){
				if(key.equals(cons[m])){
					gen_aux = new LinkedList<Integer>(Util.list(i));
					phylogeny_aux.add(gen_aux);
				}
			}
		}
		return phylogeny_aux;
	}
	

	public static void test_clusters(){
		List<Cluster> clusters = new LinkedList<Cluster>();
		List<Cluster> clusters_aux = new LinkedList<Cluster>();
		Sample sample;
		int cont=0;
		List<Integer> template;
		List<Dir> pairing = new LinkedList<Dir>(Util.list(Dir.PARA));	
		Integer[][] structure = new Integer[2][2];
		structure[0][0] = 1;
		structure[0][1] = 5;
		structure[1][0] = 10;
		structure[1][1] = 15;
		Collection<Sample> largestSets;
				
		for(int i=0;i<12;i++){
			if(i%3==0){
				cont++;
			}
			template = new LinkedList<Integer>(Util.list(1+cont,2+cont));
			sample = new Sample(template, pairing,structure,50,Math.random(), Math.random());
			largestSets = new LinkedList<Sample>();
			largestSets.add(sample);
			clusters.add(new Cluster(template, pairing, largestSets, Math.random()));
		}
		phylogeny.add(new LinkedList<Integer>(Util.list((int)(Math.random()*11))));
		
		cont =0;
		pairing = new LinkedList<Dir>(Util.list(Dir.PARA, Dir.PARA));
		for(int i=0;i<36;i++){
			if(i%3==0){
				cont++;
			}
			template = new LinkedList<Integer>(Util.list(1+cont,2+cont,3+cont));
			sample = new Sample(template, pairing,structure,50,Math.random(), Math.random());
			largestSets = new LinkedList<Sample>();
			largestSets.add(sample);
			clusters.add(new Cluster(template, pairing, largestSets, Math.random()));
		}
		phylogeny.add(new LinkedList<Integer>(Util.list((int)(12+Math.random()*36))));
		
		cont=0;
		pairing = new LinkedList<Dir>(Util.list(Dir.PARA, Dir.PARA, Dir.PARA));
		for(int i=0;i<432;i++){
			if(i%3==0){
				cont++;
			}
			template = new LinkedList<Integer>(Util.list(1+cont,2+cont,3+cont,4+cont));
			sample = new Sample(template, pairing,structure,50,Math.random(), Math.random());
			largestSets = new LinkedList<Sample>();
			largestSets.add(sample);
			clusters.add(new Cluster(template, pairing, largestSets, Math.random()));
		}
		phylogeny.add(new LinkedList<Integer>(Util.list((int)(48+Math.random()*432))));
		
		cont=0;
		pairing = new LinkedList<Dir>(Util.list(Dir.PARA, Dir.PARA, Dir.PARA, Dir.PARA));
		for(int i=0;i<3600;i++){
			if(i%3==0){
				cont++;
			}
			template = new LinkedList<Integer>(Util.list(1+cont,2+cont,3+cont,4+cont,5+cont));
			sample = new Sample(template, pairing,structure,50,Math.random(), Math.random());
			largestSets = new LinkedList<Sample>();
			largestSets.add(sample);
			clusters.add(new Cluster(template, pairing, largestSets, Math.random()));
		}
		phylogeny.add(new LinkedList<Integer>(Util.list((int)(480+Math.random()*3600))));
		
		cont=0;
		pairing = new LinkedList<Dir>(Util.list(Dir.PARA, Dir.PARA, Dir.PARA, Dir.PARA, Dir.PARA));
		for(int i=0;i<5000;i++){
			if(i%3==0){
				cont++;
			}
			template = new LinkedList<Integer>(Util.list(1+cont,2+cont,3+cont,4+cont,5+cont,6+cont));
			sample = new Sample(template, pairing,structure,50,Math.random(), Math.random());
			largestSets = new LinkedList<Sample>();
			largestSets.add(sample);
			clusters.add(new Cluster(template, pairing, largestSets, Math.random()));
		}
		phylogeny.add(new LinkedList<Integer>(Util.list((int)(4080+Math.random()*5000))));
		List<List<Integer>> test = filter_clusters(clusters, clusters_aux);
		System.out.println(test.size());
	}
	
	
}
