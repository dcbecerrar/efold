package dynamics;


import java.awt.Color;
import org.jfree.chart.ChartPanel;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.io.File;
import javax.swing.JScrollPane;  
import javax.swing.JPanel;  

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import dynamics.Constants.*;

import dynamics.SampleDistance;


import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.util.*;


import dynamics.Sample;


import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.visualization.*;


import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;

import java.awt.*;

public class Interface extends JApplet  {
	public static int edgeCount =0;
	public static Hashtable Distances = new Hashtable();
	
	
	//static double computeP()
	//Takes in a List of matrices, or a list of templates and obtains each matrix
	//under the condition that the template containing the matrix is of 1,2,3... strands
	//use findCross to check intersections
	//Find first and last intersection of each 1,2,3... strands
	//take average of x axis
	//Plug back into matrix to compute P for desired template
	
	public static double[][] fillMatrix(double[][] matrix, double x, double y)
	{
		
		
			for(int i=0; i<matrix.length; i++)
			{
				matrix[0][i] = x;
			}
		
			for(int i=0; i<matrix.length; i++)
			{
				matrix[1][i] = y;
			}
			
			return matrix;
			
		}
			
		
		
	
	

	/*
	static double[] computeP(List<Cluster> c, double min, double max)
	{
		int maxInteractions = 0;
		int current = 0;
		for(int i=0; i<c.size(); i++)
		{
			current = c.get(i).pairing.size();
			if(current > maxInteractions)
				maxInteractions = current;
		}
			double[] pMatrix = new double[maxInteractions];
			
			
			double timeSteps=0.0;
		
			for(int j=0; j<maxInteractions; j++)
			{
				double mi = max;
				double ma = min;
				for(int k=0; k<c.size(); k++)
				{
					if(c.get(k).pairing.size()==j)
					{
						timeSteps = findCross(c.get(0).matrix, c.get(k).matrix);
						if (timeSteps > ma)
							ma = timeSteps;
						if (timeSteps < mi)
							mi = timeSteps;
							
					}
					
				}
				pMatrix[j] = ((ma+mi)/2);
			}
			
			return pMatrix;
		
	}*/
	
	
//	static double[][] computeP(List<Cluster> c, double min, double max)
//	{
//		int maxInteractions = 0;
//		int current = 0;
//		for(int i=0; i<c.size(); i++)
//		{
//			current = c.get(i).pairing.size();
//			if(current > maxInteractions)
//				maxInteractions = current;
//		}
//		double[][] pMatrix = new double[maxInteractions][2];
//		pMatrix = fillMatrix(pMatrix,max,min);
//
//		double timeSteps=0.0;
//
//		for(int k=0; k<c.size(); k++)
//		{
//			int j = c.get(k).pairing.size();
//
//			timeSteps = findCross(c.get(0).matrix, c.get(k).matrix);
//
//			if (timeSteps < pMatrix[j][0])
//				pMatrix[j][0] = timeSteps;
//			if (timeSteps > pMatrix[j][1])
//				pMatrix[j][1] = timeSteps;
//
//		}
//		return pMatrix;
//
//	}
		

	
	static double roundTwoDecimals(double d)
	{
    	DecimalFormat twoDForm = new DecimalFormat("#.###");
	return Double.valueOf(twoDForm.format(d));
	}
	
	static double findCross(double[][] primary, double[][] folded)
	{
		boolean check = false;
		int i = 0;
	while((!check) && (i<primary.length)) {	
	
			
		
			if(primary[i][1]<=folded[i][1])
			{
				check = true;
				return primary[i-1][0];				
			}
		i++;	
		}
		return -1.0;
	}
	

