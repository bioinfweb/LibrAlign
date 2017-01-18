/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.pherogram.distortion;


import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;



/**
 * Represents gaps displayed in a {@link PherogramArea} within the area of one base call.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class GapPattern {
	private boolean[] gapPattern;
	private int gapCount;
	
	
	/**
	 * Creates a new instance of this class initially containing no gaps.
	 * 
	 * @param size - the size the new gap pattern shall have
	 */
	public GapPattern(int size) {
		super();
		gapPattern = new boolean[size];
		for (int i = 0; i < size; i++) {
			gapPattern[i] = false;
		}
	}


	/**
	 * Indicates whether a gap is located at the specified position.
	 * 
	 * @param index - the index in this pattern (the compound index in the editable sequence relative to the start of 
	 *        this gap pattern) 
	 * @return {@code true} if a gap is located here, {@code false} otherwise
	 */
	public boolean isGap(int index) {
		return gapPattern[index];
	}
	
	
	/**
	 * This method allows to define whether a gap is located at the specified position or not. 
	 * 
	 * @param index - the index in this pattern (the compound index in the editable sequence relative to the start of 
	 *        this gap pattern) 
	 * @param gap - Specify {@code true} here if a gap shall be located here, {@code false} otherwise.
	 */
	public void setGap(int index, boolean gap) {
		if (gap && !gapPattern[index]) {
			gapCount++;
		}
		else if (!gap && gapPattern[index]) {
			gapCount--;
		}
		gapPattern[index] = gap;
	}
	
	
	/**
	 * Returns the length of this pattern.
	 * 
	 * @return a value >= 0
	 */
	public int size() {
		return gapPattern.length;
	}
	
	
	/**
	 * Returns the number of gaps contained in this pattern
	 * 
	 * @return a value >= 0
	 */
	public int getGapCount() {
		return gapCount;
	}
	
	
	/**
	 * Counts the number of gaps located before the specified position.
	 * 
	 * @param index - the index in this pattern (the compound index in the editable sequence relative to the start of 
	 *        this gap pattern) 
	 * @return a value >= 0
	 */
	public int countGaps(int index) {
		int result = 0;
		for (int i = 0; i < index; i++) {
			if (isGap(i)) {
				result++;
			}
		}
		return result;
	}

	
	/**
	 * Calculates the number of gaps that are located before the center of the trace curves belonging to the region
	 * this gap pattern belongs to.
	 * 
	 * @return a value >= 0
	 */
	public int countGapsBeforeCurveCenter() {
		int countWOGaps = size() - getGapCount();
		int centerElement = countWOGaps / 2 + countWOGaps % 2;
		int curveElementCount = 0;
		int result = 0;
		for (int i = 0; i < size(); i++) {
			if (isGap(i)) {
				result++;
			}
			else {
				curveElementCount++;
				if (curveElementCount >= centerElement) {
					return result;
				}
			}
		}
		return result;
	}
}