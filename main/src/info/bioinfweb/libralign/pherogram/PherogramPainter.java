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
import java.util.Iterator;

import org.biojava3.core.sequence.compound.NucleotideCompound;

import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramAlignmentModel;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.ShiftChange;
import info.bioinfweb.libralign.pherogram.PherogramFormats.QualityOutputType;



/**
 * Utility class used by GUI components that display a pherogram (e.g. {@link PherogramTraceCurveView} and 
 * {@link PherogramArea}) that implements common painting routines.
 * 
 * @author Ben St&ouml;ver
 */
public class PherogramPainter {
	public static final double FONT_HEIGHT_FACTOR = 1.2;
	public static final int INDEX_LABEL_INTERVAL = 5;
	
	
	private PherogramComponent owner; 
	
	
	public PherogramPainter(PherogramComponent owner) {
		super();
		this.owner = owner;
	}

	
	private Color getNucleotideColor(String nucleotide) {
		Color color = owner.getFormats().getNucleotideColorSchema().getNucleotideColorMap().get(nucleotide); 
		if (color == null) {
			color = owner.getFormats().getNucleotideColorSchema().getFontColor();
		}
		return color;
	}
	
	
	private double paintAnnotation(int annotation, int index, int traceStart, 
			Graphics2D g, double paintX, double paintY,	double horizontalScale) {
		
		if (annotation >= 0) {
			paintBaseCallText("" + annotation, index, traceStart, g, paintX, paintY, horizontalScale);
		}
		return paintY + g.getFont().getSize() * FONT_HEIGHT_FACTOR;
	}
	
	
	private void paintBaseCallData(int index, int traceStart, Graphics2D g, double paintX, double paintY, 
			double horizontalScale) {
		
		PherogramFormats formats = owner.getFormats();
		NucleotideCompound nucleotide = owner.getProvider().getBaseCall(index);
		
		g.setFont(formats.getBaseCallFont());
		g.setColor(getNucleotideColor(nucleotide.getUpperedBase()));
		paintBaseCallText(nucleotide.getUpperedBase(), index, traceStart, 
				g, paintX, paintY, horizontalScale);
		
		paintY += g.getFont().getSize() * FONT_HEIGHT_FACTOR;
		if (!formats.getQualityOutputType().equals(QualityOutputType.NONE)) {
			g.setFont(formats.getAnnotationFont());
			if (formats.getQualityOutputType().equals(QualityOutputType.MAXIMUM)) {
				// Color is already set correctly by the nucleotide output.
				paintY = paintAnnotation(owner.getProvider().getQuality(nucleotide, index), index, traceStart, 
						g, paintX, paintY, horizontalScale);
			}
			else {  // QualityOutputType.ALL
				for (NucleotideCompound qualityNucleotide: PherogramProvider.TRACE_CURVE_NUCLEOTIDES) {
					g.setColor(getNucleotideColor(qualityNucleotide.getUpperedBase()));
					paintY = paintAnnotation(owner.getProvider().getQuality(qualityNucleotide, index), index, traceStart, 
							g, paintX, paintY, horizontalScale);
				}
			}
		}
		if (formats.isShowProbabilityValues()) {
			g.setColor(formats.getProbabilityColor());
			g.setFont(formats.getAnnotationFont());
			
			for (String label: PherogramProvider.PROBABILITY_LABELS) {
				paintY = paintAnnotation(owner.getProvider().getAnnotation(label, index), index, traceStart, 
						g, paintX, paintY, horizontalScale);
			}
		}
	}
	
	
	private void paintBaseCallText(String text, int index, int traceStart, Graphics2D g, 
			double paintX, double paintY,	double horizontalScale) {
		
		g.drawString(text, (float)(paintX + (owner.getProvider().getBaseCallPosition(index) - traceStart) * horizontalScale - 
				0.5 * g.getFontMetrics().stringWidth(text)), (float)(paintY + g.getFont().getSize()));
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
				
				paintBaseCallData(index, startX, g, paintX, paintY, horizontalScale);
				index++;
			}

			if (index <= owner.getProvider().getSequenceLength()) {
				paintBaseCallData(index, startX, g, paintX, paintY + g.getFont().getSize(), horizontalScale);;  // Also paint last possible partly visible character
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
		
		for (NucleotideCompound nucleotide: PherogramProvider.TRACE_CURVE_NUCLEOTIDES) {
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
	
	
	private int getTracePosition(int baseCallIndex) {
		if (baseCallIndex == 1) {  // BioJava indices start with 1
			return 1;
		}
		else if (baseCallIndex >= owner.getProvider().getSequenceLength()) {
			return owner.getProvider().getTraceLength();
		}
		else {
			return (owner.getProvider().getBaseCallPosition(baseCallIndex) + 
					owner.getProvider().getBaseCallPosition(baseCallIndex + 1)) / 2; 
		}
	}
	
	
	public double paintScaledTraceCurves(int startBaseCallIndex, int endBaseCallIndex, Graphics2D g, 
			double paintX, double paintY) throws IllegalStateException {
		
		if (!(owner instanceof PherogramArea)) {
			throw new IllegalStateException("This method can only be called if the owner is an instance of " + 
		      PherogramArea.class.getName());
		}
		PherogramArea pherogramArea = (PherogramArea)owner; 
		
		double height = calculateTraceCurvesHeight();
		
		for (NucleotideCompound nucleotide: PherogramProvider.TRACE_CURVE_NUCLEOTIDES) {
			Path2D path = new Path2D.Double();
			double x = paintX;
			int startTraceIndex = getTracePosition(startBaseCallIndex);
			path.moveTo(x, paintY + height - 
					owner.getProvider().getTraceValue(nucleotide, startTraceIndex) * owner.getVerticalScale());
			
			Iterator<ShiftChange> shiftChangeIterator = pherogramArea.getAlignmentModel().shiftChangeIterator();
			ShiftChange shiftChange = null;
			if (shiftChangeIterator.hasNext()) {
				shiftChange = shiftChangeIterator.next();
			}
			
			int stepWidth = 1;
			int horizontalScaleFactor = 1;
			for (int baseCallIndex = startBaseCallIndex; baseCallIndex < endBaseCallIndex; baseCallIndex += stepWidth) {
				// Treat possible gaps:
				if ((shiftChange != null) && (baseCallIndex + 1 == shiftChange.getBaseCallIndex())) {
					if (shiftChange.getShiftChange() < 0) {  // Deletion in editable sequence
						stepWidth = -shiftChange.getShiftChange() + 1;
						horizontalScaleFactor = 1;
						if (shiftChangeIterator.hasNext()) {
							shiftChange = shiftChangeIterator.next();
						}
						else {
							shiftChange = null;
						}
					}
					else {  // Insertion in editable sequence
						stepWidth = 1;
						horizontalScaleFactor = shiftChange.getShiftChange() + 1;
					}
				}
				else {
					stepWidth = 1;
					horizontalScaleFactor = 1;
				}
				
				// Calculate scale:
				int endTraceIndex = getTracePosition(baseCallIndex + stepWidth);
				double horizontalScale = horizontalScaleFactor * pherogramArea.getOwner().getCompoundWidth() / (double)(endTraceIndex - startTraceIndex);

				for (int traceX = startTraceIndex; traceX < endTraceIndex; traceX++) {
					x += horizontalScale;
					path.lineTo(x, paintY + height - 
							owner.getProvider().getTraceValue(nucleotide, traceX) * owner.getVerticalScale());  //TODO curveTo() could be used alternatively.
				}
				startTraceIndex = endTraceIndex;
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
