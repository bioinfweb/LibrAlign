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


import java.util.Iterator;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelView;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * Basic implementation of {@link AlignmentModelView} which stores the underlying provider
 * in a property and delegates sequence specific methods (which are independent of the token
 * type) to the underlying provider.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <T> - the type of sequence elements (tokens) the implementing view works with
 * @param <U> - the type of sequence elements (tokens) the underlying provider works with
 */
public abstract class AbstractAlignmentModelView<T, U> implements AlignmentModelView<T, U>{
  private AlignmentModel<U> underlyingProvider;
  private TokenSet<T> tokenSet;

  
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param underlyingProvider - the underlying provider to be viewed
	 */
	public AbstractAlignmentModelView(TokenSet<T> tokenSet, AlignmentModel<U> underlyingProvider) {
		super();
		this.tokenSet = tokenSet;
		this.underlyingProvider = underlyingProvider;
	}


	@Override
	public AlignmentModel<U> getUnderlyingModel() {
		return underlyingProvider;
	}


	@Override
	public TokenSet<T> getTokenSet() {
		return tokenSet;
	}


	@Override
	public void setTokenSet(TokenSet<T> tokenSet) {
		this.tokenSet = tokenSet;
	}


	/**
	 * Replaces the underlying provider. Note that the according sequence change events would have to be fired 
	 * if the underlying provider is changed during runtime and {@link #getChangeListeners()} would have to 
	 * be reimplemented because it currently just delegates the underlying 
	 * 
	 * @param underlyingProvider - the new underlying provider
	 */
	protected void setUnderlyingProvider(AlignmentModel<U> underlyingProvider) {
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
