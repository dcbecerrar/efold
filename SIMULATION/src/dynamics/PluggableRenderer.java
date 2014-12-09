/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dynamics;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import dynamics.Constants.Dir;
import dynamics.NetworkFigure.MyNode;
import dynamics.NetworkFigure.MyLink;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.algorithms.shortestpath.BFSDistanceLabeler;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.LayeredIcon;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.decorators.VertexIconShapeTransformer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Checkmark;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;
import edu.uci.ics.jung.visualization.util.Animator;
import edu.uci.ics.jung.visualization.util.PredicatedParallelEdgeIndexFunction;
import edu.uci.ics.jung.visualization.RenderContext;




/**
 * A demo that shows how collections of vertices can be collapsed
 * into a single vertex. In this demo, the vertices that are
 * collapsed are those mouse-picked by the user. Any criteria
 * could be used to form the vertex collections to be collapsed,
 * perhaps some common characteristic of those vertex objects.
 *
 * Note that the collection types don't use generics in this
 * demo, because the vertices are of two types: String for plain
 * vertices, and Graph<String,Number> for the collapsed vertices.
 *
 * @author Tom Nelson
 *
 */
@SuppressWarnings("serial")
public class PluggableRenderer extends JApplet {
	boolean ini = true;
    String instructions =
        "<html>Use the mouse to select multiple proteins"+
        "<p>either by dragging a region, or by shift-clicking"+
        "<p>on multiple topologies (vertices). All the labels"+
        "<p>of selected vertices will be turned to blue."+
        "<p>After you select proteins, use the Collapse button"+
        "<p>to combine them into a single vertex. The algorithm"+
        "<p>collapse all the children topologies that depend"+
        "<p>only on already collapsed topologies. A topology can"+
        "<p>not be collapsed if the in-degree of a vertex is greater"+
        "<p>than one, and the parents are not collapsed topologies"+
        "<p>Select a 'collapsed' vertex and use the Expand button"+
        "<p>to restore the collapsed vertices."+
        "<p>Use the Delete Vertex and Edge button to delete protein."+
        "<p>topologies and edges, respectively."+
        "<p>Select two vertices and use the Path button to find"+
        "<p>the most probable path between two protein topologies"+
        "<p>The Restore button will restore the original graph."+
        "<p>You can drag the vertices with the mouse." +
        "<p>Use the 'Picking'/'Transforming' combo-box to switch"+
        "<p>between picking and transforming mode.</html>";
    /**
     * the graph
     */
    Graph <MyNode, MyLink> graph;
    Graph <MyNode, MyLink> original_graph;
    Graph <MyNode, MyLink>collapsedGraph;
    VisualizationServer.Paintable rings;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer vv;
    Transformer<MyNode, Paint> transformer_old_paint;
    Transformer<MyNode, Stroke> transformer_old_stroke;

    Layout layout;

    List<MyLink> l = new LinkedList<MyLink>();

    
    GraphCollapser collapser;

    NetworkFigure figure = null;
    
