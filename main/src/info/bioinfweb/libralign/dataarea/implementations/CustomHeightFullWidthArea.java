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
package info.bioinfweb.libralign.dataarea.implementations;


import info.bioinfweb.libralign.AlignmentContentArea;
import info.bioinfweb.libralign.dataarea.DataArea;



/**
 * Abstract base class for data areas that display information over the whole width of the alignment with
 * a custom height.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public abstract class CustomHeightFullWidthArea extends DataArea {
	private int height;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that is going to contain this data area
	 * @param height - the height of the component in pixels
	 */
	public CustomHeightFullWidthArea(AlignmentContentArea owner, int height) {
		super(owner);
		this.height = height;
	}

	
	@Override
	public int getHeight() {
		return height;
	}


	/**
	 * Sets a new custom height of the area and resizes and repaints the underlying toolkit specific component.
	 * 
	 * @param height - the new component height in pixels
	 */
	public void setHeight(int height) {
		this.height = height;
		assignSize();  //TODO Is the layout in SWT working correctly here?
		repaint();
	}
}
