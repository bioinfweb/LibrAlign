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
import java.util.Collections;
import java.util.EventObject;



/**
 * Event that is used to indicate a change of data areas displayed inside an {@link AlignmentArea}.
 * <p>
 * A change in this case means adding, removing or replacing data areas. This event does not indicate
 * changes of the contents of a data area.
 * </p>
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class DataAreaChangeEvent extends EventObject {
	public static enum Type {
		INSERTION,
		REMOVAL,
		REPLACEMENT;
	}
	
	
	private DataAreaList list;
	private Type type;
	private Collection<DataArea> affectedElements;
	
	
	/**
	 * Creates a new event object.
	 * 
	 * @param source - the object that manages the data area lists
	 * @param list - the list that contains (or contained) the element the change occurred in
	 * @param type - the type of modification that happened
	 * @param affectedElements - a list of elements that have been inserted, removed or replaced
	 */
	public DataAreaChangeEvent(Object source, DataAreaList list, Type type,	
			Collection<? extends DataArea> affectedElements) {
		
		super(source);
		if (affectedElements.isEmpty()) {
			throw new IllegalArgumentException("At least one affected element has to be specified.");
		}
		this.list = list;
		this.type = type;
		this.affectedElements = Collections.unmodifiableCollection(affectedElements);
	}


	/**
	 * Creates a new event object. Use this constructor if only one element is affected.
	 * 
	 * @param source - the object that manages the data area lists
	 * @param list - the list that contains (or contained) the element the change occurred in
	 * @param type - the type of modification that happened
	 * @param affectedElement - the element that has been inserted, removed or replaced
	 */
	public DataAreaChangeEvent(Object source, DataAreaList list, Type type,	DataArea affectedElement) {
		super(source);
		this.list = list;
		this.type = type;

		if (affectedElement == null) {
			throw new NullPointerException("Null is not a valid value for the affected element.");
		}
		affectedElements = new ArrayList<DataArea>(1);
		affectedElements.add(affectedElement);
		affectedElements = Collections.unmodifiableCollection(affectedElements);
	}


	/**
	 * Returns the model object that contains the list where the change occurred.
	 */
	@Override
	public DataAreaModel getSource() {
		return (DataAreaModel)super.getSource();
	}


	/**
	 * Returns the list of data areas the change occurred in.
	 */
	public DataAreaList getList() {
		return list;
	}


	public Type getType() {
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
