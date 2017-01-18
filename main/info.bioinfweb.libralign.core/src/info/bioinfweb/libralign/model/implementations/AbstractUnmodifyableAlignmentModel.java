/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.model.implementations;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import info.bioinfweb.libralign.model.SequenceAccessAlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelWriteType;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * Implements basic functionality for a map based alignment model that uses unmodifiable sequence
 * objects as its underlying data source.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <S> - the type of the sequence objects (e.g. a BioJava sequence type or {@link String})
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public abstract class AbstractUnmodifyableAlignmentModel<S, T> extends AbstractMapBasedAlignmentModel<S, T>
    implements SequenceAccessAlignmentModel<S, T> {
	
	/**
	 * Creates a new instance of this class with a custom map and list implementation.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 * @param sequenceMap - the map instance used to assign sequences to their IDs
	 * @param sequenceOrder - the list object defining the order of the sequences
	 */
	public AbstractUnmodifyableAlignmentModel(TokenSet<T> tokenSet, SequenceIDManager idManager,	
			Map<String, S> sequenceMap, List<String> sequenceOrder) {
		
		super(tokenSet, idManager, sequenceMap, sequenceOrder);
	}


	/**
	 * Creates a new instance of this class with a custom map and list implementation.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 * @param sequenceMap - the map instance used to assign sequences to their IDs
	 */
	public AbstractUnmodifyableAlignmentModel(TokenSet<T> tokenSet, SequenceIDManager idManager, Map<String, S> sequenceMap) {
		super(tokenSet, idManager, sequenceMap);
	}


	/**
	 * Creates a new instance of this class relying on a {@link TreeMap}.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 */
	public AbstractUnmodifyableAlignmentModel(TokenSet<T> tokenSet, SequenceIDManager idManager) {
		super(tokenSet, idManager);
	}


  /**
   * Adds a the specified sequence to the underlying data source and generates an ID for it.
   * <p>
   * This method calls first {@link #addSequence(String)} and than {@link #setSequenceContent(int, Object)}
   * internally. Therefore two different events will be fired.
   * <p>
   * Note that since the sequences in this implementation are considered as unmutable it does not make
   * sense to call {@link #addSequence(String)} on an instance of this class unless you want to have
   * an empty sequence in the alignment.
   * 
   * @param sequenceName - the name of the new sequence
	 * @param content - the sequence object to be added.
   * @return the unique ID of the new sequence
	 * 
	 * @throws AlignmentSourceNotWritableException This exception is not thrown by this implementation.
   */
	@Override
	public String addSequence(String sequenceName, S content) {
		String result = addSequence(sequenceName);
		replaceSequence(result, content);
		return result;
	}
	
	
	/**
	 * Replaces the sequence object with the specified ID by the specified contents. Internally the current 
	 * sequence is first removed from the underlying map and after that the new sequence is added using
	 * the same ID. Therefore two {@link SequenceChangeEvent}s are fired, one for the removal and one for the
	 * insertion. 
	 * <p>
	 * Between these two events the underlying map does not contain any sequence with the specified ID. For the
	 * case that event handlers of the first event add any new sequences to the map they have to make sure not
	 * to use the ID passed to this method (which might accidentally be created by a call of 
	 * {@link #addSequence(String)}), because the new contents would than be overwritten in the second step
	 * of this method.
	 * <p>
	 * It also has to be noted that there 
	 * 
	 * @param sequenceID - the ID of the sequence to be replaced
	 * @param content - the new sequence object
	 * @return the previous sequence identified by the specified ID, or {@code null} if there was no sequence 
	 *         with the ID
	 */
	@Override
	public S replaceSequence(String sequenceID, S content) {
		// Remove old sequence:
		S previous = null;
		if (removeSequence(sequenceID)) {
			previous = getSequenceMap().get(sequenceID);
			fireAfterSequenceChange(SequenceChangeEvent.newRemoveInstance(this, sequenceID));
		}
		
		// Add new sequence:
		getSequenceMap().put(sequenceID, content);
		fireAfterSequenceChange(SequenceChangeEvent.newInsertInstance(this, sequenceID));
		return previous;
	}


	@Override
	public AlignmentModelWriteType getWriteType() {
		return AlignmentModelWriteType.SEQUENCES_ONLY;
	}


	@Override
	public void setTokenAt(String sequenceID, int index, T token) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void setTokensAt(String sequenceID, int beginIndex,	Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokenAt(String sequenceID, int index, T token) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokensAt(String sequenceID, int beginIndex, Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokenAt(String sequenceID, int index) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokensAt(String sequenceID, int beginIndex, int endIndex)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public S getSequence(String sequenceID) {
		return getSequenceMap().get(sequenceID);
	}
}
