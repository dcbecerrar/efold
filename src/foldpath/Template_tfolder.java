package foldpath;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Template_tfolder {

	/**
	 * @param args
	 */
	private List<List<List<Hashtable<List<Integer>,List<Double>>>>> Zs;
	private List<Hashtable<List<Integer>,List<Double>>> Z;
	private List<Double> ZSum;
	private List<NavigableMap<Double, List<Integer>>> map_ZSum;
	private List<Integer> template;
	private int parent;
	private int itself; 
	private boolean reverse;
	
	
	public Template_tfolder(List<Integer> template, boolean rev){
		this.template=template;
		this.reverse=rev;
		this.Zs = new LinkedList<List<List<Hashtable<List<Integer>,List<Double>>>>>();
		this.Z = new LinkedList<Hashtable<List<Integer>,List<Double>>>();
		this.ZSum = new LinkedList<Double>();
		this.map_ZSum = new LinkedList<NavigableMap<Double,List<Integer>>>();
	}
	public boolean get_reverse(){
		return this.reverse;
	}
	public void set_reverse(boolean rev){
		this.reverse=rev;
	}
	public List<Double> get_ZSum(){
		return this.ZSum;
	}
	public void set_ZSum(List<Double> new_ZSum){
		this.ZSum=new_ZSum;
	}
	public List<NavigableMap<Double, List<Integer>>> get_mapZSum(){
		return this.map_ZSum;
	}
	public int get_parent(){
		return this.parent;
	}
	public void set_parent(int par){
		this.parent=par;
	}
	public int get_itself(){
		return this.itself;
	}
	public void set_itself(int it){
		this.itself=it;
	}
	public List<Integer> get_template(){
		return this.template;
	}
	public List<Hashtable<List<Integer>,List<Double>>> get_Z(){
		return this.Z;
	}
	public void set_Z(List<Hashtable<List<Integer>,List<Double>>> new_Z){
		this.Z=new_Z;
	}
	public void unset_Z(){
		this.Z.clear();
		this.Z = null;
	}
	public void unset_Z(int pair){
		this.Z.get(pair).clear();
	}
	public List<List<List<Hashtable<List<Integer>,List<Double>>>>> get_Zs(){
		return this.Zs;
	}
	public void set_Zs(List<List<List<Hashtable<List<Integer>,List<Double>>>>> new_Zs){
		this.Zs=new_Zs;
	}
	public void unset_Zs(){
		this.Zs.clear();
		this.Zs = null;
	}
	public void unset_Zsarea(int area, int partitions){
		int size = this.Zs.get(area).get(0).size();
		for(int i=0;i<partitions; i++){
			for(int j=0;j<size;j++){
				if(j!=area){
					this.Zs.get(area).get(i).clear();
				}
			}
		}
	}
	public void unset_Zpartitions(int areas, int paring){
		for(int i=0;i<areas; i++){
			this.Zs.get(i).get(paring).clear();
		}
	}
	public void set_Zs(int tam_partitions, int num_strands){
		//Set Zs
		if(this.template.size()!=num_strands){
			for(int i=0;i<=template.size();i++){
				List<List<Hashtable<List<Integer>,List<Double>>>> partitions = new LinkedList<List<Hashtable<List<Integer>,List<Double>>>>(); 
				for(int k=0;k<tam_partitions;k++){
					List<Hashtable<List<Integer>,List<Double>>> siblings = new LinkedList<Hashtable<List<Integer>,List<Double>>>();
					for(int j=0;j<template.size()-1;j++){
						siblings.add(new Hashtable<List<Integer>,List<Double>>());
					}
					partitions.add(siblings);
				}
				Zs.add(partitions);
			}
		}
		//Set Z
		for(int k=0;k<tam_partitions;k++){
			this.Z.add(new Hashtable<List<Integer>,List<Double>>());
			this.ZSum.add(0d);
			this.map_ZSum.add(new TreeMap<Double,List<Integer>>());
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
