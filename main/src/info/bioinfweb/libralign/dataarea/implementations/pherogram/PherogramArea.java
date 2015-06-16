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


import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.SimpleSequenceInterval;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.tokenset.TokenSet;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;



/**
 * A data area displaying a trace file resulting from Sanger sequencing.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 * @see PherogramView
 */
public class PherogramArea extends DataArea implements PherogramComponent {
	public static final int DEFAULT_HEIGHT_FACTOR = 5;
	
	
	private PherogramModel pherogramModel;
	private PherogramAlignmentModel pherogramAlignmentModel = new PherogramAlignmentModel(this);
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
		super(owner, owner.getOwner());  // Pherogram areas are always directly attached to their sequences. 
		this.pherogramModel = pherogram;
		verticalScale = getHeight();
		leftCutPosition = 0;
		rightCutPosition = pherogram.getSequenceLength();
	}
	
	
	protected SimpleSequenceInterval calculatePaintRange(TICPaintEvent e) {
		PherogramAlignmentRelation lowerBorderRelation = getPherogramAlignmentModel().baseCallIndexByEditableIndex(
				getLabeledAlignmentArea().getContentArea().columnByPaintX(e.getRectangle().x) - 2);  // - 2 because two (expiremetally obtained) half visible column should be painted. (Why are this two?) 
		int lowerBorder;
		if (lowerBorderRelation.getCorresponding() == PherogramAlignmentRelation.GAP) {
			lowerBorder = lowerBorderRelation.getBefore();
		}
		else {  // OUT_OF_RANGE or valid index
			lowerBorder = 0;
		}

		PherogramAlignmentRelation upperBorderRelation = getPherogramAlignmentModel().baseCallIndexByEditableIndex(
				getLabeledAlignmentArea().getContentArea().columnByPaintX(e.getRectangle().x + e.getRectangle().width) + 2);  // + 1 + 1 because BioJava indices start with 1 and one half visible column should be painted.
		int upperBorder;
		if (upperBorderRelation.getCorresponding() == PherogramAlignmentRelation.GAP) {
			upperBorder = upperBorderRelation.getAfter();
		}
		else {  // OUT_OF_RANGE or valid index
			upperBorder = getPherogramModel().getSequenceLength() - 1; 
		}

		return new SimpleSequenceInterval(lowerBorder, upperBorder);
	}
	

	@Override
	public void paint(TICPaintEvent e) {
		Graphics2D g = e.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		double leftX = getLabeledAlignmentArea().getContentArea().paintXByColumn(getPherogramAlignmentModel().editableIndexByBaseCallIndex(getLeftCutPosition()).getBefore()); // getAfter());
		double rightX = getLabeledAlignmentArea().getContentArea().paintXByColumn(getPherogramAlignmentModel().editableIndexByBaseCallIndex(getRightCutPosition() - 1).getAfter() + 1);
		
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
		double x = getLabeledAlignmentArea().getContentArea().paintXByColumn(getFirstSeqPos() - getLeftCutPosition());
		double y = 0; 
		double height = getHeight();
		ScaledPherogramDistortion distortion = getPherogramAlignmentModel().createPherogramDistortion();
		
		// Paint gaps:
		if (getLabeledAlignmentArea().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {
			throw new InternalError("Support for concatenated models not yet implemented.");
		}
		double tokenWidth = getLabeledAlignmentArea().getPaintSettings().getTokenWidth(0);  //TODO Use index of an aligned column to determine correct width also for concatenated models.
		
		painter.paintGaps(g, paintRange.getFirstPos(), paintRange.getLastPos(), x, y, height,	distortion, tokenWidth);
		
		// Paint base call lines
		if (getFormats().isShowBaseCallLines()) {
			g.setColor(getFormats().getBaseCallLineColor());
			painter.paintBaseCallLines(g, paintRange.getFirstPos(), paintRange.getLastPos(), x, y, 
					getHeight(), distortion);
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
		if (Math2.isBetween(firstSeqPos, 0,  //TODO Also check right cut position to match the sequence end?
				getLabeledAlignmentArea().getAlignmentModel().getSequenceLength(getList().getLocation().getSequenceID()) - 1)) {
			
			this.firstSeqPos = firstSeqPos;
			getList().getOwner().setLocalMaxLengthBeforeAfterStartRecalculate();
		}
		else {
			throw new IndexOutOfBoundsException(firstSeqPos + " is not a valid index to attach this pherogram to.");
		}
	}


	@Override
	public PherogramModel getPherogramModel() {
		return pherogramModel;
	}
  
	
  /**
   * Reverse complements the displayed pherogram, including its cut positions and distortions.
   * <p>
   * The editable sequence is not changed by this method and needs to reverse complemented independently.
   * The implementation of this needs to make sure to replace all tokens using 
   * {@link AlignmentModel#setTokenAt(int, int, Object)} or {@link AlignmentModel#setTokenAt(int, int, Object)}
   * and not use insertion and deletion methods for this. Otherwise new invalid distortions would be created
   * in the pherogram.
   * 
   * @see info.bioinfweb.libralign.pherogram.PherogramComponent#reverseComplement()
   */
  @Override
	public void reverseComplement() {
		pherogramModel = pherogramModel.reverseComplement();
		
		int oldLeftCutPosition = leftCutPosition;
		leftCutPosition = pherogramModel.getSequenceLength() - rightCutPosition;
		rightCutPosition = pherogramModel.getSequenceLength() - oldLeftCutPosition;
		
		getPherogramAlignmentModel().reverseComplement();
		
		getList().getOwner().setLocalMaxLengthBeforeAfterStartRecalculate();  // Could happen if cut lengths at the beginning and end differ.
		getOwner().getOwner().assignSizeToAll();
		repaint();  // Necessary in SWT, if no resize happened. 
	}


	/**
   * Returns a model instance defining the alignment of this pherogram onto the associated sequence
   * in the alignment.
   * 
   * @return the model instance currently used
   */
  public PherogramAlignmentModel getPherogramAlignmentModel() {
  	return pherogramAlignmentModel;
  }
  
  
  private void updateChangedCutPosition() {
		getPherogramAlignmentModel().deleteCutOffDistortions();
		getList().getOwner().setLocalMaxLengthBeforeAfterStartRecalculate();
		repaint();
  }
  
  
  @SuppressWarnings({"rawtypes", "unchecked"})
	public void copyBaseCallSequence(int startBaseCallIndex, int endBaseCallIndex) {
  	int sequenceID = getList().getLocation().getSequenceID();
  	AlignmentModel model = getLabeledAlignmentModel();
  	TokenSet tokenSet = model.getTokenSet();
  	for (int baseCallIndex = startBaseCallIndex; baseCallIndex < endBaseCallIndex; baseCallIndex++) {
  		String base = getPherogramModel().getBaseCall(baseCallIndex).getBase().toUpperCase();
			int editableIndex = getPherogramAlignmentModel().editableIndexByBaseCallIndex(baseCallIndex).getCorresponding();
  		if ((editableIndex >= 0) && (!base.equals(tokenSet.representationByToken(model.getTokenAt(sequenceID, editableIndex))))) {
  			model.setTokenAt(sequenceID, editableIndex, tokenSet.tokenByRepresentation(base));
  		}
		}
  }
  
  
  @SuppressWarnings("unchecked")
	private void setGaps(int startEditableIndex, int length) {
  	Collection<Object> tokens = new ArrayList<Object>(length);
  	for (int i = 0; i < length; i++) {
			tokens.add(getLabeledAlignmentModel().getTokenSet().getGapToken());
		}
  	((AlignmentModel<Object>)getLabeledAlignmentModel()).setTokensAt(getList().getLocation().getSequenceID(), 
  			startEditableIndex, tokens);
  }


	@Override
	public int getLeftCutPosition() {
		return leftCutPosition;
	}


	/**
	 * Defines a new left border where the pherogram is cut off. Pherogram distortions in cut off areas are
	 * automatically deleted by this method. The tokens at newly aligned positions in the editable sequence 
	 * are replaced by the tokens from the base call sequence, if a the cut position was moved left. 
	 * <p>
	 * If the  left cut position is changed, the first sequence position is moved accordingly, so that the 
	 * visible part of the pherogram still correctly aligns to the editable sequence. If the right cut 
	 * position would be located left of the left cut position after this operation, it will be moved to 
	 * {@code baseCallIndex} as well. 
	 * 
	 * @param baseCallIndex the new first visible index in the base call sequence
	 * @throws IndexOutOfBoundsException if {@code baseCallIndex} is not between 0 and the last base call index
	 */
	@Override
	public void setLeftCutPosition(int baseCallIndex) {
		if (Math2.isBetween(baseCallIndex, 0, getPherogramModel().getSequenceLength() - 1)) {
			int oldBaseCallIndex = leftCutPosition;
			int oldEditableIndex = getPherogramAlignmentModel().editableIndexByBaseCallIndex(leftCutPosition).getBefore();  // Needs to be stored before any distortions are deleted.
			int newEditableIndex = getPherogramAlignmentModel().editableIndexByBaseCallIndex(baseCallIndex).getBefore();  // Needs to be stored before any distortions are deleted.

			setFirstSeqPos(getFirstSeqPos() + baseCallIndex - getLeftCutPosition());
			leftCutPosition = baseCallIndex;
			if (getLeftCutPosition() > getRightCutPosition()) {
				setRightCutPosition(baseCallIndex);  // Calls updateChangedCutPosition().
			}
			else {
				updateChangedCutPosition();
			}
			
			if (baseCallIndex < oldBaseCallIndex) {
				copyBaseCallSequence(baseCallIndex, oldBaseCallIndex);  // Needs to be called after all changes are performed in order to calculate correct indices.
			}
			else if (oldEditableIndex < newEditableIndex) {
				setGaps(oldEditableIndex, newEditableIndex - oldEditableIndex);
			}
		}
		else {
			throw new IndexOutOfBoundsException("The base call index " + baseCallIndex + " is not contained in this pherogram.");
		}
	}
	
	
	/**
	 * Tries to set the left border where the base call sequence is cut off according to the left end of the current
	 * selection. If the left cut position is changed, the first sequence position is moved accordingly, so that the
	 * visible part of the pherogram still correctly aligns to the editable sequence.
	 * <p>
	 * If the right cut position would be located left of the left cut position after this operation, it will
	 * be moved to {@code baseCallIndex} as well. Note that this method does not test if the sequence, this area is 
	 * attached to, is contained in the selection. It just relies on the selected columns.
	 * 
	 * @return {@code true} if the right cut position was changed according to the selection (or the right cut position), 
	 *         {@code false} if that was not possible because the current right end of the selection lies outside of the 
	 *         pherogram
	 */
	public boolean setLeftCutPositionBySelection() {
		int pos = getPherogramAlignmentModel().baseCallIndexByEditableIndex(
				getOwner().getOwner().getSelection().getFirstColumn()).getBefore();
		boolean result = pos != PherogramAlignmentRelation.OUT_OF_RANGE; 
		if (result) {
			setLeftCutPosition(pos);
		}
		return result;
	}


	@Override
	public int getRightCutPosition() {
		return rightCutPosition;
	}


	/**
	 * Defines a new right border where the pherogram is cut off.Pherogram distortions in cut off areas are
	 * automatically deleted by this method. The tokens at newly aligned positions in the editable sequence 
	 * are replaced by the tokens from the base call sequence, if a the cut position was moved right.
	 * <p>
	 * If the left cut position would be located right of the right cut position after this operation, it will
	 * be moved to {@code baseCallIndex} as well. 
	 * 
	 * @param baseCallIndex the new first invisible index in the base call sequence
	 * @throws IndexOutOfBoundsException if {@code baseCallIndex} is not between 0 and the last base call index
	 */
	@Override
	public void setRightCutPosition(int baseCallIndex) {
		if (Math2.isBetween(baseCallIndex, 0, getPherogramModel().getSequenceLength() - 1)) {
			int oldValue = rightCutPosition;
			int oldEditableIndex = getPherogramAlignmentModel().editableIndexByBaseCallIndex(rightCutPosition).getBefore();  // Needs to be stored before any distortions are deleted.
			if (rightCutPosition == getPherogramModel().getSequenceLength()) {
				oldEditableIndex++;  // corresponding is OUT_OF_RANGE and "before" is one left.  
			}
			int newEditableIndex = getPherogramAlignmentModel().editableIndexByBaseCallIndex(baseCallIndex).getCorresponding();  // Needs to be stored before any distortions are deleted.
			
			rightCutPosition = baseCallIndex;
			if (getLeftCutPosition() > getRightCutPosition()) {
				setLeftCutPosition(baseCallIndex);  // Calls updateChangedCutPosition().
			}
			else {
				updateChangedCutPosition();
			}
			
			if (oldValue < baseCallIndex) {
				copyBaseCallSequence(oldValue, baseCallIndex);  // Needs to be called after all changes are performed in order to calculate correct indices.
			}
			else if (newEditableIndex < oldEditableIndex) {
				setGaps(newEditableIndex, oldEditableIndex - newEditableIndex);
			}
		}
		else {
			throw new IndexOutOfBoundsException("The base call index " + baseCallIndex + " is not contained in this pherogram.");
		}
	}
	
	
	/**
	 * Tries to set the right border where the base call sequence is cut off according to the right end of the current
	 * selection.
	 * <p>
	 * If the left cut position would be located right of the right cut position after this operation, it will
	 * be moved to {@code baseCallIndex} as well. Note that this method does not test if the sequence, this area is 
	 * attached to, is contained in the selection. It just relies on the selected columns.
	 * 
	 * @return {@code true} if the right cut position was changed according to the selection (or the right cut position), 
	 *         {@code false} if that was not possible because the current right end of the selection lies outside of the 
	 *         pherogram
	 */
	public boolean setRightCutPositionBySelection() {
		int pos = getPherogramAlignmentModel().baseCallIndexByEditableIndex(
				getOwner().getOwner().getSelection().getLastColumn()).getAfter();
		boolean result = pos != PherogramAlignmentRelation.OUT_OF_RANGE; 
		if (result) {
			setRightCutPosition(pos);
		}
		return result;
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
		return Math.max(0, getLabeledAlignmentArea().getContentArea().paintXByColumn(getPherogramAlignmentModel().baseCallIndexByEditableIndex(0).getAfter()));
	}


	@Override
	public int getLengthAfterEnd() {
		int lastEditableIndex = getPherogramAlignmentModel().editableIndexByBaseCallIndex(getRightCutPosition() - 1).getAfter();
		double lengthOfOutputAfterAlignmentStart = getLabeledAlignmentArea().getContentArea().paintXByColumn(lastEditableIndex) + 
				(1 + getPherogramModel().getSequenceLength() - getRightCutPosition()) *  
				getOwner().getOwner().getPaintSettings().getTokenWidth(Math.max(0, getFirstSeqPos())) - getLengthBeforeStart();  // Math.max(0, ...) is used because this method might be called during the execution of setter cut position method, when other properties are not yet adjusted.  
		return Math.max(0, (int)Math.round(lengthOfOutputAfterAlignmentStart - getOwner().getOwner().getLocalMaximumNeededAlignmentWidth()));
	}


	@Override
	public int getHeight() {
		return (int)Math.round(DEFAULT_HEIGHT_FACTOR * getLabeledAlignmentArea().getPaintSettings().getTokenHeight());
	}


	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
		//TODO React if the associated sequence was removed? (AlignmentContentArea should probably better implement this behavior.)
	}


	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {}  // Nothing to do.


	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {
		if (e.getSource().equals(getLabeledAlignmentArea().getAlignmentModel()) && 
				(e.getSequenceID() == getList().getLocation().getSequenceID())) {
			
			int addend = getLabeledAlignmentArea().getEditSettings().isInsertLeftInDataArea() ? -1 : 0;
			int lastSeqPos = getPherogramAlignmentModel().editableIndexByBaseCallIndex(getRightCutPosition() - 1).getAfter() 
					- addend;
			if (e.getStartIndex() <= lastSeqPos) {  // Do not process edits behind the pherogram.
				int tokensBefore = Math.min(e.getAffectedTokens().size(), Math.max(0, getFirstSeqPos() - e.getStartIndex() - addend));
				int tokensAfter = Math.max(0, e.getAffectedTokens().size() - Math.max(0, lastSeqPos - e.getStartIndex()) + addend);
				int tokensInside = e.getAffectedTokens().size() - tokensBefore - tokensAfter;
				
				switch (e.getType()) {
					case INSERTION:
						if (tokensBefore > 0) {
							setFirstSeqPos(getFirstSeqPos() + tokensBefore);
						}
						if (tokensInside > 0) {
							getPherogramAlignmentModel().addShiftChange(getPherogramAlignmentModel().baseCallIndexByEditableIndex(
									Math.max(0, e.getStartIndex() + tokensBefore + addend)).getBeforeValidIndex(), tokensInside);
						}
						break;
					case DELETION:
						if (tokensBefore > 0) {
							setFirstSeqPos(getFirstSeqPos() - tokensBefore);
						}
						if (tokensInside > 0) {
							getPherogramAlignmentModel().addShiftChange(getPherogramAlignmentModel().baseCallIndexByEditableIndex(
									e.getStartIndex() + tokensBefore).getAfterValidIndex(), -e.getAffectedTokens().size());
						}
						break;
					case REPLACEMENT:  // Nothing to do (Replacements differing in length are not allowed.)
						break;  //TODO If a token is replaced by a gap a shift change would have to be added. (Solve this problem when gap displaying is generally implemented for all data areas.)
				}
			}
		}
		else {
			repaint();  // The space before the alignment could have changed. (Only necessary in SWT. Swing seems to repaint automatically.)
		}
	}


	@Override
	public <T, U> void afterProviderChanged(AlignmentModel<T> previous,	AlignmentModel<U> current) {}  
	// This event is currently not passed to sequence attached areas.
}
