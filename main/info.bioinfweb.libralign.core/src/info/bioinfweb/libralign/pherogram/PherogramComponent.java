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
package info.bioinfweb.libralign.pherogram;


import info.bioinfweb.libralign.pherogram.model.PherogramComponentModel;

import java.awt.Color;



/**
 * This interface should be implemented by all GUI classes displaying a pherogram (trace file from Sanger
 * sequencing).
 * 
 * @author Ben St&ouml;ver
 */
public interface PherogramComponent {
	/**
	 * Returns the model providing the base call sequence, the trace curves and the cut positions.
	 * 
	 * @return the model instance or {@code null} if not model has been specified yet
	 */
	public PherogramComponentModel getModel();

//	/**
//	 * Returns if the vertical scale is calculated automatically depending on the currently visible part
//	 * of the trace curve.
//	 * 
//	 * @return {@code true} if the scale is calculated automatically depending on the currently visible part,
//	 *         {@code false} if a constant value independent from the current view is returned by 
//	 *         {@link #getVerticalScale(int, int)}. 
//	 */
//	public boolean isAutoVerticalScale();
//	
//	/**
//	 * Allows to specify if the vertical scale shall be calculated automatically depending on the currently 
//	 * visible part of the trace curve or a constant value should be used.
//	 * 
//	 * @param value - Specify {@code true} here, if the scale shall be calculated view dependent, or {@code false}
//	 *        to use a constant value.
//	 */
//	public void setAutoVerticalScale(boolean value);
	
//	/**
//	 * Returns the horizontal zoom factor that is currently used for displaying the trace curves. The values
//	 * of the parameters {@code firstBaseCallIndex} and {@code lastBaseCallIndex} are only used if
//	 * {@link PherogramComponent#isAutoVerticalScale()} is {@code true}.
//	 * 
//	 * @param firstBaseCallIndex - the index of the first base call position that is currently displayed
//	 * @param lastBaseCallIndex - the index of the first base call position that is currently displayed
//	 * @return a floating point value greater than zero
//	 */
//	public double getVerticalScale(int firstBaseCallIndex, int lastBaseCallIndex);
	public double getVerticalScale();
	
	/**
	 * Sets the horizontal zoom factor that shall used for displaying the trace curves.
	 * 
	 * @param value - the new zoom factor (must be greater than zero)
	 */
	public void setVerticalScale(double value);
	
	/**
	 * Returns the format object that is used to paint the displayed pherogram.
	 * 
	 * @return the layout object containing color and font definitions
	 */
	public PherogramFormats getFormats();
}