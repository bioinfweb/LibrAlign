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
package info.bioinfweb.libralign;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;



/**
 * Manages the ordering of the sequences displayed by {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class SequenceOrder {
	private AlignmentArea owner;
	private List<String> nameList = new ArrayList<String>();

	
	/**
	 * Crates a new instance of this class and loads the names from the underlying data source of
	 * the specified owner, if it has a data source.
	 * <p>
	 * The initial order the determined by the oder in the data source.
	 * 
	 * @param owner - the alignment view objects the returned instance should be used for
	 */
	public SequenceOrder(AlignmentArea owner) {
		super();
		this.owner = owner;
		setSourceSequenceOrder();
	}


	public AlignmentArea getOwner() {
		return owner;
	}


	/**
	 * Returns the display index of the sequence with the specified name.
	 * 
	 * @param name - the name of the sequence
	 * @return the index of the sequence (The first sequence has the index 0.)
	 */
	public int indexByName(String name) {
		return nameList.indexOf(name);
	}
	
	
	/**
	 * Returns the name of the sequence displayed at the specified index
	 * 
	 * @param index - the index of the sequence (The first sequence has the index 0.)
	 * @return the name of the sequence in the data source 
	 */
	public String nameByIndex(int index) {
		return nameList.get(index);
	}
	
	
	/**
	 * Sorts the sequences by their occurrence in the source data.
	 * <p>
	 * This method can also be used to refresh the sequence names if the data source changed.
	 */
	public void setSourceSequenceOrder() {
		nameList.clear();
		if (getOwner().hasDataProvider()) {
			Iterator<String> iterator = getOwner().getDataProvider().sequenceNameIterator();
			while (iterator.hasNext()) {
				nameList.add(iterator.next());
			}
		}
	}
 	
	
	/**
	 * Sorts the sequences by their name.
	 */
	public void setAlphabeticalSequenceOrder() {
		Collections.sort(nameList);
	}

	
	/**
	 * Moves a sequence to a new position. All other sequences are moved by one position accordingly.
	 * <p>
	 * If the {@code index + offset} is lower than 0 the sequence if moved to the beginning, if the 
	 * result greater than the index of the last element in the list, the sequence is moved to the
	 * end.
	 * 
	 * @param index - the index of the sequence to be moved (The first sequence has the index 0.)
	 * @param offset - the number of positions to move the sequence (Can be positive or negative.)
	 * @return the new index the specified sequence has after the move operation
	 */
	public int moveSequence(int index, int offset) {
		int newIndex = Math.max(0, Math.min(nameList.size() - 1, index + offset));
		String name = nameList.get(index);
		if (newIndex < index) {
			for (int i = index; i > newIndex; i--) {
				nameList.set(i, nameList.get(i - 1));
			}
		}
		else {
			for (int i = index; i < newIndex; i++) {
				nameList.set(i, nameList.get(i + 1));
			}
		}
		nameList.set(newIndex, name);
		return newIndex;
	}
	

	/**
	 * Returns the underlying name list which can be used to change the ordering of the sequences.
	 * <p>
	 * You must not remove any names that are contained in the data source of {@link #getOwner()}.
	 * This object will not be able to function correctly anymore if that would be done.
	 * 
	 * @return a reference to the name list this object uses to determine the order of the sequences.
	 */
	public List<String> getNameList() {
		return nameList;
	}
}
