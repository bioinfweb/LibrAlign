/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.alignmentmodel.events;


import info.bioinfweb.libralign.alignmentmodel.AlignmentModel;
import info.bioinfweb.libralign.alignmentmodel.ConcatenatedAlignmentModel;

import java.util.EventObject;



/**
 * Event object that represents a change in the data provided by an implementation of 
 * {@link AlignmentModel}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class ConcatenatedModelChangeEvent extends EventObject {
	private int sequenceID;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source - the data provider where the change took place
	 * @param sequenceID - the unique ID of the affected sequence
	 */
	public ConcatenatedModelChangeEvent(ConcatenatedAlignmentModel source, int sequenceID) {
		super(source);
		this.sequenceID = sequenceID;
	}

	
	/**
	 * Returns the concatenated alignment model where the change took place.
	 * 
	 * @return the concatenated alignment model
	 */
	@Override
	public ConcatenatedAlignmentModel getSource() {
		return (ConcatenatedAlignmentModel)super.getSource();
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
