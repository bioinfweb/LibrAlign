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

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.undo.EditRecorder;



/**
 * Implements the insertion and removal of sequence elements in an instance of {@link AlignmentModel}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 * @see EditRecorder
 */
public abstract class AlignmentModelInsertRemoveTokensEdit<M extends AlignmentModel<T>, T> extends AlignmentModelTokenEdit<M, T> {
	/**
	 * Creates a new instance of this class used to insert or remove a set of tokens.
	 * 
	 * @param model the alignment model to be edited 
	 * @param sequenceID the identifier the sequence that contains the token
	 * @param beginIndex the index where the tokens should be inserted or removed (The first element in the sequence has the index 0.)
	 * @param tokens the new tokens for the specified position
	 */
	public AlignmentModelInsertRemoveTokensEdit(M alignmentModel, String sequenceID, int beginIndex, Collection<? extends T> tokens) {
		super(alignmentModel, sequenceID, beginIndex, tokens);
	}
	
	
	/**
	 * Creates a new instance of this class used to insert or remove a single token.
	 * 
	 * @param model the alignment model to be edited 
	 * @param sequenceID the identifier the sequence that contains the token
	 * @param index the index where the token should be inserted or removed (The first element in the sequence has the index 0.)
	 * @param token the new token for the specified position
	 */
	public AlignmentModelInsertRemoveTokensEdit(M alignmentModel, String sequenceID, int index, T token) {
		this(alignmentModel, sequenceID, index, Collections.nCopies(1, token));
	}
	
	
	/**
	 * Performs the insert operation (either of the new token(s) for insertions or the previously removed
	 * token(s) for deletions).
	 */
	protected void insert() {
		getAlignmentModel().insertTokensAt(getSequenceID(), getBeginIndex(), getTokens());
	}
	
	
	/**
	 * Performs the remove operation.
	 */
	protected void remove() {
		getAlignmentModel().removeTokensAt(getSequenceID(), getBeginIndex(), getBeginIndex() + getTokens().size());
	}
}
