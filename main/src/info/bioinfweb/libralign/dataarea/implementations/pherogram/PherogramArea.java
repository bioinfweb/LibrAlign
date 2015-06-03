/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.dataarea.implementations.CustomHeightFullWidthArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.pherogram.PherogramComponent;
import info.bioinfweb.libralign.pherogram.PherogramFormats;
import info.bioinfweb.libralign.pherogram.PherogramPainter;
import info.bioinfweb.libralign.pherogram.distortion.ScaledPherogramDistortion;
import info.bioinfweb.libralign.pherogram.model.PherogramModel;
import info.bioinfweb.libralign.pherogram.view.PherogramView;

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
 * @see PherogramView
 */
public class PherogramArea extends CustomHeightFullWidthArea implements PherogramComponent {
	public static final int DEFAULT_HEIGHT_FACTOR = 5;
	
	
	private PherogramModel pherogram;
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
	public PherogramArea(AlignmentContentArea owner, PherogramModel pherogram) {
		super(owner, (int)Math.round(DEFAULT_HEIGHT_FACTOR * owner.getOwner().getPaintSettings().getTokenHeight()));  //TODO Always round up?
		this.pherogram = pherogram;
		verticalScale = getHeight();
		leftCutPosition = 0;
		rightCutPosition = pherogram.getSequenceLength();
	}
	
	
	protected SimpleSequenceInterval calculatePaintRange(TICPaintEvent e) {
		PherogramAlignmentRelation lowerBorderRelation = getAlignmentModel().baseCallIndexByEditableIndex(
				getOwner().columnByPaintX(e.getRectangle().x) - 2);  // - 2 because two (expiremetally obtained) half visible column should be painted. (Why are this two?) 
		int lowerBorder;
		if (lowerBorderRelation.getCorresponding() == PherogramAlignmentRelation.GAP) {
			lowerBorder = lowerBorderRelation.getBefore();
		}
		else {  // OUT_OF_RANGE or valid index
			lowerBorder = 0;
		}

		PherogramAlignmentRelation upperBorderRelation = getAlignmentModel().baseCallIndexByEditableIndex(
				getOwner().columnByPaintX(e.getRectangle().x + e.getRectangle().width) + 2);  // + 1 + 1 because BioJava indices start with 1 and one half visible column should be painted.
		int upperBorder;
		if (upperBorderRelation.getCorresponding() == PherogramAlignmentRelation.GAP) {
			upperBorder = upperBorderRelation.getAfter();
		}
		else {  // OUT_OF_RANGE or valid index
			upperBorder = getProvider().getSequenceLength() - 1; 
		}

		return new SimpleSequenceInterval(lowerBorder, upperBorder);
	}
	

	@Override
	public void paint(TICPaintEvent e) {
		Graphics2D g = e.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		double leftX = getOwner().paintXByColumn(getAlignmentModel().editableIndexByBaseCallIndex(getLeftCutPosition()).getAfter());
		double rightX = getOwner().paintXByColumn(getAlignmentModel().editableIndexByBaseCallIndex(getRightCutPosition()).getBefore() + 1);
		
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
		double x = getOwner().paintXByColumn(getFirstSeqPos() - getLeftCutPosition());
		double y = 0; 
		double height = getHeight();
		ScaledPherogramDistortion distortion = getAlignmentModel().createPherogramDistortion();
		
		// Paint gaps:
		if (getOwner().getOwner().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
			throw new InternalError("Support for concatenated models not yet implemented.");
		}
		double tokenWidth = getOwner().getOwner().getPaintSettings().getTokenWidth(0);  //TODO Use index of an aligned column to determine correct width also for concatenated models.
		
		painter.paintGaps(g, paintRange.getFirstPos(), paintRange.getLastPos(), x, y, height,	distortion, tokenWidth);
		
		// Paint base call lines
		if (getFormats().isShowBaseCallLines()) {
			g.setColor(getFormats().getBaseCallLineColor());
			painter.paintBaseCallLines(g, paintRange.getFirstPos(), paintRange.getLastPos(), x, y, 
					getHeight(),	distortion);
		}

    // Paint indices:
		g.setFont(getFormats().getIndexFont());
		g.setColor(Color.BLACK);
		painter.paintBaseCallIndices(g, paintRange.getFirstPos(), paintRange.getLastPos(), x, y, distortion, tokenWidth);
		y += getFormats().getIndexFont().getSize() * PherogramFormats.FONT_HEIGHT_FACTOR;
		
		// Paint base calls and probabilities:
		painter.paintBaseCalls(g, paintRange.getFirstPos(), paintRange.getLastPos(), x, y, distortion);
		//y += getFormats().getBaseCallFont().getSize() * PherogramFormats.FONT_HEIGHT_FACTOR + getFormats().qualityOutputHeight();
		
		// Draw curves:
		height = painter.paintTraceCurves(g, paintRange.getFirstPos(), paintRange.getLastPos(), 
				x, getHeight() - painter.calculateTraceCurvesHeight(),	distortion, tokenWidth);
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
	public PherogramModel getProvider() {
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
		return Math.max(0, getOwner().paintXByColumn(getAlignmentModel().baseCallIndexByEditableIndex(0).getAfter()));
	}


	@Override
	public int getLength() {
		return getOwner().paintXByColumn(getAlignmentModel().editableIndexByBaseCallIndex(getRightCutPosition() - 1).getAfter() + 1 + // space until the end of the aligned part
				(getProvider().getSequenceLength() - getRightCutPosition()))  // possible unaligned part at the right end
				- getOwner().getOwner().getDataAreas().getGlobalMaxLengthBeforeStart(); 
	}


//	@Override
//	public int getHeight() {
//		return (int)Math2.roundUp(painter.calculateTraceCurvesHeight() + getFormats().qualityOutputHeight() +
//				(getFormats().getIndexFont().getSize() + getFormats().getBaseCallFont().getSize()) * PherogramFormats.FONT_HEIGHT_FACTOR);
//	}


	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
		//TODO React if the associates sequence was removed? (AlignmentContentArea should probably better implement this behavior.)
	}


	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {}  // Nothing to do.


	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {
		if (e.getSource().equals(getOwner().getOwner().getAlignmentModel()) && e.getSequenceID() == getList().getLocation().getSequenceID()) {
			switch (e.getType()) {
				case INSERTION:
					int addend = getOwner().getOwner().getEditSettings().isInsertLeftInDataArea() ? -1 : 0;
					getAlignmentModel().addShiftChange(
							getAlignmentModel().baseCallIndexByEditableIndex(Math.max(0, e.getStartIndex() + addend)).getBefore(),  //TODO is getBefore immer sinnvoll? 
							e.getAffectedTokens().size());
					assignSize();
					break;
				case DELETION:
					getAlignmentModel().addShiftChange(getAlignmentModel().baseCallIndexByEditableIndex(e.getStartIndex()).getAfter()/*getBefore()*/,  //TODO is getBefore immer sinnvoll? 
							-e.getAffectedTokens().size());
					assignSize();
					break;
				case REPLACEMENT:  // Nothing to do (Replacements differing in length are not allowed.)
					break;  //TODO If a token is replaced by a gap a shift change would have to be added. (Solve this problem when gap displaying is generally implemented for all data areas.)
			}
		}
	}


	@Override
	public <T, U> void afterProviderChanged(AlignmentModel<T> previous,	AlignmentModel<U> current) {}  
	// This event is currently not passed to sequence attached areas.
}
