package foldpath;

import java.util.Collection;
import java.util.List;

import SS.Constants.Dir;

public class Cluster extends Sample{
	private static final long serialVersionUID = 1L;
	public Collection<Sample> samples;
	
	public Cluster(List<Integer> t, List<Dir> d, Integer[][] s, int l, double z, double p) {
		super(t, d, s, l, z, p);
	}
	
	public Cluster(List<Integer> t, List<Dir> d,  Collection<Sample> s, double w) {
		super(t, d, null, 0, w, 0);
		samples = s;
	}
	
	public String toString(){
		return template + " " + pairing;
	}
	
	public Collection<Sample> get_samples(){
		return samples;
	}
}
