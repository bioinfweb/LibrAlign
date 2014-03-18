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
package info.bioinfweb.libralign.dataarea;


import info.bioinfweb.libralign.AlignmentArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;



/**
 * A list of {@link DataArea} that are displayed at a specified position in an {@link AlignmentArea}.
 * The oder of the list determines the display order, where the first element of the list is displayed
 * top most.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class DataAreaList extends ArrayList<DataArea> {
	public static final int INITIAL_LIST_SIZE = 8;
	
	
	private DataAreaModel owner;
	private DataAreaLocation location;
	

	/**
	 * Creates a new instance of this class for lists containing data areas displayed above
	 * or underneath the alignment.
	 * 
	 * @param owner - the parent element that will contain this list 
	 * @param listType - specifies if list represents data areas displayed above
	 *        or underneath the alignment
	 *        
	 * @throws IllegalArgumentException - if {@link DataAreaListType#SEQUENCE} is specified as {@code listType}
	 */
	public DataAreaList(DataAreaModel owner, DataAreaListType listType) {
		super(INITIAL_LIST_SIZE);
		if (listType.equals(DataAreaListType.SEQUENCE)) {
			throw new IllegalArgumentException("The type " + DataAreaListType.SEQUENCE + 
					" cannot be used if the sequence name is omitted.");
		}
		else {
			this.owner = owner;
			location = new DataAreaLocation(listType);
		}
	}

	
	/**
	 * Creates a new instance of this class representing a list of data areas attached to one
	 * sequence of the alignment.
	 * <p>
	 * The list type is automatically set to {@link DataAreaListType#SEQUENCE}.
	 * </p>
	 * 
	 * @param owner - the parent element that will contain this list 
	 * @param sequenceName - the name of the sequence the contained data areas will be attached to
	 */
	public DataAreaList(DataAreaModel owner, String sequenceName) {
		super();
		this.owner = owner;
		location = new DataAreaLocation(sequenceName);
	}


	/**
	 * Creates a new instance of this class representing a list of data areas attached to one
	 * sequence of the alignment.
	 * 
	 * @param owner - the parent element that will contain this list 
	 * @param location - the location this list will have in the alignment area
	 */
	public DataAreaList(DataAreaModel owner, DataAreaLocation location) {
		super();
		this.owner = owner;
		this.location = location;
	}


	public DataAreaModel getOwner() {
		return owner;
	}
	

	/**
	 * Returns the location the elements in this list have in the containing {@link AlignmentArea}.
	 * 
	 * @return the according location object
	 */
	public DataAreaLocation getLocation() {
		return location;
	}

	
	/**
	 * Fades all elements in this list in or out.
	 * 
	 * @param visible - Specify {@code true} here, if you want the elements to be displayed, {@code false} otherwise.
	 * @see DataAreaModel#setSequenceDataAreasVisible(boolean)
	 */
	public void setAllVisible(boolean visible) {
		Iterator<DataArea> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next().setVisible(visible);
		}
	}
	

	/**
	 * Appends the specified data area to the end of this list. This list is automatically set as
	 * the owning list of {@code e}.
	 * 
	 * @param e - the element to be added to the list
	 */
	@Override
	public boolean add(DataArea e) {
		boolean result = super.add(e);
		if (result) {
			e.setList(this);
			getOwner().fireChange(this);
		}
		return result;
	}
	

	@Override
	public void add(int index, DataArea element) {
		element.setList(this);
		getOwner().fireChange(this);
		super.add(index, element);
	}
	

	@Override
	public boolean addAll(Collection<? extends DataArea> c) {
		return addAll(size(), c);
	}
	

	@Override
	public boolean addAll(int index, Collection<? extends DataArea> c) {
		boolean result = super.addAll(index, c);
		if (result) {
			Iterator<? extends DataArea> iterator = c.iterator();
			while (iterator.hasNext()) {
				iterator.next().setList(this);
			}
			getOwner().fireChange(this);
		}
		return result;
	}

	
	@Override
	public void clear() {
		getOwner().fireChange(this);
		super.clear();
	}
	

	@Override
	public DataArea remove(int index) {
		DataArea result = super.remove(index); 
		getOwner().fireChange(this);
		return result;
	}
	

	@Override
	public boolean remove(Object o) {
		boolean result = super.remove(o); 
		getOwner().fireChange(this);
		return result;
	}
	

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = super.removeAll(c); 
		getOwner().fireChange(this);
		return result;
	}
	

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean result = super.retainAll(c); 
		getOwner().fireChange(this);
		return result;
	}

	
	@Override
	public DataArea set(int index, DataArea element) {
		DataArea result = super.set(index, element);
		getOwner().fireChange(this);
		return result;
	}

	
	//TODO Check if change events are produced by methods called on iterator(), listIterator(), listIterator(int index) and subList()
}
