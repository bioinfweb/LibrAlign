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
package info.bioinfweb.libralign.dataarea;


import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.AlignmentSubArea;



/**
 * All classes representing a data area in an {@link AlignmentArea} must be inherited from this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class DataArea extends AlignmentSubArea {
	private DataAreaList list = null;
	private boolean visible = true;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that will contain this instance
	 */
	public DataArea(AlignmentArea owner) {
		super(owner);
	}


	/**
	 * Returns the list this data area is contained in. (It could either be located above or underneath 
	 * the alignment or attached to one sequence.)
	 */
	public DataAreaList getList() {
		return list;
	}


	/**
	 * Updates the list this element is contained in.
	 * @param list - the list that contains this element
	 */
	public void setList(DataAreaList list) {
		this.list = list;
	}


	/**
	 * Indicates whether this data area is currently faded in or out in the containing alignment area.
	 * <p>
	 * It does not state whether this data area is contained a area of the alignment area that is 
	 * currently visible on the screen. 
	 * </p>
	 * 
	 * @return {@code true} if this data area is faded in, {@code false} if it is faded out
	 */
	public boolean isVisible() {
		return visible;
	}


	/**
	 * Fades this data area in or out and lets the containing alignment area repaint, if necessary.
	 * 
	 * @param visible
	 * @return {@code true} if the state of this element was changed, {@code false} otherwise
	 */
	public boolean setVisible(boolean visible) {
		boolean result = this.visible != visible; 
		if (result) {
			this.visible = visible;
			getList().getOwner().fireVisibilityChanged(true, this);  // Will only have an effect is updateInProgress was not set before.
		}
		return result;
	}
}
