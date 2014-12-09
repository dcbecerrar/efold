package dynamics;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.CubicCurve2D;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;


import dynamics.ContactDistance;
import dynamics.SampleDistance;




import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.VertexIconShapeTransformer;


public class NetworkFigure {
	
	/**
	 * the graph
	 */
	DirectedGraph<MyNode, MyLink> graph;
	DirectedGraph<MyNode, MyLink> original_graph;
	
	public int edgeCount;
	
	// a Set for the rings
	Set<Double> depths = new HashSet<Double>();
	Map<Number,PolarPoint> polarLocations = new HashMap<Number,PolarPoint>();
	double[][] numTemplates = new double[4][10];
//	numTemplates[0][i] = number of clusters of size i;
//	numTemplates[1][i] = Theta between clusters;
//	numTemplates[2][i] = Accumulative Theta;
//	numTemplates[3][i] = radius;
	// a Map for the icons
	Map<MyNode,Icon> iconMap;
	Map<MyNode,Icon> original_iconMap;
	
	List<MyNode> vertices;
	
	//Map between clusters and vertices.......
	public static Hashtable<String,MyNode> mapping;
	public static Hashtable<MyNode,String> map;
	
	
	
	
	public void neighborMatrix(List<Cluster> clusters, SampleDistance sd, double threshold){
		int nClusters = clusters.size(), pos1, pos2;
		for(int i=0; i< nClusters; i++){
			for(int j=i+1; j< nClusters; j++){
				Cluster c1 = clusters.get(i);
				Cluster c2 = clusters.get(j);
				pos1 = mapping.get(Util.clusterLabelShort(c1.template, c1.pairing)).id;
				pos2 = mapping.get(Util.clusterLabelShort(c2.template, c2.pairing)).id;
				MyNode n1 = vertices.get(pos1-1);
				MyNode n2 = vertices.get(pos2-1);
				//For now I commented this line........
				//if(c1 != c2 && n1!=n2 && Landscape_tfolder.areNeighbors(c1, c2, sd, threshold)){
				//if(c1 != c2 && n1!=n2 && (c1.template.size()+1==c2.template.size() || (c1.template.size()<2 && c2.template.size() == 2))){
				//if(c1 != c2 && n1!=n2 && c1.template.size()+1==c2.template.size())
				if(test_Edges(n1,n2)){
					graph.addEdge(new MyLink(Math.random()), n1, n2);
					//Ese Math.random toca reemplazarlo por un valor especifico, igual que en clonning the edge
					original_graph.addEdge(new MyLink(Math.random()), n1, n2);
					edgeCount++;
				}
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
		mapping = new Hashtable<String,MyNode>();
		map = new Hashtable<MyNode,String>();
		graph = new DirectedSparseGraph<MyNode, MyLink>();
		original_graph = new DirectedSparseGraph<MyNode, MyLink>();
		//Number[] vertices = createVertices(clusters.size());
		vertices = createVertices_Mapping(clusters);
		edgeCount = 0;
		int pos;
		// a Map for the icons
		iconMap = new HashMap<MyNode,Icon>();
		original_iconMap = new HashMap<MyNode,Icon>();
		double maxLogWeight = Double.MIN_VALUE;
		for(Cluster c : clusters){
			maxLogWeight = Math.max(maxLogWeight, -Math.log(c.weight));
		}
		
		/*
		 * create the vertices
		 */
		for(int i = 0; i < clusters.size(); i++) {
			Cluster c = clusters.get(i);
			pos = mapping.get(Util.clusterLabelShort(c.template, c.pairing)).id;
			if(c.template.size()==0){
				final Color unfoldedColor = colors.get(c);
				iconMap.put(vertices.get(pos-1), new Icon() {
					
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
			iconMap.put(vertices.get(pos-1), new StrandIcon(best, 0.4*scale,colors.get(c)));
			original_iconMap.put(vertices.get(pos-1), new StrandIcon(best, 0.4*scale,colors.get(c)));
		}
		
		infoRings(vertices);
		
		/*
		 * add edges to the graph according to the clusters
		 */
		neighborMatrix(clusters, sd, threshold);		
		
	}
	
	/**
	 * create some vertices
	 * @param count how many to create
	 * @return the Vertices in an array
	 */
	private MyNode[] createVertices(int count, List<Cluster> clusters) {
		MyNode[] v = new MyNode[count];
		String key;
		for (int i = 1; i <= count; i++) {
			key = Util.clusterLabelShort(clusters.get(i).template, clusters.get(i).pairing);
			v[i] = new MyNode(i,clusters.get(i).weight,key);
			graph.addVertex(v[i]);
		}
		return v;
	}
	
	private List<MyNode> createVertices_Mapping(List<Cluster> c) {
		int count = c.size();
		int num_vertices=0, index;
		double weight_aux=0d;
		String key;
		MyNode v;
		List<MyNode> vs = new LinkedList<MyNode>();
		for(int i = 0; i < count; i++){
			key = Util.clusterLabelShort(c.get(i).template, c.get(i).pairing);
			if(!mapping.containsKey(key)){
				num_vertices++;
				v = new MyNode(num_vertices,c.get(i).weight,key);
				mapping.put(key, v);
				map.put(v, key);
				vs.add(num_vertices-1, v);
				graph.addVertex(v);
				original_graph.addVertex(v);
				numTemplates[0][c.get(i).pairing.size()]++;
			}else{
				index = mapping.get(key).id;
				weight_aux = vs.get(index-1).getWeight() + c.get(i).weight;
				vs.remove(index-1);
				v = new MyNode(num_vertices,weight_aux,key);
				vs.add(index-1,v);
			}
		}
		return vs;
	}
	
	public boolean test_Edges(MyNode one, MyNode two){
		if(one.id()==1 && two.id()==2){
			return true;
		}
		if(one.id()==1 && two.id()==3){
			return true;
		}
		if(one.id()==3 && two.id()==4){
			return true;
		}
		if(one.id()==3 && two.id()==5){
			return true;
		}
		if(one.id()==3 && two.id()==6){
			return true;
		}
		if(one.id()==2 && two.id()==6){
			return true;
		}
		if(one.id()==2 && two.id()==7){
			return true;
		}
		if(one.id()==2 && two.id()==9){
			return true;
		}
		if(one.id()==3 && two.id()==8){
			return true;
		}
		if(one.id()==4 && two.id()==10){
			return true;
		}
		if(one.id()==5 && two.id()==11){
			return true;
		}
		if(one.id()==5 && two.id()==12){
			return true;
		}
		if(one.id()==6 && two.id()==13){
			return true;
		}
		if(one.id()==6 && two.id()==14){
			return true;
		}
		if(one.id()==7 && two.id()==15){
			return true;
		}
		if(one.id()==7 && two.id()==16){
			return true;
		}
		if(one.id()==9 && two.id()==17){
			return true;
		}
		if(one.id()==9 && two.id()==18){
			return true;
		}
		if(one.id()==8 && two.id()==19){
			return true;
		}
		if(one.id()==8 && two.id()==20){
			return true;
		}
		return false;
		
	}
	public void cloning_Graphs(){
		graph = new DirectedSparseGraph<MyNode,MyLink>();
		//iconMap = new HashMap<MyNode,Icon>();
		MyNode ver, ver_aux;
		int index;
		List<MyNode> vertices_aux = new LinkedList<MyNode>(vertices);
		iconMap.clear();
		map.clear();
	    for (MyNode v : original_graph.getVertices()){
	    	index = vertices.indexOf(v);
	    	ver = new MyNode(v.id,v.getWeight(),v.getTopology());
	    	//ver_aux = vertices_aux.get(index);
	    	vertices_aux.remove(index);
	    	vertices_aux.add(index,ver);
	        graph.addVertex(ver);
	        Icon iM_aux = original_iconMap.get(v); 
	        iconMap.put(ver, iM_aux);
	        map.put(ver, ver.getTopology());
	    }    
	    Collection<MyNode> edg;
	    MyNode ver1=null, ver2=null;
	    for (MyLink e : original_graph.getEdges()){
	    	edg = original_graph.getIncidentVertices(e);
	    	int cont = 0; 
	    	for(MyNode obj:edg){
	    		if(cont==0){
	    			ver1 =  vertices_aux.get(obj.id-1); 
	    			cont++;
	    		}else{
	    			cont=0;
	    			ver2 = vertices_aux.get(obj.id-1); ;
	    			graph.addEdge(new MyLink(Math.random()), ver1, ver2);
	    		}
	    	}
		}	
	}
	
	public void infoRings(List<MyNode> vertices){
		int size;
		double radius;
		for(int i=0;i<numTemplates[0].length;i++){
			if(numTemplates[0][i]==0){
				break;
			}else{
				numTemplates[1][i]=(2*Math.PI)/numTemplates[0][i];
			}
		}
		
		
		for(int i=0;i<vertices.size();i++){
			String topology = map.get(vertices.get(i));
			if(topology.equals("Unfolded")){
				size = 0;
				radius = 0;
			}else{
				size = topology.length()/2;
				radius = 100*size;
			}
			numTemplates[2][size]+=numTemplates[1][size];
			PolarPoint point = new PolarPoint();
			point.setTheta(numTemplates[2][size]);
			point.setRadius(radius);
			polarLocations.put(vertices.get(i).id, point);
			depths.add(radius);
		}
	}
	
	class MyNode {
	    private int id; // good coding practice would have this as private
	    private double weight;
	    private String topology;
	    public MyNode(int id, double weight, String topology) {
	        this.id = id;
	        this.weight = weight;
	        this.topology = topology;
	    }
	    public String toString() { // Always a good idea for debuging
	        return "V"+id;       // JUNG2 makes good use of these.
	    } 
	    public double getWeight(){
	    	return weight;
	    }
	    public int id(){
	    	return id;
	    }
	    public String getTopology(){
	    	return topology;
	    }
	}

	class MyLink {
	    private double distance;   // should be private for good practice
	    private int id;
	    
	    public MyLink(double distance) {
	        this.id = edgeCount++; // This is defined in the outer class.
	        this.distance = distance;
	    } 
	    public String toString() { // Always good for debugging
	        return "E"+id;
	    }
	    public double GetDistance(){
	    	return distance;
	    }
	    public double id(){
	    	return id;
	    }
	    
	}	
	
}

