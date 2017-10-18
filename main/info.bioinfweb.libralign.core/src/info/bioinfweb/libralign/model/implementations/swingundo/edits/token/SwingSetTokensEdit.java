/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.implementations.swingundo.edits.token;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.swingundo.SwingUndoAlignmentModel;



/**
 * Edit object that replaces a set of tokens in a sequence managed by an instance of 
 * {@link AlignmentModel}.
 * 
 * @author Ben St&oml;ver
 * @since 0.1.0
 * @see SwingUndoAlignmentModel
 */
public class SwingSetTokensEdit<T> extends SwingTokenEdit<T> {
	private Collection oldTokens;  // Raw type is necessary here, because the generic element type of the provider can't be determined during runtime and the class of the first token might be a subtype of the actual type.
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param model the data provider creating this instance 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param beginIndex - the index of the first element to be replaced 
	 *        (The first element in the sequence has the index 0.)
	 * @param tokens - the new tokens for the specified position
	 */
	public SwingSetTokensEdit(SwingUndoAlignmentModel<T> model, String sequenceID,
			int beginIndex, Collection<? extends T> tokens) {
		
		super(model, sequenceID, beginIndex, tokens);
		
		if (tokens.isEmpty()) {
			throw new IllegalArgumentException("The list of new tokens must not be empty.");
		}
		else {
			oldTokens = new ArrayList<Object>(tokens.size());
			for (int i = 0; i < tokens.size(); i++) {
				oldTokens.add(getModel().getTokenAt(sequenceID, beginIndex + i));
			}
		}
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param provider the alignment model creating this instance 
	 * @param sequenceID the identifier the sequence where the token is contained
	 * @param index the index of the element to be replaced 
	 *        (The first element in the sequence has the index 0.)
	 * @param token the new token for the specified position
	 */
	public SwingSetTokensEdit(SwingUndoAlignmentModel<T> provider, String sequenceID,
			int index, T token) {
		
		this(provider, sequenceID, index, Collections.nCopies(1, token));
	}


	@Override
	public void redo() throws CannotRedoException {
		getModel().getUnderlyingModel().setTokensAt(sequenceID, beginIndex, tokens);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getModel().getUnderlyingModel().setTokensAt(sequenceID, beginIndex, oldTokens);
		super.undo();
	}


	/**
	 * Returns a list of the tokens that are replaced when this edit is executed.
	 * 
	 * @return an unmodifiable collection of tokens
	 */
	public Collection<T> getOldTokens() {
		return Collections.unmodifiableCollection(oldTokens);
	}


	@Override
	protected String getOperationName() {
		return "Replace";
	}
}