	public static void main(String[] args) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException {
		
		// double[][] dynamics is a n X 2 matrix, where n is the number of time measures. 
		
		//We need to update this assignment of clusters......
		
		 
		
		double[][] unfolded = new double[241][2];
		double[][] fold1 = new double[241][2];
		double[][] fold2 = new double[241][2];
		double[][] fold3 = new double[241][2];
		double[][] fold4 = new double[241][2];
		
		
		double counter = -6.0;
		for(int i=0; i<241; i++)
		{
			unfolded[i][0]=roundTwoDecimals(counter);
			counter = counter + 0.05;
		}
		
		System.out.println(unfolded[239][0]);
		
		for(int i=0; i<241; i++)
		{
			unfolded[i][1]=roundTwoDecimals(-i+50);
			fold1[i][1]=roundTwoDecimals((3/2)*i);
			fold2[i][1]=roundTwoDecimals((14.0/36.0)*i);
			fold3[i][1]=roundTwoDecimals((-0.4*(Math.pow(i,2)))+(12*i)-40);
		}
		
		
		List<Cluster> clusters = new ArrayList<Cluster>();
		
		List<Integer> temp1 = new ArrayList<Integer>();
		temp1.add(1);
		temp1.add(2);
		List<Dir> conf1 = new ArrayList<Dir>();
		conf1.add(Dir.PARA);
		
		List<List<Integer>> st1 = new LinkedList<List<Integer>>();
		List<Integer> s1 = new LinkedList<Integer>();
		s1.add(5);
		s1.add(10);
		st1.add(s1);
		List<Integer> s2 = new LinkedList<Integer>();
		s2.add(40);
		s2.add(45);
		st1.add(s2);
		
		
		
		
		List<Integer> temp2 = new ArrayList<Integer>();
		temp2.add(1);
		temp2.add(2);
		ArrayList<Dir> conf2 = new ArrayList<Dir>();
		conf2.add(Dir.ANTI);
		List<List<Integer>> st2 = new LinkedList<List<Integer>>();
		List<Integer> s3 = new LinkedList<Integer>();
		s3.add(3);
		s3.add(6);
		st2.add(s3);
		
		List<Integer> s4 = new LinkedList<Integer>();
		s4.add(10);
		s4.add(12);
		st2.add(s4);
		

		List<Integer> temp3 = new ArrayList<Integer>();
		temp3.add(1);
		temp3.add(2);
		temp3.add(3);
		ArrayList<Dir> conf3 = new ArrayList<Dir>();
		conf3.add(Dir.PARA);
		conf3.add(Dir.PARA);
		List<List<Integer>> st3 = new LinkedList<List<Integer>>();
		List<Integer> s5 = new LinkedList<Integer>();
		s5.add(10);
		s5.add(13);
		st3.add(s5);
		
		List<Integer> s6 = new LinkedList<Integer>();
		s6.add(35);
		s6.add(45);
		st3.add(s6);
		
		List<Integer> s7 = new LinkedList<Integer>();
		s7.add(50);
		s7.add(65);
		st3.add(s7);
		
		
		
		List<Integer> temp4 = new ArrayList<Integer>();
		temp4.add(1);
		temp4.add(2);
		temp4.add(3);
		ArrayList<Dir> conf4 = new ArrayList<Dir>();
		conf4.add(Dir.PARA);
		conf4.add(Dir.ANTI);
		List<List<Integer>> st4 = new LinkedList<List<Integer>>();
		List<Integer> s8 = new LinkedList<Integer>();
		s8.add(10);
		s8.add(13);
		st4.add(s5);
		
		List<Integer> s9 = new LinkedList<Integer>();
		s9.add(35);
		s9.add(45);
		st4.add(s6);
		
		List<Integer> s10 = new LinkedList<Integer>();
		s10.add(50);
		s10.add(65);
		st4.add(s7);
		
		
		
		
		//Creating the unfolded state.
//		clusters.add(new Cluster(new ArrayList<Integer>(), new ArrayList<Dir>(), null, 0.003));
//		clusters.get(0).setMatrix(unfolded);
		
		
//		//Sample(List<Integer> iTemplate, List<Dir> d, List<List<Integer>> s, int l, double z, double p)
//		//Sample samp1 = new Sample(temp1,conf1,st1,80,0.005,0);
//		
//		//Sample samp2 = new Sample(temp2,conf2,st2,80,0.016,0);
//		
//		//Sample samp3 = new Sample(temp3,conf3,st3,80,0.025,0);
//		
//		//Sample samp4 = new Sample(temp4,conf4,st4,80,0.025,0);
//		
//		
//		List<Sample> samples1 = new LinkedList<Sample>();
//		samples1.add(samp1);
//		clusters.add(new Cluster(temp1, conf1,samples1, 0.002));
//		clusters.get(1).setMatrix(fold1);
//		
//		
//		
//		List<Sample> samples2 = new LinkedList<Sample>();
//		samples2.add(samp2);
//		clusters.add(new Cluster(temp2, conf2, samples2, 0.001));
//		clusters.get(2).setMatrix(fold2);
//		
//		
//		
//		List<Sample> samples3 = new LinkedList<Sample>();
//		samples3.add(samp3);
//		clusters.add(new Cluster(temp3, conf3, samples3, 0.004));
//		clusters.get(3).setMatrix(fold3);
//		
//		
//		List<Sample> samples4 = new LinkedList<Sample>();
//		samples4.add(samp4);
//		clusters.add(new Cluster(temp4, conf4, samples4, 0.004));
//		clusters.get(4).setMatrix(fold4);
		
		
	
		
		
		Map<Cluster,Color> colors = map(clusters, getPalette(clusters.size()));		
			//Creating landscape figure
			String landscapeSvg = "Test1SVG.svg";
			String transitionMetric = "dynamics.ContactDistance";
			String transitionMetricParameters = "2";
			SampleDistance transMetric = (SampleDistance) Class.forName(transitionMetric).getConstructor(String.class).newInstance(transitionMetricParameters);	
			
			//Ask David about transition Metric
			
			//This is one of the parameters that we have to model.
			double transitionThreshold = 1d;
			
			//NetworkFigure figure = new NetworkFigure(clusters, transMetric, transitionThreshold, colors);	
//			RadialTreeLayout figure = new RadialTreeLayout(clusters, transMetric, transitionThreshold, colors);	
			//System.out.println("Saving landscape figure '" + landscapeSvg + "'");
			/*VisualizationSaver.save(figure.viewer, landscapeSvg);	*/
		
			//JPanel panel=new JPanel();  
			JFrame f = new JFrame();
//			f.getContentPane().add(new RadialTreeLensDemo(clusters, transMetric, transitionThreshold, colors));
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	        f.getContentPane().add(figure);
	        f.pack();
	        f.setVisible(true);
	        
			
			//JScrollPane scrollBar=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  




	}
	
	

	
	public static List<Color> getPalette(int number){
		List<Color> palette = new ArrayList<Color>();
		for(float i=0; i<number; i++){
			palette.add(Color.getHSBColor(i/number, 1f, 1f));
		}
		return palette;
	}
	
	public static <S,T> Map<S, T> map(List<S> keys, List<T> values) {
		Map<S,T> m = new HashMap<S, T>();
		for(int i=0; i<keys.size(); i++)
			m.put(keys.get(i), values.get(i));
		
		return m;
	}
	
	public static void saveChartSvg(JFreeChart chart, String fileName, int width, int height) throws IOException{
		DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();
        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        // Create an instance of the SVG Generator.
        SVGGraphics2D g = new SVGGraphics2D(document);
        // Ask the test to render into the SVG Graphics2D implementation.  
        chart.draw(g, new Rectangle(width, height));
        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
     //   File toSave = new File(fileName);
        Writer out = new FileWriter(fileName);// OutputStreamWriter(System.out, "UTF-8");
        g.stream(out, useCSS);
	}

	
	public void init() {
        //Create this applet's GUI.
        try 
        {
            SwingUtilities.invokeAndWait(new Runnable() 
            {
                public void run() 
                
                {
                	JFrame jf = new JFrame();
                	add(jf);

                  
                }
            });
            
        } 
       
        catch (Exception e) 
        {
            System.err.println("createGUI didn't complete successfully");
        }
    }

	
	
}






