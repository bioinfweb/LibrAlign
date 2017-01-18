/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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


import javax.swing.event.ChangeEvent;



/**
 * All events indicating changes in an instance of {@link PherogramComponentModel} should be inherited
 * from this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class PherogramModelChangeEvent extends ChangeEvent {
	private boolean moreEventsUpcoming = false;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source the model where the change occurred
	 * @param moreEventsUpcoming Specify {@code true} here if the currently terminated operation is going to
	 *        fire additional events for other affected properties or {@code false} otherwise.
	 */ 
	public PherogramModelChangeEvent(PherogramComponentModel source,	boolean moreEventsUpcoming) {
		super(source);
		this.moreEventsUpcoming = moreEventsUpcoming;
	}


	@Override
	public PherogramComponentModel getSource() {
		return (PherogramComponentModel)super.getSource();
	}


	/**
	 * Indicates whether the currently terminated operation affected additional properties and is going to fire 
	 * more events for these right after this event. This property can e.g. be used to avoid unnecessary sequences
	 * of repaint operations.
	 * <p>
	 * Not that is not guaranteed that this flag is set to {@code true} in every case where an additional event
	 * is upcoming, although it is guaranteed that another event is coming, if this property is set to {@code true}.
	 * 
	 * @return {@code true} if more events resulting from the same operation are waiting to be fired or {@code false}
	 *         if the current operation resulted only in one element or the element is the last element in such a
	 *         sequence
	 */
	public boolean isMoreEventsUpcoming() {
		return moreEventsUpcoming;
	}
}
