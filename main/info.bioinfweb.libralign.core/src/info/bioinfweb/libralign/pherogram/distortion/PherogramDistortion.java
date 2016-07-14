/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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


import info.bioinfweb.libralign.pherogram.PherogramPainter;



/**
 * Classes to be used with {@link PherogramPainter} the define the distortion of the pherogram at each base call index
 * should implement this interface.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public interface PherogramDistortion {
	/**
	 * Returns the scale (pixels per trace value) in the area surrounding the specified base call.
	 * 
	 * @param baseCallIndex - the index of the base call
	 * @return a floating point value greater than zero
	 */
	public double getHorizontalScale(int baseCallIndex);
	
	/**
	 * Returns the x coordinate relative to the left border of the pherogram where the first trace position of the area associated
	 * with the specified base call index shall be painted.
	 * 
	 * @param baseCallIndex - the index of the base call. 
	 * @return a floating point value greater than or equal to zero
	 */
	public double getPaintStartX(int baseCallIndex);
	
	/**
	 * Returns the x coordinate relative to the left border of the pherogram where the base call with the specified 
	 * index shall be painted. (That would be the center of trace curve area belonging to this base call.)
	 * 
	 * @param baseCallIndex - the index of the base call. 
	 * @return a floating point value greater than or equal to zero
	 */
	public double getPaintCenterX(int baseCallIndex);
	
	/**
	 * A description of gaps in the pherogram in the area surrounding the specified base call.
	 * 
	 * @param baseCallIndex - the index of the base call
	 * @return a gap pattern or {@code null} if no gaps are present in the specified area
	 */
	public GapPattern getGapPattern(int baseCallIndex);
}
