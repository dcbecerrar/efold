package foldpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;



import SS.Constants;
import SS.Constants.*;

import util.*;

public class Landscape_efold {
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
		for(int i=0;i<params.numStrands-1;i++){
			total_pairings_hash.add(i,new Hashtable<List<Constants.Dir>, Integer>());
		}
		allPairings(enumeratePairings(params.numStrands-1, params.numStrands-1), params.numStrands-1);
		//Print.print_pairings(total_pairings_hash);
		adjacents(Test,null,5,false);
	}
	
	public static List<Integer> adjacents(List<Integer> node, Template_final parent, int deep, boolean reverse){
		//Print.print_template(node);
		System.out.println("***************          " + node.toString()+"          ***************");
		Template_final temp = new Template_final(node,reverse);
		list_templates.add(temp);
		temp.set_itself(list_templates.size());
		if(parent != null){
			temp.set_parent(parent.get_itself());
		}
		temp.set_Z(total_pairings_hash.get(node.size()-2).size());
		List<List<Integer>> children = new LinkedList<List<Integer>>(); 
		strands(temp, parent);
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
		boolean inserted;
		int size_sibling;
		List<Integer>new_element;
		List<List<Integer>> List_new_element;
		min_value_i1 = 0;
		maxEnd = params.sequence.length();
		max_value_i1 = maxEnd-(2*params.minL+params.minG)+1;
		max_value_i2 = maxEnd-(params.minL)+1;
		List<Hashtable<List<Integer>,List<Double>>> Tables_1 = temp.get_Zs().get(0);
		List<Hashtable<List<Integer>,List<Double>>> Tables_2 = temp.get_Zs().get(1);
		Hashtable<Integer,List<List<Integer>>> Sibling_1 = temp.get_siblings().get(0);
		Hashtable<Integer,List<List<Integer>>> Sibling_2 = temp.get_siblings().get(1);
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
								new_element=new LinkedList<Integer>(Util.list(i1,j1,i2,j2,0));
								List_new_element = new LinkedList<List<Integer>>();
								List_new_element.add(new_element);
								//I must update the Z value						
								if(i2-j1>allow_space_center){
									size_sibling = Sibling_1.size();
									inserted=check_contains(Tables_1.get(pair),new_element,value,Util.list(value,(double)i1,(double)j1,(double)i2,(double)j2,(double) size_sibling));
									if(inserted){
										Sibling_1.put(size_sibling, List_new_element);
									}
								}	
								if(seq_length-j2>allow_space_border){
									size_sibling = Sibling_2.size();
									inserted=check_contains(Tables_2.get(pair),new_element,value,Util.list(value,(double)i1,(double)j1,(double)i2,(double)j2,(double)size_sibling));
									if(inserted){
										Sibling_2.put(size_sibling, List_new_element);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static int strands_window_area_final(List<Integer> key, boolean border, Template_final node, List<Hashtable<List<Integer>,List<Double>>> Tables_parent, int area, boolean reverse, Hashtable<Integer,List<List<Integer>>> Hash_sibling, boolean sibling){
		int min_value_i3, max_value_i3, min_value_j3, max_value_j3, maxEnd, pos=0, pair, size_sibling, parent_sibling;
		double value=0d, final_value=0d;
		boolean inserted;
		List<List<Integer>> List_sibling=null;
		List<Integer> temp = new LinkedList<Integer>(node.get_template()); 
		List<Double> values = new LinkedList<Double>();
		List<Double> new_value; 
		List<List<Integer>> key_newnodes=null;
		List<Integer> key_check_1;
		List<Integer> key_check_2;
		pos = 0;
		pos=temp.size();

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
			values = previous_pairing(new_key,temp,area,Tables_parent,key,reverse,border);
			parent_sibling=values.get(5).intValue();
			List_sibling = Hash_sibling.get(parent_sibling);
			for(int i3=min_value_i3; i3<max_value_i3; i3++){
				min_value_j3=min_value_j3(i3);
				max_value_j3=max_value_j3(min_value_j3,maxEnd);
				for(int j3=min_value_j3; j3<max_value_j3; j3++){
					
					for(int f=1; f<= enviro.length;f++){
						if(reverse){
							value = energy_pairings(values.get(1).intValue(),values.get(2).intValue(),i3,j3,Util.map_environment(f),new_key.get(new_key.size()-1));
						}else{
							value = energy_pairings(values.get(values.size()-3).intValue(),values.get(values.size()-2).intValue(),i3,j3,Util.map_environment(f),new_key.get(new_key.size()-1));
						}
						final_value = energy(values.get(0),params.temp)+value;
						final_value = zScore(final_value,params.temp);

						
						if(!sibling){
						
						key_newnodes=new LinkedList<List<Integer>>();
						if(border){
							for(int i=0;i<pos-1;i++){
								key_newnodes.add(null);
							}
							for(int i=0;i<pos-2;i++){
								key_check_2 = new LinkedList<Integer>(List_sibling.get(i));
								key_check_2.set(3, j3);
								key_check_2.set(4, i);
								key_newnodes.set(i, key_check_2);
							}
							key_check_1 = new LinkedList<Integer>(Util.list(key.get(0),key.get(3),i3,j3,pos-2));
							key_newnodes.set(pos-2, key_check_1);
							//key_newnodes.set(pos-1, key_check_1);
							//check_contains(node.get_Zs().get(area+2).get(area+1).get(pair-1),0, pair-1, key_check_1, final_value, new_value);
							//key_check_2 = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),key.get(2),j3));
							//new_check_contains(node,0,pair-1,key_check,final_value, new_value);

						
							for(int i=0;i<pos;i++){
								if(i==pos-1){
									if(seq_length-j3>allow_space_border){
										size_sibling = node.get_siblings().get(i).size();
										new_value = new LinkedList<Double>(Util.list(final_value,values.get(1),values.get(2),(double)i3,(double)j3,(double)size_sibling));
										inserted = check_contains(node.get_Zs().get(i).get(pair-1), key_newnodes.get(i-1), final_value, new_value);
										if(inserted){						
											node.get_siblings().get(i).put(size_sibling, key_newnodes);
											insert_siblings_boder(node,pair,pos,key_newnodes,i,final_value,new_value);
										}
									}
								}else{
									if(key_newnodes.get(i).get(2)-key_newnodes.get(i).get(1)>allow_space_center){
										size_sibling = node.get_siblings().get(i).size();
										new_value = new LinkedList<Double>(Util.list(final_value,values.get(1),values.get(2),(double)i3,(double)j3,(double)size_sibling));
										inserted = check_contains(node.get_Zs().get(i).get(pair-1), key_newnodes.get(i), final_value, new_value);
										if(inserted){
											node.get_siblings().get(i).put(size_sibling, key_newnodes);
											insert_siblings_boder(node,pair,pos,key_newnodes,i,final_value,new_value);
										}
									}
								}
							}

						}else{


							for(int i=0;i<pos-1;i++){
								key_newnodes.add(null);
							}
							int mv=0;
							for(int i=0;i<pos-2;i++){
								if(i!=area){
										key_newnodes.set(i+mv, new LinkedList<Integer>(List_sibling.get(i)));
								}else{
										key_check_2 = new LinkedList<Integer>(List_sibling.get(i));
										key_check_2.set(2, i3);
										key_newnodes.set(i, key_check_2);
										mv=1;
										key_check_2 = new LinkedList<Integer>(List_sibling.get(i));
										key_check_2.set(1, j3);
										key_newnodes.set(i+mv, key_check_2);
								}
							}
						
							//key_check_1 = new LinkedList<Integer>(Util.list(key.get(0),key.get(3),i3,j3));
							//key_newnodes.set(pos-1, new LinkedList<Integer>(List_sibling.get(pos-2)));

					
							mv=0;
							for(int i=0;i<pos;i++){
								if(i==area+1){
									mv=1;
								}
								if(i==pos-1){
									if(seq_length-key_newnodes.get(i-mv).get(3)>allow_space_border){
										size_sibling = node.get_siblings().get(i).size();
										new_value = new LinkedList<Double>(Util.list(final_value,values.get(1),values.get(2),(double)i3,(double)j3,(double)size_sibling));
										inserted = check_contains(node.get_Zs().get(i).get(pair-1), key_newnodes.get(i-mv), final_value, new_value);
										if(inserted){
											node.get_siblings().get(i).put(size_sibling, key_newnodes);
											insert_siblings_boder(node,pair,pos,key_newnodes,i,final_value,new_value);
										}
									}
								}else{
									if(key_newnodes.get(i).get(2)-key_newnodes.get(i).get(1)>allow_space_center){
										size_sibling = node.get_siblings().get(i).size();
										new_value = new LinkedList<Double>(Util.list(final_value,values.get(1),values.get(2),(double)i3,(double)j3,(double)size_sibling));
										inserted = check_contains(node.get_Zs().get(i).get(pair-1), key_newnodes.get(i-mv), final_value, new_value);
										if(inserted){
											node.get_siblings().get(i).put(size_sibling, key_newnodes);
											insert_siblings_boder(node,pair,pos,key_newnodes,i,final_value,new_value);
										}
									}
								}
							}


						}
						
						
						
						}else{
						
						
							
							
							
							
						}	
						

					}
				}
			}
		}
		return 1;
	}
	
	public static void insert_siblings_boder(Template_final node, int pair, int pos, List<List<Integer>> key_newnodes, int i, double final_value, List<Double> new_value){
		for(int j=0;j<pos;j++){
			if(j==pos-1){
				check_contains(node.get_Zs().get(i).get(pair-1), key_newnodes.get(j-1), final_value, new_value);
			}else{
				check_contains(node.get_Zs().get(i).get(pair-1), key_newnodes.get(j), final_value, new_value);
			}
		}
	}
	
	public static void insert_siblings_center(Template_final node, int pair, int pos, List<List<Integer>> key_newnodes, int i, double final_value, List<Double> new_value, int area){
		int mv=0;
		for(int j=0;j<pos;j++){
			if(j==area+1){
				mv=1;
			}
			check_contains(node.get_Zs().get(i).get(pair-1), key_newnodes.get(j-mv), final_value, new_value);
		}
	}
	
	public static void strands_area(Template_final node, Template_final parent){
		int new_value = node.get_template().get(node.get_template().size()-1);
		int i1,j1,i2,j2;
		boolean border=false;
		List<Hashtable<List<Integer>,List<Double>>> Tables_parent; 
		Hashtable<Integer,List<List<Integer>>> Table_siblings;
		List<Integer> key;
		int size_parent = parent.get_template().size();
		int area = -1;
		if(new_value>size_parent){
			area=size_parent-1;
			border = true;
		}else{
			area=new_value-2;
		}
		Tables_parent=parent.get_Zs().get(area);
		Table_siblings = parent.get_siblings().get(area);
		Enumeration<List<Integer>> parent_keys = parent.get_Zs().get(area).get(0).keys();
		boolean sibling;
		while (parent_keys.hasMoreElements()){
			key = parent_keys.nextElement();
			if(key.equals(Util.list(0,2,10,12))){
				System.out.println("HERE.........");
			}	
			System.out.println(key.toString());
			if(area == key.get(4)){
				sibling=true;
			}else{
				sibling=false;
			}
			strands_window_area_final(key, border, node, Tables_parent, area, node.get_reverse(),Table_siblings,sibling);
		}
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
	

	
/*	public static void new_check_contains(Template_final node, int area, int pair, List<Integer> key, double value, List<Double> new_value){
		double val=0d;
		Hashtable<List<Integer>,List<Double>> table = node.g; 
		if(node.get_Zs().get(area).get(pair).containsKey(key)){
			val = node.get_Zs().get(area).get(pair).get(key).get(0);
			if(value > val){		
				node.get_Zs().get(area).get(pair).put(key,new_value);
			}
		}else{
			node.get_Zs().get(area).get(pair).put(key,new_value);
		}
	}
*/	
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
	
	public static List<Double> previous_pairing(List<Dir> Pair, List<Integer> template, int area, List<Hashtable<List<Integer>,List<Double>>> Tables_parent, List<Integer> key, boolean reverse, boolean border){
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
			return Tables_parent.get(index-1).get(key);	
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

}
