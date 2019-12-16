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
package info.bioinfweb.libralign.model.io;



/**
 * Stores information to access a data element that has been read from a data source (e.g. a file). 
 * This information includes the alignment model and the sequence its data is possibly associated with.
 * 
 * @author Ben St&ouml;ver
 * @since 0.6.0
 * @bioinfweb.module info.bioinfweb.libralign.io
 */
public class DataElementKey {
	private String alignmentID;
	private String sequenceID;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param alignmentID the ID of the alignment model the read data is associated with (Can be 
	 *        {@code null}.)
	 * @param sequenceID the ID of the sequence in the alignment model the read data is associated with 
	 *        (Can be {@code null}.)
	 * @throws IllegalArgumentException if {@code null} was specified as the alignment model ID, but 
	 *         still a sequence ID (which is not {@code null}) was specified.
	 */
	public DataElementKey(String alignmentID,	String sequenceID) {
		super();
		if ((alignmentID == null) && (sequenceID != null)) {
			throw new IllegalArgumentException("A sequence ID can only be specified, if an alignment model ID was specified as well.");
		}
		else {
			this.alignmentID = alignmentID;
			this.sequenceID = sequenceID;
		}
	}


	/**
	 * Creates a new instance of this class with no sequence ID.
	 * 
	 * @param alignmentModel the alignment model ID the read data is associated with (Can be {@code null}.)
	 * @throws IllegalArgumentException if {@code null} was specified as the alignment model ID, but still 
	 *         a sequence ID (which is not {@code null}) was specified.
	 */
	public DataElementKey(String alignmentID) {
		this(alignmentID, null);
	}
	
	
	public String getAlignmentID() {
		return alignmentID;
	}


	public String getSequenceID() {
		return sequenceID;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((alignmentID == null) ? 0 : alignmentID.hashCode());
		result = prime * result
				+ ((sequenceID == null) ? 0 : sequenceID.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataElementKey other = (DataElementKey) obj;
		if (alignmentID == null) {
			if (other.alignmentID != null)
				return false;
		} else if (!alignmentID.equals(other.alignmentID))
			return false;
		if (sequenceID == null) {
			if (other.sequenceID != null)
				return false;
		} else if (!sequenceID.equals(other.sequenceID))
			return false;
		return true;
	}
}
