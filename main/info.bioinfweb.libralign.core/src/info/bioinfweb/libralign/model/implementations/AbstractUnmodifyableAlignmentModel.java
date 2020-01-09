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
 * @param <S> the type of the sequence objects (e.g. a BioJava sequence type or {@link String})
 * @param <T> the type of sequence elements (tokens) the implementing provider object works with
 */
public abstract class AbstractUnmodifyableAlignmentModel<S, T> extends AbstractMapBasedAlignmentModel<S, T>
    implements SequenceAccessAlignmentModel<S, T> {
	
	private S nextContent = null; 
	
	
	/**
	 * Creates a new instance of this class with a custom map and list implementation.
	 * 
	 * @param tokenSet the token set which is supported by the implementation
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 * @param reuseSequenceIDs Specifies whether unused IDs of the underlying ID manager should be reused by this model.
	 *        (See the documentation of {@link #isReuseSequenceIDs()} for details. Specify {@code false}, if you are unsure
	 *        what this property does.) 
	 * @param sequenceMap the map instance used to assign sequences to their IDs
	 * @param sequenceOrder the list object defining the order of the sequences
	 */
	public AbstractUnmodifyableAlignmentModel(TokenSet<T> tokenSet, SequenceIDManager idManager, boolean reuseSequenceIDs,	
			Map<String, S> sequenceMap, List<String> sequenceOrder) {
		
		super(tokenSet, idManager, reuseSequenceIDs, sequenceMap, sequenceOrder);
	}


	/**
	 * Creates a new instance of this class with a custom map and list implementation.
	 * 
	 * @param tokenSet the token set which is supported by the implementation
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 * @param reuseSequenceIDs Specifies whether unused IDs of the underlying ID manager should be reused by this model.
	 *        (See the documentation of {@link #isReuseSequenceIDs()} for details. Specify {@code false}, if you are unsure
	 *        what this property does.) 
	 * @param sequenceMap the map instance used to assign sequences to their IDs
	 */
	public AbstractUnmodifyableAlignmentModel(TokenSet<T> tokenSet, SequenceIDManager idManager, boolean reuseSequenceIDs, 
			Map<String, S> sequenceMap) {
		
		super(tokenSet, idManager, reuseSequenceIDs, sequenceMap);
	}


	/**
	 * Creates a new instance of this class relying on a {@link TreeMap}.
	 * 
	 * @param tokenSet the token set which is supported by the implementation
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 * @param reuseSequenceIDs Specifies whether unused IDs of the underlying ID manager should be reused by this model.
	 *        (See the documentation of {@link #isReuseSequenceIDs()} for details. Specify {@code false}, if you are unsure
	 *        what this property does.) 
	 */
	public AbstractUnmodifyableAlignmentModel(TokenSet<T> tokenSet, SequenceIDManager idManager, boolean reuseSequenceIDs) {
		super(tokenSet, idManager, reuseSequenceIDs);
	}


  @Override
	protected S createNewSequence(String sequenceID, String sequenceName) {
  	if (nextContent != null) {
  		return nextContent;
  	}
  	else {
  		return createNewEmptySequence(sequenceID, sequenceName);
  	}
	}
  
  
  protected abstract S createNewEmptySequence(String sequenceID, String sequenceName);


	/**
   * Adds a the specified sequence to the underlying data source and generates an ID for it.
   * <p>
   * Note that since the sequences in this implementation are considered as immutable it does not make
   * sense to call {@link #addSequence(String)} on an instance of this class unless you want to have
   * an empty sequence in the alignment.
   * 
   * @param sequenceName the name of the new sequence
	 * @param content the sequence object to be added.
   * @return the unique ID of the new sequence
	 * 
	 * @throws AlignmentSourceNotWritableException This exception is never thrown by this implementation.
   */
	@Override
	public String addSequence(String sequenceName, S content) {
		try {
			nextContent = content;
			return addSequence(sequenceName);
		}
		finally {
			nextContent = null;
		}
	}
	
	
	/**
   * Adds a the specified sequence to the underlying data source and assigns the specified ID to it.
   * <p>
   * Note that since the sequences in this implementation are considered as immutable it does not make
   * sense to call {@link #addSequence(String)} on an instance of this class unless you want to have
   * an empty sequence in the alignment.
   * 
   * @param sequenceName the name of the new sequence
   * @param sequenceID the ID for the new sequence
	 * @param content the sequence object to be added.
   * @return the unique ID of the new sequence
	 * 
	 * @throws IllegalArgumentException if a sequence with the specified ID is already present in this model
   */
	@Override
	public String addSequence(String sequenceName, String sequenceID, S content) throws IllegalArgumentException {
		try {
			nextContent = content;
			return addSequence(sequenceName, sequenceID);
		}
		finally {
			nextContent = null;
		}
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
	public void insertTokenAt(String sequenceID, int index, T token, boolean leftBound) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokensAt(String sequenceID, int beginIndex, Collection<? extends T> tokens, boolean leftBound)
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
