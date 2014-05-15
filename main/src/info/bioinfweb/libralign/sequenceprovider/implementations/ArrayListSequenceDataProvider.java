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
import info.bioinfweb.libralign.sequenceprovider.tokenset.TokenSet;

import java.util.ArrayList;
import java.util.List;



/**
 * An abstract implementation of {@link SequenceDataProvider} using a map of array lists to store 
 * alignment data. The constructors offer different options to specify the initial capacity a new 
 * {@link ArrayList} instance.
 * 
 * @author Ben St&ouml;ever
 * @since 0.1.0
 *
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public class ArrayListSequenceDataProvider<T> extends AbstractListSequenceDataProvider<T> {
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
	public ArrayListSequenceDataProvider(TokenSet<T> tokenSet) {
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
	public ArrayListSequenceDataProvider(TokenSet<T> tokenSet, int initialCapacity, 
			boolean useMaxLength) {
		
		super(tokenSet);
		this.initialCapacity = initialCapacity;
		this.useMaxLength = useMaxLength;
	}


	public int getInitialCapacity() {
		return initialCapacity;
	}


	public boolean isUseMaxLength() {
		return useMaxLength;
	}


	@Override
	protected List<T> createNewSequence(int sequenceID, String sequenceName) {
		int capacity = initialCapacity;
		if (isUseMaxLength() && (getSequenceCount() > 0)) {
			capacity = Math.max(initialCapacity, getMaxSequenceLength());
		}
		return new ArrayList<T>(capacity);
	}
}
