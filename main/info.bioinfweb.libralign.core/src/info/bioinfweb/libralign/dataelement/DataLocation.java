/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.dataelement;

import info.bioinfweb.libralign.dataarea.DataArea;

/**
 * Specified the location of a {@link DataArea}. That is either above or below the alignment
 * or attached to a certain sequence.   
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class DataLocation {
  private DataListType listType;
  private String sequenceID;


	/**
	 * Creates a new instance of this class specifying locations above or underneath the alignment.
	 * 
	 * @param listType - specifies if list represents data areas displayed above
	 *        or underneath the alignment
	 *        
	 * @throws IllegalArgumentException - if {@link DataListType#SEQUENCE} is specified as {@code listType}
	 */
	public DataLocation(DataListType listType) {
		super();
		if (listType.equals(DataListType.SEQUENCE)) {
			throw new IllegalArgumentException("The type " + DataListType.SEQUENCE + 
					" cannot be used if no sequence name is specified.");
		}
		else {
			this.listType = listType;
			sequenceID = null;
		}
	}

	
	/**
	 * Creates a new instance of this class specifying a location attached to one
	 * sequence of the alignment.
	 * <p>
	 * The list type is automatically set to {@link DataListType#SEQUENCE}.
	 * </p>
	 * 
	 * @param sequenceID - the unique identifier of the sequence the contained data areas will be attached to
	 */
	public DataLocation(String sequenceID) {
		super();
		this.listType = DataListType.SEQUENCE;
		this.sequenceID = sequenceID;
	}
	
	
	public String getSequenceID() {
		return sequenceID;
	}


	/**
	 * Can be used to update the sequence name, if a sequence was renamed or data area is moved. 
	 * The list type is automatically set to {@link DataListType#SEQUENCE} if an non-{@code null}
	 * ID is specified.
	 * 
	 * @param sequenceID - the unique identifier of the associated sequence
	 */
	public void setSequenceID(String sequenceID) {
		if (sequenceID != null) {
			listType = DataListType.SEQUENCE;
		}
		this.sequenceID = sequenceID;
	}


	public DataListType getListType() {
		return listType;
	}


	/**
	 * Changes the list type. If a value different from {@link DataListType#SEQUENCE} is
	 * specified, the sequence name is automatically set to {@code null}.
	 * 
	 * @param listType -  the new list type
	 */
	public void setListType(DataListType listType) {
		this.listType = listType;
		if (listType != DataListType.SEQUENCE) {
			sequenceID = null;
		}
	}
}
