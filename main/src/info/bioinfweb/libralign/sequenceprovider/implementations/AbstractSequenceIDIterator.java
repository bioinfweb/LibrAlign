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
package info.bioinfweb.libralign.sequenceprovider.implementations;


import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;

import java.util.Iterator;



/**
 * Abstract implementation of an iterator returning the IDs of all sequences provided by an implementation
 * of {@link SequenceDataProvider}.
 * <p>
 * Besides the sequence provider this class needs another iterator as a constructor parameter which is 
 * responsible for returning new IDs in the correct order. This class implements a remove method which 
 * throws an {@link UnsupportedOperationException} if the underlying data source does not allow writing
 * sequences and calls the remove methods of the specified iterator. Additionally it offers an abstract
 * method {@link #doRemove()} that allows the implementations of {@link SequenceDataProvider} to perform 
 * additional specific removal operations. When overwriting this method the ID that is currently removed 
 * can be obtained by {@link #getCurrentID()}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <P> - the implementing class of {@link SequenceDataProvider} which will return the iterator
 *        inherited from this class 
 */
public abstract class AbstractSequenceIDIterator<P extends SequenceDataProvider> implements Iterator<Integer> {
	private P provider;
	private Iterator<Integer> iterator;
  private int currentID = -1;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param provider - the provider that is going to return this iterator with one of its methods
	 * @param iterator - the underlying iterator that returns the sequence IDs in the correct order
	 */
	public AbstractSequenceIDIterator(P provider, Iterator<Integer> iterator) {
		super();
		this.provider = provider;
		this.iterator = iterator;
	}


	/**
	 * Returns the sequence data provider that returned this iterator.
	 * 
	 * @return the provider instance specified in the constructor of this class.
	 */
	protected P getProvider() {
		return provider;
	}


	/**
	 * Returns the underlying iterator object.
	 * 
	 * @return the iterator instance specifying the ID order
	 */
	protected Iterator<Integer> getIterator() {
		return iterator;
	}


	/**
	 * The ID that has been returned by this iterator by the last call of {@link #next()}.
	 * 
	 * @return the current ID or {@code -1} if {@link #next()} has never been called until now
	 */
	protected int getCurrentID() {
		return currentID;
	}


	@Override
	public boolean hasNext() {
		return getIterator().hasNext();
	}

	
	@Override
	public Integer next() {
		currentID = iterator.next();
		return currentID;
	}

	
	/**
	 * Overwrite this method to perform implementation specific remove operations.
	 * <p>
	 * This method is called by {@link #remove()} if the underlying data source in writable for sequences.
	 * The {@code remove()} method of the underlying iterator has already been called before the call of 
	 * this method, therefore this method will not be executed, if an exception was thrown by 
	 * {@code getIterator().remove()}.
	 */
	protected abstract void doRemove();
	
	
	/**
	 * Removes the current ID from the underlying iterator and calls {@link #doRemove()}.
	 * 
	 * @throws UnsupportedOperationException if {@link #next()} has not yet been called or the underlying
	 *         data source is not writable for sequences or the underlying iterator does not support this
	 *         operation
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		if (getProvider().isSequencesReadOnly()) {
			throw new UnsupportedOperationException(
					"The underlying data source does not allow the removal of sequences.");
		}
		else {
			iterator.remove();  // Throws an exception if this method is called before the first call of next().
			doRemove();
		}
	}
}
