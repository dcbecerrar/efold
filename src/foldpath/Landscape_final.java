package foldpath;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import SS.Constants;
import SS.Constants.*;

import util.*;

public class Landscape_final {
	public static List<Hashtable<List<Constants.Dir>,Integer>> total_pairings_hash = new LinkedList<Hashtable<List<Constants.Dir>, Integer>>();
	public static List<List<List<Constants.Dir>>> total_pairings = new LinkedList<List<List<Constants.Dir>>>();
	public static List<Template_final> list_templates = new LinkedList<Template_final>();
	public static Arguments params;
	public static Energy ef;
	public static int seq_length;
	public static int allow_space_border;
	public static int allow_space_center;
	
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		Arguments opts = new Arguments(args);
		params = opts; 
		seq_length = params.sequence.length();
		allow_space_border = params.minG+params.minL;
		allow_space_center = 2*params.minG+params.minL;
		generator();

	}
	
	public static void generator(){
		ef = new Energy("BURIED PARALLEL","BURIED ANTIPARALLEL","EXPOSED PARALLEL","EXPOSED ANTIPARALLEL","LOOP",params.coilWeight, params.recentering,params.antiPara);
		List<Integer> Test = new LinkedList<Integer>();
		List<List<Integer>> TestPerm = new LinkedList<List<Integer>>();
		Test.add(1);
		Test.add(2);
		//Test.add(2);
		//Test.add(4);
		//TestPerm = offsprings(Test);
		//Print.printl_inputs(TestPerm);
		for(int i=0;i<params.numStrands-1;i++){
			total_pairings_hash.add(i,new Hashtable<List<Constants.Dir>, Integer>());
		}
		allPairings(enumeratePairings(params.numStrands-1, params.numStrands-1), params.numStrands-1);
		//Print.print_pairings(total_pairings_hash);
		adjacents(Test,null,5,false);
		//strands_window(null);
	}
	
	public static List<Integer> adjacents(List<Integer> node, Template_final parent, int deep, boolean reverse){
		Print.print_template(node);
		Template_final temp = new Template_final(node,reverse);
		list_templates.add(temp);
		temp.set_itself(list_templates.size());
		if(parent != null){
			temp.set_parent(parent.get_itself());
		}
		temp.set_Z(total_pairings_hash.get(node.size()-2).size());
		List<List<Integer>> children = new LinkedList<List<Integer>>(); 
		strands(temp, parent);
		//System.out.println(temp.get_Zs().get(0).get(0).size());
		if(node.size()>=deep){
			temp.unset_Zs();
			return null;
		}else{
			children=offsprings(node);
			for(int i = 0; i < children.size();i++){
				if(i==children.size()-1){
					adjacents(children.get(i),temp,deep, true);
				}else{
					adjacents(children.get(i),temp,deep, false);
				}
			}
			temp.unset_Zs();
		}
		return null;
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
							pairings.add(pairing);
							if(number==size){
								total_pairings_hash.get(size-1).put(pairing,total_pairings_hash.get(size-1).size()+1);
							}			
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
				//if(i%2==0){
				//	if(!total_pairings_hash.get(str-2-j).containsKey(total_pairings.get(0).get(i).subList(0,total_pairings.get(0).get(i).size()-1))){
						aux.add(total_pairings.get(0).get(i).subList(0,total_pairings.get(0).get(i).size()-1));
						total_pairings_hash.get(str-2-j).put(total_pairings.get(0).get(i).subList(0,total_pairings.get(0).get(i).size()-1),total);
						total++;
					}
					/*if(!total_pairings_hash.get(str-2-j).containsKey(total_pairings.get(0).get(i).subList(1,total_pairings.get(0).get(i).size()))){
						aux.add(total_pairings.get(0).get(i).subList(1,total_pairings.get(0).get(i).size()));
						total_pairings_hash.get(str-2-j).put(total_pairings.get(0).get(i).subList(1,total_pairings.get(0).get(i).size()),total);
						total++;
					}*/
				//}
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
	
	public static void strands(Template_final temp, Template_final parent){
		if(temp.get_template().size()==2){
			strands_window(temp);
		}else{
			strands_area(temp, parent);
		}
	}
	public static void strands_window(Template_final temp){
		int max_value_i1, max_value_j1, min_value_i1, min_value_j1,  max_value_i2, max_value_j2, min_value_i2, min_value_j2, maxEnd;
		SS.Constants.Environment []enviro = new Constants.Environment[]{Constants.Environment.BURIED, Constants.Environment.EXPOSED};		
		double value;
		min_value_i1 = 0;
		maxEnd = params.sequence.length();
		max_value_i1 = maxEnd-(2*params.minL+params.minG)+1;
		max_value_i2 = maxEnd-(params.minL)+1;
		for(int i1=min_value_i1; i1<max_value_i1; i1++){
			min_value_j1=i1+params.minL-1;
			max_value_j1=Math.min(min_value_j1+(params.maxL-params.minL)+1, maxEnd-(params.minL+params.minG)+1);
			for(int j1=min_value_j1; j1<max_value_j1; j1++){
				min_value_i2 = j1+(params.minG)+1;
				for(int i2=min_value_i2; i2<max_value_i2;i2++){
					min_value_j2=i2+params.minL-1;
					max_value_j2 = Math.min(min_value_j2+(params.maxL-params.minL)+1, maxEnd);
					for(int j2=min_value_j2; j2<max_value_j2;j2++){
						for(int f=1; f<= enviro.length;f++){
							for(int pair = 0; pair < total_pairings_hash.get(0).size(); pair ++){
								value = energy_pairings(i1,j1,i2,j2,Util.map_environment(f),total_pairings.get(0).get(pair).get(0));
								value = zScore(value,params.temp); 
								new_check_contains(temp,0,pair,Util.list(i1,j1,i2,j2),value,Util.list(value,(double)i1,(double)j1,(double)i2,(double)j2,0d));
								if(i2-j1>allow_space_center){
									new_check_contains(temp,1,pair,Util.list(i1,j1,i2,j2),value,Util.list(value,(double)i1,(double)j1,(double)i2,(double)j2,1d));
								}	
								if(seq_length-j2>allow_space_border){
									new_check_contains(temp,2,pair,Util.list(i1,j1,i2,j2),value,Util.list(value,(double)i1,(double)j1,(double)i2,(double)j2,1d));
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void strands_area(Template_final temp, Template_final parent){
		int new_value = temp.get_template().get(temp.get_template().size()-1);
		int i1,j1,i2,j2;
		boolean border=false;
		List<Integer> key;
		int size_parent = parent.get_template().size();
		int area = -1;
		if(new_value>size_parent){
			area=size_parent;
			border = true;
		}else{
			area=new_value-1;
		}
		Enumeration<List<Integer>> enumKey = parent.get_Zs().get(new_value-1).get(0).keys();
		while(enumKey.hasMoreElements()) {
		    List<Integer> key_parent = enumKey.nextElement();
		    i1 = key_parent.get(0);
		    j1 = key_parent.get(1);
		    i2 = key_parent.get(2);
		    j2 = key_parent.get(3);
			//System.out.println(i1 + " ** "+j1 + " ** "+i2 + " ** "+j2);
			strands_window_area_final(Util.list(i1,j1,i2,j2),border,temp,parent,area,temp.get_reverse());
		}
		for(int i=0;i<=size_parent+1;i++){
			Enumeration<List<Integer>> enu = temp.get_Zs().get(i).get(0).keys();
			System.out.println("************************        " + i + "        ************************");
			while(enu.hasMoreElements()) {
			    List<Integer> key_parent = enu.nextElement();
			    i1 = key_parent.get(0);
			    j1 = key_parent.get(1);
			    i2 = key_parent.get(2);
			    j2 = key_parent.get(3);
				System.out.println(i1 + " ** "+j1 + " ** "+i2 + " ** "+j2);
			} 
		}
	}
	
	public static int strands_window_area_final(List<Integer> key, boolean border, Template_final node, Template_final parent, int area, boolean reverse){
		int min_value_i3, max_value_i3, min_value_j3, max_value_j3, maxEnd, pos=0, pair, parent_key_mirrors, key_mirrors, size_parent, mv=0;
		double value=0d, final_value=0d, max;
		boolean twin=true;
		boolean sibling=false;
		List<Integer> temp = new LinkedList<Integer>(node.get_template()); 
		List<Double> values = new LinkedList<Double>();
		List<Double> new_value; 
		List<Integer> key_check;
		pos = 0;
		pos=temp.size();
		size_parent = parent.get_template().size();
		List<Dir> new_key;
		SS.Constants.Environment []enviro = new Constants.Environment[]{Constants.Environment.BURIED, Constants.Environment.EXPOSED};
		maxEnd = maxEnd(key, border);
		min_value_i3 = min_value_i3(key, border);
		max_value_i3 = max_value_i3(key, border);
		Enumeration<List<Dir>> enumKey = total_pairings_hash.get(pos-2).keys();
		
		while(enumKey.hasMoreElements()){
			new_key = enumKey.nextElement();
			//util.Print.print_interactions(new_key);
			pair = total_pairings_hash.get(pos-2).get(new_key);
			values = previous_pairing(new_key,temp,area,parent,key,reverse,border);
			sibling = check_sibling(values,area, border);
			for(int i3=min_value_i3; i3<max_value_i3; i3++){
				min_value_j3=min_value_j3(i3);
				max_value_j3=max_value_j3(min_value_j3,maxEnd);
				for(int j3=min_value_j3; j3<max_value_j3; j3++){
					parent_key_mirrors = values.get(values.size()-1).intValue();
					for(int f=1; f<= enviro.length;f++){
						if(reverse){
							value = energy_pairings(values.get(1).intValue(),values.get(2).intValue(),i3,j3,Util.map_environment(f),new_key.get(new_key.size()-1));
						}else{
							value = energy_pairings(values.get(values.size()-3).intValue(),values.get(values.size()-2).intValue(),i3,j3,Util.map_environment(f),new_key.get(new_key.size()-1));
						}
						final_value = energy(values.get(0),params.temp)+value;
						final_value = zScore(final_value,params.temp);
						new_value = prepare_new_value(final_value,values,i3,j3); 
						if(!sibling){
							if(border){
								key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(3),i3,j3));
								new_check_contains(node,0, pair-1, key_check, final_value, new_value);
								//key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),key.get(2),j3));
								//new_check_contains(node,0,pair-1,key_check,final_value, new_value);
								
								if(seq_length-j3>allow_space_border){
									update_areas_border(node, pos, pair, key, final_value, new_value,i3,j3);
								}
								if(i3-key.get(3)>allow_space_center){
									update_areas_border(node, pos-1, pair, key, final_value, new_value,i3,j3);
								}
								if(key.get(2)-key.get(1)>allow_space_center){
									update_areas_border(node, pos-2, pair, key, final_value, new_value,i3,j3);
								}
							}else{
								key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),i3,key.get(3)));
								new_check_contains(node,0,pair-1,key_check,final_value,new_value);
								//key_check = new LinkedList<Integer> (Util.list(key.get(0),j3,key.get(2),key.get(3)));
								//new_check_contains(node,area+1,pair-1,key_check,final_value,new_value);				
								if(seq_length-key.get(3)>allow_space_border){
									update_areas_center(node, pos, pair, key, final_value, new_value,i3,j3);
								}
								if(i3-key.get(1)>allow_space_center){
									update_areas_center(node, area, pair, key, final_value, new_value,i3,j3);
								}
								if(key.get(2)-j3>allow_space_center){
									update_areas_center(node, area+1, pair, key, final_value, new_value,i3,j3);
								}			
							}
						}else{
							if(border){
								key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(3),i3,j3));
								new_check_contains(node,0, pair-1, key_check, final_value, new_value);
								if(key.get(2)-key.get(1)>allow_space_center){
									updates_sibling_border(node,values.get(values.size()-1).intValue(), pair, key, final_value, new_value, j3);
								}
							}else{
								new_check_contains(node,0, pair-1, key, final_value, new_value);
								if(key.get(2)-key.get(1)>allow_space_center){
									updates_sibling_center(node,values.get(values.size()-1).intValue(), pair, key, final_value, new_value);
								}
							}
						}
					}
				}
			}
		}
		return 1;
	}
	
	public static void updates_sibling_border(Template_final node, int area, int pair, List<Integer> key, double value, List<Double> new_value, int j3){
		List<Integer> key_check;
		key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),key.get(2),j3));
		new_value.set(new_value.size()-1, (double)area);
		key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),key.get(2),j3));
		new_check_contains(node,area, pair-1, key_check, value, new_value);
	}
	
	public static void updates_sibling_center(Template_final node, int area, int pair, List<Integer> key, double value, List<Double> new_value){
		int new_strand = node.get_template().get(node.get_template().size()-1);
		if(new_strand<=area){
			new_value.set(new_value.size()-1, (double)area);
			new_check_contains(node,area+1, pair-1, key, value, new_value);
		}else{
			new_value.set(new_value.size()-1, (double)area);
			new_check_contains(node,area, pair-1, key, value, new_value);
		}
	}
	
	public static void update_areas_border(Template_final node, int area, int pair, List<Integer> key, double value, List<Double> new_value, int i3, int j3){
		List<Integer> key_check;
		List<Double> new_value_aux = new LinkedList<Double>(new_value);
		key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(3),i3,j3));
		new_value_aux.set(new_value.size()-1, new_value.get(new_value.size()-1)+1);
		new_check_contains(node, area, pair-1, key_check, value, new_value_aux);
		key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),key.get(2),j3));
		new_check_contains(node, area,pair-1,key_check,value, new_value);
	}
	
	public static void update_areas_center(Template_final node, int area, int pair, List<Integer> key, double value, List<Double> new_value, int i3, int j3){
		List<Integer> key_check;
		List<Double> new_value_aux = new LinkedList<Double>(new_value);
		key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),i3,key.get(3)));
		new_check_contains(node,area,pair-1,key_check,value,new_value);
		key_check = new LinkedList<Integer> (Util.list(key.get(0),j3,key.get(2),key.get(3)));
		new_value_aux.set(new_value.size()-1, new_value.get(new_value.size()-1)+1);
		new_check_contains(node,area,pair-1,key_check,value,new_value_aux);
	}
	
	public static boolean check_sibling(List<Double> value, double area, boolean border){
		if(border){
			if(value.get(value.size()-1)==area-1){
				return false;
			}else{
				return true;
			}
		}else{
			if(value.get(value.size()-1)==area){
				return false;
			}else{
				return true;
			}
		}
	}
	
	public static int maxEnd(List<Integer> key, boolean border){
		int maxEnd;
		if(border){
			maxEnd = params.sequence.length();
		}else{
			maxEnd = key.get(2)-params.minG;
		}
		return maxEnd;		
	}
	
	public static int min_value_i3(List<Integer> key, boolean border){
		int min_value_i3;
		if(border){
			min_value_i3 = key.get(3)+params.minG+1;
		}else{
			min_value_i3 = key.get(1)+params.minG+1;
		}
		return min_value_i3; 
	}
	
	public static int max_value_i3(List<Integer> key, boolean border){
		int max_value_i3;
		if(border==true){
			max_value_i3 = params.sequence.length()-params.minL+1;
		}else{
			max_value_i3 = key.get(2)-params.minL-params.minG+1;
		}
		return max_value_i3;
	}
	
	public static int min_value_j3(int i3){
		int min_value_j3;
		min_value_j3=i3+params.minL-1;
		return min_value_j3;
	}
	
	public static int max_value_j3(int min_value_j3,int maxEnd){
		int max_value_j3;
		max_value_j3=Math.min(min_value_j3+(params.maxL-params.minL)+1, maxEnd);
		return max_value_j3;
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
	
	public static double zScore(double energy, double beta){
		return Math.exp(-energy/beta);
	}
	
	public static double energy(double zScore, double beta){
		return Math.log(zScore)*-beta;
	}
	
	public static void new_check_contains(Template_final node, int area, int pair, List<Integer> key, double value, List<Double> new_value){
		double val=0d;
		if(node.get_Zs().get(area).get(pair).containsKey(key)){
			val = node.get_Zs().get(area).get(pair).get(key).get(0);
			if(value > val){		
				node.get_Zs().get(area).get(pair).put(key,new_value);
			}
		}else{
			node.get_Zs().get(area).get(pair).put(key,new_value);
		}
	}
	
	public static List<Double> previous_pairing(List<Dir> Pair, List<Integer> template, int area, Template_final parent, List<Integer> key, boolean reverse, boolean border){
		List<Dir> previous_pair= new LinkedList<Dir>(Pair.subList(0,Pair.size()-1));
		int index, new_area;
		//Temporary fix, I have to fix it.
		if(previous_pair.get(previous_pair.size()-1)==Dir.NONE){
			previous_pair.set(previous_pair.size()-1, Dir.PARA);
		}
			if(reverse){
				Collections.reverse(previous_pair);
			}
			index = total_pairings_hash.get(previous_pair.size()-1).get(previous_pair);
			return parent.get_Zs().get(area).get(index-1).get(key);
		
	}

	public static List<Double> prepare_new_value(double value, List<Double> prev_value,double i3,double j3){
		List<Double> new_value = new LinkedList<Double>(prev_value);
		new_value.set(0,value);
		new_value.add(new_value.size()-1, (double)i3);
		new_value.add(new_value.size()-1, (double)j3);
		return new_value;
	}

}
