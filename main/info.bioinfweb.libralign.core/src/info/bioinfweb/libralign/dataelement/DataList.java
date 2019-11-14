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


import java.util.ArrayList;

import info.bioinfweb.commons.collections.observable.ObservableList;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataArea;



/**
 * A list of {@link DataArea}s that are displayed at a specified position in an {@link AlignmentArea}.
 * The oder of the list determines the display order, where the first element of the list is displayed
 * top most.
 *
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class DataList<O, E> extends ObservableList<E> {
	public static final int INITIAL_LIST_SIZE = 8;


	private DataLists<O, E> owner;
	private DataLocation location;


	/**
	 * Creates a new instance of this class for lists containing data areas displayed above or underneath 
	 * the alignment.
	 *
	 * @param owner the parent element that will contain this list
	 * @param listType specifies if list represents data areas displayed above
	 *        or underneath the alignment
	 *
	 * @throws IllegalArgumentException if {@link DataListType#SEQUENCE} is specified as {@code listType}
	 */
	public DataList(DataLists<O, E> owner, DataListType listType) {
		super(new ArrayList<E>(INITIAL_LIST_SIZE));
		
		if (listType.equals(DataListType.SEQUENCE)) {
			throw new IllegalArgumentException("The type " + DataListType.SEQUENCE + " cannot be used if the sequence ID is omitted.");
		}
		else {
			this.owner = owner;
			location = new DataLocation(listType);
		}
	}


	/**
	 * Creates a new instance of this class representing a list of data areas attached to one
	 * sequence of the alignment.
	 * <p>
	 * The list type is automatically set to {@link DataListType#SEQUENCE}.
	 *
	 * @param owner the parent element that will contain this list
	 * @param sequenceID the unique identifier of the sequence the contained data areas will be attached to
	 */
	public DataList(DataLists<O, E> owner, String sequenceID) {
		super(new ArrayList<E>(INITIAL_LIST_SIZE));
		this.owner = owner;
		location = new DataLocation(sequenceID);
	}


	/**
	 * Creates a new instance of this class representing a list of data areas attached to one
	 * sequence of the alignment.
	 *
	 * @param owner the parent element that will contain this list
	 * @param location the location this list will have in the alignment area
	 */
	public DataList(DataLists<O, E> owner, DataLocation location) {
		super(new ArrayList<E>(INITIAL_LIST_SIZE));
		this.owner = owner;
		this.location = location;
	}


	public DataLists<O, E> getOwner() {
		return owner;
	}


	/**
	 * Returns the location the elements in this list have in the containing {@link AlignmentArea}.
	 *
	 * @return the according location object
	 */
	public DataLocation getLocation() {
		return location;
	}
}
