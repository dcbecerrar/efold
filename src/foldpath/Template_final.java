package foldpath;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class Template_final {

	/**
	 * @param args
	 */
	private List<List<Hashtable<List<Integer>,List<Double>>>> Zs;
	private Hashtable<List<Integer>,List<Double>> Z;
	private List<Hashtable<Integer,List<List<Integer>>>> siblings;
	private List<Integer> template;
	private int parent;
	private int itself; 
	private boolean reverse;
	
	public Template_final(List<Integer> template, boolean rev){
		this.template=template;
		this.reverse=rev;
		this.Zs = new LinkedList<List<Hashtable<List<Integer>,List<Double>>>>();
		this.Z = new Hashtable<List<Integer>,List<Double>>();
		this.siblings = new LinkedList<Hashtable<Integer,List<List<Integer>>>>();
	}
	public boolean get_reverse(){
		return this.reverse;
	}
	public boolean set_reverse(boolean rev){
		return this.reverse=rev;
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
	public Hashtable<List<Integer>,List<Double>> get_Z(){
		return this.Z;
	}
	public void set_Z(Hashtable<List<Integer>,List<Double>> new_Z){
		this.Z=new_Z;
	}
	public List<Hashtable<Integer,List<List<Integer>>>> get_siblings(){
		return this.siblings;
	}
	public void set_siblings(List<Hashtable<Integer,List<List<Integer>>>> new_siblings){
		this.siblings=new_siblings;
	}
	public List<List<Hashtable<List<Integer>,List<Double>>>> get_Zs(){
		return this.Zs;
	}
	public void set_Zs(List<List<Hashtable<List<Integer>,List<Double>>>> new_Zs){
		this.Zs=new_Zs;
	}
	public void unset_Zs(){
		this.Zs.clear();
	}
	public void set_Z(int tam_partitions){
		for(int i=0;i<template.size();i++){
			List<Hashtable<List<Integer>,List<Double>>> partitions = new LinkedList<Hashtable<List<Integer>,List<Double>>>(); 
			for(int k=0;k<tam_partitions;k++){
				partitions.add(new Hashtable<List<Integer>,List<Double>>());
			
			}
			Zs.add(partitions);
			siblings.add(new Hashtable<Integer,List<List<Integer>>>());
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
