/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.pherogram.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelAdapter;
import info.bioinfweb.libralign.model.AlignmentModelListener;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.pherogram.distortion.GapPattern;
import info.bioinfweb.libralign.pherogram.provider.PherogramProvider;
import info.bioinfweb.libralign.pherogram.view.PherogramTraceCurveView;



/**
 * The data area model of a {@link PherogramArea} containing the pherogram source, as well as cut positions, 
 * the column where the pherogram is attached to the editable sequence and pherogram distortion information.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class PherogramAreaModel extends PherogramComponentModel implements DataModel<PherogramModelListener> {
	private AlignmentModel<?> alignmentModel;
	private String labeledSequenceID;
	private int firstSeqPos;
	private List<ShiftChange> shiftChangeList = new ArrayList<ShiftChange>();
	private boolean firstSeqPosUpdateOngoing = false;
  
	
	/**
	 * Creates a new instance of this class.
	 * <p>
	 * Note that some methods of this class may throw {@link NullPointerException}s as long as no owner
	 * has been provided using {@link #setOwner(PherogramArea)}. Note that each {@link PherogramArea}
	 * needs to have its own model instance. It is not possible to share an instance between several
	 * pherogram areas. Anyway an unlimited number of instances of {@link PherogramTraceCurveView}
	 * can use an instance if this class at the same time and in addition to one pherogram model.
	 * 
	 * @param pherogramProvider the pherogram provider
	 * @throws NullPointerException if {@code null} is specified for {@code pherogramProvider}
	 */
	public PherogramAreaModel(PherogramProvider provider, AlignmentModel<?> alignmentModel, String labeledSequenceID) {
		super(provider);
		setAlignmentModel(alignmentModel);
		setLabeledSequenceID(labeledSequenceID);
	}


	/**
	 * Creates a new instance based on the specified pherogram component model instance. (The underlying pherogram 
	 * provider instance is shared and not deep copied.)
	 * <p>
	 * Note that some methods of this class may throw {@link NullPointerException}s as long as no owner
	 * has been provided using {@link #setOwner(PherogramArea)}. Note that each {@link PherogramArea}
	 * needs to have its own model instance. It is not possible to share an instance between several
	 * pherogram areas. Anyway an unlimited number of instances of {@link PherogramTraceCurveView}
	 * can use an instance if this class at the same time and in addition to one pherogram model.
	 * 
	 * @param model the source pherogram component model
	 * @throws NullPointerException if {@code null} is specified for {@code model}
	 */
	public PherogramAreaModel(PherogramComponentModel model, AlignmentModel<?> alignmentModel, String labeledSequenceID) {
		super(model);
		setAlignmentModel(alignmentModel);
		setLabeledSequenceID(labeledSequenceID);
	}
	
	
	@Override
	public AlignmentModel<?> getAlignmentModel() {
		return alignmentModel;
	}


	private void setAlignmentModel(AlignmentModel<?> alignmentModel) {
		if (alignmentModel == null) {
			throw new IllegalArgumentException("alignmentModel must not be null.");
		}
		else {  // If this method should ever be used later than in the constructor, it must also unregister the listener from the previous model.
			this.alignmentModel = alignmentModel;
			this.alignmentModel.addModelListener(new AlignmentModelAdapter<Object>() {
				@Override
				public void afterTokenChange(TokenChangeEvent<Object> e) {
					if (e.getSource().equals(getAlignmentModel()) && (e.getSequenceID() == getLabeledSequenceID())) {
						int addend = e.isLeftBound() ? -1 : 0;
						int lastSeqPos = editableIndexByBaseCallIndex(getRightCutPosition() - 1).getAfter() - addend;
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
										addShiftChange(baseCallIndexByEditableIndex(
												Math.max(0, e.getStartIndex() + tokensBefore + addend)).getBeforeValidIndex(), tokensInside);
									}
									break;
								case DELETION:
									if (tokensBefore > 0) {
										setFirstSeqPos(getFirstSeqPos() - tokensBefore);
									}
									if (tokensInside > 0) {
										addShiftChange(baseCallIndexByEditableIndex(
												e.getStartIndex() + tokensBefore).getAfterValidIndex(), -e.getAffectedTokens().size());
									}
									break;
								case REPLACEMENT:  // Nothing to do (Replacements differing in length are not allowed.)
									break;  //TODO If a token is replaced by a gap a shift change would have to be added. (Solve this problem when gap displaying is generally implemented for all data areas.)
							}
						}
					}
				}
			});
		}
	}
	
	
	public String getLabeledSequenceID() {
		return labeledSequenceID;
	}


	private void setLabeledSequenceID(String labeledSequenceID) {
		if (labeledSequenceID == null) {
			throw new IllegalArgumentException("labeledSequenceID must not be null.");
		}
		else {
			this.labeledSequenceID = labeledSequenceID;
		}
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
	 * @param firstSeqPos the new index
	 */
	public void setFirstSeqPos(int firstSeqPos) {
		if (this.firstSeqPos != firstSeqPos) {
			if (Math2.isBetween(firstSeqPos, 0, getAlignmentModel().getSequenceLength(getLabeledSequenceID()) - 1)) {
				int oldPosition = this.firstSeqPos;
				this.firstSeqPos = firstSeqPos;
				fireFirstSequencePositionChange(oldPosition, firstSeqPos, false);
			}
			else {
				throw new IndexOutOfBoundsException(firstSeqPos + " is not a valid index to attach this pherogram to.");
			}
		}
	}
	
	
	@Override
	protected PherogramAlignmentRelation getAlignmentRelation(int baseCallIndex) {
		return editableIndexByBaseCallIndex(baseCallIndex);
	}


	@Override
	protected void onSetLeftCutPosition(int oldBaseCallIndex, int newBaseCallIndex) {
		if (!firstSeqPosUpdateOngoing) {
			setFirstSeqPos(getFirstSeqPos() + newBaseCallIndex - oldBaseCallIndex);
			if (newBaseCallIndex < getRightCutPosition()) {  // Avoid deleting again, if setRightCutPosition() was anyway already called and performed that operation.
				deleteCutOffDistortions();
			}
		}
	}


	@Override
	protected void onSetRightCutPosition(int oldBaseCallIndex, int newBaseCallIndex) {
		if (getLeftCutPosition() < newBaseCallIndex) {  // Avoid deleting again, if setLeftCutPosition() was anyway already called and performed that operation.
			deleteCutOffDistortions();
		}
	}


	/**
	 * Allows to specify a new first sequence and left cut position at the same time. This method should be used
	 * if both valid values for both properties are defined (e.g. in an external data source like a file) and
	 * {@code firstSeqPos} should not be moved accordingly when {@code leftCutPos} is set, like it is done in
	 * {@link #setLeftCutPosition(int)}.
	 * 
	 * @param firstSeqPos the new index in the editable sequence to which the first nucleotide of the base call
	 *        sequence that is not cut off is aligned  
	 * @param leftCutPos the new left cut position
	 */
	public void setFirstSeqLeftCutPos(int firstSeqPos, int leftCutPos) {
		setFirstSeqPos(firstSeqPos);
		try {
			firstSeqPosUpdateOngoing = true;
			setLeftCutPosition(leftCutPos);
		}
		finally {
			firstSeqPosUpdateOngoing = false;
		}
	}


	@SuppressWarnings({"rawtypes", "unchecked"})
	private boolean isGap(int editableIndex) {
		AlignmentModel model = getAlignmentModel();
		return model.getTokenSet().isGapToken(model.getTokenAt(getLabeledSequenceID(), editableIndex));
	}
	

	/**
	 * Returns the index in the editable alignment sequence that corresponds to the specified index in the base
	 * call sequence.
	 * 
	 * @param baseCallIndex the absolute index in the base call sequence
	 * @return the absolute index in the editable sequence or {@link #GAP} if the according position in the 
	 *         editable sequence has been deleted or {@link #OUT_OF_RANGE} if the specified base call index
	 *         lies outside the range of the pherogram
	 */
	public PherogramAlignmentRelation editableIndexByBaseCallIndex(int baseCallIndex) {
  	if (baseCallIndex < 0) {
  		return new PherogramAlignmentRelation(PherogramAlignmentRelation.OUT_OF_RANGE, PherogramAlignmentRelation.OUT_OF_RANGE, 
  				1 - getLeftCutPosition() + getFirstSeqPos(), shiftChangeList.listIterator());
  	}
  	else if (baseCallIndex >= getPherogramProvider().getSequenceLength()) {
  		return new PherogramAlignmentRelation(
  				editableIndexByBaseCallIndex(getPherogramProvider().getSequenceLength() - 1).getCorresponding(), 
  				PherogramAlignmentRelation.OUT_OF_RANGE, PherogramAlignmentRelation.OUT_OF_RANGE, 
  				shiftChangeList.listIterator(shiftChangeList.size()));  // Iterator positioned behind the last element of the list.
  	}
    else {
    	int resultPos = baseCallIndex - getLeftCutPosition() + getFirstSeqPos();
    	
    	ListIterator<ShiftChange> iterator = shiftChangeList.listIterator();
    	if (!shiftChangeList.isEmpty()) {
      	while (iterator.hasNext()) {
      		ShiftChange shiftChangeEntry = iterator.next();
      		if ((shiftChangeEntry.shiftChange < 0) && (Math2.isBetween(baseCallIndex, 
      				shiftChangeEntry.baseCallIndex, shiftChangeEntry.baseCallIndex - shiftChangeEntry.shiftChange - 1))) {
      			
      			resultPos -= baseCallIndex - shiftChangeEntry.baseCallIndex;
      			return new PherogramAlignmentRelation(resultPos - 1, PherogramAlignmentRelation.GAP,	resultPos, iterator);
      		}
      		else if ((shiftChangeEntry.baseCallIndex < baseCallIndex)) {
      			resultPos += shiftChangeEntry.shiftChange;
      		}
      		else {
      			if (iterator.hasPrevious()) {
      				iterator.previous();
      			}
      			
      			int afterPos = resultPos;
      			if (Math2.isBetween(baseCallIndex, shiftChangeEntry.baseCallIndex, 
      					shiftChangeEntry.baseCallIndex + shiftChangeEntry.shiftChange - 1)) {
      				
      				afterPos += shiftChangeEntry.shiftChange;
      			}
      			return new PherogramAlignmentRelation(resultPos, resultPos, afterPos, iterator);
      		}
      	}
    	}
			return new PherogramAlignmentRelation(resultPos, resultPos, resultPos, iterator);
  	}
  }
  
  
  public PherogramAlignmentRelation baseCallIndexByEditableIndex(int editableIndex) {
  	int resultPos = editableIndex - getFirstSeqPos() + getLeftCutPosition();
  	
  	ListIterator<ShiftChange> iterator = shiftChangeList.listIterator();
  	if (!shiftChangeList.isEmpty()) {
    	while (iterator.hasNext()) {
    		ShiftChange shiftChangeEntry = iterator.next();
    		if ((shiftChangeEntry.shiftChange > 0) && (Math2.isBetween(resultPos, 
    				shiftChangeEntry.baseCallIndex, shiftChangeEntry.baseCallIndex + shiftChangeEntry.shiftChange - 1))) {
    			
    			int beforePos = shiftChangeEntry.baseCallIndex;
    			int correspondingPos = shiftChangeEntry.baseCallIndex;
    			int afterPos = shiftChangeEntry.baseCallIndex;
    			if (isGap(editableIndex)) {
    				correspondingPos = PherogramAlignmentRelation.GAP;
    				if (resultPos == shiftChangeEntry.baseCallIndex) {
    					beforePos = shiftChangeEntry.baseCallIndex - 1;  // First editable position in the distortion is the gap.
    					if (beforePos < 0) {
    						beforePos = PherogramAlignmentRelation.OUT_OF_RANGE;
    					}
    				}
    				if (resultPos == shiftChangeEntry.baseCallIndex + shiftChangeEntry.shiftChange - 1) {
    					afterPos = shiftChangeEntry.baseCallIndex + 1;  // Last editable position in the distortion is the gap.
    					if (afterPos >= getPherogramProvider().getSequenceLength()) {
    						afterPos = PherogramAlignmentRelation.OUT_OF_RANGE;
    					}
    				}
    			}
    			return new PherogramAlignmentRelation(beforePos, correspondingPos, afterPos, iterator);  //TODO Does the iterator have to be moved back in one of the cases?
    		}
    		else if ((shiftChangeEntry.baseCallIndex <= resultPos)) {
    			resultPos -= shiftChangeEntry.shiftChange;
    		}
    		else {
    			if (iterator.hasPrevious()) {
    				iterator.previous();  // Move iterator back by one position if the next gap has not been reached.
    			}
    			break;  // End loop and go on with range check. 
    		}
    	}
  	}
  	if (resultPos < 0) {
  		return new PherogramAlignmentRelation(PherogramAlignmentRelation.OUT_OF_RANGE, PherogramAlignmentRelation.OUT_OF_RANGE, 0, 
  				iterator);
  	}
  	else if (resultPos >= getPherogramProvider().getSequenceLength()) {
  		return new PherogramAlignmentRelation(getPherogramProvider().getSequenceLength() - 1, 
  				PherogramAlignmentRelation.OUT_OF_RANGE, PherogramAlignmentRelation.OUT_OF_RANGE, iterator);
  	}
  	else {
  		return new PherogramAlignmentRelation(resultPos, resultPos, resultPos, iterator);
  	}
  }
  
  
  public int shiftAtBaseCallIndex(int baseCallIndex) {
  	int result = 0;
  	ListIterator<ShiftChange> iterator = shiftChangeList.listIterator();
  	if (!shiftChangeList.isEmpty()) {
    	while (iterator.hasNext()) {
    		ShiftChange shiftChangeEntry = iterator.next();
    		if ((shiftChangeEntry.baseCallIndex <= baseCallIndex)) {
    			result += shiftChangeEntry.shiftChange;
    		}
    		else {
    			return result;
    		}
    	}
  	}
  	return result;
  }
  
  
  /**
   * Returns the index of the first list element with a base call index greater or equal to the specified one or 
   * {@code shiftChangeList.size()} if no such element exists.
   */
  private int shiftChangeListIndexByBaseCallIndex(int baseCallIndex) {
  	int pos = 0;
  	while ((pos < shiftChangeList.size()) && (shiftChangeList.get(pos).baseCallIndex < baseCallIndex)) {
  		pos++;
		}
  	return pos;
  }
  
  
//  private void printShiftChangeList() {
//  	Iterator<ShiftChange> iterator = shiftChangeIterator();
//  	while (iterator.hasNext()) {
//  		ShiftChange shiftChange = iterator.next();
//  		System.out.print("(" + shiftChange.baseCallIndex + ": " + shiftChange.shiftChange + "), ");
//  	}
//  	System.out.println();
//  }

  
  private int combineTwoShiftChanges(int firstIndex) {
  	if (Math2.isBetween(firstIndex, 0, shiftChangeList.size() - 2)) {
      ShiftChange firstChange = shiftChangeList.get(firstIndex);
      ShiftChange secondChange = shiftChangeList.get(firstIndex + 1);
      if (((firstChange.shiftChange < 0) || (secondChange.shiftChange < 0)) &&  // Avoid two neighboring positive shifts to be combined.
      		((secondChange.baseCallIndex <= firstChange.baseCallIndex - firstChange.shiftChange) || 
      		(firstChange.baseCallIndex >= secondChange.baseCallIndex - secondChange.shiftChange))) {  // Test if shifts in opposite directions neutralize each other.
      	
  			firstChange.shiftChange += secondChange.shiftChange;
      	shiftChangeList.remove(secondChange);
      	if (firstChange.shiftChange == 0) {
      		shiftChangeList.remove(firstChange);
      		return 2;
      	}
      	else {
      		return 1;
      	}
      }
  	}
  	return 0;
  }
  
  
  private void combineThreeShiftChanges(int index) {
  	int removedElements = combineTwoShiftChanges(index - 1); 
  	if (removedElements < 2) {  // No further combination possible of both elements have been removed.
  		combineTwoShiftChanges(index - removedElements);  // Indices have been shifted because of the removal.
  	}
  }
  
  
  /**
   * Adds or replaces a change in the shift between the editable sequence in the alignment and the base call
   * sequence.
   * <p>
   * If a {@code shiftChange} of 0 is specified the entry in the underlying shift list will be deleted.
   * 
   * @param baseCallIndex the first position in the base call sequence affected by the shift change
   * @param shiftChange a positive or negative integer describing the shift change as the number of positions in the editable sequence
   */
  public void setShiftChange(int baseCallIndex, int shiftChange) {
  	int listIndex = shiftChangeListIndexByBaseCallIndex(baseCallIndex);
  	if ((listIndex < shiftChangeList.size()) && (shiftChangeList.get(listIndex).baseCallIndex == baseCallIndex)) {
    	if (shiftChange == 0) {
    		shiftChangeList.remove(listIndex);
    	}
    	else {
    		shiftChangeList.get(listIndex).shiftChange = shiftChange;
    	}
  	}
  	else if (shiftChange != 0) {
  		shiftChangeList.add(listIndex, new ShiftChange(baseCallIndex, shiftChange));
  		combineThreeShiftChanges(listIndex);
  	}
  	fireShiftChangeEdited(baseCallIndex, shiftChange, false);
  }
  
  
  public void addShiftChange(int baseCallIndex, int shiftChangeAddend) {
  	int listIndex = shiftChangeListIndexByBaseCallIndex(baseCallIndex);
  	int shiftChange = shiftChangeAddend;
  	if (listIndex < shiftChangeList.size() && (shiftChangeList.get(listIndex).baseCallIndex == baseCallIndex)) {
  		shiftChange += shiftChangeList.get(listIndex).shiftChange;
  	}
  	setShiftChange(baseCallIndex, shiftChange);
  }
  
  
  public ListIterator<ShiftChange> shiftChangeIterator(int listIndex) {
  	final ListIterator<ShiftChange> iterator = shiftChangeList.listIterator(listIndex);
  	return new ListIterator<ShiftChange>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public ShiftChange next() {
				return iterator.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
				//TODO Possibly delegate to iterator in future versions and fire according events.
			}

			@Override
			public void add(ShiftChange e) {
				throw new UnsupportedOperationException();
				//TODO Possibly delegate to iterator in future versions and fire according events.
			}

			@Override
			public boolean hasPrevious() {
				return iterator.hasPrevious();
			}

			@Override
			public int nextIndex() {
				return iterator.nextIndex();
			}

			@Override
			public ShiftChange previous() {
				return iterator.previous();
			}

			@Override
			public int previousIndex() {
				return iterator.previousIndex();
			}

			@Override
			public void set(ShiftChange e) {
				throw new UnsupportedOperationException();
				//TODO Possibly delegate to iterator in future versions and fire according events.
			}
		};
	}


	public ListIterator<ShiftChange> shiftChangeIterator() {
		return shiftChangeIterator(0);
	}


	public ListIterator<ShiftChange> shiftChangeIteratorByBaseCallIndex(int baseCallIndex) {
		ListIterator<ShiftChange> result = shiftChangeIterator();
		while (result.hasNext()) {
			ShiftChange shiftChange = result.next();
			if (shiftChange.getBaseCallIndex() >= baseCallIndex) {
				result.previous();
				return result;  // Return iterator positioned before the first element at or after the specified base call index.
			}
		}
		return result;  // Return iterator positioned behind the end of the list
	}
  
  
	public GapPattern getGapPattern(ShiftChange shiftChange) {
		GapPattern result = new GapPattern(shiftChange.getShiftChange() + 1);
		int firstEditableIndex = editableIndexByBaseCallIndex(shiftChange.getBaseCallIndex()).getCorresponding();
		for (int i = 0; i < result.size(); i++) {
			result.setGap(i, isGap(firstEditableIndex + i));
		}
		return result;
	}
	
	
	/**
	 * Returns the number of shift changes that are currently contained in this model.
	 * 
	 * @return the number of shift changes
	 * @since 0.4.0
	 */
	public int getShiftChangeCount() {
		return shiftChangeList.size();
	}
	
	
	/**
	 * Deletes all distortions of the base call sequence to the editable sequence in the cut off areas at the beginning
	 * and the end of the pherogram and moves {@link PherogramArea#getFirstSeqPos()} of the owner accordingly, so that 
	 * the visible part of the pherogram still correctly aligns to the editable sequence.
	 * <p>
	 * This method is intended for internal use in <i>LibrAlign</i> and is called from within 
	 * {@link PherogramArea#setLeftCutPosition(int)} and {@link PherogramArea#setRightCutPosition(int)}. Usually there
	 * should be no need to call it in application code directly.
	 */
	public void deleteCutOffDistortions() {
		Iterator<ShiftChange> iterator = shiftChangeList.iterator();
		int firstPosShift = 0;
		while (iterator.hasNext()) {
			ShiftChange change = iterator.next();
			if (change.getBaseCallIndex() < getLeftCutPosition()) {
				int overlap = change.getBaseCallIndex() - change.getShiftChange() - getLeftCutPosition();  // Value is only valid if shift change is negative, otherwise an overlap is anyway impossible because it cannot span multiple editable positions.
				if ((change.getShiftChange() < 0) && (overlap > 0)) {  // Split up distortion that spans the cut position.
					change.baseCallIndex = getLeftCutPosition();  // New overlaps with neighboring entries because of this operation are not possible, if there were none before.
					change.shiftChange += overlap;
					firstPosShift -= overlap;
				}
				else {  // Remove distortion that is completely contained in the cut off area.
					firstPosShift += change.shiftChange;
					iterator.remove();
				}
			}
			else if (change.getBaseCallIndex() >= getRightCutPosition()) {  // No overlaps as in the left case are possible here.
				iterator.remove();
			}
		}
		setFirstSeqPos(getFirstSeqPos() + firstPosShift);	
		// Repainting and flagging recalculation of maxLengthBeforeStart/AfterEnd is performed by calling methods of PherogramArea and is not repeated here for performance reasons.
	}
	
	
	@Override
	public void reverseComplement() {
		super.reverseComplement();
		
		List<ShiftChange> newList = new ArrayList<ShiftChange>(shiftChangeList.size());
		
		ListIterator<ShiftChange> iterator = shiftChangeList.listIterator(shiftChangeList.size());
		while (iterator.hasPrevious()) {
			ShiftChange change = iterator.previous();
			int shiftInDistortion = 0;
			if (change.shiftChange < 0) {
				shiftInDistortion = change.shiftChange;
			}
			change.baseCallIndex = getPherogramProvider().getSequenceLength() - change.baseCallIndex + shiftInDistortion;
			newList.add(change);
		}
		
		shiftChangeList.clear();
		shiftChangeList.addAll(newList);  // shiftChangeList = newList; would be easier, but problematic, if references to the list are currently stored by any application objects.
	}
	
	
	protected void fireFirstSequencePositionChange(int oldPosition, int newPosition, boolean moreEventsUpcoming) {
		PherogramFirstSeqPosChangeEvent event = new PherogramFirstSeqPosChangeEvent(this, moreEventsUpcoming, oldPosition, newPosition);
		for (PherogramModelListener listener : modelListeners.toArray(new PherogramModelListener[modelListeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.firstSequencePositionChange(event);
		}
	}
	
	
	protected void fireShiftChangeEdited(int baseCallIndex, int shiftChange, boolean moreEventsUpcoming) {
		PherogramShiftChangeUpdateEvent event = new PherogramShiftChangeUpdateEvent(this, moreEventsUpcoming, baseCallIndex, shiftChange);
		for (PherogramModelListener listener : modelListeners.toArray(new PherogramModelListener[modelListeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.shiftChangeEdited(event);
		}
	}
}
