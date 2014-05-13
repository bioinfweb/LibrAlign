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
package info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.edits.token;


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.SwingUndoSequenceDataProvider;



/**
 * Edit object that replaces a set of tokens in a sequence managed by an instance of 
 * {@link SequenceDataProvider}.
 * 
 * @author Ben St&oml;ver
 * @since 0.1.0
 * @see SwingUndoSequenceDataProvider
 */
public class SwingSetTokensEdit<T> extends SwingTokenEdit<T> {
	private T[] oldTokens;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param provider - the data provider creating this instance 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param beginIndex - the index of the first element to be replaced 
	 *        (The first element in the sequence has the index 0.)
	 * @param tokens - the new tokens for the specified position
	 */
	public SwingSetTokensEdit(SwingUndoSequenceDataProvider<T> provider, int sequenceID,
			int beginIndex, Collection<? extends T> tokens) {
		
		super(provider, sequenceID, beginIndex, tokens);

		
		oldTokens = (T[])Array.newInstance(tokens.getClass().getComponentType(), tokens.size());  // Returned array should be a subclass of T[].
		//TODO Check if this works
		for (int i = 0; i < oldTokens.length; i++) {
			oldTokens[i] = getProvider().getTokenAt(sequenceID, beginIndex + i);
		}
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param provider - the data provider creating this instance 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param index - the index of the element to be replaced 
	 *        (The first element in the sequence has the index 0.)
	 * @param token - the new token for the specified position
	 */
	public SwingSetTokensEdit(SwingUndoSequenceDataProvider<T> provider, int sequenceID,
			int index, T token) {
		
		this(provider, sequenceID, index, Collections.nCopies(1, token));
	}


	@Override
	public void redo() throws CannotRedoException {
		getProvider().getUnderlyingProvider().setTokensAt(sequenceID, beginIndex, tokens);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		for (int i = 0; i < oldTokens.length; i++) {
			getProvider().getUnderlyingProvider().setTokenAt(sequenceID, beginIndex + i, oldTokens[i]);
		}
		super.undo();
	}


	/**
	 * Returns a list of the tokens that are replaced when this edit is executed.
	 * 
	 * @return an unmodifiable collection of tokens
	 */
	public Collection<T> getOldTokens() {
		return Collections.unmodifiableCollection(Arrays.asList(oldTokens));
	}


	@Override
	protected String getOperationName() {
		return "Replace";
	}
}
