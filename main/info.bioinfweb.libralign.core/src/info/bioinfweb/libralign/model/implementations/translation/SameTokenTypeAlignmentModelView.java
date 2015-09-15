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
package info.bioinfweb.libralign.model.implementations.translation;


import info.bioinfweb.libralign.model.AlignmentModelChangeListener;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelWriteType;
import info.bioinfweb.libralign.model.AlignmentModelView;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.exception.DuplicateSequenceNameException;
import info.bioinfweb.libralign.model.exception.SequenceNotFoundException;
import info.bioinfweb.libralign.model.implementations.AbstractAlignmentModelView;

import java.util.Collection;



/**
 * Abstract base class for implementations of {@link AlignmentModelView} that simply replace a set
 * of tokens by other tokens of the same type. It delegates all methods to the underlying provider
 * except {@link #getTokenAt(int, int)} which calls the abstract method {@link #translateToken(Object)}
 * to perform the actual translation.
 * <p>
 * It is still possible to write tokens that are translated to the underlying data source. Note that
 * in that case {@link #getTokenAt(int, int)} would return a different token than the one passed to a 
 * previously call of {@link #setTokenAt(int, int, Object)} or {@link #insertTokenAt(int, int, Object)}
 * or similar methods called with the same index value.
 *  
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type used by the implementing class and the underlying provider
 */
public abstract class SameTokenTypeAlignmentModelView<T> extends AbstractAlignmentModelView<T, T> {
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param underlyingProvider - the underlying provider to be viewed
	 */
	public SameTokenTypeAlignmentModelView(AlignmentModel<T> underlyingProvider) {
		super(underlyingProvider.getTokenSet().clone(), underlyingProvider);
	}


	/**
	 * Inherited classes should perform the token translation by implementing this method.
	 * 
	 * @param token - the token stored in (or shown by) the underlying provider
	 * @return the token that shall be shown instead (or the same token, if it shall not be changed)
	 */
	public abstract T translateToken(T token);
	
	
	@Override
	public T getTokenAt(int sequenceID, int index) {
		return translateToken(getUnderlyingModel().getTokenAt(sequenceID, index));
	}


	@Override
	public void setTokenAt(int sequenceID, int index, T token)
			throws AlignmentSourceNotWritableException {
		
		getUnderlyingModel().setTokenAt(sequenceID, index, token);
	}


	@Override
	public void setTokensAt(int sequenceID, int beginIndex, Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {
		
		getUnderlyingModel().setTokensAt(sequenceID, beginIndex, tokens);
	}


	@Override
	public void appendToken(int sequenceID, T token) throws AlignmentSourceNotWritableException {
		getUnderlyingModel().appendToken(sequenceID, token);
	}


	@Override
	public void appendTokens(int sequenceID, Collection<? extends T> tokens) throws AlignmentSourceNotWritableException {
		getUnderlyingModel().appendTokens(sequenceID, tokens);
	}


	@Override
	public void insertTokenAt(int sequenceID, int index, T token)
			throws AlignmentSourceNotWritableException {
		
		getUnderlyingModel().insertTokenAt(sequenceID, index, token);
	}


	@Override
	public void insertTokensAt(int sequenceID, int beginIndex, Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {
		
		getUnderlyingModel().insertTokensAt(sequenceID, beginIndex, tokens);
	}


	@Override
	public void removeTokenAt(int sequenceID, int index)
			throws AlignmentSourceNotWritableException {
		
		getUnderlyingModel().removeTokenAt(sequenceID, index);
	}


	@Override
	public void removeTokensAt(int sequenceID, int beginIndex, int endIndex)
			throws AlignmentSourceNotWritableException {
		
		getUnderlyingModel().removeTokensAt(sequenceID, beginIndex, endIndex);
	}


	@Override
	public int getSequenceLength(int sequenceID) {
		return getUnderlyingModel().getSequenceLength(sequenceID);
	}


	@Override
	public int getMaxSequenceLength() {
		return getUnderlyingModel().getMaxSequenceLength();
	}


	@Override
	public AlignmentModelWriteType getWriteType() {
		return getUnderlyingModel().getWriteType();
	}


	@Override
	public boolean isTokensReadOnly() {
		return getUnderlyingModel().isTokensReadOnly();
	}


	@Override
	public String renameSequence(int sequenceID, String newSequenceName)
			throws AlignmentSourceNotWritableException,
			DuplicateSequenceNameException, SequenceNotFoundException {
		
		return getUnderlyingModel().renameSequence(sequenceID, newSequenceName);
	}


	@Override
	public Collection<AlignmentModelChangeListener> getChangeListeners() {
		return getUnderlyingModel().getChangeListeners();
	}	
}