    public PluggableRenderer() {

        // create a simple graph for the demo
    	
    	
    	double transitionThreshold = 0.36;
    	String transitionMetric = "dynamics.ContactDistance";
    	String transitionMetricParameters = "2";
    	
    	
    	
    	SampleDistance transMetric = null; 
		try {
			transMetric = (SampleDistance) Class.forName(transitionMetric).getConstructor(String.class).newInstance(transitionMetricParameters);
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	List<Cluster> clusters = createClusters_2();
    	Map<Cluster,Color> colors = Util.map(clusters, Util.getPalette(clusters.size()));
    	
		try {
			figure = new NetworkFigure(clusters, transMetric, transitionThreshold, colors);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}	
    	
        
        graph = figure.graph;
        //graph = TestGraphs.getOneComponentGraph();
        
        collapsedGraph = graph;
        collapser = new GraphCollapser(graph);

        layout = new FRLayout(graph);

        Dimension preferredSize = new Dimension(600,600);
        final VisualizationModel visualizationModel =
            new DefaultVisualizationModel(layout, preferredSize);
        vv =  new VisualizationViewer(visualizationModel, preferredSize);

        	

        
        
        
//From here
		final VertexIconShapeTransformer<MyNode> vertexImageShapeFunction =
				new VertexIconShapeTransformer<MyNode>(new EllipseVertexShapeTransformer<MyNode>());
			
			final DefaultVertexIconTransformer<MyNode> vertexIconFunction =
				new DefaultVertexIconTransformer<MyNode>();
			
			vertexImageShapeFunction.setIconMap(figure.iconMap);
			vertexIconFunction.setIconMap(figure.iconMap);
        
        
			vv.getRenderContext().setVertexShapeTransformer(vertexImageShapeFunction);
			vv.getRenderContext().setVertexIconTransformer(vertexIconFunction);        
//        
//To here        
        


			
			
			
	        final Transformer<MyNode,String> vertexStringerImpl = 
	                new VertexStringerImpl<MyNode,String>(figure.map);
	            vv.getRenderContext().setVertexLabelTransformer(vertexStringerImpl);
	            //vv.getRenderContext().setVertexLabelRenderer(new DefaultVertexLabelRenderer(Color.cyan));
	            //vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.cyan));
	           
			
			
			
			
			

	        


	        
			
	        vv.getRenderContext().setVertexShapeTransformer(new ClusterVertexShapeFunction());
	        
	        
	        
	        
	        

        

        //final PredicatedParallelEdgeIndexFunction eif = PredicatedParallelEdgeIndexFunction.getInstance();
        //final Set exclusions = new HashSet();
        //eif.setPredicate(new Predicate() {

		//	public boolean evaluate(Object e) {

		//		return exclusions.contains(e);
		//	}});


       // vv.getRenderContext().setParallelEdgeIndexFunction(eif);
        
        
        
        
        
        PickedState<MyNode> ps = vv.getPickedVertexState();
        vv.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<MyNode>(ps, Color.red, Color.blue));
        
       
       
        

        vv.setBackground(Color.white);

        // add a listener for ToolTips
//        vv.setVertexToolTipTransformer(new ToStringLabeller() {
//
//			/* (non-Javadoc)
//			 * @see edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction#getToolTipText(java.lang.Object)
//			 */
//			@Override
//			public String transform(Object v) {
//				if(v instanceof Graph) {
//					return ((Graph)v).getVertices().toString();
//				}
//				return super.transform(v);
//			}});
        
        

        
        
        /**
         * the regular graph mouse for the normal view
         */
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        
        
       

        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());
        

        Container content = getContentPane();
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        content.add(gzsp);

        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(graphMouse.getModeListener());
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
        
