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
package info.bioinfweb.libralign.sequenceprovider.events;


import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;

import java.util.EventObject;



/**
 * Event object that represents a change in the data provided by an implementation of 
 * {@link SequenceDataProvider}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * 
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public class SequenceDataProviderChangeEvent<T> extends EventObject {
	private int sequenceID;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source - the data provider where the change took place
	 * @param sequenceID - the unique ID of the affected sequence
	 */
	public SequenceDataProviderChangeEvent(SequenceDataProvider<T> source, int sequenceID) {
		super(source);
		this.sequenceID = sequenceID;
	}

	
	/**
	 * Returns the data provider where the change took place.
	 * 
	 * @return an instance of an implementation of {@link SequenceDataProvider}
	 */
	@Override
	public SequenceDataProvider<T> getSource() {
		return (SequenceDataProvider<T>)super.getSource();
	}


	/**
	 * Returns the unique ID of the affected sequence
	 * 
	 * @return a value greater or equal to zero
	 */
	public int getSequenceID() {
		return sequenceID;
	}
}
