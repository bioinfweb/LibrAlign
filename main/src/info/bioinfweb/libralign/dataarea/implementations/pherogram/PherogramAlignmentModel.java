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


import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.biojava3.core.sequence.compound.NucleotideCompound;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.libralign.pherogram.PherogramPainter;
import info.bioinfweb.libralign.pherogram.PherogramProvider;
import info.bioinfweb.libralign.pherogram.PherogramUtils;
import info.bioinfweb.libralign.pherogram.distortion.GapPattern;
import info.bioinfweb.libralign.pherogram.distortion.ScaledPherogramDistortion;



/**
 * Classes implementing this interface allow storing data that defines the alignment between a
 * pherogram and the sequence in an alignment area it is associated with.
 * 
 * @author Ben St&ouml;ver
 */
public class PherogramAlignmentModel {
	private PherogramArea owner;
  private List<ShiftChange> shiftChangeList = new ArrayList<ShiftChange>();
  
	
  public PherogramAlignmentModel(PherogramArea owner) {
		super();
		this.owner = owner;
	}


	public PherogramArea getOwner() {
		return owner;
	}


  /**
   * Returns the index in the editable alignment sequence that corresponds to the specified index in the base
   * call sequence.
   * 
   * @param baseCallIndex - the absolute index in the base call sequence
   * @return the absolute index in the editable sequence or {@link #GAP} if the according position in the 
   *         editable sequence has been deleted or {@link #OUT_OF_RANGE} if the specified base call index
   *         lies outside the range of the pherogram
   */
  public PherogramAlignmentRelation editableIndexByBaseCallIndex(int baseCallIndex) {
  	if (baseCallIndex < 1) {  // BioJava indices start with 1
  		return new PherogramAlignmentRelation(PherogramAlignmentRelation.OUT_OF_RANGE, PherogramAlignmentRelation.OUT_OF_RANGE, 
  				1 - getOwner().getLeftCutPosition() + getOwner().getFirstSeqPos(), shiftChangeList.listIterator());
  	}
  	else if (baseCallIndex > getOwner().getProvider().getSequenceLength()) {
  		return new PherogramAlignmentRelation(
  				editableIndexByBaseCallIndex(getOwner().getProvider().getSequenceLength()).getCorresponding(), 
  				PherogramAlignmentRelation.OUT_OF_RANGE, PherogramAlignmentRelation.OUT_OF_RANGE, 
  				shiftChangeList.listIterator(shiftChangeList.size()));  // Iterator positioned behind the last element of the list.
  	}
    else {
    	int resultPos = baseCallIndex - getOwner().getLeftCutPosition() + getOwner().getFirstSeqPos();
    	
    	ListIterator<ShiftChange> iterator = shiftChangeList.listIterator();
    	if (!shiftChangeList.isEmpty()) {
      	while (iterator.hasNext()) {
      		ShiftChange shiftChangeEntry = iterator.next();
      		if ((shiftChangeEntry.shiftChange < 0) && (Math2.isBetween(baseCallIndex, 
      				shiftChangeEntry.baseCallIndex, shiftChangeEntry.baseCallIndex - shiftChangeEntry.shiftChange - 1))) {
      			
      			resultPos -= baseCallIndex - shiftChangeEntry.baseCallIndex;
      			return new PherogramAlignmentRelation(resultPos - 1, PherogramAlignmentRelation.GAP,	resultPos, iterator);
      		}
      		else if ((shiftChangeEntry.baseCallIndex <= baseCallIndex)) {
      			resultPos += shiftChangeEntry.shiftChange;
      		}
      		else {
      			if (iterator.hasPrevious()) {
      				iterator.previous();
      			}
      			return new PherogramAlignmentRelation(resultPos, resultPos, resultPos, iterator);
      		}
      	}
    	}
			return new PherogramAlignmentRelation(resultPos, resultPos, resultPos, iterator);
  	}
  }
  
  
  public PherogramAlignmentRelation baseCallIndexByEditableIndex(int editableIndex) {
  	int resultPos = editableIndex - getOwner().getFirstSeqPos() + getOwner().getLeftCutPosition();
  	
  	ListIterator<ShiftChange> iterator = shiftChangeList.listIterator();
  	if (!shiftChangeList.isEmpty()) {
    	while (iterator.hasNext()) {
    		ShiftChange shiftChangeEntry = iterator.next();
    		if ((shiftChangeEntry.shiftChange > 0) && (Math2.isBetween(resultPos, 
    				shiftChangeEntry.baseCallIndex, shiftChangeEntry.baseCallIndex + shiftChangeEntry.shiftChange - 1))) {
    			
    			return new PherogramAlignmentRelation(shiftChangeEntry.baseCallIndex - 1, PherogramAlignmentRelation.GAP,	
    					shiftChangeEntry.baseCallIndex, iterator);
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
  	if (resultPos < 1) {
  		return new PherogramAlignmentRelation(PherogramAlignmentRelation.OUT_OF_RANGE, PherogramAlignmentRelation.OUT_OF_RANGE, 1, 
  				iterator);
  	}
  	else if (resultPos > getOwner().getProvider().getSequenceLength()) {
  		return new PherogramAlignmentRelation(getOwner().getProvider().getSequenceLength(), 
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
  
  
  /**
   * Adds or replaces a change in the shift between the editable sequence in the alignment and the base call
   * sequence.
   * <p>
   * If a {@code shiftChange} of 0 is specified the entry in the underlying shift list will be deleted.
   * 
   * @param baseCallIndex - the first position in the base call sequence where the shift change shall be valid
   * @param shiftChange - A positive of negative for the the shift change (number of positions in the editable sequence)
   */
  public void setShiftChange(int baseCallIndex, int shiftChange) {
  	int listIndex = shiftChangeListIndexByBaseCallIndex(baseCallIndex);
  	if (shiftChange == 0) {
  		
  	}
  	else {
	  	if (listIndex < shiftChangeList.size() &&  (shiftChangeList.get(listIndex).baseCallIndex == baseCallIndex)) {
	  		shiftChangeList.get(listIndex).shiftChange = shiftChange;
	  	}
	  	shiftChangeList.add(listIndex, new ShiftChange(baseCallIndex, shiftChange));
  	}
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
  
  
	private GapPattern getGapPattern(ShiftChange shiftChange) {
		GapPattern result = new GapPattern(shiftChange.getShiftChange() + 1);
		int firstEditableIndex = editableIndexByBaseCallIndex(shiftChange.getBaseCallIndex()).getCorresponding() - 
				shiftChange.getShiftChange() - 1;
		for (int i = 0; i < result.size(); i++) {
			result.setGap(i, ((NucleotideCompound)getOwner().getOwner().getSequenceProvider().getTokenAt(
					getOwner().getList().getLocation().getSequenceID(), firstEditableIndex + i)).getBase().equals(
							"" + AlignmentAmbiguityNucleotideCompoundSet.GAP_CHARACTER));
		}
		return result;
	}
	
	
  public ScaledPherogramDistortion createPherogramDistortion() {
  	ScaledPherogramDistortion result = new ScaledPherogramDistortion(getOwner().getProvider().getSequenceLength());
  	
		int startTraceIndex = 1;  //getTracePosition(startBaseCallIndex);
		Iterator<ShiftChange> shiftChangeIterator = shiftChangeIterator();
		ShiftChange shiftChange = null;
		if (shiftChangeIterator.hasNext()) {
			shiftChange = shiftChangeIterator.next();
		}
		
		final int compoundWidth = getOwner().getOwner().getCompoundWidth();
		int stepWidth = 1;
		int editPosPerBaseCallPos = 1;
		double baseCallPaintX = 0; //0.5 * compoundWidth;
		for (int baseCallIndex = 1; baseCallIndex <= getOwner().getProvider().getSequenceLength(); baseCallIndex += stepWidth) {
			// Treat possible gaps:
			if ((shiftChange != null) && (baseCallIndex == shiftChange.getBaseCallIndex())) {
				if (shiftChange.getShiftChange() < 0) {  // Deletion in editable sequence
					stepWidth = -shiftChange.getShiftChange() + 1;
					editPosPerBaseCallPos = 1;
				}
				else {  // Insertion in editable sequence
					stepWidth = 1;
					GapPattern gapPattern = getGapPattern(shiftChange);
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
			int endTraceIndex = PherogramUtils.getFirstTracePosition(getOwner().getProvider(), baseCallIndex + stepWidth);
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
  			result.setPaintCenterX(baseCallIndex, baseCallPaintX + 
  					compoundWidth * gapPattern.countGapsBeforeCurveCenter());
  			baseCallPaintX += compoundWidth * gapPattern.getGapCount();
  		}
			baseCallPaintX += 0.5 * baseCallPaintDistance;
			startTraceIndex = endTraceIndex;
		}
  	
  	return result;
  }
}
