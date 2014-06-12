/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.pherogram;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import info.bioinfweb.libralign.dataarea.implementations.PherogramArea;



/**
 * Utility class used by GUI components that display a pherogram (e.g. {@link PherogramTraceCurveView} and 
 * {@link PherogramArea}) that implements common painting routines.
 * 
 * @author Ben St&ouml;ver
 */
public class PherogramPainter {
	public static final int FONT_HEIGHT = 10;
	public static final int INDEX_LABEL_INTERVAL = 5;
	
	
	private PherogramComponent owner; 
	
	
	public PherogramPainter(PherogramComponent owner) {
		super();
		this.owner = owner;
	}

	
	private void paintNucleotide(int index, int traceStart, Graphics2D g, double paintX, double paintY, 
			double horizontalScale) {
		
		String nucleotide = owner.getProvider().getBaseCall(index).getUpperedBase();
		Color color = owner.getFormats().getNucleotideColorSchema().getNucleotideColorMap().get(nucleotide); 
		if (color == null) {
			color = owner.getFormats().getNucleotideColorSchema().getFontColor();
		}
		g.setColor(color);
		
		g.drawString(nucleotide, 
				(float)(paintX + (owner.getProvider().getBaseCallPosition(index) - traceStart) * horizontalScale - 
				0.5 * g.getFontMetrics(). charWidth(nucleotide.charAt(0))), 
				(float)paintY);
	}
	
	
	/**
	 * Paints the nucleotide characters of the base call sequence at the x-positions stored in the underlying
	 * pherogram model.
	 * <p>
	 * Note that no background will be painted by this method and the font currently set in the specified 
	 * graphics context will be used.
	 * 
	 * @param startX - the index in {@code provider} of the first trace value to be painted
	 * @param endX - the index in {@code provider} after the last value to be painted
	 * @param g - the graphics context to paint on
	 * @param paintX - the left most coordinate of the area where the trace curves will be painted
	 * @param paintY - the top most coordinate of the area where the trace curves will be painted
	 * @param horizontalScale - the factor by which the trace curves shall be scaled (zoomed) on the x-axis
	 */
	public void paintUnscaledBaseCalls(int startX, int endX, Graphics2D g, double paintX, double paintY, 
			double horizontalScale) {
		
		int index = 1;  // BioJava indices start with 1.
		while ((index <= owner.getProvider().getSequenceLength()) && 
				(owner.getProvider().getBaseCallPosition(index) < startX)) {
			
			index++;			
		}
		
		if (index <= owner.getProvider().getSequenceLength()) {
			if (index > 1) {
				index--;  // Also paint first possible partly visible character
			}
			
			while ((index <= owner.getProvider().getSequenceLength()) && 
					(owner.getProvider().getBaseCallPosition(index) < endX)) {
				
				paintNucleotide(index, startX, g, paintX, paintY + g.getFont().getSize(), horizontalScale);
				index++;			
			}

			if (index <= owner.getProvider().getSequenceLength()) {
				paintNucleotide(index, startX, g, paintX, paintY + g.getFont().getSize(), horizontalScale);;  // Also paint last possible partly visible character
			}
    }
	}
	
	
	/**
	 * Returns the height that is used to paint the trace curves. Since the trace data in {@link PherogramProvider}
	 * is normalized to 1.0, the returned value is equal to the vertical scale value of the owning pherogram component
	 * ({@link PherogramComponent#getVerticalScale()}).
	 * 
	 * @return the height in pixels
	 */
	public double calculateTraceCurvesHeight() {
		return owner.getVerticalScale();  // At least one curve must have 1.0 as the maximum height, because the model is normalized.
	}
	

	/**
	 * Paints parts the four trace curves stored in the specified provider with a constant scale on x.
	 * <p>
	 * Note that no background will be painted by this method.
	 * 
	 * @param startX - the index in {@code provider} of the first trace value to be painted
	 * @param endX - the index in {@code provider} after the last value to be painted
	 * @param g - the graphics context to paint on
	 * @param paintX - the left most coordinate of the area where the trace curves will be painted
	 * @param paintY - the top most coordinate of the area where the trace curves will be painted
	 * @param horizontalScale - the factor by which the trace curves shall be scaled (zoomed) on the x-axis
	 * @return the height the curves used to be painted
	 */
	public double paintUnscaledTraceCurves(int startX, int endX, Graphics2D g, double paintX, double paintY, 
			double horizontalScale) {
		
		double height = calculateTraceCurvesHeight();
		
		for (TraceCurve nucleotide: TraceCurve.values()) {
			Path2D path = new Path2D.Double();
			double x = paintX;
			path.moveTo(x, paintY + height - 
					owner.getProvider().getTraceValue(nucleotide, startX) * owner.getVerticalScale());
			for (int traceX = startX + 1; traceX < endX; traceX++) {
				x += horizontalScale;
				path.lineTo(x, paintY + height - 
						owner.getProvider().getTraceValue(nucleotide, traceX) * owner.getVerticalScale());  //TODO curveTo() could be used alternatively.
			}

			g.setColor(owner.getFormats().getNucleotideColorSchema().getNucleotideColorMap().get("" + nucleotide.toString().charAt(0)));
			g.draw(path);
		}
		
		return height;
	}
	
	
	public void paintBaseCallLines(int startX, int endX, Graphics2D g, double paintX, double paintY, double height,
			double horizontalScale) {
		
		int index = 1;  // BioJava indices start with 1.
		while ((index <= owner.getProvider().getSequenceLength()) && 
				(owner.getProvider().getBaseCallPosition(index) < startX)) {
			
			index++;			
		}
		
		if (index <= owner.getProvider().getSequenceLength()) {
			while ((index <= owner.getProvider().getSequenceLength()) && 
					(owner.getProvider().getBaseCallPosition(index) < endX)) {
				
	  		double x = paintX + (owner.getProvider().getBaseCallPosition(index) - startX) * horizontalScale;
	  		Path2D path = new  Path2D.Double();
	  		path.moveTo(x, paintY);
	  		path.lineTo(x, paintY + height);
				g.draw(path);
				
				index++;			
			}
    }		
	}
	
	
	public void paintBaseCallIndices(int startX, int endX, Graphics2D g, double paintX, double paintY, 
			double horizontalScale) {
		
		int index = 1;  // BioJava indices start with 1.
		while ((index <= owner.getProvider().getSequenceLength()) && 
				(owner.getProvider().getBaseCallPosition(index) < startX)) {
			
			index++;			
		}
		
		float leftMostLabelStart = 0;  // Make sure the first label is always painted
		if (index <= owner.getProvider().getSequenceLength()) {
			index = Math.max(INDEX_LABEL_INTERVAL, index - index % INDEX_LABEL_INTERVAL - INDEX_LABEL_INTERVAL);
			
			while ((index <= owner.getProvider().getSequenceLength()) && 
					(owner.getProvider().getBaseCallPosition(index) < endX)) {
				
				String label = "" + index;
				int labelWidth = g.getFontMetrics().stringWidth(label);
				float labelX = (float)(paintX + (owner.getProvider().getBaseCallPosition(index) - startX) * horizontalScale - 
						0.5 * labelWidth);
				if (labelX > leftMostLabelStart) {  // Draw label only if it does not overlap with its left neighbor.
					g.drawString(label, labelX,	(float)paintY + g.getFont().getSize());
				}
				
				leftMostLabelStart = labelX + labelWidth;
				index += INDEX_LABEL_INTERVAL;			
			}
    }
	}
}
