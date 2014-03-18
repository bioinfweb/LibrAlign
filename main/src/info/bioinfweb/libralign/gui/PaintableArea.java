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
package info.bioinfweb.libralign.gui;


import java.awt.geom.Dimension2D;



/**
 * This interface should be implemented by all classes that represent a graphical area in LibrAlign.
 * 
 * @author Ben St&ouml;ver
 */
public interface PaintableArea {
	/**
	 * Painting operations of the implementing class should be performed here. The coordinates in the 
	 * provided context are relative to this are. (0, 0) represents the top left corner of this area.
	 * 
	 * @param graphics - the graphics context used to perform the paint operations in Swing and SWT
	 */
	public void paint(LibrAlignPaintEvent event);
	
	/**
	 * Returns the size this objects uses to be painted completely.
	 * <p>
	 * Note that the associated graphical component might be contained in a scroll container and the
	 * actual area displayed in the screen can be smaller than the dimension returned here.
	 * </p>  
	 * 
	 * @return the dimension in pixels
	 */
	public Dimension2D getSize();
}