        rings = new Rings();
		vv.addPreRenderPaintable(rings);

        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });

        JButton collapse = new JButton("Collapse");
        collapse.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
                if(picked.size() >= 1) {
                	Graph inGraph = layout.getGraph();
                    double sumx = 0;
                    double sumy = 0;
                    for(Object v : picked) {
                    	BFSDistanceLabeler BFS = new BFSDistanceLabeler();
                    	BFS.labelDistances(inGraph, v);
                    	Point2D p = (Point2D)layout.transform(v);
                    	sumx += p.getX();
                    	sumy += p.getY();
                    	Collection picked_aux = new HashSet();
                    	Collection successors =  BFS.getVerticesInOrderVisited();           	
                        for (Object obj : successors){
                        	if(compressVertice(inGraph,picked_aux,obj)){
                        		picked_aux.add(obj);
                        	}
                        }
                        Graph clusterGraph = collapser.getClusterGraph(inGraph, picked_aux);
                        Graph g = collapser.collapse(layout.getGraph(), clusterGraph);
                        collapsedGraph = g;
                        Point2D cp = new Point2D.Double(sumx, sumy);
                        vv.getRenderContext().getParallelEdgeIndexFunction().reset();
                        layout.setGraph(g);
                        layout.setLocation(clusterGraph, cp);
                        vv.getPickedVertexState().clear();
                        vv.repaint();
                    }
                }
                vv.getRenderContext().setEdgeDrawPaintTransformer(transformer_old_paint);
                vv.getRenderContext().setEdgeStrokeTransformer(transformer_old_stroke);
            }});

        JButton deleteEdges = new JButton("Delete Edge");
        deleteEdges.addActionListener(new ActionListener() {

        	public void actionPerformed(ActionEvent e) {
        		Collection picked = vv.getPickedEdgeState().getPicked();
        		if(picked.size() >= 1) {
        			for(Object ed : picked) {
        				Graph inGraph = layout.getGraph();
        				Object successor = inGraph.getDest(ed); 
        				if(inGraph.inDegree(successor)>1){
        					inGraph.removeEdge(ed);
        				}else{
        					continue;
        				}
        				vv.repaint();
        			}
        		}
                vv.getRenderContext().setEdgeDrawPaintTransformer(transformer_old_paint);
                vv.getRenderContext().setEdgeStrokeTransformer(transformer_old_stroke);
        	}});

        JButton deleteVertix = new JButton("Delete Vertex");
        deleteVertix.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
                if(picked.size() >= 1) {
                	Graph inGraph = layout.getGraph();
                    for(Object v : picked) {
                    	BFSDistanceLabeler BFS = new BFSDistanceLabeler();
                    	BFS.labelDistances(inGraph, v);
                    	Point2D p = (Point2D)layout.transform(v);
                    	Collection picked_aux = new HashSet();
                    	Collection successors =  BFS.getVerticesInOrderVisited();           	
                        for (Object obj : successors){
                        	if(compressVertice(inGraph,picked_aux,obj)){
                        		picked_aux.add(obj);
                        	}
                        }
                        for (Object obj : picked_aux){
                            inGraph.removeVertex(obj);
                            vv.repaint();
                        }
                    }

                }
                vv.getRenderContext().setEdgeDrawPaintTransformer(transformer_old_paint);
                vv.getRenderContext().setEdgeStrokeTransformer(transformer_old_stroke);
            }});
        
        JButton path = new JButton("Path");
        path.addActionListener(new ActionListener() {

        	public void actionPerformed(ActionEvent e) {
        		Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
        		if(picked.size()==2){
        			
        			Pair<MyNode> pair = new Pair<MyNode>(picked);
        			if((NetworkFigure.MyNode.class.isInstance(pair.getFirst()) && NetworkFigure.MyNode.class.isInstance(pair.getSecond()))){

        				Transformer<MyLink, Double> wtTransformer = new Transformer<MyLink,Double>() {
        					public Double transform(MyLink link) {
        						return 1 - link.GetDistance();
        					}
        				};
        				Graph inGraph = layout.getGraph();
        				DijkstraShortestPath<MyNode,MyLink> alg = new DijkstraShortestPath(inGraph,wtTransformer);
        				List<MyLink> l1 = alg.getPath((MyNode)pair.getFirst(),(MyNode)pair.getSecond());
        				List<MyLink> l2 = alg.getPath((MyNode)pair.getSecond(),(MyNode)pair.getFirst());

        				if(l1.size()>l2.size()){
        					l = l1; 
        				}else{
        					l = l2; 
        				}
        				vv.getPickedVertexState().clear();
        				//To paint the path
        				vv.getRenderContext().setEdgeDrawPaintTransformer(new MyEdgePaintFunction());
        				vv.getRenderContext().setEdgeStrokeTransformer(new MyEdgeStrokeFunction());
        				vv.repaint();
        			}

        		}
        	}});
        
        
        JButton expand = new JButton("Expand");
        expand.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
                for(Object v : picked) {
                    if(v instanceof Graph) {
                    	Graph inGraph = layout.getGraph();
                        Graph g = collapser.expand(layout.getGraph(), (Graph)v);
                        vv.getRenderContext().getParallelEdgeIndexFunction().reset();
                        layout.setGraph(g);
                    }
                    vv.getPickedVertexState().clear();
                   vv.repaint();
                }
                vv.getRenderContext().setEdgeDrawPaintTransformer(transformer_old_paint);
                vv.getRenderContext().setEdgeStrokeTransformer(transformer_old_stroke);
            }});
        
        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            	figure.cloning_Graphs();
                graph = figure.graph;
                collapsedGraph = graph;
                layout.setGraph(graph);
                collapser = new GraphCollapser(graph);
                //exclusions.clear();
                vv.repaint();
                ini = true;
                vv.getRenderContext().setEdgeDrawPaintTransformer(transformer_old_paint);
                vv.getRenderContext().setEdgeStrokeTransformer(transformer_old_stroke);
            }});

        JButton help = new JButton("Help");
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog((JComponent)e.getSource(), instructions, "Help", JOptionPane.PLAIN_MESSAGE);
                vv.getRenderContext().setEdgeDrawPaintTransformer(transformer_old_paint);
                vv.getRenderContext().setEdgeStrokeTransformer(transformer_old_stroke);
            }
        });
