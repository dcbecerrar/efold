package util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import metrics.SampleDistance;

public class Arguments {

	public String sequence, landscapeSvg, pathFiles, clusterMetric, clusterMetricParameters, transitionMetric, transitionMetricParameters, data, codebase, jnlp, Pfam, Seq_ID, protein, target_topology, target_topology_reverse, DI_file, chain, Pfam_range, PDB;
	public boolean BFS, DSampling, loops;
	public int numStrands, numSamples, minL, maxL, minG, maxComb, maxClusters, evFoldStart, evFoldBase, evFoldIndex, limitClusters;
	public double coilWeight, transitionThreshold, ev_threshold, loopEnergy;
	public Double temp, startTime, endTime, granularity,  recentering, antiPara, clusterThreshold, alpha, evFold_threshold;
	public SampleDistance clustMetric, transMetric; 


	Map<String,String> opts;

	public Arguments(String[] args){		
		opts = new HashMap<String,String>();
		String key = null;
		Pattern p = Pattern.compile("^-");
		for(int i=0; i<args.length; i++){
			Matcher m = p.matcher(args[i]); // get a matcher object
			if(m.find()){
				key = args[i].substring(1);	
				opts.put(key,null);
			}
			else{
				opts.put(key,args[i]);
			}
		}
		//1EM7
		//protein = optional("protein","1EM7");
		//sequence = optional("sequence","TTYKLILNGKTLKGETTTEAVDAETAERVFKEYAKKNGVDGEWTYDDATKTFTVTE");
		//target_topology = optional("target_topology","3A4P1A2");
		//minL = new Integer(optional("minL","5"));
		//maxL = new Integer(optional("maxL","5"));
		//minG = new Integer(optional("minG","4"));
		//numStrands = new Integer(optional("numStrands","4"));
		
		//1WVN
		protein = optional("protein","1WVN");
		sequence = optional("sequence","PLGSQTTHELTIPNNLIGCIIGRQGANINEIRQMSGAQIKIANPVEGSSGRQVTITGSAASISLAQYLINARLSSEKGMGCS");
		target_topology = optional("target_topology","1A3A2");
		minL = new Integer(optional("minL","8"));
		maxL = new Integer(optional("maxL","8"));
		minG = new Integer(optional("minG","8"));
		DI_file = optional("DI_file","PF00013_Q15365_MI_DI.csv");
		ev_threshold = new Double(optional("ev_threshold","0.018252"));
		evFoldStart = new Integer(optional("evFoldStart","7"));
		evFoldBase = new Integer(optional("evFoldBase","281"));
		numStrands = new Integer(optional("numStrands","3"));
		
		
		//2HDA
		//protein = optional("protein","2HDA");
		//sequence = optional("sequence","MGGGVTIFVALYDYEARTTEDLSFKKGERFQIINNTEGDWWEARSIATGKNGYIPSNYVAPADS");
		//target_topology = optional("target_topology","5A1A2A3A4");
		//minL = new Integer(optional("minL","5"));
		//maxL = new Integer(optional("maxL","5"));
		//minG = new Integer(optional("minG","4"));
		//DI_file = optional("DI_file","PF00018_P07947_MI_DI.csv");
		//ev_threshold = new Double(optional("ev_threshold","0.017162"));
		//evFoldStart = new Integer(optional("evFoldStart","9"));
		//evFoldBase = new Integer(optional("evFoldBase","97"));
		//numStrands = new Integer(optional("numStrands","5"));
		
		//1E6K
		//protein = optional("protein","1E6K");
		//sequence = optional("sequence","MRSDKELKFLVVADFSTMRRIVRNLLKELGFNNVEEAEDGVDALNKLQAGGYGFVISDWNMPNMDGLELLKTIRADGAMSALPVLMVTAEAKKENIIAAAQAGASGYVVKPFTAATLEEKLNKIFEKLGM");
		//target_topology = optional("target_topology","5P4P3P1P2");
		//minL = new Integer(optional("minL","4"));
		//maxL = new Integer(optional("maxL","5"));
		//minG = new Integer(optional("minG","16"));
		//DI_file = optional("DI_file","PF00072_P0AE67_MI_DI.csv");
		//ev_threshold = new Double(optional("ev_threshold","0.0058734"));
		//evFoldStart = new Integer(optional("evFoldStart","10"));
		//evFoldBase = new Integer(optional("evFoldBase","9"));
		//numStrands = new Integer(optional("numStrands","5"));
		
		//1RQM
		//protein = optional("protein","1RQM");
		//sequence = optional("sequence","ATMTLTDANFQQAIQGDGPVLVDFWAAWCGPCRMMAPVLEEFAEAHADKVTVAKLNVDENPETTSQFGIMSIPTLILFKGGEPVKQLIGYQPKEQLEAQLADVLQ");
		//target_topology = optional("target_topology","4A3A1P2");
		//minL = new Integer(optional("minL","6"));
		//maxL = new Integer(optional("maxL","6"));
		//minG = new Integer(optional("minG","3"));
		//DI_file = optional("DI_file","PF00085_P80579_MI_DI.csv");
		//ev_threshold = new Double(optional("ev_threshold","0.012009"));
		//evFoldStart = new Integer(optional("evFoldStart","3"));
		//evFoldBase = new Integer(optional("evFoldBase","3"));
		//numStrands = new Integer(optional("numStrands","4"));		
		
		//1F21
//		protein = optional("protein","1F21");
//		sequence = optional("sequence","MLKQVEIFTDGSALGNPGPGGYGAILRYRGREKTFSAGYTRTTNNRMELMAAIVALEALKEHAEVILSTDSQYVRQGITQWIHNWKKRGWKTADKKPVKNVDLWQRLDAALGQHQIKWEWVKGHAGHPENERADELARAAAMNPTLEDTGYQVEV");
//		target_topology = optional("target_topology","5P4P1A2A3");
//		minL = new Integer(optional("minL","11"));
//		maxL = new Integer(optional("maxL","11"));
//		minG = new Integer(optional("minG","5"));
//		DI_file = optional("DI_file","PF00075_P0A7Y4_MI_DI.csv");
//		ev_threshold = new Double(optional("ev_threshold","0.032396"));
//		evFoldStart = new Integer(optional("evFoldStart","3"));
//		evFoldBase = new Integer(optional("evFoldBase","3"));
//		numStrands = new Integer(optional("numStrands","5"));	
		
		//5PTI
		//protein = optional("protein","5PTI");
		//sequence = optional("sequence","RPDFCLEPPYTGPCKARIIRYFYNAKAGLCQTFVYGGCRAKRNNFKSAEDCMRTCGGA");
		//target_topology = optional("target_topology","3A1A2");
		//minL = new Integer(optional("minL","2"));
		//maxL = new Integer(optional("maxL","7"));
		//minG = new Integer(optional("minG","4"));
		//DI_file = optional("DI_file","PF00014_P00974_MI_DI.csv");
		//ev_threshold = new Double(optional("ev_threshold","0.028996"));
		//evFoldStart = new Integer(optional("evFoldStart","4"));
		//evFoldBase = new Integer(optional("evFoldBase","39"));
		//numStrands = new Integer(optional("numStrands","3"));

		
		
		
		evFold_threshold = new Double(optional("evFold_threshold","0"));
		alpha = new Double(optional("alpha","0.5")); 
		//numStrands = new Integer(optional("numStrands","3"));
		numSamples = new Integer(optional("numSamples","150"));
		//minL = new Integer(optional("minL","8"));
		//maxL = new Integer(optional("maxL","8"));
		//minG = new Integer(optional("minG","8"));
		maxComb = new Integer(optional("maxComb","1000000"));
		coilWeight = new Double(optional("coilWeight","0.25"));
		temp = new Double(optional("temp","1"));
		maxClusters = new Integer(optional("maxClusters","3"));
		startTime = new Double(optional("startTime","1E-6"));
		endTime = new Double(optional("endTime","1E6"));
		granularity = new Double(optional("granularity","0.05"));
		landscapeSvg = optional("landscapeSvg","");
		//pathFiles = optional("pathFiles","/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/Version_RECOMB11/eFold/experimentation/Files/StudyCase/Sets0/");
		pathFiles = optional("dynamicsPng","/scratch/dcbecerrar/efold/experimentation/Files/EvFold/Sets1/");
		loops = new Boolean(optional("loops","true"));
		limitClusters = new Integer(optional("limitClusters","998"));
		
		
		clusterMetric = optional("clusterMetric","metrics.ContactDistance");
		clusterMetricParameters = optional("clusterMetricParameters","2");
		//clusterThreshold = new Double(optional("clusterThreshold","0.12"));
		clusterThreshold = new Double(optional("clusterThreshold","1"));
		transitionMetric = optional("transitionMetric","metrics.ContactDistance");
		transitionMetricParameters = optional("transitionMetricParameters","2");
		data = opts.get("data");
		codebase = opts.get("codebase");
		jnlp = opts.get("jnlp");
		//transitionThreshold = new Double(optional("transitionThreshold","0.36"));
		transitionThreshold = new Double(optional("transitionThreshold","2"));
		recentering = new Double(optional("recentering","0"));
		antiPara = new Double(optional("antiPara","1.2"));
		Seq_ID = new String(optional("Seq_ID","RASH_HUMAN"));
		Pfam = new String(optional("Pfam","PF00547"));
		evFoldBase = new Integer(optional("evFoldBase","500"));
		PDB = new String(optional("PDB",pathFiles+"/Pdb/"+protein));
		BFS = new Boolean(optional("BFS","true"));
		//Change DSampling to true...........
		DSampling = new Boolean(optional("DSampling","false"));
		try {
			clustMetric = (SampleDistance) Class.forName(clusterMetric).getConstructor(String.class).newInstance(clusterMetricParameters);
			transMetric = (SampleDistance) Class.forName(transitionMetric).getConstructor(String.class).newInstance(transitionMetricParameters);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String get(String option){
		if(!opts.containsKey(option))
			throw new IllegalArgumentException("Command line option '-" + option + "' is required");
		if(opts.get(option) == null)
			throw new IllegalArgumentException("The value of the command line option '-" + option + "' is null");	
		return opts.get(option);
	}

	public String optional(String option, String alternative) {
		if(!opts.containsKey(option))
			return alternative;
		else
			return opts.get(option);
	}


}
