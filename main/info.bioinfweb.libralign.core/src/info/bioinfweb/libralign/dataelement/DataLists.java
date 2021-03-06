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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import info.bioinfweb.commons.collections.observable.ListChangeListener;
import info.bioinfweb.libralign.alignmentarea.DataAreaLists;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.model.DataModelLists;



/**
 * Manages the data elements attached to sequences of an alignment.
 * <p>
 * This is the super-class of {@link DataAreaLists} and {@link DataModelLists} and implements their shared functionality.
 *
 * @author Ben St&ouml;ver
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class DataLists<O, E> {
	private final O owner;
  private final Map<String, DataList<O, E>> sequenceLists = new HashMap<String, DataList<O, E>>();
  private final ListChangeListener<E> listChangeListener;


	/**
	 * Creates a new instance of this class.
	 *
	 * @param owner the object that will be using the new instance to organize its subelements
	 * @param listChangeListener This listener will be informed on changes in all lists contained in the new instance. Owning classes should provide an 
	 *        implementation that processes the events, e.g., by forwarding them to their own respective listeners. 
	 */
  public DataLists(O owner, ListChangeListener<E> listChangeListener) {
		super();
		this.owner = owner;
		this.listChangeListener = listChangeListener;
	}


	/**
	 * Returns the alignment content area that uses this instance.
	 */
	public O getOwner() {
		return owner;
	}


	/**
	 * Returns a list of data areas to be displayed underneath the specified sequence.
	 *
	 * @param sequenceID the unique identifier of the sequence carrying the data areas in the returned list
	 * @return a modifiable list
	 */
	public DataList<O, E> getSequenceList(String sequenceID) {
		DataList<O, E> result = sequenceLists.get(sequenceID);
		if (result == null) {
			result = new DataList<O, E>(this, sequenceID);
			result.addListChangeListener(listChangeListener);
			sequenceLists.put(sequenceID, result);
		}
		return result;
	}
	
	
	/**
	 * Removes the list of data areas attached to the specified sequence from this model.
	 *
	 * @param sequenceID the unique identifier of the sequence the data areas to be removed are attached to
	 */
	public void removeSequenceList(String sequenceID) {
		DataList<O, E> list = sequenceLists.remove(sequenceID);
		if (list != null) {
			list.clear();  // Fire remove events for all entries.
			list.removeListChangeListener(listChangeListener);
		}
	}
	
	
	public void clearSequenceLists() {
		for (String sequenceID : sequenceLists.keySet().toArray(new String[sequenceLists.size()])) {  // Copy key set to be able to remove elements during iteration.
			removeSequenceList(sequenceID);
		}
	}
	
	
	public Iterator<DataList<O, E>> sequenceListIterator() {
		return sequenceLists.values().iterator();
	}
	
	
	public void forEach(Consumer<? super E> action) {
		sequenceListIterator().forEachRemaining(list -> list.forEach(action));
	}
}
