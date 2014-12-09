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

public class Landscape {
	public static List<Hashtable<List<Constants.Dir>,Integer>> total_pairings_hash = new LinkedList<Hashtable<List<Constants.Dir>, Integer>>();
	public static List<List<List<Constants.Dir>>> total_pairings = new LinkedList<List<List<Constants.Dir>>>();
	public static List<Template> list_templates = new LinkedList<Template>();
	public static Arguments params;
	public static Energy ef;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		Arguments opts = new Arguments(args);
		params = opts; 
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

	
	public static List<Integer> adjacents(List<Integer> node, Template parent, int deep, boolean reverse){
		Print.print_template(node);
		Template temp = new Template(node,reverse);
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
	public static void strands(Template temp, Template parent){
		if(temp.get_template().size()==2){
			strands_window(temp);
		}else{
			strands_area(temp, parent);
		}
	}
	
	public static void strands_area(Template temp, Template parent){
		int new_value = temp.get_template().get(temp.get_template().size()-1);
		boolean border=false;
		List<Integer> key;
		int size_parent = parent.get_template().size();
		int area = -1;
		if(new_value>size_parent){
			area=size_parent-2;
			border = true;
		}else{
			area=new_value-2;
		}
		possible_areas(area,temp,parent,border);
		for(int i=0;i<size_parent;i++){
			if(i!=area && i!=area+1){
				//fill_siblings(node,parent,i,pair-1, new_key,(double)i3,(double)j3,value,reverse,border);
				possible_areas_siblings(area, i, temp, parent, border);
			}
		}
	}

	public static void possible_areas(int area, Template temp, Template parent, boolean border){
		int i1, i2, j1, j2, size, maxEnd, size_parent;
		int min_i1, max_i1, min_i2, max_i2, min_j1, max_j1, min_j2, max_j2;
		size = temp.get_template().size();
		size_parent = parent.get_template().size();
		maxEnd = params.sequence.length();
		min_i1=0;
		max_i1= maxEnd - ((size*params.minL)+((size-1)*params.minG)) + 1;
		for(i1=min_i1;i1<max_i1;i1++){
			min_j1=i1+(area+1)*params.minL+(area)*params.minG-1;
			if(border){
				if(area==0){
					max_j1 = Math.min(i1 + params.maxL, maxEnd -(((size-(area+1))*params.minL)+((size-(area+1))*params.minG))+1);
				}else{
					max_j1= maxEnd -(((size-(area+1))*params.minL)+((size-(area+1))*params.minG))+1;
				}	
			}else{
				if(area==0){
					max_j1 = Math.min(i1 + params.maxL,maxEnd -(((size-(area+1))*params.minL)+((size-(area+1))*params.minG)));
				}else{
					max_j1= maxEnd -(((size-(area+1))*params.minL)+((size-(area+1))*params.minG));
				}
			}
			for(j1=min_j1;j1<max_j1;j1++){
				if(border){
					min_i2=j1+params.minG+1;
					max_i2= maxEnd - (((size-(area+2))*params.minL)+((size-(area+2))*params.minG));
				}else{
					min_i2=j1+(2*params.minG)+params.minL+1;
					max_i2=maxEnd - (((size-(area+2))*params.minL)+((size-(area+3))*params.minG)) +1;
				}
				for(i2=min_i2;i2<max_i2;i2++){
					if(border){
						min_j2=i2+(size-(area+2))*params.minL+(size-(area+3))*params.minG-1;
						if(size_parent-(area+1)==1){
							max_j2=Math.min(maxEnd-(params.minL+params.minG), i2+(size-(area+2))*params.maxL+(size-(area+3))*params.minG);
						}else{
							max_j2=maxEnd-(params.minL+params.minG);
						}
					}else{
						min_j2=i2+(size-(area+2))*params.minL+(size-(area+3))*params.minG-1;
						if(size_parent-(area+1)==1){
							max_j2=Math.min(maxEnd, i2+(size-(area+2))*params.maxL+(size-(area+3))*params.minG);
						}else{
							max_j2=maxEnd;
						}
					}
					for(j2=min_j2;j2<max_j2;j2++){
						System.out.println(i1 + " ** "+j1 + " ** "+i2 + " ** "+j2);
						strands_window_area_final(Util.list(i1,j1,i2,j2),border,temp,parent,area,-1,temp.get_reverse(),false, false);
					}
				}
			}
		}
	}
	
	public static void possible_areas_siblings(int area, int area_i, Template temp, Template parent, boolean border){
		System.out.println("********************  "+ area_i+ "    ********************");
		int i1, i2, j1, j2, size, maxEnd;
		int min_i1, max_i1, min_i2, max_i2, min_j1, max_j1, min_j2, max_j2;
		boolean add=false;
		//Delet those two variables.
		int sum_found=0, sum_nofound=0, val;
		size = temp.get_template().size();
		maxEnd = params.sequence.length();
		min_i1=0;
		max_i1= maxEnd - ((size*params.minL)+((size-1)*params.minG)) + 1;
		if(area_i==size-2){
			area_i=area_i-1; //This is because the last area is found using the last of the parent.
			add=true;
		}
		for(i1=min_i1;i1<max_i1;i1++){
			if(area>area_i){
				min_j1=i1+(area_i+1)*params.minL+(area_i)*params.minG-1;
				max_j1=Math.min(maxEnd-(((size-(area_i+1))*params.minL)+((size-(area_i+1))*params.minG)),i1+(area_i+1)*params.maxL+(area_i)*params.minG);
			}else{
				if(add){
					min_j1=i1+(area_i+2)*params.minL+(area_i+1)*params.minG-1;
				}else{
					min_j1=i1+(area_i+1)*params.minL+(area_i)*params.minG-1;
				}
				if(border){
						max_j1=Math.min(maxEnd-(((size-(area_i+2))*params.minL)+((size-(area_i+2))*params.minG)),i1+(area_i+2)*params.maxL+(area_i+1)*params.minG);
				}else{
					if(add){
						//HERE...........
						if((size-1)-(area_i+1)==1){
							max_j1=maxEnd-(((size-(area_i+2))*params.minL)+((size-(area_i+2))*params.minG));
						}else{
							max_j1=Math.min(maxEnd-(((size-(area_i+2))*params.minL)+((size-(area_i+2))*params.minG)),i1+(area_i+2)*params.maxL+(area_i+2)*params.minG);
						}
					}else{
						if((size-1)-(area_i)==1){
							max_j1=maxEnd-(((size-(area_i+1))*params.minL)+((size-(area_i+1))*params.minG));
						}else{
							max_j1=Math.min(maxEnd-(((size-(area_i+1))*params.minL)+((size-(area_i+1))*params.minG)),i1+(area_i+1)*params.maxL+(area_i+1)*params.minG);
						}
						
					}
				}
			}
			for(j1=min_j1;j1<max_j1;j1++){
				min_i2=j1+params.minG+1;
				if(area>area_i){			
						max_i2= maxEnd - (((size-(area_i+1))*params.minL)+((size-(area_i+2))*params.minG)) +1;
				}else{
					if(border){
						max_i2= maxEnd - (((size-(area_i+1))*params.minL)+((size-(area_i+2))*params.minG)) +1;
					}else{
						if(add){
							max_i2=maxEnd - (((size-(area_i+2))*params.minL)+((size-(area_i+3))*params.minG)) +1;
						}else{
							max_i2=maxEnd - (((size-(area_i+1))*params.minL)+((size-(area_i+2))*params.minG)) +1;
						}
					}
				}
				for(i2=min_i2;i2<max_i2;i2++){
					if(area>area_i){
						if(border){
							min_j2=i2+(size-(area_i+2))*params.minL+(size-(area_i+3))*params.minG-1;
							max_j2=maxEnd-(params.minL+params.minG);
						}else{
							min_j2=i2+(size-(area_i+1))*params.minL+(size-(area_i+2))*params.minG-1;
							max_j2=maxEnd;
						}
					}else{
						if (add){
							min_j2=i2+(size-(area_i+2))*params.minL+(size-(area_i+3))*params.minG-1;
						}else{
							min_j2=i2+(size-(area_i+1))*params.minL+(size-(area_i+2))*params.minG-1;
						}
						if(border){
							if((size-1)-(area_i+1)==1){
								max_j2=Math.min(i2+params.maxL, maxEnd-(params.minL+params.minG));
							}else{
								max_j2=maxEnd-(params.minL+params.minG);
							}
						}else{
							if(add){
								if((size-1)-(area_i+1)==1){
									max_j2=Math.min(i2+params.maxL, maxEnd);
								}else{
									max_j2=maxEnd;
								}
							}else{
								if((size-1)-(area_i)==1){
									max_j2=Math.min(i2+params.maxL, maxEnd);
								}else{
									max_j2=maxEnd;
								}
							}

						}
					}
					for(j2=min_j2;j2<max_j2;j2++){
						System.out.println(i1 + " ** "+j1 + " ** "+i2 + " ** "+j2);
						val=strands_window_area_final(Util.list(i1,j1,i2,j2), border, temp, parent, area, area_i, temp.get_reverse(), true, add);
						if(val==1){
							sum_nofound++;
						}else{
							sum_found++;
						}
					}
				}
			}
		}
		System.out.println("Twins = "+ sum_found + " **************** "+ "NoTwins = "+ sum_nofound);
	}

	public static List<Integer> parent_template(List<Integer> template, double new_value){
		List<Integer> parent_template = new LinkedList<Integer>(template);
		for(int i=0;i<parent_template.size();i++){
			if(parent_template.get(i)>new_value){
				parent_template.set(i, parent_template.get(i));
			}
		}
		return parent_template;
	}
/*	public static void strands_window_area(List<Integer> key, boolean border, Template node, Template parent, int area, int area_i, boolean reverse, boolean sibling, boolean add){
		int min_value_i3, max_value_i3, min_value_j3, max_value_j3, maxEnd, pos=0, pair;
		double value=0d, final_value=0d, max;
		List<Double> values = new LinkedList<Double>();
		List<Integer> key_check;
		pos = 0;
		pos=node.get_template().size();
		List<Dir> new_key;
		SS.Constants.Environment []enviro = new Constants.Environment[]{Constants.Environment.BURIED, Constants.Environment.EXPOSED};
		maxEnd = maxEnd(key, sibling, border, area, area_i, node.get_template(),add);
		min_value_i3 = min_value_i3(key, sibling, border, area, area_i,add);
		max_value_i3 = max_value_i3(key, sibling, border, area, area_i,maxEnd,add);
		Enumeration<List<Dir>> enumKey = total_pairings_hash.get(pos-2).keys();
		while(enumKey.hasMoreElements()){
			new_key = enumKey.nextElement();
			//util.Print.print_interactions(new_key);
			pair = total_pairings_hash.get(pos-2).get(new_key);
			if(!sibling){
				values = previous_pairing(new_key,node.get_template(),area,parent,key,reverse,border,sibling);
			}else{
				if(area>area_i){
					values = previous_pairing(new_key,node.get_template(),area_i,parent,key,reverse,border,sibling);
				}else{
					if(add){
						values = previous_pairing(new_key,node.get_template(),area_i,parent,key,reverse,border,sibling);
					}else{
						values = previous_pairing(new_key,node.get_template(),area_i-1,parent,key,reverse,border,sibling);
					}
				}
			}
			for(int i3=min_value_i3; i3<max_value_i3; i3++){
				min_value_j3=min_value_j3(i3);
				max_value_j3=max_value_j3(min_value_j3,maxEnd);
				for(int j3=min_value_j3; j3<max_value_j3; j3++){
					for(int f=1; f<= enviro.length;f++){
						//double max_value=Double.MIN_VALUE;
						if(reverse){
							value = energy_pairings(values.get(1).intValue(),values.get(2).intValue(),i3,j3,Util.map_environment(f),new_key.get(new_key.size()-1));
						}else{
							value = energy_pairings(values.get(3).intValue(),values.get(4).intValue(),i3,j3,Util.map_environment(f),new_key.get(new_key.size()-1));
						}
						final_value = energy(values.get(0),params.temp)+value;
						final_value = zScore(final_value,params.temp);
						if(!sibling){
							if(border){
								key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(3),i3,j3));
								check_contains(node, area+1, pair-1, key_check, final_value, values.get(1),values.get(2),(double) i3,(double) j3);
								key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),key.get(2),j3));
								check_contains(node,area,pair-1,key_check,final_value, values.get(1),values.get(2),(double) i3,(double) j3);
								//updating the big areas
								key_check = new LinkedList<Integer>(Util.list(key.get(0),j3));
								check_contains(node, area+1, pair-1, key_check, final_value, values.get(1),values.get(2),(double) i3,(double) j3);
								check_contains(node,area,pair-1,key_check,final_value, values.get(1),values.get(2),(double) i3,(double) j3);								
							}else{
								key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),i3,key.get(3)));
								check_contains(node,area,pair-1,key_check,final_value,values.get(1),values.get(2),(double) i3,(double) j3);
								key_check = new LinkedList<Integer> (Util.list(key.get(0),j3,key.get(2),key.get(3)));
								check_contains(node,area+1,pair-1,key_check,final_value,values.get(1),values.get(2),(double) i3,(double) j3);
								//updating the big areas
								key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(3)));
								check_contains(node,area,pair-1,key_check,final_value,values.get(1),values.get(2),(double) i3,(double) j3);
								key_check = new LinkedList<Integer> (Util.list(key.get(0),key.get(3)));
								check_contains(node,area+1,pair-1,key_check,final_value,values.get(1),values.get(2),(double) i3,(double) j3);
							}
						}else{
							if(border){
								key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),key.get(2),j3));
								check_contains(node,area_i,pair-1,key_check,final_value,values.get(1),values.get(2),(double) i3,(double) j3);
								//updating the big areas
								key_check = new LinkedList<Integer>(Util.list(key.get(0),j3));
								check_contains(node,area_i,pair-1,key_check,final_value,values.get(1),values.get(2),(double) i3,(double) j3);
							}else{
								if(add){
									check_contains(node,area_i+1,pair-1,key,final_value,values.get(1),values.get(2),(double) i3,(double) j3);
									//updating the big areas
									key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(3)));
									check_contains(node,area_i+1,pair-1,key_check,final_value,values.get(1),values.get(2),(double) i3,(double) j3);
								}else{
									check_contains(node,area_i,pair-1,key,final_value,values.get(1),values.get(2),(double) i3,(double) j3);
									//updating the big areas
									key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(3)));
									check_contains(node,area_i,pair-1,key_check,final_value,values.get(1),values.get(2),(double) i3,(double) j3);
								}
							}
							//System.out.println(key.get(0) + " ** "+key.get(1) + " ** "+key.get(2) + " ** "+key.get(3)+ " ** "+i3 + " ** "+j3);
						}
					}

				}
			}
		}
	}
	*/
	
	public static int strands_window_area_final(List<Integer> key, boolean border, Template node, Template parent, int area, int area_i, boolean reverse, boolean sibling, boolean add){
		int min_value_i3, max_value_i3, min_value_j3, max_value_j3, maxEnd, pos=0, pair, parent_key_mirrors, key_mirrors, size_parent, mv=0;
		double value=0d, final_value=0d, max;
		boolean twin=true;
		List<Integer> temp = new LinkedList<Integer>(node.get_template()); 
		List<Double> values = new LinkedList<Double>();
		List<Double> new_value; 
		List<Integer> key_check;
		pos = 0;
		pos=temp.size();
		size_parent = parent.get_template().size();
		List<Dir> new_key;
		SS.Constants.Environment []enviro = new Constants.Environment[]{Constants.Environment.BURIED, Constants.Environment.EXPOSED};
		maxEnd = maxEnd(key, sibling, border, area, area_i, node.get_template(),add);
		min_value_i3 = min_value_i3(key, sibling, border, area, area_i,add);
		max_value_i3 = max_value_i3(key, sibling, border, area, area_i,maxEnd,add);
		Enumeration<List<Dir>> enumKey = total_pairings_hash.get(pos-2).keys();
		while(enumKey.hasMoreElements()){
			new_key = enumKey.nextElement();
			//util.Print.print_interactions(new_key);
			pair = total_pairings_hash.get(pos-2).get(new_key);
			if(!sibling){
				values = previous_pairing(new_key,temp,area,parent,key,reverse,border,sibling);
			}else{
				if(area>area_i){
					values = previous_pairing(new_key,temp,area_i,parent,key,reverse,border,sibling);
				}else{
					if(add){
						values = previous_pairing(new_key,temp,area_i,parent,key,reverse,border,sibling);
					}else{
						values = previous_pairing(new_key,temp,area_i-1,parent,key,reverse,border,sibling);
					}
				}
			}
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
								new_check_contains(node, area+1, pair-1, key_check, final_value, new_value);
								key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),key.get(2),j3));
								new_check_contains(node,area,pair-1,key_check,final_value, new_value);
								//updating the big areas
								//key_check = new LinkedList<Integer>(Util.list(key.get(0),j3));
								//new_check_contains(node, area+1, pair-1, key_check, final_value, new_value);
								//new_check_contains(node,area,pair-1,key_check,final_value, new_value);
								//updating the twins
								if(temp.size()>3){
									update_twins(node, area, pair, value, new_value, temp);
								}	
							}else{
								key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),i3,key.get(3)));
								new_check_contains(node,area,pair-1,key_check,final_value,new_value);
								key_check = new LinkedList<Integer> (Util.list(key.get(0),j3,key.get(2),key.get(3)));
								new_check_contains(node,area+1,pair-1,key_check,final_value,new_value);
								//updating the big areas
								//key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(3)));
								//new_check_contains(node,area,pair-1,key_check,final_value,new_value);
								//key_check = new LinkedList<Integer> (Util.list(key.get(0),key.get(3)));
								//new_check_contains(node,area+1,pair-1,key_check,final_value,new_value);
								//updating the twins
								if(temp.size()>3){
									update_twins(node, area, pair, value, new_value, temp);
								}	
							}
						}else{
							if(border){
								key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(1),key.get(2),j3));
								if(twin && node.get_Zs().get(area_i).get(pair-1).containsKey(key_check)){
									return -1;
								}else{
									twin=false;
								}
								new_check_contains(node,area_i,pair-1,key_check,final_value,new_value);
								//updating the big areas
								//key_check = new LinkedList<Integer>(Util.list(key.get(0),j3));
								//new_check_contains(node,area_i,pair-1,key_check,final_value,new_value);
								//Updating the twins
							}else{
								if(add){
									if(twin && node.get_Zs().get(area_i+1).get(pair-1).containsKey(key)){
										return -1;
									}else{
										twin=false;
									}
									new_check_contains(node,area_i+1,pair-1,key,final_value,new_value);
									//updating the big areas
									//key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(3)));
									//new_check_contains(node,area_i+1,pair-1,key_check,final_value,new_value);
								}else{
									if(twin && node.get_Zs().get(area_i).get(pair-1).containsKey(key)){
										return -1;
									}else{
										twin=false;
									}
									new_check_contains(node,area_i,pair-1,key,final_value,new_value);
									//updating the big areas
									//key_check = new LinkedList<Integer>(Util.list(key.get(0),key.get(3)));
									//new_check_contains(node,area_i,pair-1,key_check,final_value,new_value);
								}
								
								///Updating the twins
							}
							//System.out.println(key.get(0) + " ** "+key.get(1) + " ** "+key.get(2) + " ** "+key.get(3)+ " ** "+i3 + " ** "+j3);
						}
					}
				}
			}
		}
		return 1;
	}
	

	
	public static int min_value_i3(List<Integer> key, boolean sibling, boolean border, int area, int area_i, boolean add){
		int min_value_i3;
		if(border==true){
			min_value_i3 = key.get(3)+params.minG+1;
		}else{
			if(!sibling){
				min_value_i3 = key.get(1)+params.minG+1;
			}else{
				if(area>area_i){
					min_value_i3 =  key.get(2)+(area-area_i)*params.minL+(area-area_i)*params.minG;
				}else{
					min_value_i3 = key.get(0)+(area+1)*params.minL+(area+1)*params.minG;
				}
			}
		}
		return min_value_i3; 
	}
	public static int max_value_i3(List<Integer> key, boolean sibling, boolean border,int area, int area_i, int maxEnd, boolean add){
		int max_value_i3;
		if(border==true){
			max_value_i3 = params.sequence.length()-params.minL+1;
		}else{
			if(!sibling){
				max_value_i3 = key.get(2)-params.minL-params.minG+1;
			}else{
				if(area>area_i){
					max_value_i3 =  Math.min(maxEnd-params.minL+1,key.get(2)+(area-area_i)*params.maxL+(area-area_i)*params.minG);
				}else{


						max_value_i3 = Math.min(maxEnd-params.minL+1,key.get(0)+(area+1)*params.maxL+(area+1)*params.minG);

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
	public static int max_value_j3(int min_value_j3,int maxEnd){
		int max_value_j3;
		max_value_j3=Math.min(min_value_j3+(params.maxL-params.minL)+1, maxEnd);
		return max_value_j3;
	}
	public static int maxEnd(List<Integer> key, boolean sibling, boolean border, int area, int area_i, List<Integer> Template, boolean add){
		int maxEnd;
		int new_element = Template.get(Template.size()-1);
		int size = Template.size();
		if(border==true){
			maxEnd = params.sequence.length();
		}else{
			if(!sibling){
				maxEnd = key.get(2)-params.minG;
			}else{
				if(area>area_i){
					maxEnd = key.get(3)-((size-new_element)*params.minL+(size-new_element)*params.minG)+1;
				}else{
					if(add){
						maxEnd = key.get(1)-(((area_i+2)-new_element)*params.minL+((area_i+2)-new_element)*params.minG)+1;
					}else{
						maxEnd = key.get(1)-(((area_i+1)-new_element)*params.minL+((area_i+1)-new_element)*params.minG)+1;
					}
				}
				
			}
		}
		return maxEnd;		
	}
	
	public static List<Double> previous_pairing(List<Dir> Pair, List<Integer> template, int area, Template parent, List<Integer> key, boolean reverse, boolean border, boolean siblings){
		List<Dir> previous_pair= new LinkedList<Dir>(Pair.subList(0,Pair.size()-1));
		int index, new_area;
		if(previous_pair.get(previous_pair.size()-1)==Dir.NONE){
			double new_z=0d;
			List<Integer> aux_temp = null;
			List<Integer> new_key;
			List<Dir> aux_pair = new LinkedList<Dir>(previous_pair);
			aux_pair.set(previous_pair.size()-1, Dir.PARA);
			index = total_pairings_hash.get(previous_pair.size()-1).get(aux_pair);
			List<Double> previous_value_parent = new LinkedList<Double>(parent.get_Zs().get(area).get(index-1).get(key));
			List<Dir> grandparent_pair;
			if(reverse){
				Collections.reverse(previous_pair);
				grandparent_pair= new LinkedList<Dir>(previous_pair.subList(1,previous_pair.size()));
				aux_temp = new LinkedList<Integer>(template.subList(0, template.size()-1));
				Collections.reverse(aux_temp);
			}else{
				grandparent_pair= new LinkedList<Dir>(previous_pair.subList(0,previous_pair.size()-1));
			}
			Template grand_parent=list_templates.get(parent.get_parent()-1);
			
			//List<Integer> new_key = get_none_key(template,grand_parent.get_template(), key, reverse, border, siblings, area);
			index = total_pairings_hash.get(grandparent_pair.size()-1).get(grandparent_pair);	
			new_area = get_none_area(parent);
			if(reverse){
				if(parent.get_reverse()){
					Collections.reverse(aux_temp);
				}	
				new_key = get_none_key(previous_value_parent,new_area, grand_parent.get_template(), aux_temp);	
			}else{
				new_key = get_none_key(previous_value_parent,new_area, grand_parent.get_template());
			}
			new_z = grand_parent.get_Zs().get(new_area).get(index-1).get(new_key).get(0);
			previous_value_parent.set(0, new_z);
			return previous_value_parent;
		}else{
			if(reverse){
				Collections.reverse(previous_pair);
			}
			index = total_pairings_hash.get(previous_pair.size()-1).get(previous_pair);
			return parent.get_Zs().get(area).get(index-1).get(key);
		}
	}
	
	public static List<Integer> get_none_key(List<Double> prev_value, int new_area, List<Integer> Template){
		int i1,i2,j1,j2;
		i1 = prev_value.get(2*Template.indexOf(1)+1).intValue();
		j1 = prev_value.get(2*Template.indexOf(new_area+1)+2).intValue();
		i2 = prev_value.get(2*Template.indexOf(new_area+2)+1).intValue();
		j2 = prev_value.get(2*Template.indexOf(Template.size())+2).intValue();	
		return Util.list(i1,j1,i2,j2);
	}
	
	public static List<Integer> get_none_key(List<Double> prev_value, int new_area, List<Integer> Template, List<Integer> aux_Template){
		int i1,i2,j1,j2;
		int min, max, aux=0;
		if(aux_Template.get(0)==1){
			min=3;
			aux++;
		}else{
			min=1;
		}
		if(new_area+1>=aux_Template.get(0)){
			aux++;
		}
		if(aux_Template.get(0)==aux_Template.size()+1){
			max=aux_Template.size();
		}else{
			max=aux_Template.size()+1;
		}
		i1 = prev_value.get(2*aux_Template.indexOf(min)+1).intValue();
		j1 = prev_value.get(2*aux_Template.indexOf(new_area+1+aux)+2).intValue();
		i2 = prev_value.get(2*aux_Template.indexOf(new_area+2+aux)+1).intValue();
		j2 = prev_value.get(2*aux_Template.indexOf(max)+2).intValue();	
		if(i2-j1<=params.minG){
			i2++;
		}
		return Util.list(i1,j1,i2,j2);
	}
	
	public static List<Integer> get_none_key(List<Integer> prev_template, List<Integer> new_template, List<Integer> key, boolean reverse, boolean border, boolean sibling, int area){
		int max;
		int new_value; 
		boolean border_parent=false;
		int size_parent = new_template.size();
		List<Integer> none_key = new LinkedList<Integer>();
		if(!reverse){
			new_value = new_template.get(new_template.size()-1);
		}else{
			new_value = new_template.get(0);
		}
		if(!reverse){
			none_key.add(key.get(0));
			if(true)
			if(new_value>size_parent-1){
				border_parent = true;
			}
		}else{
			none_key.add(key.get(3));
			if(new_value==1){
				border_parent = true;
			}
		}
		if(!sibling){
			if(border_parent){
				if(border){
					none_key.add(key.get(1));
				}else{
					if(!reverse){
						none_key.add(key.get(3)-(params.minL+params.minG));
					}else{
						none_key.add(0,key.get(2));
					}
				}	
			}else{
				if(!reverse){
					none_key.add(key.get(3));
				}else{
					none_key.add(0,key.get(0));
				}
			}
		}else{
			if(border_parent){
				if(border){
					none_key.add(key.get(3)-(params.minL+params.minG));
				}else{
					if(!reverse){
						if(area>new_value){
							none_key.add(key.get(1));
						}else{
							max = get_max(prev_template.subList(0, size_parent));
							none_key.add(key.get(3)-(((prev_template.size())-max)*(params.minL+params.minG)));
						}
					}else{
						none_key.add(0,key.get(0)+2*(params.minL+params.minG));
					}
				}
			}else{
				if(!reverse){
					none_key.add(key.get(3));
				}else{
					none_key.add(0,key.get(0));
				}
			}
		}
		return none_key;
	}
	
	public static int get_max(List<Integer> template){
		int max=-1;
		if(true)
		for(int i=0;i<template.size();i++){
			if(template.get(i)>max){
				max=template.get(i);
			}
		}
		return max;
	}
	public static int get_none_area(Template parent){
		int area;
		int size_parent = parent.get_template().size();
		int new_value = parent.get_template().get(size_parent-1);
		if(new_value>size_parent-1){
			area=(size_parent-1)-2;
		}else{
			area=new_value-2;
		}
		return area;	
	}
	
	public static void update_twins(Template node, int area, int pair, double value, List<Double> new_value, List<Integer> Template){
		int i1,j1,i2,j2, size_template;
		List<Integer> key_check;
		size_template=Template.size();
		i1 = new_value.get(2*Template.indexOf(1)+1).intValue();
		j2 = new_value.get(2*Template.indexOf(size_template)+2).intValue();		
		for(int i=0;i<size_template-1;i++){
			if(i!=area && i!=area+1){
				j1 = new_value.get(2*Template.indexOf(i+1)+2).intValue();
				i2 = new_value.get(2*Template.indexOf(i+2)+1).intValue();
				key_check = new LinkedList<Integer>(Util.list(i1,j1,i2,j2));
				
				check_contains(node,i,pair-1,key_check,value,new_value);
			}
		}
		
	}
	
	public static void check_contains(Template node, int area, int pair, List<Integer> key, double value, List<Double> new_value){
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
	
	public static void new_check_contains(Template node, int area, int pair, List<Integer> key, double value, List<Double> new_value){
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
	public static List<Double> prepare_new_value(double value, List<Double> prev_value,double i3,double j3){
		List<Double> new_value = new LinkedList<Double>(prev_value);
		new_value.set(0,value);
		new_value.add(new_value.size(), (double)i3);
		new_value.add(new_value.size(), (double)j3);
		return new_value;
	}
	
	public static void fill_siblings(Template node, Template parent,int i,int pair_index,List<Dir> Pair,double i3,double j3,double value, boolean reverse, boolean border){
		List<Integer> new_key;
		int index, min_area;
		List<Dir> previous_pair= new LinkedList<Dir>(Pair.subList(0,Pair.size()-1));
		if(reverse){
			Collections.reverse(new LinkedList<Dir>(previous_pair));
		}
		index = total_pairings_hash.get(previous_pair.size()-1).get(previous_pair);
		Enumeration<List<Integer>> enumKey = parent.get_Zs().get(i).get(index-1).keys();
		while(enumKey.hasMoreElements()){
			new_key = enumKey.nextElement();
			if(border){
				if((params.sequence.length()-new_key.get(3))>(params.minL+params.minG)){
					fill_sibling(node,parent,pair_index, i, index, new_key, value,Util.list(new_key.get(0),new_key.get(1),new_key.get(2),(int)j3),(int)i3,(int)j3);
				}
			}else{
				min_area = ((i+2)*params.minL)+((i+1)*params.minG);
				if(i3<new_key.get(1)){ //It is in the left part
					if((new_key.get(1)-new_key.get(0))>min_area){
						fill_sibling(node,parent,pair_index, i, index, new_key, value,new_key,(int)i3,(int)j3);
					}
				}else{
					if((new_key.get(3)-new_key.get(2))>min_area){
						fill_sibling(node,parent,pair_index, i, index, new_key, value,new_key,(int)i3,(int)j3);
					}
				}
			}
		}
	}
	
	public static void fill_sibling(Template node, Template parent, int pair_index, int i, int index, List<Integer> new_key, double value, List<Integer> key_check, int i3, int j3){
		double final_value;
		List<Double> values = parent.get_Zs().get(i).get(index-1).get(new_key);
		final_value = energy(values.get(0),params.temp)+value;
		final_value = zScore(final_value,params.temp);
		//check_contains(node,i,pair_index,key_check,final_value, values.get(1),values.get(2),(double) i3,(double) j3);
	}
	public static void strands_window(Template temp){
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
								new_check_contains(temp,0,pair,Util.list(i1,j1,i2,j2),value,Util.list(value,(double)i1,(double)j1,(double)i2,(double)j2));
								//check_contains(temp,0,pair,Util.list(i1,j1,i2,j2),value,(double)i1,(double)j1,(double)i2,(double)j2,-1);
								//new_check_contains(temp,0,pair,Util.list(i1,j2),value,Util.list(value,(double)i1,(double)j1,(double)i2,(double)j2));
								//check_contains(temp,0,pair,Util.list(i1,j2),value,(double)i1,(double)j1,(double)i2,(double)j2,-1);
								//System.out.println(i1 + " ** "+j1 + " ** "+i2 + " ** "+j2);
							}
						}
					}
				}
			}
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

}
