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
package info.bioinfweb.libralign.model.events;


import java.util.Collection;
import java.util.Collections;

import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * Event object that indicates that a sequence provided by an instance of {@link AlignmentModel}
 * was added or removed.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * 
 * @param <T> the type of sequence elements (tokens) the implementing provider object works with
 */
public class SequenceChangeEvent<T> extends TypedAlignmentModelChangeEvent<T> {
	private Collection<T> deletedContent;
	private String sequenceName;
	private int index;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source the alignment model from which the sequence was removed
	 * @param sequenceID the ID of the sequence that was removed
	 * @param type the type of
	 * @param deletedContent a collection containing the tokens that were contained in the sequence 
	 *        (The collection implementation used here should ideally be as memory-efficient as the ones used by {@code source} internally.
	 *        If {@code null} is passed here, an empty collection will be used.)
	 * @throws IllegalArgumentException if {@code source}, {@code sequenceID} or {@code type} are {@code null} 
	 */
	protected SequenceChangeEvent(AlignmentModel<T> source, String sequenceID, ListChangeType type, Collection<T> deletedContent, String sequenceName, int index) {
		super(source, sequenceID, type);
		if (deletedContent == null) {
			deletedContent = Collections.emptyList();
		}
		this.index = index;
		this.deletedContent = Collections.unmodifiableCollection(deletedContent);
		this.sequenceName = sequenceName;
	}
	

	public static <T> SequenceChangeEvent<T> newInsertInstance(int index, AlignmentModel<T> source, String sequenceID,String sequenceName) {
		return new SequenceChangeEvent<T>(source, sequenceID, ListChangeType.INSERTION, null, sequenceName, index);
	}
	
	
	/**
	 * Creates a new instance of this event indicating the insertion of a sequence at a specific index.
	 * 
	 * @param source the alignment model from which the sequence was removed
	 * @param sequenceID the ID of the sequence that was inserted
	 * @param sequenceName the name of the sequence that was inserted
	 * @param index the index where the sequence was inserted
	 * @return the new event instance
	 */
	public static <T> SequenceChangeEvent<T> newInsertInstanceAtIndex(AlignmentModel<T> source, String sequenceID,String sequenceName, Integer index) {
		return new SequenceChangeEvent<T>(source, sequenceID, ListChangeType.INSERTION, null, sequenceName, index);
	}
	
	
	/**
	 * Creates a new instance of this event indicating the removal of a sequence.
	 * 
	 * @param source the alignment model from which the sequence was removed
	 * @param sequenceID the ID of the sequence that was removed
	 * @param deletedContent a collection containing the tokens that were contained in the sequence 
	 *        (The collection implementation used here should ideally be as memory-efficient as the ones used by {@code source} internally.
	 *        If {@code null} is passed here, an empty collection will be used.)
	 * @return the new event instance
	 */
	public static <T> SequenceChangeEvent<T> newRemoveInstance(int index, AlignmentModel<T> source, String sequenceID, Collection<T> deletedContent) {
		return new SequenceChangeEvent<T>(source, sequenceID, ListChangeType.DELETION, deletedContent, null, index);
	}

	
	/**
	 * Creates a new instance of this event indicating the removal of a sequence and records the index of the deleted sequence.
	 * The index is necessary if the deleted sequence should be added again in the same position.
	 * 
	 * @param source the alignment model from which the sequence was removed
	 * @param sequenceID the ID of the sequence that was removed
	 * @param deletedContent a collection containing the tokens that were contained in the sequence 
	 *        (The collection implementation used here should ideally be as memory-efficient as the ones used by {@code source} internally.
	 *        If {@code null} is passed here, an empty collection will be used.)
	 * @param index the index of the deleted sequence
	 * @return the new event instance
	 */
	public static <T> SequenceChangeEvent<T> newRemoveInstanceAtIndex(AlignmentModel<T> source, String sequenceID, Collection<T> deletedContent, Integer index) {
		return new SequenceChangeEvent<T>(source, sequenceID, ListChangeType.DELETION, deletedContent, null, index);
	}
	
	
	/**
	 * Returns a collection of the tokens that were contained in the deleted sequence.
	 * 
	 * @return the deleted tokens or an empty collection if this event indicates the introduction of a new sequence.
	 */
	public Collection<T> getDeletedContent() {
		return deletedContent;
	}

	
	public String getSequenceName() {
		return sequenceName;
	}
	

	public int getIndex() {
		return index;
	}


	@Override
	public SequenceChangeEvent<T> cloneWithNewSource(AlignmentModel<T> source) {
		return (SequenceChangeEvent<T>)super.cloneWithNewSource(source);
	}

	
	@Override
	public SequenceChangeEvent<T> clone() {
		return (SequenceChangeEvent<T>)super.clone();
	}
}
