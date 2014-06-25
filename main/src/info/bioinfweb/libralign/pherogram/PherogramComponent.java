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
package info.bioinfweb.libralign.pherogram;


import java.awt.Color;



/**
 * This interface should be implemented by all GUI classes displaying a pherogram (trace file from Sanger
 * sequencing).
 * 
 * @author Ben St&ouml;ver
 */
public interface PherogramComponent {
	/**
	 * Returns the underlying class providing the pherogram data.
	 * 
	 * @return the underlying implementation of {@link PherogramProvider}
	 */
	public PherogramProvider getProvider();
	
	/**
	 * Returns the horizontal zoom factor that is currently used for displaying the trace curves.
	 * 
	 * @return a floating point value greater than zero
	 */
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

	/**
	 * Sets a new format object that is used to paint the displayed pherogram.
	 * 
	 * @param colorSchema - the new color schema to be used
	 * @see #setBackgroundColor(Color)
	 * @see #setBaseCallLineColor(Color)
	 */
	public void setFormats(PherogramFormats layout);
	
	/**
	 * Returns the first base call index of the pherogram which has not been cut off.
	 * 
	 * @return a base call index > 0
	 */
	public int getLeftCutPosition();
	
	/**
	 * Sets a new cut position for the left border of the visible part of the pherogram.
	 * 
	 * @param baseCallIndex - the index of the first nucleotide in the base call sequence that shall be visible
	 */
	public void setLeftCutPosition(int baseCallIndex);
	
	/**
	 * Returns the first base call index of the pherogram that has been cut off (so that the length of the visible
	 * area of the pherogram can be calculated as {@code getRightCutPosition() - }{@link #getLeftCutPosition()}).
	 * 
	 * @return a base call index >= {@link #getLeftCutPosition()}
	 */
	public int getRightCutPosition();
	
	/**
	 * Sets a new cut position for the right border of the visible part of the pherogram.
	 * 
	 * @param baseCallIndex - the index of the first nucleotide after the visible part in the base call sequence
	 */
	public void setRightCutPosition(int baseCallIndex);
}