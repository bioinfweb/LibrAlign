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


import info.bioinfweb.commons.collections.SimpleSequenceInterval;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.AlignmentContentArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.dataarea.implementations.CustomHeightFullWidthArea;
import info.bioinfweb.libralign.pherogram.PherogramComponent;
import info.bioinfweb.libralign.pherogram.PherogramFormats;
import info.bioinfweb.libralign.pherogram.PherogramPainter;
import info.bioinfweb.libralign.pherogram.PherogramProvider;
import info.bioinfweb.libralign.pherogram.distortion.ScaledPherogramDistortion;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceChangeEvent;
import info.bioinfweb.libralign.sequenceprovider.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.sequenceprovider.events.TokenChangeEvent;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.Set;



/**
 * A data area displaying a trace file resulting from Sanger sequencing.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class PherogramArea extends CustomHeightFullWidthArea implements PherogramComponent {
	public static final int DEFAULT_HEIGHT_FACTOR = 5;
	
	
	private PherogramProvider pherogram;
	private PherogramAlignmentModel alignmentModel = new PherogramAlignmentModel(this);
	private int firstSeqPos;
	private int leftCutPosition;
	private int rightCutPosition;
	private double verticalScale;
	private PherogramFormats formats = new PherogramFormats();
	private PherogramPainter painter = new PherogramPainter(this);
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that will be containing the returned data area instance
	 * @param pherogram - the provider for the pherogram data to be displayed by the returned instance
	 */
	public PherogramArea(AlignmentContentArea owner, PherogramProvider pherogram) {
		super(owner, DEFAULT_HEIGHT_FACTOR * owner.getCompoundHeight());
		this.pherogram = pherogram;
		verticalScale = getHeight();
		leftCutPosition = 0;
		rightCutPosition = pherogram.getSequenceLength();
	}
	
	
	protected SimpleSequenceInterval calculatePaintRange(TICPaintEvent e) {
		PherogramAlignmentRelation lowerBorderRelation = getAlignmentModel().baseCallIndexByEditableIndex(
				getOwner().columnByPaintX(e.getRectangle().x) - 1);  // + 1 - 2 because BioJava indices start with 1 and two (experimetally obtained) half visible column should be painted. (Why are this two?) 
		int lowerBorder;
		if (lowerBorderRelation.getCorresponding() == PherogramAlignmentRelation.GAP) {
			lowerBorder = lowerBorderRelation.getBefore();
		}
		else {  // OUT_OF_RANGE or valid index
			lowerBorder = 1;  // BioJava indices start with 1.
		}

		PherogramAlignmentRelation upperBorderRelation = getAlignmentModel().baseCallIndexByEditableIndex(
				getOwner().columnByPaintX(e.getRectangle().x + e.getRectangle().width) + 2);  // + 1 + 1 because BioJava indices start with 1 and one half visible column should be painted.
		int upperBorder;
		if (upperBorderRelation.getCorresponding() == PherogramAlignmentRelation.GAP) {
			upperBorder = upperBorderRelation.getAfter();
		}
		else {  // OUT_OF_RANGE or valid index
			upperBorder = getProvider().getSequenceLength(); 
		}

		return new SimpleSequenceInterval(lowerBorder, upperBorder);
	}
	

	@Override
	public void paint(TICPaintEvent e) {
		Graphics2D g = e.getGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		double leftX = (getAlignmentModel().editableIndexByBaseCallIndex(getLeftCutPosition()).getAfter() - 1) * 
				getOwner().getCompoundWidth() + getOwner().getDataAreas().getMaxLengthBeforeStart();
		double rightX = getAlignmentModel().editableIndexByBaseCallIndex(getRightCutPosition()).getBefore() * 
				getOwner().getCompoundWidth() + getOwner().getDataAreas().getMaxLengthBeforeStart();
		
		// Draw cut off background:
		g.setColor(getFormats().getCutBackgroundColor());
		if (leftX >= e.getRectangle().x) {
			g.fill(new Rectangle2D.Double(e.getRectangle().x, e.getRectangle().y, 
					leftX - e.getRectangle().x, e.getRectangle().height));
		}
		else {
			leftX = e.getRectangle().x;
		}
		if (rightX <= e.getRectangle().x + e.getRectangle().width) {
			g.fill(new Rectangle2D.Double(rightX, e.getRectangle().y, 
					e.getRectangle().x + e.getRectangle().width - rightX, e.getRectangle().height));
		}
		else {
			rightX = e.getRectangle().x + e.getRectangle().width;
		}
		
		// Draw center background:
		g.setColor(getFormats().getBackgroundColor());
		g.fill(new Rectangle2D.Double(leftX, e.getRectangle().y, rightX - leftX, e.getRectangle().height));

		SimpleSequenceInterval paintRange = calculatePaintRange(e);
		double x = (getFirstSeqPos() - getLeftCutPosition()) * getOwner().getCompoundWidth() + 
				getOwner().getDataAreas().getMaxLengthBeforeStart();
		double y = 0; 
		double height = getHeight();
		ScaledPherogramDistortion distortion = getAlignmentModel().createPherogramDistortion();
		
		// Paint gaps:
		painter.paintGaps(g, paintRange.getFirstPos(), paintRange.getLastPos(), x, y, height,	distortion, 
				getOwner().getCompoundWidth());
		
		// Paint base call lines
		if (getFormats().isShowBaseCallLines()) {
			g.setColor(getFormats().getBaseCallLineColor());
			painter.paintBaseCallLines(g, paintRange.getFirstPos(), paintRange.getLastPos(), x, y, 
					getHeight(),	distortion);
		}

    // Paint indices:
		g.setFont(getFormats().getIndexFont());
		g.setColor(Color.BLACK);
		painter.paintBaseCallIndices(g, paintRange.getFirstPos(), paintRange.getLastPos(), x, y, 
				distortion, getOwner().getCompoundWidth());
		y += getFormats().getIndexFont().getSize() * PherogramFormats.FONT_HEIGHT_FACTOR;
		
		// Paint base calls and probabilities:
		painter.paintBaseCalls(g, paintRange.getFirstPos(), paintRange.getLastPos(), x, y, distortion);
		//y += getFormats().getBaseCallFont().getSize() * PherogramFormats.FONT_HEIGHT_FACTOR + getFormats().qualityOutputHeight();
		
		// Draw curves:
		height = painter.paintTraceCurves(g, paintRange.getFirstPos(), paintRange.getLastPos(), 
				x, getHeight() - painter.calculateTraceCurvesHeight(),	distortion, getOwner().getCompoundWidth());
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


//	@Override
//	public int getHeight() {
//		return (int)Math2.roundUp(painter.calculateTraceCurvesHeight() + getFormats().qualityOutputHeight() +
//				(getFormats().getIndexFont().getSize() + getFormats().getBaseCallFont().getSize()) * PherogramFormats.FONT_HEIGHT_FACTOR);
//	}


	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
		// TODO React if the associates sequence was removed? (AlignmentContentArea should probably better implement this behavior.)
	}


	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {}  // Nothing to do.


	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {
		if (e.getSequenceID() == getList().getLocation().getSequenceID()) {
			switch (e.getType()) {
				case INSERTION:
					getAlignmentModel().addShiftChange(getAlignmentModel().baseCallIndexByEditableIndex(e.getStartIndex()).getBefore(),  //TODO is getBefore immer sinnvoll? 
							e.getAffectedTokens().size());
					break;
				case DELETION:
					System.out.println("Deletion: " + e.getStartIndex() + " " + getAlignmentModel().baseCallIndexByEditableIndex(e.getStartIndex()).getBefore());
					getAlignmentModel().addShiftChange(getAlignmentModel().baseCallIndexByEditableIndex(e.getStartIndex()).getBefore(),  //TODO is getBefore immer sinnvoll? 
							-e.getAffectedTokens().size());
					break;
				case REPLACEMENT:  // Nothing to do
					break;
			}
		}
	}


	@Override
	public <T, U> void afterProviderChanged(SequenceDataProvider<T> previous,	SequenceDataProvider<U> current) {}  
	// This event is currently not passed to sequence attached areas.
}
