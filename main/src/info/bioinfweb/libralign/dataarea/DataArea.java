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


import java.awt.Dimension;
import java.util.Set;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelSubArea;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataChangeListener;



/**
 * All classes representing a data area in an {@link AlignmentArea} must be inherited from this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class DataArea extends AlignmentSubArea implements SequenceDataChangeListener {
	private DataAreaList list = null;
	private boolean visible = true;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that will contain this instance
	 */
	public DataArea(AlignmentContentArea owner) {
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
	
	
	/**
	 * The result should enumerate all valid locations where the implementing data area is allowed to be located.
	 * <p>
	 * An implementation for a data area could be located everywhere could look like this:
	 * <pre>return EnumSet.of(DataAreaListType.TOP, DataAreaListType.BOTTOM, DataAreaListType.SEQUENCE);</pre>
	 * 
	 * @return a non-empty set of locations
	 */
	public abstract Set<DataAreaListType> validLocations();
	
	
	/**
	 * Returns the length in pixels considering the current zoom factor this component needs to display
	 * data left of the first alignment column.
	 * <p>
	 * Overwrite this method if your component displays additional information left of the alignment.
	 * 
	 * @return This default implementation always returns 0. 
	 */
	public int getLengthBeforeStart() {
		return 0;
	}
	
	
	/**
	 * Returns the length in pixels considering the current zoom factor starting at the left most alignment column.
	 * 
	 * @return an integer value >= 0
	 */
	public abstract int getLength();
	

	/**
	 * Returns the height in pixels considering the current zoom factor this component needs.
	 * 
	 * @return an integer value > 0
	 */
	public abstract int getHeight();


	/**
	 * Returns the size of the component depending on the return values of {@link #getLength()}, {@link #getHeight()}
	 * and the maximum length before the first alignment position in the associated alignment area. (That means this
	 * method might return a different dimension depending on the {@link AlignmentArea} is it contained in.)
	 * 
	 * @return the (minimal) width and height of this component
	 */
	@Override
	public Dimension getSize() {
		return new Dimension(getOwner().getDataAreas().getGlobalMaxLengthBeforeStart() + getLength(),	getHeight());  
		//TODO Add additional space on the right, depending on the longest component in the alignment area? (Do area components need to have the same width?)
	}
}
