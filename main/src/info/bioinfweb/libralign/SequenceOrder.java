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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;



/**
 * Manages the ordering of the sequences displayed by {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class SequenceOrder {
	private final Comparator<Integer> ASCENDING_ALPHABETICAL_COMPARATOR = new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			return getOwner().getSequenceProvider().sequenceNameByID(o1).compareTo(
					getOwner().getSequenceProvider().sequenceNameByID(o2));
		}
	};


	private final Comparator<Integer> DESCENDING_ALPHABETICAL_COMPARATOR = new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			return getOwner().getSequenceProvider().sequenceNameByID(o2).compareTo(
					getOwner().getSequenceProvider().sequenceNameByID(o1));
		}
	};


	private AlignmentContentArea owner;
	private List<Integer> idList = new ArrayList<Integer>();

	
	/**
	 * Crates a new instance of this class and loads the names from the underlying data source of
	 * the specified owner, if it has a data source.
	 * <p>
	 * The initial order the determined by the oder in the data source.
	 * 
	 * @param owner - the alignment view objects the returned instance should be used for
	 */
	public SequenceOrder(AlignmentContentArea owner) {
		super();
		this.owner = owner;
		setSourceSequenceOrder();
	}


	public AlignmentContentArea getOwner() {
		return owner;
	}


	/**
	 * Returns the display index of the sequence with the specified ID.
	 * 
	 * @param id - the unique identifier of the sequence
	 * @return the index of the sequence (The first sequence has the index 0.)
	 */
	public int indexByID(int id) {
		return idList.indexOf(id);
	}
	
	
	/**
	 * Returns the unique identifier of the sequence displayed at the specified index
	 * 
	 * @param index - the index of the sequence (The first sequence has the index 0.)
	 * @return the ID of the sequence in the data source 
	 */
	public int idByIndex(int index) {
		return idList.get(index);
	}
	
	
	/**
	 * Sorts the sequences by their occurrence in the source data.
	 * <p>
	 * This method can also be used to refresh the sequence names if the data source changed.
	 */
	public void setSourceSequenceOrder() {
		idList.clear();
		if (getOwner().hasSequenceProvider()) {
			Iterator<Integer> iterator = getOwner().getSequenceProvider().sequenceIDIterator();
			while (iterator.hasNext()) {
				idList.add(iterator.next());
			}
		}
	}
 	
	
	/**
	 * Sorts the sequences by their name.
	 * 
	 * @param ascending - Specify {@code true} here, if you want the sequences to have an ascending
	 *        alphabetical order and {@code false} for a descending order. 
	 */
	public void setAlphabeticalSequenceOrder(boolean ascending) {
		if (ascending) {
			Collections.sort(idList, ASCENDING_ALPHABETICAL_COMPARATOR);
		}
		else {
			Collections.sort(idList, DESCENDING_ALPHABETICAL_COMPARATOR);
		}
	}

	
	/**
	 * Sorts the sequences by their name in ascending order.
	 */
	public void setAscendingAlphabeticalSequenceOrder() {
		Collections.sort(idList, ASCENDING_ALPHABETICAL_COMPARATOR);
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
		int newIndex = Math.max(0, Math.min(idList.size() - 1, index + offset));
		int id = idList.get(index);
		if (newIndex < index) {
			for (int i = index; i > newIndex; i--) {
				idList.set(i, idList.get(i - 1));
			}
		}
		else {
			for (int i = index; i < newIndex; i++) {
				idList.set(i, idList.get(i + 1));
			}
		}
		idList.set(newIndex, id);
		return newIndex;
	}
	

	/**
	 * Returns the underlying name list which can be used to change the ordering of the sequences.
	 * <p>
	 * You must not remove any names from this list that are contained in the data source of 
	 * {@link #getOwner()}. This object will not be able to function correctly anymore if that would 
	 * be done.
	 * 
	 * @return a reference to the name list this object uses to determine the order of the sequences.
	 */
	public List<Integer> getIdList() {
		return idList;
	}
}
