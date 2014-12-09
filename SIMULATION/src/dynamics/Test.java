package dynamics;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import dynamics.SampleDistance;

import org.apache.commons.collections15.Factory;

import dynamics.Util;
import dynamics.Cluster;
import dynamics.Constants.Dir;


import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.decorators.VertexIconShapeTransformer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.transform.LensSupport;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;



public class Test extends JApplet {
	
	//Forest<String,Integer> graph;
	Graph graph;
	Graph collapsedGraph;
	
	Factory<DirectedGraph<String,Integer>> graphFactory = 
		new Factory<DirectedGraph<String,Integer>>() {

		public DirectedGraph<String, Integer> create() {
			return new DirectedSparseGraph<String,Integer>();
		}
	};

	Factory<Tree<String,Integer>> treeFactory =
		new Factory<Tree<String,Integer>> () {

		public Tree<String, Integer> create() {
			return new DelegateTree<String,Integer>(graphFactory);
		}
	};
	Factory<Integer> edgeFactory = new Factory<Integer>() {
		int i=0;
		public Integer create() {
			return i++;
		}
	};

	Factory<String> vertexFactory = new Factory<String>() {
		int i=0;
		public String create() {
			return "V"+i++;
		}
	};

	VisualizationServer.Paintable rings;

	String root;

	KKLayout layout;

	RadialTreeLayout<Number,Number> radialLayout;

	/**
	 * the visual component and renderer for the graph
	 */
	VisualizationViewer<Number,Number> vv;

    /**
     * provides a Hyperbolic lens for the view
     */
    LensSupport hyperbolicViewSupport;
    
    ScalingControl scaler;
    
