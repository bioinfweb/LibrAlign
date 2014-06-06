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
package info.bioinfweb.libralign.pherogram;


import java.util.ArrayList;
import java.util.List;



/**
 * The default implementation of {@link PherogramAlignmentModel}.
 * 
 * @author Ben St&ouml;ver
 */
public class DefaultPherogramAlignmentModel implements PherogramAlignmentModel {
	private int firstVisibleBaseCallIndex = 0;
	private int visibleBaseCallLength = 0;
	private int sequenceStartIndex = 0;
	//protected List<Integer> sequenceByBaseCallIndexList = new ArrayList<Integer>();
	protected List<Integer> baseCallBySequenceIndexList = new ArrayList<Integer>();
	
	
	private void changeListSizeAtEnd(int dif) {
		if (dif > 0) {  // elongate list
			int value = getSequenceStartIndex();
			if (baseCallBySequenceIndexList.size() > 0) {
				value = baseCallBySequenceIndexList.get(baseCallBySequenceIndexList.size() - 1);
			}
			for (int i = 0; i < dif; i++) {
				baseCallBySequenceIndexList.add(value);
				value++;
			}
		}
		else {  // cut list
			for (int i = 0; i < -dif; i++) {  // does nothing if dif == 0
				baseCallBySequenceIndexList.remove(baseCallBySequenceIndexList.size() - 1);
			}
		}
	}
	
	
	private void shiftList(int shift, int startIndex, int endIndex) {
		if (shift != 0) {
			for (int i = startIndex; i < endIndex; i++) {
				if (baseCallBySequenceIndexList.get(i) >= 0) {  // Leave gap entries unchanged.
					baseCallBySequenceIndexList.set(i, baseCallBySequenceIndexList.get(i) + shift);
				}
			}
		}
	}
	
	@Override
	public int getFirstVisibleBaseCallIndex() {
		return firstVisibleBaseCallIndex;
	}

	
	@Override
	public void setFirstVisibleBaseCallIndex(int baseCallIndex) {
		firstVisibleBaseCallIndex = baseCallIndex;
		//TODO update baseCallBySequenceIndexList
	}

	
	@Override
	public int getVisibleBaseCallLength() {
		return visibleBaseCallLength;
	}

	
	@Override
	public void setVisibleBaseCallLength(int length) {
		changeListSizeAtEnd(length - visibleBaseCallLength);
		visibleBaseCallLength = length;
		//TODO Somewhere the new nucleotides have to be copied. Should the list index changes also be done outside this class then?
	}

	
	@Override
	public int getSequenceStartIndex() {
		return sequenceStartIndex;
	}

	
	@Override
	public void setSequenceStartIndex(int sequenceIndex) {
		sequenceStartIndex = sequenceIndex;
		//TODO update baseCallBySequenceIndexList
	}

	
//	@Override
//	public int sequenceByBaseCallIndex(int baseCallIndex) {
//		if ((baseCallIndex < getFirstVisibleBaseCallIndex()) || 
//				(baseCallIndex >= getFirstVisibleBaseCallIndex() + getVisibleBaseCallLength())) {
//			
//			return OUT_OF_RANGE;
//		}
//		else {
//			return sequenceByBaseCallIndexList.get(baseCallIndex);
//		}
//	}


	@Override
	public int baseCallBySequenceIndex(int sequenceIndex) {
		if ((sequenceIndex < getSequenceStartIndex()) || 
				(sequenceIndex >= getSequenceStartIndex() + baseCallBySequenceIndexList.size())) {
			
			return OUT_OF_RANGE;
		}
		else {
			return baseCallBySequenceIndexList.get(sequenceIndex);
		}
	}

	
	@Override
	public void setSequenceInsertion(int sequenceStart, int length) {
		//TODO Check range
		
		int listIndex = sequenceStart - getSequenceStartIndex();
		int shift = 0;
		for (int i = 0; i < length; i++) {
			if ((listIndex == 0) || (baseCallBySequenceIndexList.get(listIndex) == GAP) ||
					(baseCallBySequenceIndexList.get(listIndex) == baseCallBySequenceIndexList.get(listIndex - 1) + 1)) {
				  // No base call position was skipped.
				
				baseCallBySequenceIndexList.add(listIndex, GAP);  // Time could be saved here, if the underlying array would only be moved once for all upcoming insertions, which is not trivial to implement in this case, even if array list would support it. 
			}
			else {
				shift--;
				baseCallBySequenceIndexList.set(listIndex, baseCallBySequenceIndexList.get(listIndex) + shift);
			}
			listIndex++;
		}
		
		changeListSizeAtEnd(-length);  // Cut off end of list.
		shiftList(shift, listIndex, baseCallBySequenceIndexList.size());  // Shift all indices behind the new gap if necessary.
	}


	@Override
	public void setSequenceDeletion(int sequenceStart, int length) {
		// TODO Auto-generated method stub
		
	}
}
