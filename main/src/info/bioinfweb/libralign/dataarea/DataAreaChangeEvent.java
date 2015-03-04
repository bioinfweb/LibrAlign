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
package info.bioinfweb.libralign.dataarea;


import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;

import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;



/**
 * Event that is used to indicate a change of data areas displayed inside an {@link AlignmentArea}.
 * <p>
 * A change in this case means adding, removing or replacing data areas. This event does not indicate
 * changes of the contents of a data area.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class DataAreaChangeEvent extends EventObject {
	private boolean eventsFromSingleList;
	private ListChangeType type;
	private Collection<DataArea> affectedElements;
	
	
	/**
	 * Creates a new event object.
	 * 
	 * @param source - the object that manages the data area lists
	 * @param eventsFromSingleList - Specify {@code true} here if all elements in {@code affectedElements}
	 *        are contained in the same {@link DataAreaList}.
	 * @param type - the type of modification that happened
	 * @param affectedElements - a list of elements that have been affected (inserted, removed or replaced
	 *        or visibility change)
	 */
	public DataAreaChangeEvent(Object source, boolean eventsFromSingleList, ListChangeType type,	
			Collection<? extends DataArea> affectedElements) {
		
		super(source);
		if (affectedElements.isEmpty()) {
			throw new IllegalArgumentException("At least one affected element has to be specified.");
		}
		this.eventsFromSingleList = eventsFromSingleList;
		this.type = type;
		this.affectedElements = Collections.unmodifiableCollection(affectedElements);
	}


	/**
	 * Creates a new event object. Use this constructor if only one element is affected.
	 * 
	 * @param source - the object that manages the data area lists
	 * @param eventsFromSingleList - Specify {@code true} here if all elements in {@code affectedElements}
	 *        are contained in the same {@link DataAreaList}.
	 * @param type - the type of modification that happened
	 * @param affectedElements - a list of elements that have been affected (inserted, removed or replaced
	 *        or visibility change)
	 */
	public DataAreaChangeEvent(Object source, boolean eventsFromSingleList, ListChangeType type,	
			DataArea affectedElement) {
		
		super(source);
		this.eventsFromSingleList = eventsFromSingleList;
		this.type = type;

		if (affectedElement == null) {
			throw new NullPointerException("Null is not a valid value for the affected element.");
		}
		affectedElements = Collections.nCopies(1, affectedElement);
	}


	/**
	 * Returns the model object that contains the list where the change occurred.
	 */
	@Override
	public DataAreaModel getSource() {
		return (DataAreaModel)super.getSource();
	}


	/**
	 * Returns if all elements in {@link #getAffectedElements()} are contained in the same {@link DataAreaList}.
	 * 	 
	 * @return {@code true} if all elements are from the same list, {@code false} otherwise
	 */
	public boolean isEventsFromSingleList() {
		return eventsFromSingleList;
	}


	/**
	 * Returns the type of change that happened.
	 * 
	 * @return a value enumerated by {@link ListChangeType} if an element was inserted, removed or 
	 *         replaced or {@code null} if the visibility of a data area was changed instead
	 */
	public ListChangeType getType() {
		return type;
	}


	public Collection<? extends DataArea> getAffectedElements() {
		return affectedElements;
	}
	
	
	/**
	 * Returns the first affected element. Convenience method if only one element is affected.
	 * 
	 * @return the fist affected element or {@code null} if {@link #getAffectedElements()} return an empty list
	 */
	public DataArea getAffectedElement() {
		if (!getAffectedElements().isEmpty()) {
			return getAffectedElements().iterator().next();
		}
		else {
			return null;
		}
	}
}
