/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.dataarea;


import java.util.Set;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.SizeManager;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;
import info.bioinfweb.libralign.dataelement.DataList;
import info.bioinfweb.libralign.dataelement.DataListType;



/**
 * All classes representing a data area in an {@link AlignmentArea} must be inherited from this class.
 * <p>
 * For performance reasons the space possibly required by data areas left and right of the aligned sequences is stored and not recalculated in every access.
 * Data area implementations must make sure to inform the {@link SizeManager} instance of their owning {@link AlignmentArea} (accessible via 
 * {@link AlignmentArea#getSizeManager()}) when recalculation becomes necessary by calling {@link SizeManager#setLocalMaxLengthBeforeAfterRecalculate()}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public abstract class DataArea extends AlignmentSubArea {
	private DataList<AlignmentArea, DataArea> list = null;
	private boolean visible = true;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner the alignment area that will contain this instance
	 * @param labeledArea the alignment area displays the sequence which is labeled by the new instance
	 *        (If {@code null} is specified here, the parent alignment area of {@code owner} will be assumed.)  
	 */
	public DataArea(AlignmentArea owner) {
		super(owner);
	}
	

	/**
	 * Returns the list this data area is contained in. This list defines whether this data area is located above 
	 * or underneath the alignment or attached to a sequence.
	 */
	public DataList<AlignmentArea, DataArea> getList() {
		return list;
	}


	/**
	 * Updates the list this element is contained in.
	 * 
	 * @param list - the list that contains this element
	 */
	public void setList(DataList<AlignmentArea, DataArea> list) { 
		this.list = list;
	}


	/**
	 * Indicates whether this data area is currently faded in or out in the containing alignment area.
	 * <p>
	 * It does not state whether this data area is contained a area of the alignment area that is 
	 * currently visible on the screen. 
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
			getList().getOwner().getOwner().fireDataAreaVisibilitChanged(this, visible);
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
	public abstract Set<DataListType> validLocations();
	
	
	/**
	 * Returns the length in pixels considering the current zoom factor this component needs to display
	 * data left of the first alignment column.
	 * <p>
	 * Overwrite this method if your component displays additional information left of the alignment.
	 * 
	 * @return This default implementation always returns 0. 
	 */
	public double getLengthBeforeStart() {
		return 0;
	}
	
	
	/**
	 * Returns the length in pixels considering the current zoom factor this component needs to display
	 * data right of the last alignment column.
	 * <p>
	 * Overwrite this method if your component displays additional information right of the alignment.
	 * 
	 * @return This default implementation always returns 0. 
	 */
	public double getLengthAfterEnd() {
		return 0;
	}
}
