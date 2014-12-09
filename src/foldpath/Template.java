package foldpath;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class Template {

	/**
	 * @param args
	 */
	private List<List<Hashtable<List<Integer>,List<Double>>>> Zs;
	private List<Integer> template;
	private Hashtable<Integer,List<List<Integer>>> mirror;
	private int parent;
	private int itself; 
	private boolean reverse;
	
	public Template(List<Integer> template, boolean rev){
		this.template=template;
		this.reverse=rev;
		this.Zs = new LinkedList<List<Hashtable<List<Integer>,List<Double>>>>();
		this.mirror = new Hashtable<Integer,List<List<Integer>>>();
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
		for(int i=0;i<template.size()-1;i++){
			List<Hashtable<List<Integer>,List<Double>>> partitions = new LinkedList<Hashtable<List<Integer>,List<Double>>>(); 
			for(int j=0;j<tam_partitions;j++){
				partitions.add(new Hashtable<List<Integer>,List<Double>>()); 
			}
			Zs.add(partitions);
		}
	}
	public Hashtable<Integer,List<List<Integer>>> get_mirror(){
		return this.mirror;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
