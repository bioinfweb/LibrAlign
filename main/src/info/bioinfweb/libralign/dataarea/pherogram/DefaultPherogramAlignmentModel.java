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
package info.bioinfweb.libralign.dataarea.pherogram;


import info.bioinfweb.commons.collections.NonOverlappingIntervalList;
import info.bioinfweb.libralign.AlignmentArea;



/**
 * The default implementation of {@link PherogramAlignmentModel}.
 * 
 * @author Ben St&ouml;ver
 */
public class DefaultPherogramAlignmentModel implements PherogramAlignmentModel {
	private int firstVisibleBaseCallIndex = 0;
	private int visibleBaseCallLength = 0;
	private int sequenceStartIndex = 0;
	private NonOverlappingIntervalList sequenceGaps = new NonOverlappingIntervalList();
	private NonOverlappingIntervalList baseCallGaps = new NonOverlappingIntervalList();
	
	
	@Override
	public int getFirstVisibleBaseCallIndex() {
		return firstVisibleBaseCallIndex;
	}

	
	@Override
	public void setFirstVisibleBaseCallIndex(int baseCallIndex) {
		firstVisibleBaseCallIndex = baseCallIndex;
	}

	
	@Override
	public int getVisibleBaseCallLength() {
		return visibleBaseCallLength;
	}

	
	@Override
	public void setVisibleBaseCallLength(int length) {
		visibleBaseCallLength = length;
	}

	
	@Override
	public int getSequenceStartIndex() {
		return sequenceStartIndex;
	}

	
	@Override
	public void setSequenceStartIndex(int sequenceIndex) {
		sequenceStartIndex = sequenceIndex;
	}

	
	@Override
	public int sequenceByBaseCallIndex(int baseCallIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public int baseCallBySequenceIndex(int sequenceIndex) {
		// TODO Auto-generated method stub
		return 0;
	}


	/**
	 * Returns the list of gaps inserted in the alignment sequence (this pherogram is attached to inside the
	 * according {@link AlignmentArea}) in order to align it to the base call sequence of this pherogram.
	 * <p>
	 * Note that these gaps are different from the gaps already contained in the alignment sequence which 
	 * align it to other sequences in its {@link AlignmentArea}.
	 * 
	 * @return an editable list of gaps
	 */
	public NonOverlappingIntervalList getSequenceGaps() {
		return sequenceGaps;
	}

	
	/**
	 * Returns the list of gaps inserted in base call sequence in order to align it to the alignment sequence 
	 * (this pherogram is attached to inside the according {@link AlignmentArea}).
	 * 
	 * @return an editable list of gaps
	 */
	public NonOverlappingIntervalList getBaseCallGaps() {
		return baseCallGaps;
	}
}
