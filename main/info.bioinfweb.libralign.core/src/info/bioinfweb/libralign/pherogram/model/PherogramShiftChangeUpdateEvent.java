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
package info.bioinfweb.libralign.pherogram.model;



/**
 * Event object describing a change in the alignment between the editable sequence in a matrix and the associated base call sequence of a pherogram.
 * <p>
 * Note that this event indicates that the a shift stored in {@link PherogramAreaModel was increased or decreased at the specified position by a call 
 * of {@link PherogramAreaModel#addShiftChange(int, int)}. It does not contain any information on absolute shift values, but only on relative changes.
 * <p>
 * The shift change list used internally by {@link PherogramAreaModel} may change in different ways depending on the values of {@link #getBaseCallIndex()} 
 * and {@link #getShiftChange()}, also at multiple positions at a time. These are internal implementation-related operations and such changes of absolute 
 * values are not necessarily indicated by this event. (See {@link PherogramAreaModel#setShiftChange(int, int)} for details.) This event only indicates
 * relative changes triggered by calls of {@link PherogramAreaModel#addShiftChange(int, int)}, which are relevant for the application.
 * 
 * @author Ben St&ouml;ver
 */
public class PherogramShiftChangeUpdateEvent extends PherogramModelChangeEvent {
	private int baseCallIndex;
	private int relativeShiftChange;
	
	
	public PherogramShiftChangeUpdateEvent(PherogramComponentModel source, boolean moreEventsUpcoming, int baseCallIndex, int relativeShiftChange) {
		super(source, moreEventsUpcoming);
		this.baseCallIndex = baseCallIndex;
		this.relativeShiftChange = relativeShiftChange;
	}


	/**
	 * Returns the first position in the base call sequence affected by the shift change
	 * 
	 * @return the base call index of the shift change
	 */
	public int getBaseCallIndex() {
		return baseCallIndex;
	}


	/**
	 * Returns a positive or negative integer describing the shift change as the number of positions in the editable sequence.
	 * <p>
	 * This value only describes the shift change at the current base call index position in this operation and does not reflect
	 * the absolute shift change value that may be stored internally. 
	 * 
	 * @return the shift change after the edit
	 */
	public int getRelativeShiftChange() {
		return relativeShiftChange;
	}
}
