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
package info.bioinfweb.libralign.alignmentmodel.implementations.swingundo.edits.token;


import info.bioinfweb.libralign.alignmentmodel.AlignmentModel;
import info.bioinfweb.libralign.alignmentmodel.implementations.swingundo.SwingUndoSequenceDataProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;



/**
 * Implements the insertion and removal of sequence elements in an instance of {@link AlignmentModel}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 * @see SwingUndoSequenceDataProvider
 */
public abstract class SwingInsertRemoveTokensEdit<T> extends SwingTokenEdit<T> {
	/**
	 * Creates a new instance of this class used to insert a new set of tokens into a sequence.
	 * 
	 * @param provider - the data provider creating this instance 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param beginIndex - the index where the new tokens shall be inserted 
	 *        (The first element in the sequence has the index 0.)
	 * @param tokens - the new tokens for the specified position
	 */
	public SwingInsertRemoveTokensEdit(SwingUndoSequenceDataProvider<T> provider, int sequenceID, int beginIndex, 
			Collection<? extends T> tokens) {

		super(provider, sequenceID, beginIndex, tokens);
	}
	
	
	/**
	 * Creates a new instance of this class used to insert a new single token into a sequence.
	 * 
	 * @param provider - the data provider creating this instance 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param index - the index where the new token shall be inserted 
	 *        (The first element in the sequence has the index 0.)
	 * @param token - the new token for the specified position
	 */
	public SwingInsertRemoveTokensEdit(SwingUndoSequenceDataProvider<T> provider, int sequenceID, int index, 
			 T token) {

		this(provider, sequenceID, index, Collections.nCopies(1, token));
	}
	
	
	/**
	 * Creates a new instance of this class used to remove a set of tokens from a sequence. These tokens
	 * will be stored in the inherited field {@link #tokens} during the execution of this constructor.
	 * 
	 * @param provider - the data provider creating this instance 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param beginIndex - the index of the first element to be removed 
	 *        (The first element in the sequence has the index 0.)
	 * @param endIndex - the index after the last element to be removed
	 */
	public SwingInsertRemoveTokensEdit(SwingUndoSequenceDataProvider<T> provider, int sequenceID, int beginIndex, 
			int endIndex) {

		super(provider, sequenceID, beginIndex, null);
		
		int length = beginIndex - endIndex;
		tokens = new ArrayList<T>(length);
		for (int i = 0; i < length; i++) {
			((ArrayList<T>)tokens).add(getProvider().getTokenAt(sequenceID, beginIndex + i));
		}
	}
	
	
	/**
	 * Creates a new instance of this class used to remove a single token from a sequence. This token
	 * will be stored as the only element in the inherited field {@link #tokens} during the execution 
	 * of this constructor.
	 * 
	 * @param provider - the data provider creating this instance 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param index - the index of the element to be removed 
	 *        (The first element in the sequence has the index 0.)
	 */
	public SwingInsertRemoveTokensEdit(SwingUndoSequenceDataProvider<T> provider, int sequenceID, int index) {
		this(provider, sequenceID, index, index + 1);
	}
	
	
	/**
	 * Performs the insert operation (either of the new token(s) for insertions or the previously removed
	 * token(s) for deletions).
	 */
	protected void insert() {
		getProvider().getUnderlyingProvider().insertTokensAt(sequenceID, beginIndex, tokens);
	}
	
	
	/**
	 * Performs the remove operation.
	 */
	protected void remove() {
		getProvider().getUnderlyingProvider().removeTokensAt(sequenceID, beginIndex, beginIndex + tokens.size());
	}
}
