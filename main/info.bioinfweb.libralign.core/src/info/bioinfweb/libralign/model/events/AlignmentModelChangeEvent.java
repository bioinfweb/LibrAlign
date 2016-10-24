/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
package info.bioinfweb.libralign.model.events;


import info.bioinfweb.libralign.model.AlignmentModel;

import java.util.EventObject;



/**
 * Event object that represents a change in the data provided by an implementation of 
 * {@link AlignmentModel}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * 
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public class AlignmentModelChangeEvent<T> extends EventObject {
	private String sequenceID;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source - the data provider where the change took place
	 * @param sequenceID - the unique ID of the affected sequence
	 */
	public AlignmentModelChangeEvent(AlignmentModel<T> source, String sequenceID) {
		super(source);
		this.sequenceID = sequenceID;
	}

	
	/**
	 * Returns the data provider where the change took place.
	 * 
	 * @return an instance of an implementation of {@link AlignmentModel}
	 */
	@Override
	public AlignmentModel<T> getSource() {
		return (AlignmentModel<T>)super.getSource();
	}


	/**
	 * Returns the unique ID of the affected sequence
	 * 
	 * @return a value greater or equal to zero
	 */
	public String getSequenceID() {
		return sequenceID;
	}
}
