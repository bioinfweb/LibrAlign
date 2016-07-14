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
package info.bioinfweb.libralign.pherogram.model;

import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;



/**
 * Event indicating that a cut position of an instance of {@link PherogramComponentModel} was changed.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class PherogramCutPositionChangeEvent extends PherogramModelChangeEvent {
	private int oldBaseCallIndex;
	private int newBaseCallIndex;
	private PherogramAlignmentRelation oldEditableIndex;
	private PherogramAlignmentRelation newEditableIndex;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source the model where the change occurred
	 * @param moreEventsUpcoming Specify {@code true} here if the currently terminated operation is going to
	 *        fire additional events for other affected properties or {@code false} otherwise.
	 * @param oldBaseCallIndex the previous cut position
	 * @param newBaseCallIndex the current (new) cut position
	 * @param oldEditableIndex the relation to the index in the editable sequence corresponding to {@code oldBaseCallIndex}
	 * @param newEditableIndex the relation to the index in the editable sequence corresponding to {@code newBaseCallIndex}
	 */
	public PherogramCutPositionChangeEvent(PherogramComponentModel source, boolean moreEventsUpcoming, int oldBaseCallIndex, int newBaseCallIndex, 
			PherogramAlignmentRelation oldEditableIndex,	PherogramAlignmentRelation newEditableIndex) {
		
		super(source, moreEventsUpcoming);
		this.oldBaseCallIndex = oldBaseCallIndex;
		this.newBaseCallIndex = newBaseCallIndex;
		this.oldEditableIndex = oldEditableIndex;
		this.newEditableIndex = newEditableIndex;
	}


	/**
	 * Returns the previous cut position.
	 * 
	 * @return the base call index of the previous cut position
	 */
	public int getOldBaseCallIndex() {
		return oldBaseCallIndex;
	}


	/**
	 * Returns the current cut position.
	 * 
	 * @return the base call index of the new cut position
	 */
	public int getNewBaseCallIndex() {
		return newBaseCallIndex;
	}


	/**
	 * The relation to the index of the left cut position in the editable sequence associated with the 
	 * displaying {@link PherogramArea}.   
	 * 
	 * @return the relation or {@code null} if {@link #getSource()} is not an instance of {@link PherogramAreaModel}
	 */
	public PherogramAlignmentRelation getOldEditableIndex() {
		return oldEditableIndex;
	}


	/**
	 * The relation to the index of the right cut position in the editable sequence associated with the #
	 * displaying {@link PherogramArea}.   
	 * 
	 * @return the relation or {@code null} if {@link #getSource()} is not an instance of {@link PherogramAreaModel}
	 */
	public PherogramAlignmentRelation getNewEditableIndex() {
		return newEditableIndex;
	}
}
