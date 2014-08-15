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
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.biojava3.core.sequence.compound.NucleotideCompound;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.ShiftChange;
import info.bioinfweb.libralign.pherogram.PherogramFormats.QualityOutputType;
import info.bioinfweb.libralign.pherogram.distortion.GapPattern;
import info.bioinfweb.libralign.pherogram.distortion.PherogramDistortion;



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
	
	
	private double paintAnnotation(Graphics2D g, int annotation, double paintX, double paintY) {
		if (annotation >= 0) {
			paintBaseCallText(g, "" + annotation, paintX, paintY);
		}
		return paintY + g.getFont().getSize() * FONT_HEIGHT_FACTOR;
	}
	
	
	private void paintBaseCallData(Graphics2D g, int index, double paintX, double paintY) {
		PherogramFormats formats = owner.getFormats();
		NucleotideCompound nucleotide = owner.getProvider().getBaseCall(index);
		
		g.setFont(formats.getBaseCallFont());
		g.setColor(getNucleotideColor(nucleotide.getUpperedBase()));
		paintBaseCallText(g, nucleotide.getUpperedBase(), paintX, paintY);
		
		paintY += g.getFont().getSize() * FONT_HEIGHT_FACTOR;
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
				
    		paintBaseCallData(g, index, paintStartX + (owner.getProvider().getBaseCallPosition(index) - startX) * horizontalScale, 
    				paintY);
				index++;
			}

			if (index <= owner.getProvider().getSequenceLength()) {
				paintBaseCallData(g, index, paintStartX + (owner.getProvider().getBaseCallPosition(index) - startX) * horizontalScale, 
						paintY + g.getFont().getSize());  // Also paint last possible partly visible character
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
	
	
	private GapPattern getGapPattern(PherogramArea pherogramArea, ShiftChange shiftChange) {
		GapPattern result = new GapPattern(shiftChange.getShiftChange() + 1);
		int firstEditableIndex = pherogramArea.getAlignmentModel().editableIndexByBaseCallIndex(
				shiftChange.getBaseCallIndex()).getCorresponding() - shiftChange.getShiftChange() - 1;
		for (int i = 0; i < result.size(); i++) {
			result.setGap(i, ((NucleotideCompound)pherogramArea.getOwner().getSequenceProvider().getTokenAt(
					pherogramArea.getList().getLocation().getSequenceID(), firstEditableIndex + i)).getBase().equals(
							"" + AlignmentAmbiguityNucleotideCompoundSet.GAP_CHARACTER));
		}
		return result;
	}
	
	
	private void paintTraceGap(Graphics2D g, PherogramArea pherogramArea, double x, double y, double height) {
		g.setColor(pherogramArea.getFormats().getNucleotideColorSchema().getNucleotideColorMap().get(
				"" + AlignmentAmbiguityNucleotideCompoundSet.GAP_CHARACTER));
		g.fill(new Rectangle2D.Double(x, y, pherogramArea.getOwner().getCompoundWidth(), height));
	}
	
	
	public double paintScaledTraceCurves(int startBaseCallIndex, int endBaseCallIndex, Graphics2D g, 
			double paintX, double paintY) throws IllegalStateException {
		
		if (!(owner instanceof PherogramArea)) {
			throw new IllegalStateException("This method can only be called if the owner is an instance of " + 
		      PherogramArea.class.getName());
		}
		PherogramArea pherogramArea = (PherogramArea)owner; 
		
		double height = calculateTraceCurvesHeight();
		NucleotideCompound lastNucleotide = PherogramProvider.TRACE_CURVE_NUCLEOTIDES.get(
				PherogramProvider.TRACE_CURVE_NUCLEOTIDES.size() - 1);
		for (NucleotideCompound nucleotide: PherogramProvider.TRACE_CURVE_NUCLEOTIDES) {
			Path2D path = new Path2D.Double();
			double x = paintX;
			int startTraceIndex = PherogramUtils.getFirstTracePosition(owner.getProvider(), startBaseCallIndex);
			path.moveTo(x, paintY + height - 
					owner.getProvider().getTraceValue(nucleotide, startTraceIndex) * owner.getVerticalScale());
			
			Iterator<ShiftChange> shiftChangeIterator = 
					pherogramArea.getAlignmentModel().shiftChangeIteratorByBaseCallIndex(startBaseCallIndex);
			ShiftChange shiftChange = null;
			if (shiftChangeIterator.hasNext()) {
				shiftChange = shiftChangeIterator.next();
			}
			
			int stepWidth = 1;
			int editPosPerBaseCallPos = 1;
			for (int baseCallIndex = startBaseCallIndex; baseCallIndex < endBaseCallIndex; baseCallIndex += stepWidth) {
				// Treat possible gaps:
				GapPattern gapPattern = null;
				if ((shiftChange != null) && (baseCallIndex + 1 == shiftChange.getBaseCallIndex())) {
					if (shiftChange.getShiftChange() < 0) {  // Deletion in editable sequence
						stepWidth = -shiftChange.getShiftChange() + 1;
						editPosPerBaseCallPos = 1;
					}
					else {  // Insertion in editable sequence
						stepWidth = 1;
						gapPattern = getGapPattern(pherogramArea, shiftChange);
						editPosPerBaseCallPos = shiftChange.getShiftChange() + 1 - gapPattern.getGapCount();
					}

					if (shiftChangeIterator.hasNext()) {
						shiftChange = shiftChangeIterator.next();
					}
					else {
						shiftChange = null;
					}
				}
				else {
					stepWidth = 1;
					editPosPerBaseCallPos = 1;
				}
				
				// Calculate scale and initialize variables:
				int endTraceIndex = PherogramUtils.getFirstTracePosition(owner.getProvider(), baseCallIndex + stepWidth);
				double horizontalScale = editPosPerBaseCallPos * pherogramArea.getOwner().getCompoundWidth() / 
						(double)(endTraceIndex - startTraceIndex);
				double previousX = x - pherogramArea.getOwner().getCompoundWidth();
				int editablePos = 0;

        // Create path for trace curve:
				for (int traceX = startTraceIndex; traceX < endTraceIndex; traceX++) {
					if (x - previousX >= pherogramArea.getOwner().getCompoundWidth()) {
						previousX += pherogramArea.getOwner().getCompoundWidth();
						if ((gapPattern != null) && (gapPattern.isGap(editablePos))) {
							paintTraceGap(g, pherogramArea, x, paintY, height);
							x += pherogramArea.getOwner().getCompoundWidth();
							path.moveTo(x, paintY + height - owner.getProvider().getTraceValue(nucleotide, 
									Math.max(startTraceIndex, traceX - 1)) * owner.getVerticalScale());
							previousX += pherogramArea.getOwner().getCompoundWidth();
							editablePos++;
						}
						editablePos++;
					}
					x += horizontalScale;
					path.lineTo(x, paintY + height - 
							owner.getProvider().getTraceValue(nucleotide, traceX) * owner.getVerticalScale());  //TODO curveTo() could be used alternatively.
				}
				
				// Leave space for remaining gaps at the end:
				if (gapPattern != null) {
					for (int i = editablePos + 1; i <= editPosPerBaseCallPos + gapPattern.getGapCount(); i++) { 
						if (nucleotide.equals(lastNucleotide)) {  // Make sure gap is painted only once and to for each trace curve.  
  						paintTraceGap(g, pherogramArea, x, paintY, height);
						}
						x += pherogramArea.getOwner().getCompoundWidth();
						path.moveTo(x, paintY + height - owner.getProvider().getTraceValue(nucleotide, 
								Math.max(startTraceIndex, endTraceIndex - 1)) * owner.getVerticalScale());
					}
				}
				
				if (nucleotide.equals(lastNucleotide)) {  // Make sure further information is painted only once and to for each trace curve.  
					//g.setColor(Color.BLUE);
					double baseCallPaintDistance = pherogramArea.getOwner().getCompoundWidth() * editPosPerBaseCallPos / stepWidth; 
					double baseCallPaintX = x - 0.5 * baseCallPaintDistance;
					for (int i = 0; i < stepWidth; i++) {
						paintBaseCallData(g, baseCallIndex + i, baseCallPaintX, paintY);
						//g.draw(new Line2D.Double(baseCallPaintX,	paintY, baseCallPaintX, paintY + height));
						baseCallPaintX -= baseCallPaintDistance;
					}
				}
				startTraceIndex = endTraceIndex;
			}

			// Paint trace curve path:
			g.setColor(pherogramArea.getFormats().getNucleotideColorSchema().getNucleotideColorMap().get(
					"" + nucleotide.toString().charAt(0)));
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
	
	
	public void paintTraceCurves(Graphics2D g, int firstBaseCallIndex, int lastBaseCallIndex, int x, int y, 
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
				
				double paintX = x + distortion.getPaintStartX(baseCallIndex); // - 
						//0.5 * (endTraceIndex - startTraceIndex /* + 1 ? */) * distortion.getHorizontalScale(baseCallIndex);
				double previousX = paintX - compoundWidth;
				int editablePos = 0;
				
				for (int traceX = startTraceIndex; traceX < endTraceIndex; traceX++) {
					if (paintX - previousX >= compoundWidth) {
						previousX += compoundWidth;
						if ((distortion.getGapPattern(baseCallIndex) != null) && (distortion.getGapPattern(baseCallIndex).isGap(editablePos))) {
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
	}
}
