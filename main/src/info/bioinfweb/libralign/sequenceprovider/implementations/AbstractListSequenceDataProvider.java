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


import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProviderWriteType;
import info.bioinfweb.libralign.sequenceprovider.events.TokenChangeEvent;
import info.bioinfweb.libralign.sequenceprovider.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.sequenceprovider.tokenset.TokenSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;



/**
 * An abstract implementation of {@link SequenceDataProvider} using a map of lists to store alignment data.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public abstract class AbstractListSequenceDataProvider<T> 
    extends AbstractMapBasedSequenceDataProvider<List<T>, T> {
	
	public static final int DEFAULT_INITIAL_CAPACITY = 256;
	
		
  private int initialCapacity;
  private boolean useMaxLength;
  
  
	/**
	 * Creates a new instance of this class using {@link #DEFAULT_INITIAL_CAPACITY} as the initial capacity
	 * which is only used if it is lower than {@link #getMaxSequenceLength()} to create new sequence array 
	 * lists.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 */
	public AbstractListSequenceDataProvider(TokenSet<T> tokenSet) {
		this(tokenSet, DEFAULT_INITIAL_CAPACITY, true);
	}


	/**
	 * Creates a new instance of this class.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 * @param initialCapacity - the initial capacity newly generated array lists will have
	 * @param useMaxLength - Specify {@code true} here if {@code initialCapacity} shall only be used if it 
	 *        is lower than {@link #getMaxSequenceLength()} to create new sequence array lists.
	 */
	public AbstractListSequenceDataProvider(TokenSet<T> tokenSet, int initialCapacity, 
			boolean useMaxLength) {
		
		super(tokenSet);
		this.initialCapacity = initialCapacity;
		this.useMaxLength = useMaxLength;
	}

	
	public List<T> getSequence(int sequenceID) {
		return getSequenceMap().get(sequenceID);
	}
	
	
	@Override
	public T getTokenAt(int sequenceID, int index) {
		return getSequence(sequenceID).get(index);
	}
	

	@Override
	public void setTokenAt(int sequenceID, int index, T token) throws AlignmentSourceNotWritableException {
		T replacedToken = getSequence(sequenceID).get(index);
		getSequence(sequenceID).set(index, token);
		
		fireAfterTokenChange(TokenChangeEvent.newReplaceInstance(this, sequenceID, index, replacedToken));
	}

	
	@Override
	public void setTokensAt(int sequenceID, int beginIndex,	Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {

		List<T> sequence = getSequence(sequenceID);
		Collection<T> replacedTokens = new ArrayList<T>(tokens.size());
		Iterator<? extends T> iterator = tokens.iterator();
		int index = beginIndex;
		while (iterator.hasNext()) {
			replacedTokens.add(sequence.get(index));
			sequence.set(index, iterator.next());
			index++;
		}
		
		fireAfterTokenChange(TokenChangeEvent.newReplaceInstance(this, sequenceID, beginIndex, replacedTokens));
	}

	
	@Override
	public void insertTokenAt(int sequenceID, int index, T token)
			throws AlignmentSourceNotWritableException {

		getSequence(sequenceID).add(index, token);
		fireAfterTokenChange(TokenChangeEvent.newInsertInstance(this, sequenceID, index, token));
	}

	
	@Override
	public void insertTokensAt(int sequenceID, int beginIndex, Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {

		getSequence(sequenceID).addAll(beginIndex, tokens);
		fireAfterTokenChange(TokenChangeEvent.newInsertInstance(this, sequenceID, beginIndex, tokens));
	}

	
	@Override
	public void removeTokenAt(int sequenceID, int index) throws AlignmentSourceNotWritableException {
		List<T> sequence = getSequence(sequenceID);
		T removedToken = sequence.get(index);
		sequence.remove(index);
		fireAfterTokenChange(TokenChangeEvent.newRemoveInstance(this, sequenceID, index, removedToken));
	}

	
	@Override
	public void removeTokensAt(int sequenceID, int beginIndex, int endIndex)
			throws AlignmentSourceNotWritableException {

		List<T> sequence = getSequence(sequenceID);
		Collection<T> removedTokens = new ArrayList<T>(endIndex - beginIndex);
		List<T> subList = sequence.subList(beginIndex, endIndex);
		removedTokens.addAll(subList);
		subList.clear();
		
		fireAfterTokenChange(TokenChangeEvent.newRemoveInstance(this, sequenceID, beginIndex, removedTokens));
	}

	
	@Override
	public int getSequenceLength(int sequenceID) {
		return getSequence(sequenceID).size();
	}

	
	@Override
	public SequenceDataProviderWriteType getWriteType() {
		return SequenceDataProviderWriteType.BOTH;
	}

	
	/**
	 * Returns the default initial capacity of new list objects created by 
	 * {@link #createNewSequence(int, String, int)}.
	 * 
	 * @return the default initial capacity specified in the constructor
	 */
	public int getInitialCapacity() {
		return initialCapacity;
	}


	/**
	 * Determines if the initial capacity of newly generated list objects shall only depend on the
	 * default capacity or also on the maximum length of the other lists already contained in this provider. 
	 * 
	 * @return {@code true} if new lists shall have the maximum length of all other lists already present in 
	 *         this provider if this maximum is higher than the return value of {@link #getInitialCapacity()},
	 *         {@code false} if all new sequences shall always have an initial capacity of 
	 *         {@link #getInitialCapacity()}. 
	 */
	public boolean isUseMaxLength() {
		return useMaxLength;
	}
	
	
  /**
   * This method is called by {@link #createNewSequence(int, String)} and must be implemented by inherited classes.
   * In contrast to {@link #createNewSequence(int, String)} it provides an additional parameter specifying the
   * initial capacity the returned list object shall have. This parameter value if set depending on the return 
   * values of {@link #isUseMaxLength()} either to a fixed value as specified by {@link #getInitialCapacity()}
   * or by the maximum length of other sequences in this provider if any of these contain more elements than
   * {@link #getInitialCapacity()}. 
   * 
	 * @param sequenceID - the ID the new sequence must have
	 * @param sequenceName - the name the new sequence will have
   * @param initialCapacity - the initial capicity the returned list object shall have
	 * @return the new sequence object
   */
  protected abstract List<T> createNewSequence(int sequenceID, String sequenceName, int initialCapacity);
	

	@Override
	protected List<T> createNewSequence(int sequenceID, String sequenceName) {
		int capacity = initialCapacity;
		if (isUseMaxLength() && (getSequenceCount() > 0)) {
			capacity = Math.max(initialCapacity, getMaxSequenceLength());
		}
		return createNewSequence(sequenceID, sequenceName, capacity);
	}

}
