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
 * The shift change list of {@link PherogramAreaModel} may change in different ways depending on the values of {@link #getBaseCallIndex()} and
 * {@link #getShiftChange()}. See {@link PherogramAreaModel#setShiftChange(int, int)} for details.
 * 
 * @author Ben St&ouml;ver
 */
public class PherogramShiftChangeUpdateEvent extends PherogramModelChangeEvent {
	private int baseCallIndex;
	private int shiftChange;
	
	
	public PherogramShiftChangeUpdateEvent(PherogramComponentModel source, boolean moreEventsUpcoming, int baseCallIndex, int shiftChange) {
		super(source, moreEventsUpcoming);
		this.baseCallIndex = baseCallIndex;
		this.shiftChange = shiftChange;
	}


	/**
	 * Returns the first position in the base call sequence affected by the shift change
   * @param shiftChange a positive or negative integer describing the shift change as the number of positions in the editable sequence
	 * 
	 * @return the base call index as passed to {@link PherogramAreaModel#setShiftChange(int, int)} (Note that this method is also called indirectly 
	 *         by other methods like {@link PherogramAreaModel#addShiftChange(int, int)})
	 */
	public int getBaseCallIndex() {
		return baseCallIndex;
	}


	/**
	 * Returns a positive or negative integer describing the shift change as the number of positions in the editable sequence.
	 * 
	 * @return the shift change as passed to {@link PherogramAreaModel#setShiftChange(int, int)} (Note that this method is also called indirectly 
	 *         by other methods like {@link PherogramAreaModel#addShiftChange(int, int)})
	 */
	public int getShiftChange() {
		return shiftChange;
	}
}
