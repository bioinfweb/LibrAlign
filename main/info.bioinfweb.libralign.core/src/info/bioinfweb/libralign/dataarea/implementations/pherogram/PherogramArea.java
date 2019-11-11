/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.dataarea.implementations.pherogram;


import info.bioinfweb.commons.collections.SimpleSequenceInterval;
import info.bioinfweb.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentPaintEvent;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainter;
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
import info.bioinfweb.libralign.pherogram.PherogramUtils;
import info.bioinfweb.libralign.pherogram.distortion.GapPattern;
import info.bioinfweb.libralign.pherogram.distortion.ScaledPherogramDistortion;
import info.bioinfweb.libralign.pherogram.model.PherogramAlignmentRelation;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;
import info.bioinfweb.libralign.pherogram.model.PherogramModelListener;
import info.bioinfweb.libralign.pherogram.model.PherogramCutPositionChangeEvent;
import info.bioinfweb.libralign.pherogram.model.PherogramFirstSeqPosChangeEvent;
import info.bioinfweb.libralign.pherogram.model.PherogramProviderChangeEvent;
import info.bioinfweb.libralign.pherogram.model.PherogramShiftChangeUpdateEvent;
import info.bioinfweb.libralign.pherogram.model.ShiftChange;
import info.bioinfweb.libralign.pherogram.view.PherogramTraceCurveView;
import info.bioinfweb.libralign.pherogram.view.PherogramView;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
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
	
	
	private PherogramAreaModel model;
	private boolean updateEditableSequence = true;
	private double verticalScale;
	private PherogramFormats formats;
	private PherogramPainter painter = new PherogramPainter(this);

	
	private final PherogramModelListener MODEL_LISTENER = new PherogramModelListener() {
		@Override
		public void pherogramProviderChange(PherogramProviderChangeEvent event) {
			getLabeledAlignmentArea().getSizeManager().setLocalMaxLengthBeforeAfterRecalculate();  // Could happen if cut lengths at the beginning and end differ.
			getOwner().getOwner().assignSizeToAll();
			if (!event.isMoreEventsUpcoming()) {
				repaint();  // Necessary in SWT, if no resize happened. 
			}
		}

		
		@Override
		public void leftCutPositionChange(PherogramCutPositionChangeEvent event) {
			if (!event.isMoreEventsUpcoming()) {
				updateChangedPosition();
			}
			
			//TODO Changes to the alignment model should not be done here in the view. The two models should communicate directly.
			if (isUpdateEditableSequence() && !getOwner().getOwner().getAlignmentModel().isTokensReadOnly()) {
				if (event.getNewBaseCallIndex() < event.getOldBaseCallIndex()) {
					copyBaseCallSequence(event.getNewBaseCallIndex(), event.getOldBaseCallIndex());  // Needs to be called after all changes are performed in order to calculate correct indices.
				}
				else {
					int oldEditableIndex = event.getOldEditableIndex().getBefore();
					int newEditableIndex = event.getNewEditableIndex().getBefore();
					if (oldEditableIndex < newEditableIndex) {
						setGaps(oldEditableIndex, newEditableIndex - oldEditableIndex);
					}
				}
			}
		}

		
		@Override
		public void rightCutPositionChange(PherogramCutPositionChangeEvent event) {
			if (!event.isMoreEventsUpcoming()) {
				updateChangedPosition();
			}
			
			//TODO Changes to the alignment model should not be done here in the view. The two models should communicate directly.
			if (isUpdateEditableSequence() && !getOwner().getOwner().getAlignmentModel().isTokensReadOnly()) {
				if (event.getOldBaseCallIndex() < event.getNewBaseCallIndex()) {
					copyBaseCallSequence(event.getOldBaseCallIndex(), event.getNewBaseCallIndex());  // Needs to be called after all changes are performed in order to calculate correct indices.
				}
				else {
					int oldEditableIndex = event.getOldEditableIndex().getBefore();
					if (event.getOldBaseCallIndex() == getModel().getPherogramProvider().getSequenceLength()) {
						oldEditableIndex++;  // corresponding is OUT_OF_RANGE and "before" is one left.  
					}
					int newEditableIndex = event.getNewEditableIndex().getCorresponding();
					if (newEditableIndex < oldEditableIndex) {
						setGaps(newEditableIndex, oldEditableIndex - newEditableIndex);
					}
				}
			}
		}


		@Override
		public void firstSequencePositionChange(PherogramFirstSeqPosChangeEvent event) {
			if (!event.isMoreEventsUpcoming()) {
				updateChangedPosition();
			}
		}


		@Override
		public void shiftChangeEdited(PherogramShiftChangeUpdateEvent event) {
			if (!event.isMoreEventsUpcoming()) {
				repaint();
			}
		}
	};
	

	private final PropertyChangeListener FORMATS_LISTENER = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			getOwner().getOwner().assignSizeToAll();
			repaint();  // Necessary for SWT if there was no size change.
		}
	}; 
	
	
	/**
	 * Creates a new instance of this class with its own {@link PherogramFormats} instance.
	 * 
	 * @param owner the alignment area that will be containing the returned data area instance
	 * @param model the provider for the pherogram data to be displayed by the returned instance (Note that each
	 *        instance of this class needs a separate model instance. {@code model} can anyway be shared with instances
	 *        of {@link PherogramTraceCurveView}.)
	 * @throws IllegalArgumentException if {@code model} is already owned by another pherogram area 
	 */
	public PherogramArea(AlignmentContentArea owner, PherogramAreaModel model) {
		this(owner, model, new PherogramFormats());
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner the alignment area that will be containing the returned data area instance
	 * @param model the provider for the pherogram data to be displayed by the returned instance (Note that each
	 *        instance of this class needs a separate model instance. {@code model} can anyway be shared with instances
	 *        of {@link PherogramTraceCurveView}.)
	 * @param formats the formats object to be used with this area
	 * @throws IllegalArgumentException if {@code model} is already owned by another pherogram area 
	 */
	public PherogramArea(AlignmentContentArea owner, PherogramAreaModel model, PherogramFormats formats) {
		super(owner, owner.getOwner());  // Pherogram areas are always directly attached to their sequences.
		this.model = model;
		model.addListener(MODEL_LISTENER);
		this.formats = formats;
		formats.addPropertyChangeListener(FORMATS_LISTENER);
		verticalScale = getHeight();
	}
	
	
	protected SimpleSequenceInterval calculatePaintRange(TICPaintEvent e) {
		PherogramAlignmentRelation lowerBorderRelation = getModel().baseCallIndexByEditableIndex(
				getLabeledAlignmentArea().getContentArea().columnByPaintX(e.getRectangle().getMinX()) - 2);  // - 2 because two (experimentally obtained) half visible column should be painted. (Why are this two?)
		int lowerBorder;
		if (lowerBorderRelation.getCorresponding() == PherogramAlignmentRelation.GAP) {
			lowerBorder = lowerBorderRelation.getBefore();
		}
		else {  // OUT_OF_RANGE or valid index
			lowerBorder = 0;
		}

		PherogramAlignmentRelation upperBorderRelation = getModel().baseCallIndexByEditableIndex(
				getLabeledAlignmentArea().getContentArea().columnByPaintX(e.getRectangle().getMinX() + e.getRectangle().getWidth()) + 2);  // + 1 + 1 because BioJava indices start with 1 and one half visible column should be painted.
		int upperBorder;
		if (upperBorderRelation.getCorresponding() == PherogramAlignmentRelation.GAP) {
			upperBorder = upperBorderRelation.getAfter();
		}
		else {  // OUT_OF_RANGE or valid index
			upperBorder = getModel().getPherogramProvider().getSequenceLength() - 1; 
		}

		return new SimpleSequenceInterval(lowerBorder, upperBorder);
	}
	

	public ScaledPherogramDistortion createPherogramDistortion() {
		ScaledPherogramDistortion result = new ScaledPherogramDistortion(getModel().getPherogramProvider().getSequenceLength());
  	
		int startTraceIndex = 0;  //getTracePosition(startBaseCallIndex);
		Iterator<ShiftChange> shiftChangeIterator = getModel().shiftChangeIterator();
		ShiftChange shiftChange = null;
		if (shiftChangeIterator.hasNext()) {
			shiftChange = shiftChangeIterator.next();
		}
		
		if (getModel().getAlignmentModel() instanceof ConcatenatedAlignmentModel) {  //TODO This reference could also me made using getOwner().getOwner().getAlignmentModel(). Would this be better?
			throw new InternalError("Support for concatenated models not yet implemented.");
		}
		final double compoundWidth = getEditableTokenWidth();
		int stepWidth = 1;
		int editPosPerBaseCallPos = 1;
		double baseCallPaintX = 0; //0.5 * compoundWidth;
		for (int baseCallIndex = 0; baseCallIndex < getModel().getPherogramProvider().getSequenceLength(); baseCallIndex += stepWidth) {
			// Treat possible gaps:
			if ((shiftChange != null) && (baseCallIndex == shiftChange.getBaseCallIndex())) {
				if (shiftChange.getShiftChange() < 0) {  // Deletion in editable sequence
					stepWidth = -shiftChange.getShiftChange() + 1;
					editPosPerBaseCallPos = 1;
				}
				else {  // Insertion in editable sequence
					stepWidth = 1;
					GapPattern gapPattern = getModel().getGapPattern(shiftChange);
					editPosPerBaseCallPos = shiftChange.getShiftChange() + 1 - gapPattern.getGapCount();
					result.setGapPattern(baseCallIndex, gapPattern);
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
			int endTraceIndex = PherogramUtils.getFirstTracePosition(getModel().getPherogramProvider(), baseCallIndex + stepWidth);
			result.setHorizontalScale(baseCallIndex, editPosPerBaseCallPos * compoundWidth / (double)(endTraceIndex - startTraceIndex));

			// Calculate paint positions:
			double baseCallPaintDistance = compoundWidth * editPosPerBaseCallPos / stepWidth;
			result.setPaintStartX(baseCallIndex, baseCallPaintX);
			baseCallPaintX += 0.5 * baseCallPaintDistance;
			if (result.getGapPattern(baseCallIndex) == null) {
				result.setPaintCenterX(baseCallIndex, baseCallPaintX);
				for (int i = 1; i < stepWidth; i++) {
					result.setHorizontalScale(baseCallIndex + i, result.getHorizontalScale(baseCallIndex));  // Scale remains constant.
					baseCallPaintX += 0.5 * baseCallPaintDistance;
					result.setPaintStartX(baseCallIndex + i, baseCallPaintX);
					baseCallPaintX += 0.5 * baseCallPaintDistance;
					result.setPaintCenterX(baseCallIndex + i, baseCallPaintX);
					// GapPattern does not need to be set, because it must be null in this case.
				}
			}
			else {	// Treat gaps (in this case stepWidth should always be 1):
				GapPattern gapPattern = result.getGapPattern(baseCallIndex);
				result.setPaintCenterX(baseCallIndex, baseCallPaintX + compoundWidth * gapPattern.countGapsBeforeCurveCenter());
				baseCallPaintX += compoundWidth * gapPattern.getGapCount();
			}
			baseCallPaintX += 0.5 * baseCallPaintDistance;
			startTraceIndex = endTraceIndex;
		}
  	
  	return result;
  }	

	
	@Override
	public void paintPart(AlignmentPaintEvent e) {
		Graphics2D g = e.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		double leftX = getLabeledAlignmentArea().getContentArea().paintXByColumn(
				getModel().editableIndexByBaseCallIndex(getModel().getLeftCutPosition()).getBefore()); // getAfter());
		double rightX = getLabeledAlignmentArea().getContentArea().paintXByColumn(
				getModel().editableIndexByBaseCallIndex(getModel().getRightCutPosition() - 1).getAfter() + 1);
		
		// Draw cut off background:
		g.setColor(getFormats().getCutBackgroundColor());
		if (leftX >= e.getRectangle().getMinX()) {
			g.fill(new Rectangle2D.Double(e.getRectangle().getMinX(), e.getRectangle().getMinY(), 
					leftX - e.getRectangle().getMinX(), e.getRectangle().getHeight()));
		}
		else {
			leftX = e.getRectangle().getMinX();
		}
		if (rightX <= e.getRectangle().getMinX() + e.getRectangle().getWidth()) {
			g.fill(new Rectangle2D.Double(rightX, e.getRectangle().getMinY(), 
					e.getRectangle().getMinX() + e.getRectangle().getWidth() - rightX, e.getRectangle().getHeight()));
		}
		else {
			rightX = e.getRectangle().getMinX() + e.getRectangle().getWidth();
		}
		
		// Draw center background:
		g.setColor(getFormats().getBackgroundColor());
		g.fill(new Rectangle2D.Double(leftX, e.getRectangle().getMinY(), rightX - leftX, e.getRectangle().getHeight()));

		SimpleSequenceInterval paintRange = calculatePaintRange(e);
		double x = getLabeledAlignmentArea().getContentArea().paintXByColumn(getModel().getFirstSeqPos() - getModel().getLeftCutPosition());
		double y = 0; 
		double height = getHeight();
		double fontZoom = getFormats().calculateFontZoomFactor(this);
		ScaledPherogramDistortion distortion = createPherogramDistortion();
		
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
					height, distortion);
		}

    // Paint indices:
		Font indexFont = getFormats().getIndexFont().createFont(fontZoom); 
		g.setFont(indexFont);
		g.setColor(Color.BLACK);
		painter.paintBaseCallIndices(g, paintRange.getFirstPos(), paintRange.getLastPos(), x, y, distortion, tokenWidth);
		y += indexFont.getSize2D() * PherogramFormats.FONT_HEIGHT_FACTOR;
		
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


	@Override
	public PherogramAreaModel getModel() {
		return model;
	}
  
	
  /**
   * Indicates whether the editable sequence should be updated if the cut positions of the model
   * are modified. 
   * <p>
   * Note that such updates only affect parts of the editable sequence that are or were associated with 
   * the cut off parts of the pherogram. In a reverse complement operation the editable sequence between
   * the cut positions will never be updated. This needs to be done in application code e.g. by calling
   * {@link #copyBaseCallSequence(int, int)}. 
   * 
   * @return {@code true} if updated in the editable sequence are performed automatically, {@code false} otherwise
   * @see #copyBaseCallSequence(int, int)
   */
  public boolean isUpdateEditableSequence() {
		return updateEditableSequence;
	}


	/**
	 * Specifies whether updated of the editable sequence shall be performed by this class automatically,
	 * if the cut positions in the underlying model are changed.
	 * 
	 * @param updateEditableSequence Specify {@code true} here, if automatic updated shall be performed from
	 *        now on or {@code false} if the editable sequence shall be left unchanged from now on 
	 */
	public void setUpdateEditableSequence(boolean updateEditableSequence) {
		this.updateEditableSequence = updateEditableSequence;
	}


	private void updateChangedPosition() {
		getLabeledAlignmentArea().getSizeManager().setLocalMaxLengthBeforeAfterRecalculate();
		repaint();  //TODO Is this necessary?
  }
  
  
  /**
   * Copies a part of the base call sequence from the underlying model to the editable sequence this data
   * area is attached to.
   * <p>
   * Note that this method will do nothing, if this instance has not been attached to any sequence yet.
   * 
   * @param startBaseCallIndex the index of the first position in the base call sequence to be copied 
   * @param endBaseCallIndex the index after the last position of the base call sequence to be copied
   * @return {@code true} if the editable sequence was updated, {@code false} if this area is currently
   *         not attached to any sequence
   * @see #isUpdateEditableSequence()
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
	public boolean copyBaseCallSequence(int startBaseCallIndex, int endBaseCallIndex) {
  	if (getList() != null) {
	  	String sequenceID = getList().getLocation().getSequenceID();
	  	if (sequenceID != null) {
		  	AlignmentModel model = getLabeledAlignmentModel();
		  	TokenSet tokenSet = model.getTokenSet();
		  	for (int baseCallIndex = startBaseCallIndex; baseCallIndex < endBaseCallIndex; baseCallIndex++) {
		  		String base = Character.toString(getModel().getPherogramProvider().getBaseCall(baseCallIndex)).toUpperCase();
					int editableIndex = getModel().editableIndexByBaseCallIndex(baseCallIndex).getCorresponding();
		  		if ((editableIndex >= 0) && (!base.equals(tokenSet.representationByToken(model.getTokenAt(sequenceID, editableIndex))))) {
		  			model.setTokenAt(sequenceID, editableIndex, tokenSet.tokenByRepresentation(base));
		  		}
				}
		  	return true;
	  	}
  	}
  	return false;
  }
  
  
  @SuppressWarnings("unchecked")
	private void setGaps(int startEditableIndex, int length) {
  	if (getList() != null) {
	  	Collection<Object> tokens = new ArrayList<Object>(length);
	  	for (int i = 0; i < length; i++) {
				tokens.add(getLabeledAlignmentModel().getTokenSet().getGapToken());
			}
	  	String sequenceID = getList().getLocation().getSequenceID();
	  	if (sequenceID != null) {
	  		((AlignmentModel<Object>)getLabeledAlignmentModel()).setTokensAt(sequenceID, startEditableIndex, tokens);
	  	}
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
		int pos = getModel().baseCallIndexByEditableIndex(
				getOwner().getOwner().getSelection().getFirstColumn()).getBefore();
		boolean result = pos != PherogramAlignmentRelation.OUT_OF_RANGE; 
		if (result) {
			getModel().setLeftCutPosition(pos);
		}
		return result;
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
	 *         {@code false} if that was not possible
	 */
	public boolean setRightCutPositionBySelection() {
		PherogramAlignmentRelation relation = getModel().baseCallIndexByEditableIndex(
				getOwner().getOwner().getSelection().getLastColumn()); 
		int pos = relation.getAfter();
		if (pos == PherogramAlignmentRelation.OUT_OF_RANGE) {
			pos = relation.getBefore() + 1;  // Set cut position behind the end of the pherogram.
		}
		boolean result = pos != PherogramAlignmentRelation.OUT_OF_RANGE;  //TODO This would only be possible, if the pherogram would not be attached to the sequence and can be removed? 
		if (result) {
			getModel().setRightCutPosition(pos);
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


	/**
	 * Returns the width of the token in the editable sequence at {@link #getFirstSeqPos()}, considering the 
	 * current zoom factor. 
	 * 
	 * @return the width of a token in the associated part of the editable sequence
	 */
	public double getEditableTokenWidth() {
		return getOwner().getOwner().getPaintSettings().getTokenWidth(getModel().getFirstSeqPos());
	}
	
	
	public TokenPainter getRelatedTokenPainter() {
		return getOwner().getOwner().getPaintSettings().getTokenPainterList().painterByColumn(getModel().getFirstSeqPos());
	}


	@Override
	public double getLengthBeforeStart() {
		return Math.max(0, getLabeledAlignmentArea().getContentArea().paintXByColumn(getModel().baseCallIndexByEditableIndex(0).getAfter()));
	}


	@Override
	public double getLengthAfterEnd() {
		int lastEditableIndex = getModel().editableIndexByBaseCallIndex(getModel().getRightCutPosition() - 1).getAfter();
		double lengthOfOutputAfterAlignmentStart = getLabeledAlignmentArea().getContentArea().paintXByColumn(lastEditableIndex) + 
				(1 + getModel().getPherogramProvider().getSequenceLength() - getModel().getRightCutPosition()) *  
				getOwner().getOwner().getPaintSettings().getTokenWidth(Math.max(0, getModel().getFirstSeqPos())) - getLengthBeforeStart();  // Math.max(0, ...) is used because this method might be called during the execution of setter cut position method, when other properties are not yet adjusted.  
		return Math.max(0, lengthOfOutputAfterAlignmentStart - getOwner().getOwner().getSizeManager().getLocalMaximumNeededAlignmentWidth());
	}


	@Override
	public double getHeight() {
		return DEFAULT_HEIGHT_FACTOR * getLabeledAlignmentArea().getPaintSettings().getTokenHeight();
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
			int lastSeqPos = getModel().editableIndexByBaseCallIndex(getModel().getRightCutPosition() - 1).getAfter() - addend;
			if (e.getStartIndex() <= lastSeqPos) {  // Do not process edits behind the pherogram.
				int tokensBefore = Math.min(e.getAffectedTokens().size(), Math.max(0, getModel().getFirstSeqPos() - e.getStartIndex() - addend));
				int tokensAfter = Math.max(0, e.getAffectedTokens().size() - Math.max(0, lastSeqPos - e.getStartIndex()) + addend);
				int tokensInside = e.getAffectedTokens().size() - tokensBefore - tokensAfter;
				
				switch (e.getType()) {
					case INSERTION:
						if (tokensBefore > 0) {
							getModel().setFirstSeqPos(getModel().getFirstSeqPos() + tokensBefore);
						}
						if (tokensInside > 0) {
							getModel().addShiftChange(getModel().baseCallIndexByEditableIndex(
									Math.max(0, e.getStartIndex() + tokensBefore + addend)).getBeforeValidIndex(), tokensInside);
						}
						break;
					case DELETION:
						if (tokensBefore > 0) {
							getModel().setFirstSeqPos(getModel().getFirstSeqPos() - tokensBefore);
						}
						if (tokensInside > 0) {
							getModel().addShiftChange(getModel().baseCallIndexByEditableIndex(
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


//	@Override
	public <T, U> void afterModelChanged(AlignmentModel<T> previous,	AlignmentModel<U> current) {}  
	// This event is currently not passed to sequence attached areas.
}
