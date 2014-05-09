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
 * @since 0.0.1
 * @see SwingUndoSequenceDataProvider
 */
public class SwingSetTokensEdit extends SwingTokenEdit {
	private Object[] oldTokens;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param provider - the data provider creating this instance 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param beginIndex - the index of the first element to be replaced 
	 *        (The first element in the sequence has the index 0.)
	 * @param tokens - the new tokens for the specified position
	 */
	public SwingSetTokensEdit(SwingUndoSequenceDataProvider provider, int sequenceID,
			int beginIndex, Collection<? extends Object> tokens) {
		
		super(provider, sequenceID, beginIndex, tokens);

		oldTokens = new Object[tokens.size()];
		for (int i = 0; i < oldTokens.length; i++) {
			oldTokens[i] = getProvider().getTokenAt(sequenceID, beginIndex + i);
		}
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
	public Collection<Object> getOldTokens() {
		return Collections.unmodifiableCollection(Arrays.asList(oldTokens));
	}


	@Override
	protected String getOperationName() {
		return "Replace";
	}
}