    GraphCollapser collapser;
    /**
     * create an instance of a simple graph with controls to
     * demo the zoomand hyperbolic features.
     * 
     */
    public Test() {
        
        // create a simple graph for the demo
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
    	
    	List<Cluster> clusters = createClusters();
    	Map<Cluster,Color> colors = Util.map(clusters, Util.getPalette(clusters.size()));
    	NetworkFigure figure = null;
		try {
			figure = new NetworkFigure(clusters, transMetric, transitionThreshold, colors);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}	
    	
        graph = new DelegateForest<Number,Number>();
        graph = figure.graph;
        collapsedGraph = graph;

        //createTree();
        
        
        
        

		

		

	


		

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        layout = new KKLayout<Number,Number>(graph);
     
        //Layout.setSize(new Dimension(600,600));
        
        collapser = new GraphCollapser(graph);

        Dimension preferredSize = new Dimension(600,600);
        
        final VisualizationModel<Number,Number> visualizationModel = 
            new DefaultVisualizationModel<Number,Number>(layout, preferredSize);
        vv =  new VisualizationViewer<Number,Number>(visualizationModel, preferredSize);
        
        
		final VertexIconShapeTransformer<Number> vertexImageShapeFunction =
				new VertexIconShapeTransformer<Number>(new EllipseVertexShapeTransformer<Number>());
			
			final DefaultVertexIconTransformer<Number> vertexIconFunction =
				new DefaultVertexIconTransformer<Number>();
			
			//vertexImageShapeFunction.setIconMap(figure.iconMap);
			//vertexIconFunction.setIconMap(figure.iconMap);
        
        
			//vv.getRenderContext().setVertexShapeTransformer(vertexImageShapeFunction);
			//vv.getRenderContext().setVertexIconTransformer(vertexIconFunction);
				
        
        
        

        PickedState<Number> ps = vv.getPickedVertexState();
        PickedState<Number> pes = vv.getPickedEdgeState();
        vv.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<Number>(ps, Color.red, Color.yellow));
        vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<Number>(pes, Color.black, Color.cyan));
        vv.setBackground(Color.white);
        
        //vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Number>());
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Number,Number>());
        
        
        
        
        
        // add a listener for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller<Number>());
        
        Container content = getContentPane();
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        content.add(gzsp);
        
        /**
         * the regular graph mouse for the normal view
         */
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());
        //rings = new Rings();
		//vv.addPreRenderPaintable(rings);

        hyperbolicViewSupport = 
            new ViewLensSupport<Number,Number>(vv, new HyperbolicShapeTransformer(vv, 
            		vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)), 
                    new ModalLensGraphMouse());
        
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
        
        final JRadioButton hyperView = new JRadioButton("Hyperbolic View");
        hyperView.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                hyperbolicViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        graphMouse.addItemListener(hyperbolicViewSupport.getGraphMouse().getModeListener());
        
        

        JButton collapse = new JButton("Collapse");
        collapse.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
                if(picked.size() > 1) {
                    Graph inGraph = layout.getGraph();
                    Graph clusterGraph = collapser.getClusterGraph(inGraph, picked);

                    Graph g = collapser.collapse(layout.getGraph(), clusterGraph);
                    collapsedGraph = g;
                    double sumx = 0;
                    double sumy = 0;
                    for(Object v : picked) {
                    	Point2D p = (Point2D)layout.transform(v);
                    	sumx += p.getX();
                    	sumy += p.getY();
                    }
                    Point2D cp = new Point2D.Double(sumx/picked.size(), sumy/picked.size());
                    vv.getRenderContext().getParallelEdgeIndexFunction().reset();
                    layout.setGraph(g);
                    layout.setLocation(clusterGraph, cp);
                    vv.getPickedVertexState().clear();
                    vv.repaint();
                }
            }});
        JButton expand = new JButton("Expand");
        expand.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
                for(Object v : picked) {
                    if(v instanceof Graph) {

                        Graph g = collapser.expand(layout.getGraph(), (Graph)v);
                        vv.getRenderContext().getParallelEdgeIndexFunction().reset();
                        layout.setGraph(g);
                    }
                    vv.getPickedVertexState().clear();
                   vv.repaint();
                }
            }});

        
        JMenuBar menubar = new JMenuBar();
        menubar.add(graphMouse.getModeMenu());
        gzsp.setCorner(menubar);

        JPanel controls = new JPanel();
        JPanel zoomControls = new JPanel(new GridLayout(2,1));
        zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
        JPanel hyperControls = new JPanel(new GridLayout(3,2));
        hyperControls.setBorder(BorderFactory.createTitledBorder("Examiner Lens"));
        zoomControls.add(plus);
        zoomControls.add(minus);
        JPanel modeControls = new JPanel(new BorderLayout());
        modeControls.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
        modeControls.add(graphMouse.getModeComboBox());
        hyperControls.add(hyperView);
        JPanel expandControls = new JPanel(new GridLayout(2,1));
        expandControls.setBorder(BorderFactory.createTitledBorder("Expand-Collapse"));
        expandControls.add(collapse);
        expandControls.add(expand);

        
        controls.add(zoomControls);
        controls.add(hyperControls);
        controls.add(modeControls);
        controls.add(expandControls);
        content.add(controls, BorderLayout.SOUTH);
    }
    
    private List<Cluster> createClusters() {
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
    
    private void createTree() {
    /* 	graph.addVertex("V0");
    	graph.addEdge(edgeFactory.create(), "V0", "V1");
    	graph.addEdge(edgeFactory.create(), "V0", "V2");
    	graph.addEdge(edgeFactory.create(), "V1", "V4");
    	graph.addEdge(edgeFactory.create(), "V2", "V3");
    	graph.addEdge(edgeFactory.create(), "V2", "V5");
    	graph.addEdge(edgeFactory.create(), "V4", "V6");
    	graph.addEdge(edgeFactory.create(), "V4", "V7");
    	graph.addEdge(edgeFactory.create(), "V3", "V8");
    	graph.addEdge(edgeFactory.create(), "V6", "V9");
    	graph.addEdge(edgeFactory.create(), "V4", "V10");
    	graph.addEdge(edgeFactory.create(), "V0", "A1");
    	
       	graph.addVertex("A0");
       	graph.addEdge(edgeFactory.create(), "A0", "A1");
       	graph.addEdge(edgeFactory.create(), "A0", "A2");
       	graph.addEdge(edgeFactory.create(), "A0", "A3");
       	
       	graph.addVertex("B0");
    	graph.addEdge(edgeFactory.create(), "B0", "B1");
    	graph.addEdge(edgeFactory.create(), "B0", "B2");
    	graph.addEdge(edgeFactory.create(), "B1", "B4");
    	graph.addEdge(edgeFactory.create(), "B2", "B3");
    	graph.addEdge(edgeFactory.create(), "B2", "B5");
    	graph.addEdge(edgeFactory.create(), "B4", "B6");
    	graph.addEdge(edgeFactory.create(), "B4", "B7");
    	graph.addEdge(edgeFactory.create(), "B3", "B8");
    	graph.addEdge(edgeFactory.create(), "B6", "B9");
      */ 	
    }

    class Rings implements VisualizationServer.Paintable {
    	
    	Collection<Double> depths;
    	
    	public Rings() {
    		depths = getDepths();
    	}
    	
    	private Collection<Double> getDepths() {
//    		Set<Double> depths = new HashSet<Double>();
//    		Map<Number,PolarPoint> polarLocations = radialLayout.getPolarLocations();
//    		for(Number v : graph.getVertices()) {
//    			PolarPoint pp = polarLocations.get(v);
//    			depths.add(pp.getRadius());
//    		}
    		return depths;
    		
    		
    	}

		public void paint(Graphics g) {
			g.setColor(Color.gray);
			Graphics2D g2d = (Graphics2D)g;
			Point2D center = radialLayout.getCenter();

			Ellipse2D ellipse = new Ellipse2D.Double();
			for(double d : depths) {
				ellipse.setFrameFromDiagonal(center.getX()-d, center.getY()-d, 
						center.getX()+d, center.getY()+d);
				Shape shape = 
					vv.getRenderContext().getMultiLayerTransformer().transform(ellipse);
//					vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
				g2d.draw(shape);
			}
		}

		public boolean useTransform() {
			return true;
		}
    }

    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new Test());
        f.pack();
        f.setVisible(true);
    }

}
