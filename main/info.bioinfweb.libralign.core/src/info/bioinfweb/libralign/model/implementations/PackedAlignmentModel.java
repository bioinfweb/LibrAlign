/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.model.implementations;


import info.bioinfweb.commons.collections.PackedObjectArrayList;
import info.bioinfweb.libralign.model.tokenset.TokenSet;

import java.util.Iterator;
import java.util.List;



/**
 * A sequence data provider using a {@link PackedObjectArrayList} is the underlying data source.
 * This class can be used to efficiently store large editable sequences, because it will usually use only
 * a small fraction of the space {@link ArrayListAlignmentModel} will use to store its tokens.
 * <p>
 * Due to the compression method the token set cannot be changed during runtime for instances of this class. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type to be stored
 */
public class PackedAlignmentModel<T> extends AbstractListAlignmentModel<T> {
	private int minTokenCount;
	
	
	/**
	 * Creates a new instance of this class
	 * 
	 * @param tokenSet the token set to be used by this alignment model
	 * @param minTokenCount the minimal number of different tokens to be supported by the compression method
	 *        of this model
	 * @see #getMaxSequenceLength()
	 */
	public PackedAlignmentModel(TokenSet<T> tokenSet, int minTokenCount) {
		super(tokenSet);
		if (tokenSet.size() > minTokenCount) {
			throw new IllegalArgumentException(
					"The specified token count must be greater or equal to the number of tokens in the specified set.");
		}
		else {
			this.minTokenCount = minTokenCount;
		}
	}


	/**
	 * Creates a new instance of this class with the maximum number of different tokens that is equal to the
	 * number of tokens in the specified set.
	 * <p>
	 * Note that adding additional elements to the token set later on may cause problems if the returned 
	 * instance cannot hold enough different types of tokens (see {@link #getMaxTokenCount()} for further 
	 * information). 
	 * 
	 * @param tokenSet the token set to be used by this alignment model
	 */
	public PackedAlignmentModel(TokenSet<T> tokenSet) {
		this(tokenSet, tokenSet.size());
	}


	/**
	 * Returns the maximum number of different tokens that can be modeled by this packed list. (This does not affect
	 * the maximum number of such tokens in one or all sequences of the alignment.)
	 * <p>
	 * Note that the returned value might be higher than the value passed to the constructor depending on the maximum 
	 * integer value that can be represented by the number of necessary bits.
	 * 
	 * @return the maximum number of different tokens
	 */
	public int getMaxTokenCount() {
		Iterator<Integer> iterator = sequenceIDIterator();
		List<T> sequence;  // PackedObjectArrayList may take up more tokens than specified
		if (iterator.hasNext()) {
			sequence = getSequence(iterator.next());
		}
		else {  // Create dummy instance of no sequence is present yet.
			sequence = createNewSequence(0, "");
		}
		return ((PackedObjectArrayList<T>)sequence).getMaxObjectTypeCount();
	}


	@Override
	protected List<T> createNewSequence(int sequenceID, String sequenceName, int initialCapacity) {
		return new PackedObjectArrayList<T>(minTokenCount, initialCapacity);
	}


	/**
	 * This method is not supported by this class.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public void setTokenSet(TokenSet<T> set) {
		throw new UnsupportedOperationException("This class does not support changing the token set during runtume.");
	}
}