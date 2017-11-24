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
package info.bioinfweb.libralign.dataarea.implementations.charset.events;


import info.bioinfweb.libralign.dataarea.implementations.charset.CharSet;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetDataModel;

import javax.swing.event.ChangeEvent;



/**
 * This is the base class of all events providing information on content changes changes
 * of an instance of {@link CharSetDataModel}. Concrete events will always be of classes
 * inherited from this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.6.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class CharSetDataModelChangeEvent extends ChangeEvent {
	private boolean lastEvent;
	private String charSetID;
	private CharSet charSet;

	
	public CharSetDataModelChangeEvent(CharSetDataModel source, boolean lastEvent, String charSetID, CharSet charSet) {
		super(source);
		
		if (charSetID == null) {
			throw new IllegalArgumentException("The character set ID must not be null.");
		}
		else if (charSet == null) {
			throw new IllegalArgumentException("The character set must not be null.");
		}
		else {
			this.lastEvent = lastEvent;
			this.charSetID = charSetID;
			this.charSet = charSet;
		}
	}


	@Override
	public CharSetDataModel getSource() {
		return (CharSetDataModel)super.getSource();
	}


	/**
	 * Determines whether this event is the last in a possible row of events triggered by a single operation.
	 * <p>
	 * {@link CharSetDataModel#clear()} will e.g. trigger one event for each character set that is removed.
	 * Listeners that only need to react to that change once, may only react to the event which stores
	 * {@code true} for this property.
	 * 
	 * @return {@code true} if this event is the last or {@code false} if additional events will follow as a 
	 *         result of the same operation
	 */
	public boolean isLastEvent() {
		return lastEvent;
	}


	/**
	 * Returns the ID of the affected character set used by the associated model.
	 * 
	 * @return the ID of the character set, never {@code null}
	 */
	public String getCharSetID() {
		return charSetID;
	}


	public CharSet getCharSet() {
		return charSet;
	}
}
