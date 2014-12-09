package edu.uci.ics.jung.visualization;/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.CubicCurve2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import foldpath.Landscape_tfolder;
import metrics.ContactDistance;
import metrics.SampleDistance;

import foldpath.Cluster;
import foldpath.Sample;
import foldpath.Template_tfolder;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.VertexIconShapeTransformer;
import foldpath.StrandIcon;

public class NetworkFigure {
	
	/**
	 * the graph
	 */
	UndirectedSparseGraph<Number, Number> graph;
	
	/**
	 * the visual component and renderer for the graph
	 */
	public VisualizationViewer<Number,Number> viewer;
	
	//Map between clusters and vertices.......
	public static Hashtable<String,Integer> mapping;
	
	
	
	public void neighborMatrix(List<Cluster> clusters, Number[] vertices, SampleDistance sd, double threshold){
		int nClusters = clusters.size(), pos1, pos2;
		for(int i=0; i< nClusters; i++){
			for(int j=0; j< nClusters; j++){
				Cluster c1 = clusters.get(i);
				Cluster c2 = clusters.get(j);
				pos1 = mapping.get(Landscape_tfolder.clusterLabelShort(c1.template, c1.pairing));
				pos2 = mapping.get(Landscape_tfolder.clusterLabelShort(c2.template, c2.pairing));
				Number n1 = vertices[pos1];
				Number n2 = vertices[pos2];
				if(c1 != c2 && n1!=n2 && Landscape_tfolder.areNeighbors(c1, c2, sd, threshold))
					graph.addEdge(new Double(Math.random()), n1, n2, EdgeType.UNDIRECTED);
			}
		}
		
	}
	
	/**
	 * create an instance of a simple graph with controls to
	 * demo the zoom features.
	 * @param colors 
	 * @throws IOException 
	 * 
	 */
	public NetworkFigure(List<Cluster> clusters, SampleDistance sd, double threshold, Map<Cluster, Color> colors) throws IOException {
		mapping = new Hashtable<String,Integer>();
		graph = new UndirectedSparseGraph<Number,Number>();
		//Number[] vertices = createVertices(clusters.size());
		Number[] vertices = createVertices_Mapping(clusters);
		int pos;
		// a Map for the icons
		Map<Number,Icon> iconMap = new HashMap<Number,Icon>();
		double maxLogWeight = Double.MIN_VALUE;
		for(Cluster c : clusters){
			maxLogWeight = Math.max(maxLogWeight, -Math.log(c.weight));
		}
		
		/*
		 * create the vertices
		 */
		for(int i = 0; i < clusters.size(); i++) {
			Cluster c = clusters.get(i);
			pos = mapping.get(Landscape_tfolder.clusterLabelShort(c.template, c.pairing));
			if(c.template.size()==0){
				final Color unfoldedColor = colors.get(c);
				iconMap.put(vertices[pos], new Icon() {
					
					public void paintIcon(Component c, Graphics g, int x, int y) {
						Graphics2D g2d = (Graphics2D) g;
						g2d.setColor(unfoldedColor);
						g2d.fillOval(x, y, getIconWidth(), getIconHeight());
				/*		CubicCurve2D unfolded = new CubicCurve2D.Double();
						// draw CubicCurve2D.Double with set coordinates
						unfolded.setCurve(x+(width/2), y+5, x, 
								y+(height/3), x+width, y+(height*2/3), x+(width/2), y-5+height);						
						g2d.setStroke(new BasicStroke(4));
						g2d.setColor(Color.black);
						g2d.draw(unfolded);*/
						 
					}
					
					public int getIconWidth() {					
						return 25;
					}
					
					public int getIconHeight() {						
						return 25;
					}
				});
				continue;
			}
			double weight = Double.MIN_VALUE;
			Sample best = null;
			for(Sample s : c.samples){
				if(s.weight > weight){
					weight = s.weight;
					best = s;
				}
			}
			double scale = Math.pow(Math.pow(10, Math.log10(c.weight)/maxLogWeight)+1, 3);
			iconMap.put(vertices[pos], new StrandIcon(best, 0.4*scale,colors.get(c)));		
		}
		
		/*
		 * add edges to the graph according to the clusters
		 */
		neighborMatrix(clusters, vertices, sd, threshold);		
		
		KKLayout<Number, Number> layout = new KKLayout<Number, Number>(graph);
		
		//FRLayout<Number, Number> layout = new FRLayout<Number, Number>(graph);
		//	SpringLayout2<Number, Number> layout = new SpringLayout2<Number, Number>(graph);
		//	ISOMLayout<Number, Number> layout = new ISOMLayout<Number, Number>(graph);
		//	layout.setMaxIterations(300);
		
		viewer =  new VisualizationViewer<Number, Number>(layout, new Dimension(600,600));
		
		while (!layout.done() ) {
			layout.step();
		}
		
		final VertexIconShapeTransformer<Number> vertexImageShapeFunction =
			new VertexIconShapeTransformer<Number>(new EllipseVertexShapeTransformer<Number>());
		
		final DefaultVertexIconTransformer<Number> vertexIconFunction =
			new DefaultVertexIconTransformer<Number>();
		
		vertexImageShapeFunction.setIconMap(iconMap);
		vertexIconFunction.setIconMap(iconMap);
		
		viewer.getRenderContext().setVertexShapeTransformer(vertexImageShapeFunction);
		viewer.getRenderContext().setVertexIconTransformer(vertexIconFunction);
		viewer.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Number,Number>());	
	}
	
	/**
	 * create some vertices
	 * @param count how many to create
	 * @return the Vertices in an array
	 */
	private Number[] createVertices(int count) {
		Number[] v = new Number[count];
		for (int i = 0; i < count; i++) {
			v[i] = new Integer(i);
			graph.addVertex(v[i]);
		}
		return v;
	}
	
	private Number[] createVertices_Mapping(List<Cluster> c) {
		int count = c.size();
		int num_vertices=0;
		String key;
		Number v, vs[];
		for(int i = 0; i < count; i++){
			key = Landscape_tfolder.clusterLabelShort(c.get(i).template, c.get(i).pairing);
			if(!mapping.containsKey(key)){
				mapping.put(key, num_vertices);
				v = new Integer(num_vertices);				
				graph.addVertex(v);
				num_vertices++;
			}
		}
		
		vs = new Number[num_vertices];
		
		for (int i = 0; i < num_vertices; i++) {
			vs[i] = new Integer(i);
		}
		return vs;
	}
}

