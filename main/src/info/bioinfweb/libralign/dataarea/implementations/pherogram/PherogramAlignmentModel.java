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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.bioinfweb.commons.Math2;



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
  				1 - getOwner().getLeftCutPosition() + getOwner().getFirstSeqPos());
  	}
  	else if (baseCallIndex > getOwner().getProvider().getSequenceLength()) {
  		return new PherogramAlignmentRelation(
  				editableIndexByBaseCallIndex(getOwner().getProvider().getSequenceLength()).getCorresponding(), 
  				PherogramAlignmentRelation.OUT_OF_RANGE, PherogramAlignmentRelation.OUT_OF_RANGE);
  	}
    else {
    	int resultPos = baseCallIndex - getOwner().getLeftCutPosition() + getOwner().getFirstSeqPos();
    	
    	if (!shiftChangeList.isEmpty()) {
      	Iterator<ShiftChange> iterator = shiftChangeList.iterator();
      	while (iterator.hasNext()) {
      		ShiftChange shiftChangeEntry = iterator.next();
      		if ((shiftChangeEntry.shiftChange < 0) && (Math2.isBetween(baseCallIndex, 
      				shiftChangeEntry.baseCallIndex, shiftChangeEntry.baseCallIndex - shiftChangeEntry.shiftChange - 1))) {
      			
      			resultPos -= baseCallIndex - shiftChangeEntry.baseCallIndex;
      			return new PherogramAlignmentRelation(resultPos - 1, PherogramAlignmentRelation.GAP,	resultPos);
      		}
      		else if ((shiftChangeEntry.baseCallIndex <= baseCallIndex)) {
      			resultPos += shiftChangeEntry.shiftChange;
      		}
      		else {
      			return new PherogramAlignmentRelation(resultPos, resultPos, resultPos);
      		}
      	}
    	}
			return new PherogramAlignmentRelation(resultPos, resultPos, resultPos);
  	}
  }
  
  
  public PherogramAlignmentRelation baseCallIndexByEditableIndex(int editableIndex) {
  	int resultPos = editableIndex - getOwner().getFirstSeqPos() + getOwner().getLeftCutPosition();
  	
  	if (!shiftChangeList.isEmpty()) {
    	Iterator<ShiftChange> iterator = shiftChangeList.iterator();
    	while (iterator.hasNext()) {
    		ShiftChange shiftChangeEntry = iterator.next();
    		if ((shiftChangeEntry.shiftChange > 0) && (Math2.isBetween(resultPos, 
    				shiftChangeEntry.baseCallIndex, shiftChangeEntry.baseCallIndex + shiftChangeEntry.shiftChange - 1))) {
    			
    			return new PherogramAlignmentRelation(shiftChangeEntry.baseCallIndex - 1, PherogramAlignmentRelation.GAP,	
    					shiftChangeEntry.baseCallIndex);
    		}
    		else if ((shiftChangeEntry.baseCallIndex <= resultPos)) {
    			resultPos -= shiftChangeEntry.shiftChange;
    		}
    		else {
    			break;  // End loop and go on with range check. 
    		}
    	}
  	}
  	if (resultPos < 1) {
  		return new PherogramAlignmentRelation(PherogramAlignmentRelation.OUT_OF_RANGE, PherogramAlignmentRelation.OUT_OF_RANGE, 1);
  	}
  	else if (resultPos > getOwner().getProvider().getSequenceLength()) {
  		return new PherogramAlignmentRelation(getOwner().getProvider().getSequenceLength(), 
  				PherogramAlignmentRelation.OUT_OF_RANGE, PherogramAlignmentRelation.OUT_OF_RANGE);
  	}
  	else {
  		return new PherogramAlignmentRelation(resultPos, resultPos, resultPos);
  	}
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
  
  
  public Iterator<ShiftChange> shiftChangeIterator() {
  	final Iterator<ShiftChange> iterator = shiftChangeList.iterator();
  	return new Iterator<ShiftChange>() {
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
				//TODO Possibly delegate to interator in future versions and fire according events.
			}
		};
  }
}
