/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.libralign.model.undo.alignment.token;


import java.util.Collection;
import java.util.Collections;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.undo.EditRecorder;



/**
 * Edit object that replaces a set of tokens in a sequence managed by an instance of 
 * {@link AlignmentModel}.
 * 
 * @author Ben St&oml;ver
 * @since 0.10.0
 * @see EditRecorder
 */
public class AlignmentModelSetTokensEdit<M extends AlignmentModel<T>, T> extends AlignmentModelTokenEdit<M, T> {
	private Collection<? extends T> oldTokens;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param alignmentModel the alignment model to be edited 
	 * @param sequenceID the identifier of the sequence that contains the token(s)
	 * @param beginIndex the index of the first element to be replaced (The first element in the sequence has the index 0.)
	 * @param newTokens the new tokens for the specified position
	 * @param oldTokens the previous tokens at the specified position
	 */
	public AlignmentModelSetTokensEdit(M alignmentModel, String sequenceID, int beginIndex, Collection<? extends T> newTokens, Collection<? extends T> oldTokens) {
		super(alignmentModel, sequenceID, beginIndex, newTokens);
		
		if (newTokens.size() != oldTokens.size()) {
			throw new IllegalArgumentException("The collections sizes for the new (" + newTokens.size() + ") and the old tokens (" + oldTokens.size() + 
					") do not match.");
		}
		else {
			this.oldTokens = oldTokens;
		}
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param alignmentModel the alignment model to be edited 
	 * @param sequenceID the identifier of the sequence that contains the token(s)
	 * @param index the index of the element to be replaced 
	 *        (The first element in the sequence has the index 0.)
	 * @param newToken the new token for the specified position
	 * @param oldToken the old token at the specified position
	 */
	public AlignmentModelSetTokensEdit(M alignmentModel, String sequenceID, int index, T newToken, T oldToken) {
		this(alignmentModel, sequenceID, index, Collections.nCopies(1, newToken), Collections.nCopies(1, oldToken));
	}


	@Override
	public void redo() throws CannotRedoException {
		getAlignmentModel().setTokensAt(getSequenceID(), getBeginIndex(), getTokens());
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getAlignmentModel().setTokensAt(getSequenceID(), getBeginIndex(), oldTokens);
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
