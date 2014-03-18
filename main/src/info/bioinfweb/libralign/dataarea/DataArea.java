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
import info.bioinfweb.libralign.gui.PaintableArea;



/**
 * All classes representing a data area in an {@link AlignmentArea} should implement this interface.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public interface DataArea extends PaintableArea {
	/**
	 * Return the alignment area that displays this data area.
	 */
	public AlignmentArea getOwner();
	
	/**
	 * Returns the list this data area is contained in. (It could either be located above or underneath 
	 * the alignment or attached to one sequence.)
	 */
	public DataAreaList getList();	

	/**
	 * Updates the list this element is contained in.
	 * @param list - the list that contains this element
	 */
	public void setList(DataAreaList list);
	
	/**
	 * Indicates whether this data area is currently faded in or out in the containing alignment area.
	 * <p>
	 * It does not state whether this data area is contained a area of the alignment area that is 
	 * currently visible on the screen. 
	 * </p>
	 * 
	 * @return {@code true} if this data area is faded in, {@code false} if it is faded out
	 */
	public boolean isVisible();
	
	/**
	 * Fades this data area in or out and lets the containing alignment area repaint, if necessary.
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible);
}
