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
package info.bioinfweb.libralign.sequenceprovider.implementations;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.biojava.bio.seq.Sequence;

import info.bioinfweb.libralign.sequenceprovider.SequenceDataProviderWriteType;
import info.bioinfweb.libralign.sequenceprovider.SequenceObjectDataProvider;
import info.bioinfweb.libralign.sequenceprovider.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.sequenceprovider.tokenset.TokenSet;



/**
 * Implements basic functionality for a map base sequence data provider that uses unmodifiable sequence
 * objects as its underlying data source.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <S> - the type of the sequence objects (e.g. {@link Sequence} or {@link String})
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public abstract class AbstractUnmodifyableSequenceDataProvider<S, T>
    extends AbstractMapBasedSequenceDataProvider<S, T>
    implements SequenceObjectDataProvider<S, T> {
	
	/**
	 * Creates a new instance of this class with a custom map and list implementation.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 * @param sequenceMap - the map instance used to assign sequences to their IDs
	 * @param sequenceOrder - the list object defining the order of the sequences
	 */
	public AbstractUnmodifyableSequenceDataProvider(TokenSet<T> tokenSet,	Map<Integer, S> sequenceMap, 
			List<Integer> sequenceOrder) {
		
		super(tokenSet, sequenceMap, sequenceOrder);
	}


	/**
	 * Creates a new instance of this class with a custom map and list implementation.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 * @param sequenceMap - the map instance used to assign sequences to their IDs
	 */
	public AbstractUnmodifyableSequenceDataProvider(TokenSet<T> tokenSet,	Map<Integer, S> sequenceMap) {
		super(tokenSet, sequenceMap);
	}


	/**
	 * Creates a new instance of this class relying on a {@link TreeMap}.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 */
	public AbstractUnmodifyableSequenceDataProvider(TokenSet<T> tokenSet) {
		super(tokenSet);
	}


	@Override
	public int addSequence(String sequenceName, S content) {
		int result = addSequence(sequenceName);
		setSequenceContent(result, content);
		return result;
	}
	
	
	@Override
	public void setSequenceContent(int sequenceID, S content) {
		getSequenceMap().put(sequenceID, content);
	}


	@Override
	public SequenceDataProviderWriteType getWriteType() {
		return SequenceDataProviderWriteType.SEQUENCES_ONLY;
	}


	@Override
	public void setTokenAt(int sequenceID, int index, T token) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void setTokensAt(int sequenceID, int beginIndex,	Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {

		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokenAt(int sequenceID, int index, T token) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void insertTokensAt(int sequenceID, int beginIndex, Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokenAt(int sequenceID, int index) throws AlignmentSourceNotWritableException {
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public void removeTokensAt(int sequenceID, int beginIndex, int endIndex)
			throws AlignmentSourceNotWritableException {
		
		throw new AlignmentSourceNotWritableException(this);
	}


	@Override
	public S getSequence(int sequenceID) {
		return getSequenceMap().get(sequenceID);
	}
}
