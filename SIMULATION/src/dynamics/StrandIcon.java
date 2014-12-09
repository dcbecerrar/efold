package dynamics;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import dynamics.Util;
import dynamics.Sample;
import dynamics.Constants.Dir;
import dynamics.StrandIcon;



import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dynamics.Util;

import dynamics.Sample;
import dynamics.Constants.Dir;

import edu.uci.ics.jung.visualization.LayeredIcon;

public class StrandIcon implements Icon{
	Sample s;
	double scale;
	private int nStrands;
	private int w;
	private int h;
	private double padding;
	private Color borderColor;
	
	public static Shape strand  = new Polygon(
			new int[]{2, 2, 0, 4, 8, 6,6,2},
			new int[]{0,14,14,20,14,14,0,0},
			8);
	
	/*
	 * start and end are in the range of 0 to 1 for the first and last
	 * residues of the sequence. This returns a gradient paint with the 
	 * appropriate colors 
	 */
	static Paint getColor(double start, double end, AffineTransform at){
		Point2D startPoint = at.transform(new Point2D.Double(0,0), null);
		Point2D endPoint = at.transform(new Point2D.Double(0,20), null);
		float scale = 5f/6;
		return new GradientPaint(startPoint, Color.getHSBColor(((float) start)*scale, 1f, 1f), endPoint, Color.getHSBColor(((float) end)*scale, 1f, 1f));
	}
	
	public StrandIcon(Sample s, double scale, Color color){
		this.s = s;
		this.scale = scale;
		this.padding = 0.5;
		this.nStrands = s.numStrands;
		this.w = (int) ((scale*(strand.getBounds().width + padding))*nStrands + (padding*(nStrands-1)));
		this.h = (int) (scale*(strand.getBounds().height+1));
		this.borderColor = color;
	
	}
	