//        Class[] combos = getCombos();
//        final JComboBox jcb = new JComboBox(combos);
//        // use a renderer to shorten the layout name presentation
//        jcb.setRenderer(new DefaultListCellRenderer() {
//            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//                String valueString = value.toString();
//                valueString = valueString.substring(valueString.lastIndexOf('.')+1);
//                return super.getListCellRendererComponent(list, valueString, index, isSelected,
//                        cellHasFocus);
//            }
//        });
//        jcb.addActionListener(new LayoutChooser(jcb, vv));
//        jcb.setSelectedItem(FRLayout.class);

        JPanel controls = new JPanel();
        JPanel zoomControls = new JPanel(new GridLayout(2,1));
        zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
        zoomControls.add(plus);
        zoomControls.add(minus);
        controls.add(zoomControls);
        JPanel collapseControls = new JPanel(new GridLayout(3,2));
        collapseControls.setBorder(BorderFactory.createTitledBorder("Actions"));
        collapseControls.add(collapse);
        collapseControls.add(expand);
        collapseControls.add(deleteVertix);
        collapseControls.add(deleteEdges);
        collapseControls.add(path);
        collapseControls.add(reset);
        controls.add(collapseControls);
        controls.add(modeBox);
        controls.add(help);
        //controls.add(jcb);
        content.add(controls, BorderLayout.SOUTH);
    }
    
    private boolean compressVertice(Graph inGraph, Collection collected, Object vertice){
    	Collection predecessors = inGraph.getPredecessors(vertice);
    	if(collected.size()==0 && inGraph.getIncidentCount(vertice)<=1){
    		return true;
    	}
    	for (Object obj : predecessors){
    		if(!collected.contains(obj)){
    			return false;
    		}
    	}
    	return true;

    }
    

    
    private List<Cluster> createClusters_1() {
	List<Cluster> clusters = new ArrayList<Cluster>();
		
		List<Integer> temp2 = new ArrayList<Integer>(Util.list(1,2));
		List<Integer> temp3 = new ArrayList<Integer>(Util.list(1,2,3));
		
		
		List<Dir> conf1 = new ArrayList<Dir>();
		conf1.add(Dir.PARA);
		Integer[][] st1 = new Integer[2][2];
		st1[0][0] = 5; 
		st1[0][1] = 10;
		st1[1][0] =	40;
		st1[1][1] = 45;		
		
		List<Dir> conf2 = new ArrayList<Dir>();
		conf2.add(Dir.ANTI);
		Integer[][] st2 = new Integer[2][2];
		st2[0][0] = 5; 
		st2[0][1] = 10;
		st2[1][0] =	40;
		st2[1][1] = 45;	
		
		
		ArrayList<Dir> conf3 = new ArrayList<Dir>();
		conf3.add(Dir.PARA);
		conf3.add(Dir.PARA);
		Integer[][] st3 = new Integer[3][2];
		st3[0][0] = 5; 
		st3[0][1] = 10;
		st3[1][0] =	30;
		st3[1][1] = 35;
		st3[2][0] = 40; 
		st3[2][1] = 45;
		

		ArrayList<Dir> conf4 = new ArrayList<Dir>();
		conf4.add(Dir.PARA);
		conf4.add(Dir.ANTI);
		Integer[][] st4 = new Integer[3][2];
		st4[0][0] = 5; 
		st4[0][1] = 10;
		st4[1][0] =	30;
		st4[1][1] = 35;
		st4[2][0] = 40; 
		st4[2][1] = 45;
		
		
		
		
		//Creating the unfolded state.
		clusters.add(new Cluster(new ArrayList<Integer>(), new ArrayList<Dir>(), null, 0.003));

		
		
		
		Sample samp1 = new Sample(temp2,conf1,st1,80,0.005,0);
		
		Sample samp2 = new Sample(temp2,conf2,st2,80,0.016,0);
		
		Sample samp3 = new Sample(temp3,conf3,st3,80,0.025,0);
		
		Sample samp4 = new Sample(temp3,conf4,st4,80,0.025,0);
		
		
		List<Sample> samples1 = new LinkedList<Sample>();
		samples1.add(samp1);
		clusters.add(new Cluster(temp2, conf1,samples1, 0.002));
		
		
		
		List<Sample> samples2 = new LinkedList<Sample>();
		samples2.add(samp2);
		clusters.add(new Cluster(temp2, conf2, samples2, 0.001));
		
		
		
		List<Sample> samples3 = new LinkedList<Sample>();
		samples3.add(samp3);
		clusters.add(new Cluster(temp3, conf3, samples3, 0.004));

		
		
		List<Sample> samples4 = new LinkedList<Sample>();
		samples4.add(samp4);
		clusters.add(new Cluster(temp3, conf4, samples4, 0.004));

		return clusters;	
    }
    
    
    private List<Cluster> createClusters_2() {
	List<Cluster> clusters = new ArrayList<Cluster>();
		
		List<Integer> temp12 = new ArrayList<Integer>(Util.list(1,2));
		List<Integer> temp123 = new ArrayList<Integer>(Util.list(1,2,3));
		List<Integer> temp132 = new ArrayList<Integer>(Util.list(1,3,2));
		List<Integer> temp312 = new ArrayList<Integer>(Util.list(3,1,2));
		List<Integer> temp4312 = new ArrayList<Integer>(Util.list(4,3,1,2));
		List<Integer> temp4123 = new ArrayList<Integer>(Util.list(4,1,2,3));
		List<Integer> temp3142 = new ArrayList<Integer>(Util.list(3,1,4,2));
		List<Integer> temp3124 = new ArrayList<Integer>(Util.list(3,1,2,4));
		List<Integer> temp1234 = new ArrayList<Integer>(Util.list(1,2,3,4));
		List<Integer> temp1342 = new ArrayList<Integer>(Util.list(1,3,4,2));
		List<Integer> temp1432 = new ArrayList<Integer>(Util.list(1,4,3,2));
		List<Integer> temp4132 = new ArrayList<Integer>(Util.list(4,1,3,2));
		List<Integer> temp1423 = new ArrayList<Integer>(Util.list(1,4,2,3));
		List<Integer> temp1324 = new ArrayList<Integer>(Util.list(1,3,2,4));
		
		
		
		List<Dir> confP = new ArrayList<Dir>(Util.list(Dir.PARA));
		List<Dir> confA = new ArrayList<Dir>(Util.list(Dir.ANTI));
		List<Dir> confPP = new ArrayList<Dir>(Util.list(Dir.PARA,Dir.PARA));		
		List<Dir> confPA = new ArrayList<Dir>(Util.list(Dir.PARA,Dir.ANTI));
		List<Dir> confAA = new ArrayList<Dir>(Util.list(Dir.ANTI,Dir.ANTI));
		List<Dir> confAAA = new ArrayList<Dir>(Util.list(Dir.ANTI,Dir.ANTI,Dir.ANTI));
		List<Dir> confAAP = new ArrayList<Dir>(Util.list(Dir.ANTI,Dir.ANTI,Dir.PARA));
		List<Dir> confPAP = new ArrayList<Dir>(Util.list(Dir.PARA,Dir.ANTI,Dir.PARA));
		List<Dir> confPAA = new ArrayList<Dir>(Util.list(Dir.PARA,Dir.ANTI,Dir.ANTI));
		List<Dir> confPPP = new ArrayList<Dir>(Util.list(Dir.PARA,Dir.PARA,Dir.PARA));
		List<Dir> confPPA = new ArrayList<Dir>(Util.list(Dir.PARA,Dir.PARA,Dir.ANTI));
		List<Dir> confAPP = new ArrayList<Dir>(Util.list(Dir.ANTI,Dir.PARA,Dir.PARA));
		
		
		
		Integer[][] st2 = new Integer[2][2];
		st2[0][0] = 5; 
		st2[0][1] = 10;
		st2[1][0] =	40;
		st2[1][1] = 45;	
		

		Integer[][] st3 = new Integer[3][2];
		st3[0][0] = 5; 
		st3[0][1] = 10;
		st3[1][0] =	30;
		st3[1][1] = 35;
		st3[2][0] = 40; 
		st3[2][1] = 45;
		


		Integer[][] st4 = new Integer[4][2];
		st4[0][0] = 5; 
		st4[0][1] = 10;
		st4[1][0] =	30;
		st4[1][1] = 35;
		st4[2][0] = 40; 
		st4[2][1] = 45;
		st4[3][0] = 50; 
		st4[3][1] = 55;
		
		
		
		
		//Creating the unfolded state.
		clusters.add(new Cluster(new ArrayList<Integer>(), new ArrayList<Dir>(), null, 0.003));

		
		
		
		Sample samp1 = new Sample(temp12,confP,st2,80,0.005,0);	
		Sample samp2 = new Sample(temp12,confA,st2,80,0.016,0);
		Sample samp3 = new Sample(temp123,confAA,st3,80,0.025,0);
		Sample samp4 = new Sample(temp312,confAA,st3,80,0.025,0);
		Sample samp5 = new Sample(temp312,confPA,st3,80,0.025,0);
		Sample samp6 = new Sample(temp123,confPP,st3,80,0.025,0);
		Sample samp7 = new Sample(temp132,confAA,st3,80,0.025,0);
		Sample samp8 = new Sample(temp132,confPP,st3,80,0.025,0);
		Sample samp9 = new Sample(temp4312,confAAA,st4,80,0.025,0);
		Sample samp10 = new Sample(temp4123,confAAP,st4,80,0.025,0);
		Sample samp11 = new Sample(temp3142,confAAP,st4,80,0.025,0);
		Sample samp12 = new Sample(temp3124,confPAP,st4,80,0.025,0);
		Sample samp13 = new Sample(temp3124,confPAA,st4,80,0.025,0);
		Sample samp14 = new Sample(temp1234,confPPP,st4,80,0.025,0);
		Sample samp15 = new Sample(temp1342,confPPA,st4,80,0.025,0);
		Sample samp16 = new Sample(temp1432,confPPA,st4,80,0.025,0);
		Sample samp17 = new Sample(temp4132,confAPP,st4,80,0.025,0);
		Sample samp18 = new Sample(temp1423,confAAP,st4,80,0.025,0);
		Sample samp19 = new Sample(temp1324,confAAA,st4,80,0.025,0);
		
		List<Sample> samples1 = new LinkedList<Sample>();
		samples1.add(samp1);
		List<Sample> samples2 = new LinkedList<Sample>();
		samples2.add(samp2);
		List<Sample> samples3 = new LinkedList<Sample>();
		samples3.add(samp3);
		List<Sample> samples4 = new LinkedList<Sample>();
		samples4.add(samp4);
		List<Sample> samples5 = new LinkedList<Sample>();
		samples5.add(samp5);
		List<Sample> samples6 = new LinkedList<Sample>();
		samples6.add(samp6);
		List<Sample> samples7 = new LinkedList<Sample>();
		samples7.add(samp7);
		List<Sample> samples8 = new LinkedList<Sample>();
		samples8.add(samp8);
		List<Sample> samples9 = new LinkedList<Sample>();
		samples9.add(samp9);
		List<Sample> samples10 = new LinkedList<Sample>();
		samples10.add(samp10);
		List<Sample> samples11 = new LinkedList<Sample>();
		samples11.add(samp11);
		List<Sample> samples12 = new LinkedList<Sample>();
		samples12.add(samp12);
		List<Sample> samples13 = new LinkedList<Sample>();
		samples13.add(samp13);
		List<Sample> samples14 = new LinkedList<Sample>();
		samples14.add(samp14);
		List<Sample> samples15 = new LinkedList<Sample>();
		samples15.add(samp15);
		List<Sample> samples16 = new LinkedList<Sample>();
		samples16.add(samp16);
		List<Sample> samples17 = new LinkedList<Sample>();
		samples17.add(samp17);
		List<Sample> samples18 = new LinkedList<Sample>();
		samples18.add(samp18);
		List<Sample> samples19 = new LinkedList<Sample>();
		samples19.add(samp19);
		
		clusters.add(new Cluster(temp12, confP,samples1, 0.002));
		clusters.add(new Cluster(temp12, confA, samples2, 0.001));
		clusters.add(new Cluster(temp123, confAA, samples3, 0.004));
		clusters.add(new Cluster(temp312, confAA, samples4, 0.004));
		clusters.add(new Cluster(temp312, confPA, samples5, 0.004));
		clusters.add(new Cluster(temp123, confPP, samples6, 0.004));
		clusters.add(new Cluster(temp132, confAA, samples7, 0.004));
		clusters.add(new Cluster(temp132, confPP, samples8, 0.004));
		clusters.add(new Cluster(temp4312, confAAA, samples9, 0.004));
		clusters.add(new Cluster(temp4123, confAAP, samples10, 0.004));
		clusters.add(new Cluster(temp3142, confAAP, samples11, 0.004));
		clusters.add(new Cluster(temp3124, confPAP, samples12, 0.004));
		clusters.add(new Cluster(temp3124, confPAA, samples13, 0.004));
		clusters.add(new Cluster(temp1234, confPPP, samples14, 0.004));
		clusters.add(new Cluster(temp1342, confPPA, samples15, 0.004));
		clusters.add(new Cluster(temp1432, confPPA, samples16, 0.004));
		clusters.add(new Cluster(temp4132, confAPP, samples17, 0.004));
		clusters.add(new Cluster(temp1423, confAAP, samples18, 0.004));
		clusters.add(new Cluster(temp1324, confAAA, samples19, 0.004));

		return clusters;	
    }
    
    

    /**
     * a demo class that will create a vertex shape that is either a
     * polygon or star. The number of sides corresponds to the number
     * of vertices that were collapsed into the vertex represented by
     * this shape.
     *
     * @author Tom Nelson
     *
     * @param <V>
     */
    class ClusterVertexShapeFunction<V> extends EllipseVertexShapeTransformer<V> {

        ClusterVertexShapeFunction() {
            setSizeTransformer(new ClusterVertexSizeFunction<V>(20));
        }
        @Override
        public Shape transform(V v) {
            if(v instanceof Graph) {
                int size = ((Graph)v).getVertexCount();
                if (size < 8) {
                    int sides = Math.max(size, 3);
                    return factory.getRegularPolygon(v, sides);
                }
                else {
                    return factory.getRegularStar(v, size);
                }
            }
            return super.transform(v);
        }
    }

    /**
     * A demo class that will make vertices larger if they represent
     * a collapsed collection of original vertices
     * @author Tom Nelson
     *
     * @param <V>
     */
    class ClusterVertexSizeFunction<V> implements Transformer<V,Integer> {
    	int size;
        public ClusterVertexSizeFunction(Integer size) {
            this.size = size;
        }

        public Integer transform(V v) {
            if(v instanceof Graph) {
                return 30;
            }
            return size;
        }
    }


    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    private Class<? extends Layout>[] getCombos()
    {
        List<Class<? extends Layout>> layouts = new ArrayList<Class<? extends Layout>>();
        layouts.add(KKLayout.class);
        layouts.add(FRLayout.class);
        layouts.add(CircleLayout.class);
        layouts.add(SpringLayout.class);
        layouts.add(SpringLayout2.class);
        layouts.add(ISOMLayout.class);
        return layouts.toArray(new Class[0]);
    }

    
    /**
     * A simple implementation of VertexStringer that
     * gets Vertex labels from a Map  
     * 
     * @author Tom Nelson
     *
     *
     */
    public static class VertexStringerImpl<V,S> implements Transformer<V,String> {
        
        Map<V,String> map = new HashMap<V,String>();
        
        boolean enabled = true;
        
        public VertexStringerImpl(Map<V,String> map) {
            this.map = map;
        }
        
        /* (non-Javadoc)
         * @see edu.uci.ics.jung.graph.decorators.VertexStringer#getLabel(edu.uci.ics.jung.graph.Vertex)
         */
        public String transform(V v) {
            if(isEnabled()) {
                return map.get(v);
            } else {
                return "";
            }
            
        }
        
        /**
         * @return Returns the enabled.
         */
        public boolean isEnabled() {
            return enabled;
        }
        
        /**
         * @param enabled The enabled to set.
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    class Rings implements VisualizationServer.Paintable {
    	
    	Collection<Double> depths;
    	Map<Number,PolarPoint> polarLocations;
    	double inicio;
    	
    	public Rings() {
    		depths = getDepths();
    		polarLocations = figure.polarLocations;
    	}
    	
    	private Collection<Double> getDepths() {
//    		Set<Double> depths = new HashSet<Double>();
//    		Map<Number,PolarPoint> polarLocations = figure.polarLocations;
//    		for(Object v : graph.getVertices()) {
//    			PolarPoint pp = polarLocations.get(v);
//    			depths.add(pp.getRadius());
//    		}
//    		return depths;
    		return figure.depths;
    	}

		public void paint(Graphics g) {
			g.setColor(Color.gray);
			Graphics2D g2d = (Graphics2D)g;
			Point2D center = new Point2D.Double(300,300);

			Ellipse2D ellipse = new Ellipse2D.Double();

			for(double d : depths) {
				ellipse.setFrameFromDiagonal(center.getX()-d, center.getY()-d, 
						center.getX()+d, center.getY()+d);
				Shape shape = 
					vv.getRenderContext().getMultiLayerTransformer().transform(ellipse);
//					vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
				g2d.draw(shape);
			}
			if(ini){
				for(MyNode v : graph.getVertices()) {
					Point2D location = PolarPoint.polarToCartesian(polarLocations.get(v.id()));
					location.setLocation(location.getX()+300, location.getY()+300);
					layout.setLocation(v, location);
					transformer_old_paint = vv.getRenderContext().getEdgeDrawPaintTransformer(); 
					transformer_old_stroke = vv.getRenderContext().getEdgeStrokeTransformer();
				}
			}
			ini=false;
		}

		public boolean useTransform() {
			return true;
		}
    }    

    
	public class MyEdgePaintFunction implements Transformer<MyLink,Paint> {
	    
		public Paint transform(MyLink e) {
			if(!l.equals(null) && l.contains( e )) {
				return new Color(0.0f, 0.0f, 1.0f, 0.5f);//Color.BLUE;
			} else {
				return Color.BLACK;
			}
		}
	}
	
	public class MyEdgeStrokeFunction implements Transformer<MyLink,Stroke> {
        protected final Stroke THIN = new BasicStroke(1);
        protected final Stroke THICK = new BasicStroke(5);

        public Stroke transform(MyLink e) {
			if(!l.equals(null) && l.contains( e )) { 
			    return THICK;
			} else 
			    return THIN;
        }
	    
	}
    
    
    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new PluggableRenderer());
        f.pack();
        f.setVisible(true);
    }
}
