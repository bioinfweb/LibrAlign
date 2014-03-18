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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



/**
 * Manages the data areas attached to an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 */
public class DataAreaModel {
  private DataAreaList topAreas = new DataAreaList(this, DataAreaListType.TOP);	
  private DataAreaList bottomAreas = new DataAreaList(this, DataAreaListType.BOTTOM);	
  private Map<String, DataAreaList> sequenceAreaLists = new TreeMap<String, DataAreaList>();
  private List<DataAreaModelListener> listeners = new ArrayList<DataAreaModelListener>(8);
  
  
	/**
	 * Returns a list of data areas to be displayed on the top of the alignment.
	 * 
	 * @return a modifiable list
	 */
	public DataAreaList getTopAreas() {
		return topAreas;
	}
	
	
	/**
	 * Returns a list of data areas to be displayed underneath the alignment.
	 * 
	 * @return a modifiable list
	 */
	public DataAreaList getBottomAreas() {
		return bottomAreas;
	}
	
	
	/**
	 * Returns a list of data areas to be displayed underneath the specified sequence.
	 * 
	 * @param sequenceName - the name of the sequence carrying the data areas in the returned list 
	 * @return a modifiable list
	 */
	public DataAreaList getSequenceAreas(String sequenceName) {
		DataAreaList result = sequenceAreaLists.get(sequenceName);
		if (result == null) {
			result = new DataAreaList(this, DataAreaListType.SEQUENCE);
			sequenceAreaLists.put(sequenceName, result);
		}
		return result;
	}
	
	
	/**
	 * Removes the list of data areas attached to the specified sequence from this model.
	 * 
	 * @param sequenceName - the name of the sequence the data areas to be removed are attached to
	 */
	public void removeSequence(String sequenceName) {
		sequenceAreaLists.remove(sequenceName);
	}
	
	
	/**
	 * Attaches all data area currently associated with one sequence name to another name.
	 * This method could be used if a sequence with associated data areas was renamed or if a
	 * whole list of data areas should be moved from sequence to another.
	 * 
	 * @param currentName - the name of the sequence the data areas are currently attached to
	 * @param newName - the new name of the sequence the data areas shall be attached to
	 */
	public void renameSequence(String currentName, String newName) {
		sequenceAreaLists.put(newName, getSequenceAreas(currentName));
		removeSequence(currentName);
	}
	
	
	/**
	 * Fades all data areas associated with any sequence in or out.
	 * 
	 * @param visible - Specify {@code true} here, if you want the elements to be displayed, {@code false} otherwise.
	 * @see DataAreaList#setAllVisible(boolean)
	 */
	public void setSequenceDataAreasVisible(boolean visible) {
		Iterator<String> nameIterator = sequenceAreaLists.keySet().iterator();
		while (nameIterator.hasNext()) {
			getSequenceAreas(nameIterator.next()).setAllVisible(visible);
		}
	}
	
	
	/**
	 * Calculates the sum of the heights of all visible data areas contained in this model, that are 
	 * attached to any sequence. 
	 * 
	 * @return a double value greater of equal to zero
	 */
	public double getVisibleSequenceAreaHeight() {
		double result = 0.0;
		Iterator<String> iterator = sequenceAreaLists.keySet().iterator(); 
		while (iterator.hasNext()) {
			result += getSequenceAreas(iterator.next()).getVisibleHeight();
		}
		return result;
	}
	
	
	/**
	 * Calculates the sum of the heights of all visible data areas contained in this model (above, 
	 * underneath and attached to any sequence). 
	 * 
	 * @return a double value greater of equal to zero
	 */
	public double getVisibleAreaHeight() {
		return getTopAreas().getVisibleHeight() + getVisibleSequenceAreaHeight() + getBottomAreas().getVisibleHeight();
	}
	
	
	/**
	 * Adds a lister to this object that will be informed about future insertions or deletions
	 * of data areas.
	 * 
	 * @param listener - the listener object to be notified in the future
	 * @return {@code true} (as specified by {@link Collection#add(Object)}) 
	 */
	public boolean addListener(DataAreaModelListener listener) {
		return listeners.add(listener);
	}


	/**
	 * Removes the specified listener from this objects list.
	 * 
	 * @param listener - the listener to be removed 
	 * @return {@code true} if this list contained the specified element
	 */
	public boolean removeListener(DataAreaModelListener listener) {
		return listeners.remove(listener);
	}


	/**
	 * Informs all listeners that a data area has been added or removed.
	 */
	protected void fireChange(DataAreaList list) {
		Iterator<DataAreaModelListener> iterator = listeners.iterator();
		DataAreaChangeEvent e = new DataAreaChangeEvent(this, list);
		while (iterator.hasNext()) {
			iterator.next().dataAreaModelInsertedRemoved(e);
		}
	}
}
