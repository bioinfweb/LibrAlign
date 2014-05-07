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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.bioinfweb.libralign.AlignmentArea;



/**
 * The default implementation of {@link PherogramAlignmentModel}.
 * 
 * @author Ben St&ouml;ver
 */
public class DefaultPherogramAlignmentModel implements PherogramAlignmentModel {
	protected static enum ListEntryType {
		/** Both sequences are aligned onto each other. */
		OVERLAP,
		
		/** An insertion in the aligned sequence (not the base call sequence). */
		INSERTION,

		/** An deletion in the aligned sequence (not the base call sequence). */
    DELETION;
	}
	
	
	protected static class ListEntry {
		public ListEntryType type;
		public int length;

		public ListEntry(ListEntryType type, int length) {
			super();
			this.type = type;
			this.length = length;
		}
	}
	
	
	private int firstVisibleBaseCallIndex = 0;
	private int visibleBaseCallLength = 0;
	private int sequenceStartIndex = 0;
	private List<ListEntry> alignmentList = new ArrayList<ListEntry>();
	
	
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
		if ((baseCallIndex < getFirstVisibleBaseCallIndex()) || 
				(baseCallIndex >= getFirstVisibleBaseCallIndex() + getVisibleBaseCallLength())) {
			
			return OUT_OF_RANGE;
		}
		else {
			int result = getSequenceStartIndex();
//			int 
//      Iterator<ListEntry> iterator = getAlignmentList().iterator();
//      while (iterator.hasNext()) {
//      	ListEntry entry = iterator.next();
//      	switch (entry.type) {
//      		case OVERLAP:
//      			result += entry.length;
//      			break;
//      		case INSERTION:
//      		case DELETION:
//      	}
//      }
			return result;
		}
	}

	
	@Override
	public int baseCallBySequenceIndex(int sequenceIndex) {
		// TODO Auto-generated method stub
		return 0;
	}


	protected List<ListEntry> getAlignmentList() {
		return alignmentList;
	}
}
