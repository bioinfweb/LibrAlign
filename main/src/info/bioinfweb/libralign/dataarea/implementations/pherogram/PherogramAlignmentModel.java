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
  /**
   * This value returned as an index indicates that there is a gap in the associated sequence.
   */
  public static final int GAP = -1;
  
  /**
   * This value returned as an index indicates that the position specified to obtain lies outside
   * the parts of the two sequences that are aligned with each other.
   */
  public static final int OUT_OF_RANGE = -2;
  
  
	private static class ShiftChange {
		public int baseCallIndex = 0;
		public int shiftChange = 0;

	
		public ShiftChange(int baseCallIndex, int shiftChange) {
			super();
			this.baseCallIndex = baseCallIndex;
			this.shiftChange = shiftChange;
		}		
	}
	
	
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
   *         lies outside the area of the pherogram that is represented by the associated editable sequence
   */
  public int editableIndexByBaseCallIndex(int baseCallIndex) {
  	if (Math2.isBetween(baseCallIndex, getOwner().getLeftCutPosition(), getOwner().getRightCutPosition() - 1)) {
    	int result = baseCallIndex - getOwner().getLeftCutPosition() + getOwner().getFirstSeqPos();
    	
    	if (!shiftChangeList.isEmpty()) {
      	Iterator<ShiftChange> iterator = shiftChangeList.iterator();
      	while (iterator.hasNext()) {
      		ShiftChange shiftChangeEntry = iterator.next();
      		if ((shiftChangeEntry.shiftChange < 0) && (Math2.isBetween(baseCallIndex, 
      				shiftChangeEntry.baseCallIndex, shiftChangeEntry.baseCallIndex - shiftChangeEntry.shiftChange - 1))) {
      			
      			return GAP;
      		}
      		else if ((shiftChangeEntry.baseCallIndex <= baseCallIndex)) {
      			result += shiftChangeEntry.shiftChange;
      		}
      		else {
      			return result;
      		}
      	}
    	}
  		return result;
  	}
  	else {
  		return OUT_OF_RANGE;
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
}
