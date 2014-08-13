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
 * Stores scales and gap pattern for each base call index of a pherogram. (Used with {@link PherogramArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class ScaledPherogramDistortion implements PherogramDistortion {
	class BaseCallInfo {
		public double horizontalScale = 0;
		public double paintX = 0;
		public GapPattern gapPattern = null;
	}
	
	
	private BaseCallInfo[] baseCallInfos;
  
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param size - the number of values to be stored (should be equal to the length of the associated pherogram)
	 */
	public ScaledPherogramDistortion(int size) {
		super();
		baseCallInfos = new BaseCallInfo[size];
		for (int i = 0; i < size; i++) {
			baseCallInfos[i] = new BaseCallInfo();
		}
	}


	public void setHorizontalScale(int baseCallIndex, double scale) {
		baseCallInfos[baseCallIndex].horizontalScale = scale; 
	}


	@Override
	public double getHorizontalScale(int baseCallIndex) {
		return baseCallInfos[baseCallIndex].horizontalScale;
	}

	
	public void setPaintX(int baseCallIndex, double paintX) {
		baseCallInfos[baseCallIndex].paintX = paintX;
	}


	@Override
	public double getPaintX(int baseCallIndex) {
		return baseCallInfos[baseCallIndex].paintX;
	}


	public void setGapPattern(int baseCallIndex, GapPattern pattern) {
		baseCallInfos[baseCallIndex].gapPattern = pattern;
	}

	
	@Override
	public GapPattern getGapPattern(int baseCallIndex) {
		return baseCallInfos[baseCallIndex].gapPattern;
	}
}
