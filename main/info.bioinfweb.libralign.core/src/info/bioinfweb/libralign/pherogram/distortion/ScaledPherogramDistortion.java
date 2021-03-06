/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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
 * Stores scale and gap pattern for each base call index of a pherogram. (Used with {@link PherogramArea}).
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class ScaledPherogramDistortion implements PherogramDistortion {
	class BaseCallInfo {
		public double horizontalScale = 0;
		public double paintStartX = 0;
		public double paintCenterX = 0;
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

	
	public void setPaintStartX(int baseCallIndex, double paintX) {
		baseCallInfos[baseCallIndex].paintStartX = paintX;
	}


	@Override
	public double getPaintStartX(int baseCallIndex) {
		return baseCallInfos[baseCallIndex].paintStartX;
	}


	public void setPaintCenterX(int baseCallIndex, double paintX) {
		baseCallInfos[baseCallIndex].paintCenterX = paintX;
	}


	@Override
	public double getPaintCenterX(int baseCallIndex) {
		return baseCallInfos[baseCallIndex].paintCenterX;
	}


	public void setGapPattern(int baseCallIndex, GapPattern pattern) {
		baseCallInfos[baseCallIndex].gapPattern = pattern;
	}

	
	@Override
	public GapPattern getGapPattern(int baseCallIndex) {
		return baseCallInfos[baseCallIndex].gapPattern;
	}
}
