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


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;



/**
 * A list of {@link DataArea} that are displayed at a specified position in an {@link AlignmentArea}.
 * The oder of the list determines the display order, where the first element of the list is displayed
 * top most.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class DataAreaList extends DataAreaChangeEventList {
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
	 * @throws IllegalArgumentException if {@link DataAreaListType#SEQUENCE} is specified as {@code listType}
	 */
	public DataAreaList(DataAreaModel owner, DataAreaListType listType) {
		super(new ArrayList<DataArea>(INITIAL_LIST_SIZE));
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
	 * @param sequenceID - the unique identifier of the sequence the contained data areas will be attached to
	 */
	public DataAreaList(DataAreaModel owner, int sequenceID) {
		super(new ArrayList<DataArea>(INITIAL_LIST_SIZE));
		this.owner = owner;
		location = new DataAreaLocation(sequenceID);
	}


	/**
	 * Creates a new instance of this class representing a list of data areas attached to one
	 * sequence of the alignment.
	 * 
	 * @param owner - the parent element that will contain this list 
	 * @param location - the location this list will have in the alignment area
	 */
	public DataAreaList(DataAreaModel owner, DataAreaLocation location) {
		super(new ArrayList<DataArea>(INITIAL_LIST_SIZE));
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
	 * @return a collection containing all affected elements
	 * @see DataAreaModel#setSequenceDataAreasVisible(boolean)
	 */
	public Collection<DataArea> setAllVisible(boolean visible) {
		boolean informListeners = !getOwner().isVisibilityUpdateInProgress(); 
		if (informListeners) {
			getOwner().startVisibilityUpdate();
		}
		
		List<DataArea> affectedAreas = new ArrayList<DataArea>(size());  // The whole size is specified as the array size, since the number of data areas attached to one sequence will ususally be small.
		Iterator<DataArea> iterator = iterator();
		while (iterator.hasNext()) {
			DataArea area = iterator.next();
			if (area.setVisible(visible)) {  // state changed
				affectedAreas.add(area);
			}
		}
		
		affectedAreas = Collections.unmodifiableList(affectedAreas);
		if (informListeners) {
			getOwner().finishVisibilityUpdate(true, affectedAreas);
		}
		return affectedAreas;
	}
	
	
	/**
	 * Returns an iterator that only iterates over the visible data areas.
	 * 
	 * @return an instance of {@link DataAreaVisibleIterator}
	 */
	public Iterator<DataArea> visibleIterator() {
		return new DataAreaVisibleIterator(iterator());
	}
	
	
	/**
	 * Calculates the sum of the heights of all visible data areas contained in this list. 
	 * 
	 * @return an integer value greater of equal to zero
	 */
	public int getVisibleHeight() {
		int result = 0;
		Iterator<DataArea> iterator = visibleIterator();
		while (iterator.hasNext()) {
			result += iterator.next().getHeight();
		}
		return result;
	}
	
	
	/**
	 * Returns maximum space left of the alignment start that is needed by any currently visible data area in this list.
	 * 
	 * @return an integer >= 0
	 */
	public int getMaxLengthBeforeStart() {
		int result = 0;
		Iterator<DataArea> iterator = visibleIterator();
		while (iterator.hasNext()) {
			result = Math.max(result, iterator.next().getLengthBeforeStart());
		}
		return result;
	}
}
