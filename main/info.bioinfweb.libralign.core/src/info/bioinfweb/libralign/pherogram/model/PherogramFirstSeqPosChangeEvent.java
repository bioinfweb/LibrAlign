/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
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



/**
 * Event that indicates that {@link PherogramAreaModel#getFirstSeqPos()} was changed.
 * <p>
 * Note that this event will only be fired by instances of {@link PherogramAreaModel} and never
 * by an instance of {@link PherogramComponentModel}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class PherogramFirstSeqPosChangeEvent extends PherogramModelChangeEvent {
	private int oldPosition;
	private int newPosition;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source the model where the change occurred
	 * @param moreEventsUpcoming Specify {@code true} here if the currently terminated operation is going to
	 *        fire additional events for other affected properties or {@code false} otherwise.
	 * @param oldPosition the previous position in the editable sequence where the pherogram was anchored
	 * @param newPosition the current (new) position in the editable sequence where the pherogram is anchored
	 */
	public PherogramFirstSeqPosChangeEvent(PherogramComponentModel source, boolean moreEventsUpcoming, 
			int oldPosition, int newPosition) {
		
		super(source, moreEventsUpcoming);
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}


	/**
	 * Returns the previous position in the editable sequence where the pherogram was anchored.
	 * 
	 * @return the previous first sequence position
	 */
	public int getOldPosition() {
		return oldPosition;
	}
	
	
	/**
	 * Returns the current position in the editable sequence where the pherogram is anchored.
	 * 
	 * @return the new first sequence position
	 */
	public int getNewPosition() {
		return newPosition;
	}
}
