/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.dataarea.implementations.pherogram;


import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.SimpleSequenceInterval;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.pherogram.PherogramComponent;
import info.bioinfweb.libralign.pherogram.PherogramFormats;
import info.bioinfweb.libralign.pherogram.PherogramPainter;
import info.bioinfweb.libralign.pherogram.PherogramProvider;

import java.awt.RenderingHints;
import java.util.EnumSet;
import java.util.Set;



/**
 * A data area displaying a trace file resulting from Sanger sequencing.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class PherogramArea extends DataArea implements PherogramComponent {
	private PherogramProvider pherogram;
	private PherogramAlignmentModel alignmentModel = new PherogramAlignmentModel(this);
	private int firstSeqPos;
	private int leftCutPosition;
	private int rightCutPosition;
	private double verticalScale = 100f;
	private PherogramFormats formats = new PherogramFormats();
	private PherogramPainter painter = new PherogramPainter(this);
	
	
	public PherogramArea(AlignmentArea owner, PherogramProvider pherogram) {
		super(owner);
		this.pherogram = pherogram;
		leftCutPosition = 0;
		rightCutPosition = pherogram.getSequenceLength();
	}

	
	protected SimpleSequenceInterval calculatePaintRange(TICPaintEvent e) {
		PherogramAlignmentRelation upperBorderRelation = getAlignmentModel().baseCallIndexByEditableIndex((e.getRectangle().x + e.getRectangle().width) / 
				getOwner().getCompoundWidth() + 1);  // BioJava indices start with 1
		int upperBorder;
		if (upperBorderRelation.getCorresponding() == PherogramAlignmentRelation.GAP) {
			upperBorder = upperBorderRelation.getAfter();
		}
		else {  // OUT_OF_RANGE or valid index
			upperBorder = upperBorderRelation.getBefore();  // For a valid index, getBefore() is equal to getCorresponding() 
		}

		PherogramAlignmentRelation lowerBorderRelation = getAlignmentModel().baseCallIndexByEditableIndex(e.getRectangle().x / getOwner().getCompoundWidth() + 1); 
		int lowerBorder;
		if (lowerBorderRelation.getCorresponding() == PherogramAlignmentRelation.GAP) {
			lowerBorder = lowerBorderRelation.getBefore();
		}
		else {  // OUT_OF_RANGE or valid index
			lowerBorder = lowerBorderRelation.getAfter();  // For a valid index, getAfter() is equal to getCorresponding() 
		}

		return new SimpleSequenceInterval(Math.max(1, lowerBorder - 1),	upperBorder);  // One is subtracted to draw half visible positions.
	}
	

	@Override
	public void paint(TICPaintEvent e) {
		e.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		e.getGraphics().setColor(getFormats().getBackgroundColor());
		e.getGraphics().fillRect(e.getRectangle().x, e.getRectangle().y, e.getRectangle().width, e.getRectangle().height);

		SimpleSequenceInterval paintRange = calculatePaintRange(e);
		painter.paintScaledTraceCurves(paintRange.getFirstPos(), paintRange.getLastPos(), e.getGraphics(), 
				(paintRange.getFirstPos() - 0.5) * getOwner().getCompoundWidth(), 0);
	}


	@Override
	public Set<DataAreaListType> validLocations() {
		return EnumSet.of(DataAreaListType.SEQUENCE);
	}


	/**
	 * Returns the position in the sequence this pherogram is attached to where the output of the visible part
	 * of the pherogram starts. 
	 * 
	 * @return a valid index in the sequence carrying this data area 
	 */
	public int getFirstSeqPos() {
		return firstSeqPos;
	}


	/**
	 * Sets the index in the sequence this pherogram is attached to where the displaying of the visible part 
	 * of the pherogram starts. 
	 * 
	 * @param firstSeqPos - the new index
	 */
	public void setFirstSeqPos(int firstSeqPos) {
		this.firstSeqPos = firstSeqPos;
	}


	@Override
	public PherogramProvider getProvider() {
		return pherogram;
	}
  
  /**
   * Returns a model instance defining the alignment of this pherogram onto the associated sequence
   * in the alignment.
   * 
   * @return the model instance currently used
   */
  public PherogramAlignmentModel getAlignmentModel() {
  	return alignmentModel;
  }


	@Override
	public int getLeftCutPosition() {
		return leftCutPosition;
	}


	@Override
	public void setLeftCutPosition(int baseCallIndex) {
		leftCutPosition = baseCallIndex;
		repaint();
	}


	@Override
	public int getRightCutPosition() {
		return rightCutPosition;
	}


	@Override
	public void setRightCutPosition(int baseCallIndex) {
		rightCutPosition = baseCallIndex;
		repaint();
	}


	@Override
	public double getVerticalScale() {
		return verticalScale;
	}


	@Override
	public void setVerticalScale(double value) {
		this.verticalScale = value;
	}


	@Override
	public PherogramFormats getFormats() {
		return formats;
	}


	@Override
	public void setFormats(PherogramFormats formats) {
		this.formats = formats;
	}


	@Override
	public int getLengthBeforeStart() {
		return Math.max(0, getProvider().getSequenceLength() * getOwner().getCompoundWidth() - getLength());
	}


	@Override
	public int getLength() {
		return (getFirstSeqPos() + getProvider().getSequenceLength() - getLeftCutPosition()) * getOwner().getCompoundWidth();
	}


	@Override
	public int getHeight() {
		return (int)Math2.roundUp(painter.calculateTraceCurvesHeight());
	}
}
