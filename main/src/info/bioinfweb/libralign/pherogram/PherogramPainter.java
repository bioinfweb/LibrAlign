/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
import java.awt.geom.Rectangle2D;

import org.biojava3.core.sequence.compound.NucleotideCompound;

import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.pherogram.PherogramFormats.QualityOutputType;
import info.bioinfweb.libralign.pherogram.distortion.GapPattern;
import info.bioinfweb.libralign.pherogram.distortion.PherogramDistortion;
import info.bioinfweb.libralign.pherogram.provider.PherogramProvider;
import info.bioinfweb.libralign.pherogram.view.PherogramTraceCurveView;



/**
 * Utility class used by GUI components that display a pherogram (e.g. {@link PherogramTraceCurveView} and 
 * {@link PherogramArea}) that implements common painting routines.
 * 
 * @author Ben St&ouml;ver
 */
public class PherogramPainter {
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
	
	
	private double paintAnnotation(Graphics2D g, int annotation, double paintX, double paintY) {
		if (annotation >= 0) {
			paintBaseCallText(g, "" + annotation, paintX, paintY);
		}
		return paintY + g.getFont().getSize() * PherogramFormats.FONT_HEIGHT_FACTOR;
	}
	
	
	private void paintBaseCallData(Graphics2D g, int index, double paintX, double paintY) {
		PherogramFormats formats = owner.getFormats();
		NucleotideCompound nucleotide = owner.getProvider().getBaseCall(index);
		
		g.setFont(formats.getBaseCallFont());
		g.setColor(getNucleotideColor(nucleotide.getUpperedBase()));
		paintBaseCallText(g, nucleotide.getUpperedBase(), paintX, paintY);
		
		paintY += g.getFont().getSize() * PherogramFormats.FONT_HEIGHT_FACTOR;
		if (!formats.getQualityOutputType().equals(QualityOutputType.NONE)) {
			g.setFont(formats.getAnnotationFont());
			if (formats.getQualityOutputType().equals(QualityOutputType.MAXIMUM)) {
				// Color is already set correctly by the nucleotide output.
				paintY = paintAnnotation(g, owner.getProvider().getQuality(nucleotide, index), paintX, paintY);
			}
			else {  // QualityOutputType.ALL
				for (NucleotideCompound qualityNucleotide: PherogramProvider.TRACE_CURVE_NUCLEOTIDES) {
					g.setColor(getNucleotideColor(qualityNucleotide.getUpperedBase()));
					paintY = paintAnnotation(g, owner.getProvider().getQuality(qualityNucleotide, index), paintX, paintY);
				}
			}
		}
		if (formats.isShowProbabilityValues()) {
			g.setColor(formats.getProbabilityColor());
			g.setFont(formats.getAnnotationFont());
			
			for (String label: PherogramProvider.PROBABILITY_LABELS) {
				paintY = paintAnnotation(g, owner.getProvider().getAnnotation(label, index), paintX, paintY);
			}
		}
	}
	
	
	private void paintBaseCallText(Graphics2D g, String text, double paintX, double paintY) {
		g.drawString(text, (float)(paintX - 0.5 * g.getFontMetrics().stringWidth(text)), (float)(paintY + g.getFont().getSize()));
	}
	
	
	public void paintBaseCalls(Graphics2D g, int firstBaseCallIndex, int lastBaseCallIndex, double startX, double startY, 
			PherogramDistortion distortion) {
		
		for (int baseCallIndex = firstBaseCallIndex; baseCallIndex <= lastBaseCallIndex; baseCallIndex++) {
			paintBaseCallData(g, baseCallIndex, startX + distortion.getPaintCenterX(baseCallIndex), startY);
		}
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
	 * @param paintCenterX - the left most coordinate of the area where the trace curves will be painted
	 * @param paintY - the top most coordinate of the area where the trace curves will be painted
	 * @param horizontalScale - the factor by which the trace curves shall be scaled (zoomed) on the x-axis
	 */
	public void paintUnscaledBaseCalls(int startX, int endX, Graphics2D g, double paintStartX, double paintY, 
			double horizontalScale) {
		
		int index = 0;
		while ((index < owner.getProvider().getSequenceLength()) && 
				(owner.getProvider().getBaseCallPosition(index) < startX)) {
			
			index++;
		}
		
		if (index < owner.getProvider().getSequenceLength()) {
			if (index > 0) {
				index--;  // Also paint first possible partly visible character
			}
			
			while ((index < owner.getProvider().getSequenceLength()) && 
					(owner.getProvider().getBaseCallPosition(index) < endX)) {
				
    		paintBaseCallData(g, index, paintStartX + (owner.getProvider().getBaseCallPosition(index) - startX) * horizontalScale, 
    				paintY);
				index++;
			}

			if (index < owner.getProvider().getSequenceLength()) {
				paintBaseCallData(g, index, paintStartX + (owner.getProvider().getBaseCallPosition(index) - startX) * horizontalScale, 
						paintY);  // Also paint last possible partly visible character
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
		startX = Math.max(startX, 0);
		endX = Math.min(endX + 1, owner.getProvider().getTraceLength());
		
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
	
	
	public void paintUnscaledBaseCallLines(int startX, int endX, Graphics2D g, double paintX, double paintY, double height,
			double horizontalScale) {
		
		int index = 0;
		while ((index < owner.getProvider().getSequenceLength()) && 
				(owner.getProvider().getBaseCallPosition(index) < startX)) {
			
			index++;			
		}
		
		if (index < owner.getProvider().getSequenceLength()) {
			while ((index < owner.getProvider().getSequenceLength()) && 
					(owner.getProvider().getBaseCallPosition(index) <= endX)) {
				
	  		double x = paintX + (owner.getProvider().getBaseCallPosition(index) - startX) * horizontalScale;
	  		Path2D path = new  Path2D.Double();
	  		path.moveTo(x, paintY);
	  		path.lineTo(x, paintY + height);
				g.draw(path);
				
				index++;			
			}
    }		
	}
	
	
	public void paintBaseCallLines(Graphics2D g, int firstBaseCallIndex, int lastBaseCallIndex, double startX, double startY,
			double height, PherogramDistortion distortion) {
		
		int baseCallIndex = firstBaseCallIndex;
		if (baseCallIndex < owner.getProvider().getSequenceLength()) {
			while ((baseCallIndex < owner.getProvider().getSequenceLength()) && 
					(baseCallIndex <= lastBaseCallIndex)) {
				
	  		double x = startX + distortion.getPaintCenterX(baseCallIndex);
	  		Path2D path = new  Path2D.Double();
	  		path.moveTo(x, startY);
	  		path.lineTo(x, startY + height);
				g.draw(path);
				
				baseCallIndex++;			
			}
    }		
	}
	
	
	public void paintUnscaledBaseCallIndices(int startX, int endX, Graphics2D g, double paintX, double paintY, 
			double horizontalScale) {
		
		int index = 0;
		while ((index < owner.getProvider().getSequenceLength()) && 
				(owner.getProvider().getBaseCallPosition(index) < startX)) {
			
			index++;			
		}
		
		float leftMostLabelStart = 0;  // Make sure the first label is always painted
		if (index < owner.getProvider().getSequenceLength()) {
			index = Math.max(INDEX_LABEL_INTERVAL, index - index % INDEX_LABEL_INTERVAL - INDEX_LABEL_INTERVAL);
			
			while ((index < owner.getProvider().getSequenceLength()) && 
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
			
			if (index < owner.getProvider().getSequenceLength()) {  // Draw possibly partly visible label
				String label = "" + index;
				int labelWidth = g.getFontMetrics().stringWidth(label);
				float labelX = (float)(paintX + (owner.getProvider().getBaseCallPosition(index) - startX) * horizontalScale - 
						0.5 * labelWidth);
				if (labelX > leftMostLabelStart) {  // Draw label only if it does not overlap with its left neighbor.
					g.drawString(label, labelX,	(float)paintY + g.getFont().getSize());
				}
			}
    }
	}
	
	
	public void paintBaseCallIndices(Graphics2D g, int firstBaseCallIndex, int lastBaseCallIndex, double startX, double startY, 
			PherogramDistortion distortion, double compoundWidth) {
		
		int baseCallIndex = firstBaseCallIndex;
		float leftMostLabelStart = 0;  // Make sure the first label is always painted
		if (baseCallIndex < owner.getProvider().getSequenceLength()) {
			baseCallIndex = Math.max(INDEX_LABEL_INTERVAL, baseCallIndex - baseCallIndex % INDEX_LABEL_INTERVAL - INDEX_LABEL_INTERVAL);
			
			while ((baseCallIndex < owner.getProvider().getSequenceLength()) && 
					(baseCallIndex <= lastBaseCallIndex)) {
				
				String label = "" + baseCallIndex;
				int labelWidth = g.getFontMetrics().stringWidth(label);
				float labelX = (float)(startX + distortion.getPaintCenterX(baseCallIndex) - 0.5 * labelWidth);
				if (labelX > leftMostLabelStart) {  // Draw label only if it does not overlap with its left neighbor.
					g.drawString(label, labelX,	(float)startY + g.getFont().getSize());
				}
				
				leftMostLabelStart = labelX + labelWidth;
				baseCallIndex += INDEX_LABEL_INTERVAL;			
			}
    }
	}
	
	
	public double paintTraceCurves(Graphics2D g, int firstBaseCallIndex, int lastBaseCallIndex, double x, double y, 
			PherogramDistortion distortion, double compoundWidth) {
		
		final double height = calculateTraceCurvesHeight();
		for (NucleotideCompound nucleotide: PherogramProvider.TRACE_CURVE_NUCLEOTIDES) {
			int startTraceIndex = PherogramUtils.getFirstTracePosition(owner.getProvider(), firstBaseCallIndex);
			Path2D path = new Path2D.Double();
			path.moveTo(x + distortion.getPaintStartX(firstBaseCallIndex), 
					y + height - owner.getProvider().getTraceValue(nucleotide, startTraceIndex) * owner.getVerticalScale());
			for (int baseCallIndex = firstBaseCallIndex; baseCallIndex <= lastBaseCallIndex; baseCallIndex++) {
        // Create path for trace curve:
				int endTraceIndex = PherogramUtils.getFirstTracePosition(owner.getProvider(), baseCallIndex + 1);
				
				double paintX = x + distortion.getPaintStartX(baseCallIndex);
				double previousX = paintX - compoundWidth;
				int editablePos = 0;
				
				for (int traceX = startTraceIndex; traceX < endTraceIndex; traceX++) {
					if (paintX - previousX >= compoundWidth) {
						previousX += compoundWidth;
						while ((distortion.getGapPattern(baseCallIndex) != null) && 
								(distortion.getGapPattern(baseCallIndex).isGap(editablePos))) {
							
							paintX += compoundWidth;
							path.moveTo(paintX, y + height - owner.getProvider().getTraceValue(nucleotide, 
									Math.max(startTraceIndex, traceX - 1)) * owner.getVerticalScale());
							previousX += compoundWidth;
							editablePos++;
						}
						editablePos++;
					}
					paintX += distortion.getHorizontalScale(baseCallIndex);
					path.lineTo(paintX, y + height - 
							owner.getProvider().getTraceValue(nucleotide, traceX) * owner.getVerticalScale());  //TODO curveTo() could be used alternatively.
				}
				
				// Leave space for remaining gaps at the end:
				if (distortion.getGapPattern(baseCallIndex) != null) {
					paintX += compoundWidth * (distortion.getGapPattern(baseCallIndex).size() - editablePos); 
					path.moveTo(paintX, y + height - owner.getProvider().getTraceValue(nucleotide, 
								Math.max(startTraceIndex, endTraceIndex - 1)) * owner.getVerticalScale());
				}
				
				startTraceIndex = endTraceIndex;
			}

			// Paint trace curve path:
			g.setColor(owner.getFormats().getNucleotideColorSchema().getNucleotideColorMap().get(
					"" + nucleotide.toString().charAt(0)));
			g.draw(path);
		}
		
		return height;
	}
	
	
	public void paintGaps(Graphics2D g, int firstBaseCallIndex, int lastBaseCallIndex, double startX, double startY, double height, 
			PherogramDistortion distortion, double compoundWidth) {
		
		g.setColor(owner.getFormats().getNucleotideColorSchema().getNucleotideColorMap().get(
				"" + AlignmentAmbiguityNucleotideCompoundSet.GAP_CHARACTER));
		
		for (int baseCallIndex = firstBaseCallIndex; baseCallIndex <= lastBaseCallIndex; baseCallIndex++) {
			if (distortion.getGapPattern(baseCallIndex) != null) {
				GapPattern gapPattern = distortion.getGapPattern(baseCallIndex);
				double x = startX + distortion.getPaintStartX(baseCallIndex);
				for (int gapPos = 0; gapPos < gapPattern.size(); gapPos++) {
					if (gapPattern.isGap(gapPos)) {
						g.fill(new Rectangle2D.Double(x, startY, compoundWidth, height));
					}
					x += compoundWidth;
				}
			}
		}
	}
}
