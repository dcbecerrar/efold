package plots;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.data.general.HeatMapUtilities;

public class ContactChart {

	private ContactChart(){}

	public static void main(String[] args) {
		//JFrame f = new JFrame();
		String scale_seq = "";
		HashMap<List<Integer>,Double> mm = new HashMap<List<Integer>,Double>();
		for(int i=0; i<6; i++){
			for(int j=0; j<1; j++){
				mm.put(Arrays.asList(new Integer[]{i,j}), (i+j)/12.0);
			}
		}
		BufferedImage img1 =getChart(mm,"ABCDEFGHIJKLMNOPQRSTUVWXYZ", Color.white, Color.blue);
		/*JPanel image = new ImagePanel(img);
		image.setPreferredSize(new Dimension(img.getWidth(),img.getHeight()));
		f.getContentPane().add(image);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);*/
	
		JFrame f = new JFrame();
		HashMap<List<Integer>,Double> m = new HashMap<List<Integer>,Double>();
		for(int i=1; i<2; i++){
			for(int j=1; j<4; j++){
							m.put(Arrays.asList(new Integer[]{j,i}), (i+j)/5.0);
			}
		}

		for(int i=2; i<4; i++){
			for(int j=1; j<4; j++){
							m.put(Arrays.asList(new Integer[]{j,i}), 0.0);
			}
		}	
		
		HashMap<List<Integer>,Double> scale = new HashMap<List<Integer>,Double>();
		for(int i=1; i<=10; i++){
			scale.put(Arrays.asList(new Integer[]{i,1}), Double.valueOf(i));
		}
		scale_seq =    "0 .2 .4 .6 .8 1";

		String label = "1    2    3    4"; 
		
		
		//scale_seq =    "0   .16 .25 .33 .42 .36  .5 .58 .66 .75 .83 .92 1";
		//label = "1    2    3    4"; 
		
		//BufferedImage img =getChartHeat(m,m,m,scale,label, scale_seq,Color.white, Color.red);
		BufferedImage img =getChartProb(m,scale,"1P2 1P2P3 1P2P3",scale_seq,Color.white, Color.red);
		//BufferedImage img =getChart(m,"1   2   3",Color.white, Color.red);
		
		JPanel image = new ImagePanel(img);
		image.setPreferredSize(new Dimension(img.getWidth(),img.getHeight()));
		
		
		f.getContentPane().add(image);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		
	}


	static BufferedImage axes(String label){
		int dim = label.length()*11 + 11;
		BufferedImage img = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setBackground(Color.white);
		g.clearRect(0, 0, dim, dim);
		g.drawImage(hAxis(label), 11, label.length()*11 , null);
		g.drawImage(vAxis(label), 0, 0, null);
		return img;
	}
	
