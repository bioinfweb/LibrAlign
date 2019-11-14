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
import java.util.Map;

import info.bioinfweb.commons.collections.observable.ListChangeListener;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;



/**
 * Manages the data areas attached to an {@link AlignmentArea}.
 *
 * @author Ben St&ouml;ver
 */
public class DataLists<O, E> {
	private final O owner;
  private final DataList<O, E> topAreas = new DataList<O, E>(this, DataListType.TOP);
  private final DataList<O, E> bottomAreas = new DataList<O, E>(this, DataListType.BOTTOM);
  private final Map<String, DataList<O, E>> sequenceAreaLists = new HashMap<String, DataList<O, E>>();
  private final ListChangeListener<E> listChangeListener;


	/**
	 * Creates a new instance of this class.
	 *
	 * @param owner the alignment content area that will be using this instance
	 * @param listChangeListener This listener will be informed on changes in all lists contained in the new instance. Owning classes should provide an 
	 *        implementation that processes the events, e.g., by forwarding them to their own respective listeners. 
	 */
  public DataLists(O owner, ListChangeListener<E> listChangeListener) {
		super();
		this.owner = owner;
		this.listChangeListener = listChangeListener;
		
		topAreas.addListChangeListener(listChangeListener);
		bottomAreas.addListChangeListener(listChangeListener);
	}


	/**
	 * Returns the alignment content area that uses this instance.
	 */
	public O getOwner() {
		return owner;
	}


	/**
	 * Returns a list of data areas to be displayed on the top of the alignment.
	 *
	 * @return a modifiable list
	 */
	public DataList<O, E> getTopAreas() {
		return topAreas;
	}


	/**
	 * Returns a list of data areas to be displayed underneath the alignment.
	 *
	 * @return a modifiable list
	 */
	public DataList<O, E> getBottomAreas() {
		return bottomAreas;
	}


	/**
	 * Returns a list of data areas to be displayed underneath the specified sequence.
	 *
	 * @param sequenceID the unique identifier of the sequence carrying the data areas in the returned list
	 * @return a modifiable list
	 */
	public DataList<O, E> getSequenceAreas(String sequenceID) {
		DataList<O, E> result = sequenceAreaLists.get(sequenceID);
		if (result == null) {
			result = new DataList<O, E>(this, sequenceID);
			result.addListChangeListener(listChangeListener);
			sequenceAreaLists.put(sequenceID, result);
		}
		return result;
	}
	
	
	/**
	 * Removes the list of data areas attached to the specified sequence from this model.
	 *
	 * @param sequenceID the unique identifier of the sequence the data areas to be removed are attached to
	 */
	public void removeSequence(String sequenceID) {
		DataList<O, E> list = sequenceAreaLists.remove(sequenceID);
		if (list != null) {
			list.removeListChangeListener(listChangeListener);
		}
	}
}
