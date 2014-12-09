package util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class ChartUtils {
	
	public static XYSeriesCollection createXYDataset(List<String> labels, List<Double[][]> data){
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		for(int i=0; i<labels.size(); i++){
			String seriesName = labels.get(i);
			Double[][] seriesData = data.get(i);
			XYSeries series = new XYSeries(seriesName);
			
			for(Double[] d : seriesData)
				series.add(d[0], d[1]);
			

			dataset.addSeries(series);
		}
		
		return dataset;
	}
	
	public static JFreeChart createChart(List<String> labels, List<Double[][]> data, List<Color> colors, String title, String xAxis, String yAxis){
		// Make the PDB series and add it to the predicted strand locations
		XYDataset dataset = createXYDataset(labels, data);
	
		JFreeChart chart =  ChartFactory.createXYLineChart(
				title,
				xAxis,
				yAxis,
				dataset,
				PlotOrientation.VERTICAL,
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
		);
		
		chart.getPlot().setBackgroundAlpha(0);
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			((XYPlot)chart.getPlot()).getRenderer().setSeriesPaint(i, colors.get(i));
		}
		return chart;
	}
	
	public static void showChart(JFreeChart chart){
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		ApplicationFrame af = new ApplicationFrame(chart.getTitle().getText());
		af.setContentPane(chartPanel);
		af.pack();
		RefineryUtilities.centerFrameOnScreen(af);
		af.setVisible(true);	
	}
	
	public static List<Color> getPalette(int number){
		List<Color> palette = new ArrayList<Color>();
		for(float i=0; i<number; i++){
			palette.add(Color.getHSBColor(i/number, 1f, 1f));
		}
		return palette;
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
	
	public static void saveChart(JFreeChart chart, String fileName, int width, int height) throws IOException{
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		chart.draw((Graphics2D) img.getGraphics(), new Rectangle(width, height));
		
		ImageIO.write(img, "png", new File(fileName));
	/*	// Get a DOMImplementation.
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D g = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.  
        chart.draw(g, new Rectangle2D.Double(0, 0, width, height));
       // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
     //   File toSave = new File(fileName);
        Writer out = new FileWriter(fileName);// OutputStreamWriter(System.out, "UTF-8");
        g.stream(out, useCSS);*/
	}
	
}
