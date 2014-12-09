package edu.uci.ics.jung.visualization;/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.imageio.ImageIO;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class VisualizationSaver {
	/*	@SuppressWarnings("unchecked")
	public static void save(VisualizationViewer viewer, String fileName) throws IOException{
		BufferedImage img = new BufferedImage(viewer.getSize().width, viewer.getSize().height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.createGraphics();
		viewer.paintComponent(g);	
		ImageIO.write(img, "png", new File(fileName));
	}*/
	
	public static void save(VisualizationViewer viewer, String fileName) throws IOException{
		// Get a DOMImplementation.
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D g = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.  
        viewer.paintComponent(g);	
    	
        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
     //   File toSave = new File(fileName);
        Writer out = new FileWriter(fileName);// OutputStreamWriter(System.out, "UTF-8");
        g.stream(out, useCSS);
	}
}