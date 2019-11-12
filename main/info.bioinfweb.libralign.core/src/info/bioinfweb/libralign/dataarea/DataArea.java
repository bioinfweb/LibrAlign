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


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.SizeManager;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelChangeListener;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;

import java.util.Set;



/**
 * All classes representing a data area in an {@link AlignmentArea} must be inherited from this class.
 * <p>
 * For performance reasons the space possibly required by data areas left and right of the aligned sequences is stored and not recalculated in every access.
 * Data area implementations must make sure to inform the {@link SizeManager} instance of their owning {@link AlignmentArea} (accessible via 
 * {@link AlignmentArea#getSizeManager()}) when recalculation becomes necessary by calling {@link SizeManager#setLocalMaxLengthBeforeAfterRecalculate()}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class DataArea extends AlignmentSubArea implements AlignmentModelChangeListener {
	private AlignmentArea labeledAlignmentArea;
	private DataAreaList list = null;
	private boolean visible = true;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner the alignment area that will contain this instance
	 * @param labeledArea the alignment area displays the sequence which is labeled by the new instance
	 *        (If {@code null} is specified here, the parent alignment area of {@code owner} will be assumed.)  
	 */
	public DataArea(AlignmentContentArea owner, AlignmentArea labeledArea) {
		super(owner);
		if (labeledArea == null) {
			this.labeledAlignmentArea = owner.getOwner();
		}
		else {
			this.labeledAlignmentArea = labeledArea;
		}
	}
	

	/**
	 * Returns the alignment area that is labeled by this data area.
	 * <p>
	 * Note that inside a {@link MultipleAlignmentsContainer} a data area could be located in a different 
	 * alignment area than the related alignment data. Therefore the returned instance is not necessarily 
	 * identical with the instance returned by {@code getOwner().getOwner()}. 
	 * 
	 * @return the labeled alignment area
	 * @see #getLabeledAlignmentModel()
	 */
	public AlignmentArea getLabeledAlignmentArea() {
		return labeledAlignmentArea;
	}

	
	/**
	 * Convenience method that returns the alignment model of the labeled alignment area.
	 * 
	 * @return the alignment model providing the sequence data related to the contents of this data area
	 * @see #getLabeledAlignmentArea()
	 */
	public AlignmentModel<?> getLabeledAlignmentModel() {
		return getLabeledAlignmentArea().getAlignmentModel();
	}
	

	/**
	 * Returns the list this data area is contained in. This list defines whether this data area is located above 
	 * or underneath the alignment or attached to a sequence.
	 */
	public DataAreaList getList() {
		return list;
	}


	/**
	 * Updates the list this element is contained in.
	 * 
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
			getList().getOwner().fireVisibilityChanged(true, this);  // Will only have an effect if updateInProgress was not set before.
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
