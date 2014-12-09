package util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;

import plots.ImagePanel;
import SS.*;
import SS.Constants.Dir;

public class Util {
	public static <T> List<T> list(T ...elements){
		return Arrays.asList(elements);
	}
	public static Constants.Environment map_environment(int val){
		switch(val){
		case 1:
			return Constants.Environment.BURIED;
		case 2:
			return Constants.Environment.EXPOSED;
		case 3:
			return Constants.Environment.LOOP;
		}
		return null;	
	}
	public static List<Integer> String_template(String temp){
		String temp_aux = temp.replace('[', ' ');
		temp_aux=temp_aux.replace(']', ' ');
		temp_aux =temp_aux.trim();
		String[] tmp = temp_aux.split(", ");
		List<Integer> list_tmp = new LinkedList<Integer>(); 
		for(int i=0;i<tmp.length;i++){
			list_tmp.add(Integer.valueOf(tmp[i]));
		}
		return list_tmp;
	}
	
	public static List<Dir> String_pair(String pair){
		String pair_aux = pair.replace('[', ' ');
		pair_aux=pair_aux.replace(']', ' ');
		pair_aux =pair_aux.trim();
		String[] pr = pair_aux.split(", ");
		List<Dir> list_pr = new LinkedList<Dir>(); 
		for(int i=0;i<pr.length;i++){
			if(pr[i].equals("PARA")){
				list_pr.add(Dir.PARA);
			}else{
				if(pr[i].equals("ANTI")){
					list_pr.add(Dir.ANTI);
				}else{
					list_pr.add(Dir.NONE);
				}
			}
		}
		return list_pr;
	}
	
	public static void save(Object toSave, String fileName) throws IOException{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
		oos.writeObject(toSave);
		oos.close();
	}
	
	public static Object load(String fileName) throws IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
		Object toRead = ois.readObject();
		ois.close();
		return toRead;
	}
	
	public static class GetOpts{
		Map<String,String> opts;
		public GetOpts(String[] args){
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
		}
		
		public String get(String option){
			if(!opts.containsKey(option))
				throw new IllegalArgumentException("Command line option '-" + option + "' is required");
			return opts.get(option);
		}
		
		public String optional(String option, String alternative) {
			if(!opts.containsKey(option))
				return alternative;
			else
				return opts.get(option);
		}
		
		public boolean contains(String option){
			return opts.containsKey(option);
		}
	}
	
	public static List<Integer> seq(int num) {
		ArrayList<Integer> list = new ArrayList<Integer>(num);
		for(int i=0;i<num;i++){
			list.add(i);
		}
		return list;
	}
	
	public static void showImage(BufferedImage img) {
		JFrame f = new JFrame();	
		JPanel image = new ImagePanel(img);
		image.setPreferredSize(new Dimension(img.getWidth(),img.getHeight()));
		f.getContentPane().add(image);
		f.pack();
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
	
	public static <S,T> Map<S, T> map(List<S> keys, List<T> values) {
		Map<S,T> m = new HashMap<S, T>();
		for(int i=0; i<keys.size(); i++)
			m.put(keys.get(i), values.get(i));
		
		return m;
	}
	
}
