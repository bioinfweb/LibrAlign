/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
	
	
	public GapPattern(int size) {
		super();
		gapPattern = new boolean[size];
		for (int i = 0; i < size; i++) {
			gapPattern[i] = false;
		}
	}


	public boolean isGap(int index) {
		return gapPattern[index];
	}
	
	
	public void setGap(int index, boolean gap) {
		if (gap && !gapPattern[index]) {
			gapCount++;
		}
		else if (!gap && gapPattern[index]) {
			gapCount--;
		}
		gapPattern[index] = gap;
	}
	
	
	public int size() {
		return gapPattern.length;
	}
	
	
	public int getGapCount() {
		return gapCount;
	}	
}