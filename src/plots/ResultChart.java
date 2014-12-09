package plots;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ResultChart {

	private static XYSeriesCollection makeDataSet(double[] precision, double[] sensitivity, double[] fMeasure){
		XYSeriesCollection dataset = new XYSeriesCollection();

		XYSeries p = new XYSeries("Precision");
		XYSeries s = new XYSeries("Sensitivity");
		XYSeries f = new XYSeries("F-Measure");
		
		for(int i = 0; i< precision.length; i++){
			p.add(((double) i)/(precision.length-1), precision[i]);
		}
		for(int i = 0; i< sensitivity.length; i++){
			s.add(((double) i)/(sensitivity.length-1), sensitivity[i]);
		}
		for(int i = 0; i< fMeasure.length; i++){
			f.add(((double) i)/(fMeasure.length-1), fMeasure[i]);
		}
		
		dataset.addSeries(p);
		dataset.addSeries(s);
		dataset.addSeries(f);

		return dataset;
	}

	private static JFreeChart createChart(String title, String xLabel, String yLabel, double[] precision, double[] sensitivity, double[] fMeasure){
		return ChartFactory.createXYLineChart(title, // Title
				xLabel, // x-axis Label
				yLabel, // y-axis Label
				makeDataSet(precision, sensitivity, fMeasure), // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
		);
	}

	

	public static void saveChart(double[] precision, double[] sensitivity, double[] fMeasure, String fileName, int width, int height, String title, String xLabel, String yLabel) throws IOException{
		ChartUtilities.saveChartAsPNG(new File(fileName), createChart(title, xLabel, yLabel, precision, sensitivity, fMeasure), width, height);
	}

	public static void showChart(double[] precision, double[] sensitivity, double[] fMeasure, String title, String xLabel, String yLabel){
		JFreeChart chart = createChart(title, xLabel, yLabel, precision, sensitivity, fMeasure);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		ApplicationFrame af = new ApplicationFrame("Results");
		af.setContentPane(chartPanel);
		af.pack();
		RefineryUtilities.centerFrameOnScreen(af);
		af.setVisible(true);	
	}
	
}
