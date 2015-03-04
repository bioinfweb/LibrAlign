/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.dataarea.implementations.pherogram;


import java.util.ListIterator;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;



/**
 * A bean class storing the base call sequence position corresponding to editable sequence position or the other way
 * around in an alignment between the base call sequence from Sanger sequencing and the corresponding editable sequence
 * in an {@link AlignmentArea}. 
 * 
 * @author Ben St&ouml;ver
 *
 */
public class PherogramAlignmentRelation {
  /**
   * This value returned as an index indicates that there is a gap in the associated sequence.
   */
  public static final int GAP = -1;
  
  /**
   * This value returned as an index indicates that the position specified to obtain lies outside
   * the parts of the two sequences that are aligned with each other.
   */
  public static final int OUT_OF_RANGE = -2;
  
  
  private int before = OUT_OF_RANGE;	
  private int corresponding = OUT_OF_RANGE;	
  private int after = OUT_OF_RANGE;
	private ListIterator<ShiftChange> iterator;
  
  
	public PherogramAlignmentRelation() {
		super();
	}


	public PherogramAlignmentRelation(int before, int corresponding, int after, ListIterator<ShiftChange> iterator) {
		super();
		this.before = before;
		this.corresponding = corresponding;
		this.after = after;
		this.iterator = iterator;
	}


	/**
	 * This value is only needed if {@link #getCorresponding()} returns {@link #GAP} or {@link #OUT_OF_RANGE}. In that case
	 * it returns the first index in the corresponding sequence that is aligned with a position in the original sequence, that
	 * is located before the specified position of the original sequence that produced this result.
	 * 
	 * @return a valid index or {@link #OUT_OF_RANGE} but never {@link #GAP}
	 */
	public int getBefore() {
		return before;
	}
	
	
	/**
	 * Returns the position corresponding to the one specified to obtain this result object. 
	 * 
	 * @return a valid index or {@link #GAP} if the specified index producing this result object is aligned with a 
	 *         gap in the other sequence or {@link #OUT_OF_RANGE} if the specified index lies outside the aligned area 
	 */
	public int getCorresponding() {
		return corresponding;
	}
	
	
	/**
	 * This value is only needed if {@link #getCorresponding()} returns {@link #GAP} or {@link #OUT_OF_RANGE}. In that case
	 * it returns the first index in the corresponding sequence that is aligned with a position in the original sequence, that
	 * is located after the specified position of the original sequence that produced this result.
	 * 
	 * @return a valid index or {@link #OUT_OF_RANGE} but never {@link #GAP}
	 */
	public int getAfter() {
		return after;
	}
	
	
	/**
	 * Returns an iterator positioned before the next gap (shift change entry). 
	 */
	public ListIterator<ShiftChange> getIterator() {
		return iterator;
	}	
}
