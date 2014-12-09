package plots;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StrandChart {
	
	private static DefaultCategoryDataset makeDataSet(double[][] strandDistribution){
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int i = 0; i < strandDistribution.length; i++){
			String series = "Strand "+(i+1);
			for(int j = 0; j< strandDistribution[i].length; j++){
				dataset.addValue(strandDistribution[i][j],series,new Integer(j+1));
			}
		}
		return dataset;
	}

	private static XYSeriesCollection pdbDataSet(double[] strandDistribution){
		XYSeriesCollection dataset = new XYSeriesCollection();

		XYSeries series = new XYSeries("PDB Structure");
		Double last = null;
		for(int i = 0; i< strandDistribution.length; i++){
			if(last != null && strandDistribution[i] != last){
				series.add(i,last);
			}
			series.add(i, strandDistribution[i]);
			last = strandDistribution[i];
		}
		dataset.addSeries(series);

		return dataset;
	}

	private static JFreeChart createChartWithPDB(double[][] strandDistribution, Double[] pdbAssignment){
		// Make the PDB series and add it to the predicted strand locations
		DefaultCategoryDataset dataset = makeDataSet(strandDistribution);
		double max = Double.MIN_VALUE;
		for(int i = 0; i < strandDistribution.length; i++){
			for(int j = 0; j< strandDistribution[i].length; j++){
				max = Math.max(max, strandDistribution[i][j]);
			}
		}
		for(int i = 0; i< pdbAssignment.length; i++){
			if(pdbAssignment[i] != null)
				dataset.addValue(pdbAssignment[i]*max*1.05,"PDB",new Integer(i+1));
			else
				dataset.addValue(null,"PDB",new Integer(i+1));		
		}
		
		// Create the chart
		JFreeChart chart =  ChartFactory.createLineChart("Strand Distribution", // Title
				"Amino-Acid index", // y-axis Label
				"Probability", // x-axis Label
				dataset, // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
		);
		((CategoryPlot)chart.getPlot()).getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		return chart;
	}
	
	private static JFreeChart createChart(double[][] strandDistribution){
		// Make the PDB series and add it to the predicted strand locations
		DefaultCategoryDataset dataset = makeDataSet(strandDistribution);
		
		// Create the chart
		JFreeChart chart =  ChartFactory.createLineChart("Strand Distribution", // Title
				"Amino-Acid index", // y-axis Label
				"Probability", // x-axis Label
				dataset, // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
		);
		((CategoryPlot)chart.getPlot()).getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		return chart;
	}

	private static JFreeChart createPDBChart(double[] strandDistribution){
		return ChartFactory.createXYLineChart("Strand Distribution", // Title
				"Amino-Acid index", // y-axis Label
				"Probability", // x-axis Label
				pdbDataSet(strandDistribution), // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
		);
	}

	public static void saveChart(double[][] strandDistribution, String fileName, int width, int height) throws IOException{
		ChartUtilities.saveChartAsPNG(new File(fileName), createChart(strandDistribution), width, height);
	}


	public static void showChart(double[][] strandDistribution){
		JFreeChart chart = createChart(strandDistribution);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		ApplicationFrame af = new ApplicationFrame("Strand Probabilities");
		af.setContentPane(chartPanel);
		af.pack();
		RefineryUtilities.centerFrameOnScreen(af);
		af.setVisible(true);	
	}

	public static void saveChartWithPDB(double[][] strandDistribution, Double[] pdbAssignments, String fileName, int width, int height) throws IOException{
		ChartUtilities.saveChartAsPNG(new File(fileName), createChartWithPDB(strandDistribution,pdbAssignments), width, height);
	}

	public static void showChartWithPDB(double[][] strandDistribution, Double[] pdbAssignments){
		JFreeChart chart = createChartWithPDB(strandDistribution,pdbAssignments);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		ApplicationFrame af = new ApplicationFrame("Strand Probabilities");
		af.setContentPane(chartPanel);
		af.pack();
		RefineryUtilities.centerFrameOnScreen(af);
		af.setVisible(true);	
	}

	public static void showPDB(double[] strandDistribution){
		JFreeChart chart = createPDBChart(strandDistribution);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		ApplicationFrame af = new ApplicationFrame("Strand Probabilities");
		af.setContentPane(chartPanel);
		af.pack();
		RefineryUtilities.centerFrameOnScreen(af);
		af.setVisible(true);	
	}

}