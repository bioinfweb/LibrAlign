/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben St�ver
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
import java.util.Iterator;

import info.bioinfweb.libralign.AlignmentSourceDataType;
import info.bioinfweb.libralign.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.exception.DuplicateSequenceNameException;
import info.bioinfweb.libralign.exception.SequenceNotFoundException;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataChangeListener;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProviderWriteType;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataView;



/**
 * Basic implementation of {@link SequenceDataView} which stores the underlying provider
 * in a property and delegates sequence specific methods (which are independent of the token
 * type) to the underlying provider.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <T> - the type of sequence elements (tokens) the implementing view works with
 * @param <U> - the type of sequence elements (tokens) the underlying provider works with
 */
public abstract class AbstractSequenceDataView<T, U> implements SequenceDataView<T, U>{
  private SequenceDataProvider<U> underlyingProvider;

  
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param underlyingProvider - the underlying provider to be viewed
	 */
	public AbstractSequenceDataView(SequenceDataProvider<U> underlyingProvider) {
		super();
		this.underlyingProvider = underlyingProvider;
	}


	@Override
	public SequenceDataProvider<U> getUnderlyingProvider() {
		return underlyingProvider;
	}


	/**
	 * Replaces the underlying provider. Note that the according sequence change events would have to be fired 
	 * if the underlying provider is changed during runtime and {@link #getChangeListeners()} would have to 
	 * be reimplemented because it currently just delegates the underlying 
	 * 
	 * @param underlyingProvider - the new underlying provider
	 */
	protected void setUnderlyingProvider(SequenceDataProvider<U> underlyingProvider) {
		this.underlyingProvider = underlyingProvider;
	}


	@Override
	public boolean isSequencesReadOnly() {
		return underlyingProvider.isSequencesReadOnly();
	}


	@Override
	public boolean containsSequence(int sequenceID) {
		return underlyingProvider.containsSequence(sequenceID);
	}


	@Override
	public int sequenceIDByName(String sequenceName) {
		return underlyingProvider.sequenceIDByName(sequenceName);
	}


	@Override
	public String sequenceNameByID(int sequenceID) {
		return underlyingProvider.sequenceNameByID(sequenceID);
	}


	@Override
	public int addSequence(String sequenceName) throws AlignmentSourceNotWritableException {
		return underlyingProvider.addSequence(sequenceName);
	}


	@Override
	public boolean removeSequence(int sequenceID)	throws AlignmentSourceNotWritableException {
		return underlyingProvider.removeSequence(sequenceID);
	}


	@Override
	public Iterator<Integer> sequenceIDIterator() {
		return underlyingProvider.sequenceIDIterator();
	}


	@Override
	public int getSequenceCount() {
		return underlyingProvider.getSequenceCount();
	}
}