	static BufferedImage axesProb(String label, String scale_label){
		int dim = label.length()*11 + 11;
		BufferedImage img = new BufferedImage(dim, dim+50, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setBackground(Color.white);
		g.clearRect(0, 0, dim, dim+50);
		g.drawImage(hAxis(label), 11, label.length()*11+31 , null);
		g.drawImage(hAxisHeat(scale_label), 0, 5 , null);
		//g.drawImage(vAxisProb(label_aux), 0, 0, null);
		return img;
	}
	
	static BufferedImage axesHeat(String label, String max_strand, String scale_label){
		int dim = label.length()*11 + 11;
		String labelV = label.replace(max_strand, " ");
		String labelH = label.replace("1", " ")+label.replace("1", " ")+label.replace("1", " ");
		BufferedImage img = new BufferedImage(dim*3, dim + 50, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setBackground(Color.white);
		g.clearRect(0, 0, dim*3, dim+50);
		g.drawImage(hAxisHeat(labelH), 11, label.length()*11 , null);
		g.drawImage(hAxisHeat(scale_label), 11, label.length()*11+40 , null);
		g.drawImage(vAxisHeat(labelV), 0, 0, null);
		return img;
	}
	
	static BufferedImage axesHeatScale(String label, String label_series){
		int dim = label.length()*11 + 11;
		String labelH = label.replace("1", " ")+label.replace("1", " ")+label.replace("1", " ");
		BufferedImage img = new BufferedImage(dim*3, dim + 50, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setBackground(Color.white);
		g.clearRect(0, 0, dim*3, dim+50);
		g.drawImage(hAxisHeat(labelH), 11, label.length()*11+40 , null);
		return img;
	}

	static BufferedImage hAxis(String label){
		int dim = label.length()*11;
		BufferedImage img = new BufferedImage(dim, 11, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = img.createGraphics();
		graphics.setFont(new Font("Monospaced", Font.PLAIN, 10));
		graphics.setBackground(Color.white);
		graphics.setColor(Color.black);
		graphics.clearRect(0, 0, dim, 11);
		int i = 0;
		for(char c = label.charAt(i); i < label.length(); i++ ){
			c = label.charAt(i);
			graphics.drawString(""+c, (i*11)+3, 9);
		}

		return img;
	}
	
	static BufferedImage hAxisHeat(String label){
		int dim = label.length()*11;
		BufferedImage img = new BufferedImage(dim, 11, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = img.createGraphics();
		graphics.setFont(new Font("Monospaced", Font.PLAIN, 10));
		graphics.setBackground(Color.white);
		graphics.setColor(Color.black);
		graphics.clearRect(0, 0, dim, 11);
		int i = 0;
		for(char c = label.charAt(i); i < label.length(); i++ ){
			c = label.charAt(i);
			graphics.drawString(""+c, (i*11)+3, 9);
		}

		return img;
	}

	static BufferedImage vAxis(String label){
		int dim = label.length()*11;
		BufferedImage img = new BufferedImage(11, dim, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = img.createGraphics();
		graphics.setFont(new Font("Monospaced", Font.PLAIN, 10));
		graphics.setBackground(Color.white);
		graphics.setColor(Color.black);
		graphics.clearRect(0, 0, 11, dim);
		int i = 0;
		for(char c = label.charAt(i); i < label.length(); i++ ){
			c = label.charAt(i);
			graphics.drawString(""+c, 3, 11*(label.length()-i) - 1);
		}

		return img;
	}
	
	static BufferedImage vAxisProb(String label){
		int dim = label.length()*11;
		BufferedImage img = new BufferedImage(11, dim, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = img.createGraphics();
		graphics.setFont(new Font("Monospaced", Font.PLAIN, 10));
		graphics.setBackground(Color.white);
		graphics.setColor(Color.black);
		graphics.clearRect(0, 0, 11, dim);
		int i=0;
		for(char c = label.charAt(i); i < label.length(); i++ ){
			c = label.charAt(i);
			graphics.drawString(""+c, 3, 11*(label.length()-i) - 1);
		}
		return img;
	}
	
	static BufferedImage vAxisHeat(String label){
		int dim = label.length()*11;
		BufferedImage img = new BufferedImage(11, dim, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = img.createGraphics();
		graphics.setFont(new Font("Monospaced", Font.PLAIN, 10));
		graphics.setBackground(Color.white);
		graphics.setColor(Color.black);
		graphics.clearRect(0, 0, 11, dim);
		int i = 0;
		for(char c = label.charAt(i); i < label.length(); i++ ){
			c = label.charAt(i);
			graphics.drawString(""+c, 3, 11*(label.length()-i) - 1);
		}

		return img;
	}


	private static BufferedImage scale(BufferedImage image, int newWidth, int newHeight){
		double width = (double) image.getWidth();
		double height = (double) image.getHeight();
		double wScale = newWidth/width;
		double hScale = newHeight/height;
		BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D graphics = newImage.createGraphics();
		AffineTransform affineTransform = AffineTransform.getScaleInstance(wScale, hScale);
		graphics.drawRenderedImage(image, affineTransform);
		return newImage;
	}

	public static BufferedImage getChart(Map<List<Integer>,Double> Data, Color base, Color foreground){
		ContactDataset data = new ContactDataset(Data);
		BufferedImage image = HeatMapUtilities.createHeatMapImage(data, new ContactPaintScale(data.zMin,data.zMax,Arrays.asList(new Color[]{base, foreground})));
		return image;
	}
	
	public static BufferedImage getChartHeat(Map<List<Integer>,Double> Data, Color base, Color foreground){
		ContactDataset data = new ContactDataset(Data,true);
		BufferedImage image = HeatMapUtilities.createHeatMapImage(data, new ContactPaintScale(data.zMin,data.zMax,Arrays.asList(new Color[]{base, foreground})));
		return image;
	}

	public static BufferedImage getChart(Map<List<Integer>,Double> Data, Map<List<Integer>,Double> Data2, Color color1, Color color2){
		Map<List<Integer>,Double> temp = new HashMap<List<Integer>, Double>();

		for(List<Integer> key : Data2.keySet()){
			temp.put(key, 0.0);
		}

		for(Entry<List<Integer>, Double> e : Data.entrySet()){
			temp.put(e.getKey(), e.getValue());
		}

		ContactDataset data = new ContactDataset(temp);
	
		BufferedImage image = HeatMapUtilities.createHeatMapImage(data, new ContactPaintScale(data.zMin,data.zMax,Arrays.asList(new Color[]{Color.black, color1})));

		temp = new HashMap<List<Integer>, Double>();

		for(List<Integer> key : Data.keySet()){
			temp.put(key, 0.0);
		}

		for(Entry<List<Integer>, Double> e : Data2.entrySet()){
			temp.put(e.getKey(), e.getValue());
		}

		ContactDataset data2 = new ContactDataset(temp);
		
		BufferedImage image2 = HeatMapUtilities.createHeatMapImage(data2, new ContactPaintScale(data2.zMin,data2.zMax,Arrays.asList(new Color[]{Color.black, color2})));
		return merge(image,image2);
	}
	
	public static BufferedImage getChart(Map<List<Integer>,Double> Data, Map<List<Integer>,Double> Data2, Map<List<Integer>,Double> Data3,  Color color1, Color color2, Color color3){
		Map<List<Integer>,Double> temp = new HashMap<List<Integer>, Double>();

		for(List<Integer> key : Data2.keySet()){
			temp.put(key, 0.0);
		}
		
		for(List<Integer> key : Data3.keySet()){
			temp.put(key, 0.0);
		}

		for(Entry<List<Integer>, Double> e : Data.entrySet()){
			temp.put(e.getKey(), e.getValue());
		}

		ContactDataset data = new ContactDataset(temp);
	
		BufferedImage image = HeatMapUtilities.createHeatMapImage(data, new ContactPaintScale(data.zMin,data.zMax,Arrays.asList(new Color[]{Color.black, color1})));

		temp = new HashMap<List<Integer>, Double>();

		for(List<Integer> key : Data.keySet()){
			temp.put(key, 0.0);
		}
		
		for(List<Integer> key : Data3.keySet()){
			temp.put(key, 0.0);
		}

		for(Entry<List<Integer>, Double> e : Data2.entrySet()){
			temp.put(e.getKey(), e.getValue());
		}

		ContactDataset data2 = new ContactDataset(temp);
		
		BufferedImage image2 = HeatMapUtilities.createHeatMapImage(data2, new ContactPaintScale(data2.zMin,data2.zMax,Arrays.asList(new Color[]{Color.black, color2})));
		
		//Here
		temp = new HashMap<List<Integer>, Double>();

		for(List<Integer> key : Data.keySet()){
			temp.put(key, 0.0);
		}
		
		for(List<Integer> key : Data2.keySet()){
			temp.put(key, 0.0);
		}

		for(Entry<List<Integer>, Double> e : Data3.entrySet()){
			temp.put(e.getKey(), e.getValue());
		}

		ContactDataset data3 = new ContactDataset(temp);
		
		BufferedImage image3 = HeatMapUtilities.createHeatMapImage(data3, new ContactPaintScale(data3.zMin,data3.zMax,Arrays.asList(new Color[]{Color.black, color3})));
		
		return merge(merge(image,image2),image3);
	}
	

	
	public static BufferedImage getChart(Map<List<Integer>,Double> Data, Map<List<Integer>,Double> Data2, String sequence, Color color1, Color color2){
		BufferedImage image = getChart(Data, Data2, color1, color2);
		BufferedImage plot = axes(sequence);
		int dim = sequence.length()*11;
		Graphics2D g = plot.createGraphics();
		g.drawImage(scale(image, dim, dim), 11, 0, null);

		return plot;
	}
	
	public static BufferedImage getChart(Map<List<Integer>,Double> Data,Map<List<Integer>,Double> Data2, Map<List<Integer>,Double> Data3, String sequence, Color color1, Color color2,Color color3){
		BufferedImage image = getChart(Data, Data2, Data3,color1, color2, color3);
		BufferedImage plot = axes(sequence);
		int dim = sequence.length()*11;
		Graphics2D g = plot.createGraphics();
		g.drawImage(scale(image, dim, dim), 11, 0, null);

		return plot;
	}

	public static BufferedImage getChart(Map<List<Integer>,Double> Data, String sequence, Color base, Color foreground){
		BufferedImage image = getChart(Data, base, foreground);
		BufferedImage plot = axes(sequence);
		int dim = sequence.length()*11;
		Graphics2D g = plot.createGraphics();
		g.drawImage(scale(image, dim, dim), 11, 0, null);
		return plot;
	}
	
	public static BufferedImage getChartProb(Map<List<Integer>,Double> Data, Map<List<Integer>,Double> Data_Scale,String sequence, String sequence_aux, Color base, Color foreground){
		BufferedImage image = getChartHeat(Data, base, foreground);
		BufferedImage image_Scale = getChart(Data_Scale, base, foreground);
		BufferedImage plot = axesProb(sequence,sequence_aux);
		int dim = sequence.length()*11;
		Graphics2D g = plot.createGraphics();
		g.drawImage(scale(image, dim, dim), 6, 31, null);
		g.drawImage(scale(image_Scale, dim, 11), 6, 15, null);
		return plot;
	}

	public static BufferedImage getChartHeat(Map<List<Integer>,Double> Data_PARA, Map<List<Integer>,Double> Data_ANTI, Map<List<Integer>,Double> Data_NONE, Map<List<Integer>,Double> Data_Scale,String sequence, String scale_seq, Color base, Color foreground){
		BufferedImage image_PARA = getChartHeat(Data_PARA, base, foreground);
		BufferedImage image_ANTI = getChartHeat(Data_ANTI, base, foreground);
		BufferedImage image_NONE = getChartHeat(Data_NONE, base, foreground);
		BufferedImage image_Scale = getChart(Data_Scale, base, foreground);
		BufferedImage plot = axesHeat(sequence, "5", scale_seq);
		int dim = sequence.length()*11;
		Graphics2D g = plot.createGraphics();
		g.drawImage(scale(image_PARA, dim, dim), 11, 0, null);
		g.drawImage(scale(image_ANTI, dim, dim), dim+11, 0, null);
		g.drawImage(scale(image_NONE, dim, dim), (dim*2)+11, 0, null);
		g.drawImage(scale(image_Scale, dim*3, 11), 11, dim+30, null);
		return plot;
	}
	
	public static BufferedImage merge(BufferedImage one, BufferedImage two){
		BufferedImage temp = new BufferedImage(Math.max(one.getWidth(),two.getWidth()),Math.max(one.getHeight(),two.getHeight()),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) temp.getGraphics();
		g.setComposite(new Composite(){

			public CompositeContext createContext(final ColorModel srcColorModel,
					final ColorModel dstColorModel, RenderingHints hints) {

				return new CompositeContext(){
					int ALPHA = 0xFF000000; // alpha mask
					int MASK7Bit = 0xFEFEFF; // mask for additive/subtractive shading


					// the earlier mentioned algorithm
					int add(int color1, int color2) {
						int pixel = (color1 & MASK7Bit) + (color2 & MASK7Bit);
						int overflow = pixel & 0x1010100;
						overflow = overflow - (overflow >> 8);
						return ALPHA | overflow | pixel;
					}

					public void dispose() {
					}

					public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
						Rectangle srcRect = src.getBounds();
						Rectangle dstInRect = dstIn.getBounds();
						Rectangle dstOutRect = dstOut.getBounds();
						int x = 0, y = 0;
						int w = Math.min(Math.min(srcRect.width, dstOutRect.width), dstInRect.width);
						int h = Math.min(Math.min(srcRect.height, dstOutRect.height), dstInRect.height);
						Object srcPix = null, dstPix = null;
						for (y = 0; y < h; y++)
							for (x = 0; x < w; x++) {
								srcPix = src.getDataElements(x + srcRect.x, y + srcRect.y, srcPix);
								dstPix = dstIn.getDataElements(x + dstInRect.x, y + dstInRect.y, dstPix);
								int sp = srcColorModel.getRGB(srcPix);
								int dp = dstColorModel.getRGB(dstPix);
								int rp = add(sp,dp);
								dstOut.setDataElements(x + dstOutRect.x, y + dstOutRect.y, dstColorModel.getDataElements(rp, null));
							}
					}
				};
			}

		});
		g.drawImage(one, 0, temp.getHeight()-one.getHeight(), null);
		g.drawImage(two, 0, temp.getHeight()-two.getHeight(), null);

		return temp;
	}

	public static void showChart(Map<List<Integer>,Double> predicted, Color color, Map<List<Integer>,Double> pdb, String sequence, Color pdbColor, int width, int height){
		JFrame f = new JFrame();
		JPanel image = new ImagePanel(getChart(predicted, pdb, sequence, color, pdbColor));
		image.setPreferredSize(new Dimension(width,height));
		f.getContentPane().add(image);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	public static void saveChart(Map<List<Integer>,Double> predicted, Color color, Map<List<Integer>,Double> pdb, Color pdbColor, String sequence, Color base, int width, int height, String fileName) throws IOException{
		ImageIO.write(getChart(predicted, pdb, sequence, color, pdbColor),"PNG",new File(fileName));
	}
	
	public static void saveChart(Map<List<Integer>,Double> predicted, Color color, Map<List<Integer>,Double> predicted_helix, Color helixColor, Map<List<Integer>,Double> pdb, Color pdbColor ,String sequence, Color base, int width, int height, String fileName) throws IOException{
		ImageIO.write(getChart(predicted,predicted_helix, pdb, sequence, color, helixColor,pdbColor),"PNG",new File(fileName));
	}
	
	public static void saveHeat(BufferedImage img, String fileName)throws IOException{
		ImageIO.write(img, "PNG", new File(fileName));
	}
	
	public static void saveChart(Map<List<Integer>,Double> predicted, Color base, Color color, int width, int height, String fileName) throws IOException{
		ImageIO.write(scale(getChart(predicted, base, color),width,height),"PNG",new File(fileName));
	}

	public static void showChart(Map<List<Integer>, Double> contactMap, Color base,	Color color, int width, int height) {
		JFrame f = new JFrame();
		JPanel image = new ImagePanel(getChart(contactMap, base, color));
		image.setPreferredSize(new Dimension(width,height));
		f.getContentPane().add(image);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	public static void saveChart(Map<List<Integer>,Double> contactMap, String sequence, Color base, Color color, int width, int height, String fileName) throws IOException{
		ImageIO.write(scale(getChart(contactMap, sequence, base, color),width,height),"PNG",new File(fileName));
	}

	public static void showChart(Map<List<Integer>, Double> contactMap, String sequence, Color base, Color color, int width, int height) {
		JFrame f = new JFrame();
		JPanel image = new ImagePanel(getChart(contactMap, sequence, base, color));
		image.setPreferredSize(new Dimension(width,height));
		f.getContentPane().add(image);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
	
}	