	public static Image strandImage(double start, double end, Dir d, double scale){
		
		//	AffineTransform at = new AffineTransform(scale, 0, 0, scale, 0, 0);
		AffineTransform at = new AffineTransform();
		if(d == Dir.PARA)
			at.rotate(Math.PI, 4*scale, 10*scale);
		at.scale(scale, scale);
		Shape arrow = at.createTransformedShape(strand);
		System.out.println(arrow.getBounds2D().getMaxY());
		BufferedImage img = new BufferedImage(arrow.getBounds().width, arrow.getBounds().height+1, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setPaint(getColor(start,end,at));
		g2d.fill(arrow);
		g2d.setPaint(Color.black);
		//	g2d.setStroke(new BasicStroke(1.5f));
		g2d.draw(arrow);
		
		return img;
	}
	
	public BufferedImage sheetImage(Sample s, double scale, int algo){
		Image sheet = tImage(s, scale);
		
		int w = sheet.getWidth(null);
		int h = sheet.getHeight(null);
		int length = (int) Math.sqrt((w*w) + (h*h));
		BufferedImage circledImage = new BufferedImage(length, length, BufferedImage.TYPE_4BYTE_ABGR);
		int xOffset = (length-w)/2;
		int yOffset = (length-h)/2;
		Graphics2D g2d = (Graphics2D) circledImage.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(Color.white);
		g2d.fillOval(0, 0, length, length);
		g2d.setColor(borderColor);
//		g2d.fillOval((length/2)-2, (length/2)-2, 4, 4);
		g2d.drawOval(0, 0, length, length);
		g2d.drawImage(sheet, xOffset, yOffset, null);
		
		return circledImage;
	}
	
	public static BufferedImage tImage(Sample s, double scale){		
		double padding =  0.5;
		int nStrands = s.numStrands;
		int width = (int) (scale*(strand.getBounds().width + padding));
		int height = (int) (scale*(strand.getBounds().height+1));
		BufferedImage sheet = new BufferedImage((int) (width*nStrands + (padding*(nStrands-1))), height, BufferedImage.TYPE_4BYTE_ABGR);
		int xOffset = 0;
		Graphics2D g = (Graphics2D) sheet.getGraphics();
		Dir d = Dir.PARA;
		double len = s.length;
		List<Integer> template = s.template;
		List<Dir> pairing = s.pairing;
		Integer[][] structure = s.structure;
		for(int i = 0; i<template.size(); i++){
			double start = structure[i][0] / len;
			double end =   structure[i][1] / len;
			
			g.drawImage(strandImage(start, end, d, scale), xOffset, 0, null);
			
			xOffset += width;
			
			if(i < template.size() - 1 && pairing.get(i) == Dir.ANTI)
				d = d.opposite();
		}
		
		return sheet;
	}
	
	public void drawStrand(Graphics2D g, double start, double end, Dir d, double xOffset, double yOffset, Color borderColor){
		AffineTransform at = new AffineTransform();
		if(d == Dir.PARA)
			at.rotate(Math.PI, 4*scale, 10*scale);
		
		if(d==Dir.PARA)
			at.translate(-xOffset, -yOffset);
		else
			at.translate(xOffset, yOffset);
		
		at.scale(scale, scale);
			
		Shape arrow = at.createTransformedShape(strand);
		
		//g.setPaint(getColor(start,end,at));
		g.setPaint(borderColor);
		g.fill(arrow);
		
		g.setPaint(borderColor);
		((Graphics2D)g).setStroke(new BasicStroke(2.0f));
		g.draw(arrow);
		
		//g.drawString(label,(int)arrow.getBounds2D().getMinX(),(int)arrow.getBounds2D().getMinY());
	
	}
	
	public void drawSheet(Graphics g, int x, int y){		
		
		((Graphics2D) g).setStroke(new BasicStroke((float)(scale*0.5)));
		double xOffset = 0;
		String label="";
		Dir d = Dir.PARA;
		double len = s.length;
		List<Integer> template = s.template;
		List<Dir> pairing = s.pairing;
		Integer[][] structure = s.structure;
		for(int i = 0; i<template.size(); i++){
			double start = structure[i][0] / len;
			double end =  structure[i][1] / len;
			
			//			g.drawImage(strandImage(start, end, d, scale), xOffset, 0, null);

			drawStrand((Graphics2D) g, start, end, d, xOffset+x, y, borderColor);
			
			
			xOffset += scale*(strand.getBounds().width + padding);
			
			
			if(i < template.size() - 1 && pairing.get(i) == Dir.ANTI)
				d = d.opposite();
		}
		
	}
	
	public static void main(String[] args) {
		//JFrame f = new JFrame();
		//Sample s = new Sample(Util.list(0,1,2), Util.list(Dir.PARA,Dir.ANTI), new Integer[][]{{21,4},{3,3},{32,5}}, 40, null, 1, 1);
		//f.getContentPane().add(new JLabel( new StrandIcon(s, 3.0,Color.red)));
		//	f.setPreferredSize(new Dimension(200, 200));
		//f.pack();
		//f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//f.setVisible(true);
	}
	
	public int getIconHeight() {
		// TODO Auto-generated method stub
		return h;
	}
	
	
	public int getIconWidth() {
		// TODO Auto-generated method stub
		return w;
	}
	
	public void paintIcon(Component c, Graphics g, int x, int y) {
	//	Graphics2D g2d = (Graphics2D) g;
	//	g2d.setColor(borderColor);
	//	g2d.fillRect(x, y, w, h);
	//	g2d.setColor(borderColor);
//		g2d.fillOval((length/2)-2, (length/2)-2, 4, 4);
	//	g2d.drawOval(x, y, x+w, y+h);
		drawSheet(g, x, y);
	}
	
}

class aCanvas extends Canvas{
	public void paint(Graphics g){
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		AffineTransform at = new AffineTransform(5, 0, 0, 5, 10, 10);
		
		Shape a = at.createTransformedShape(StrandIcon.strand);
		Area area1 = new Area(a);
		g2d.setPaint(StrandIcon.getColor(.9,1,at));		
		g2d.fill(area1);
		
		Area area2 = new Area(StrandIcon.strand);
		
		g2d.setPaint(StrandIcon.getColor(0,.1,new AffineTransform()));		
		g2d.fill(area2);
		
		area1.add(area2);
		
		g2d.setPaint(Color.black);
		g2d.draw(area1);
		
	}
}