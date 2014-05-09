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


import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.SwingUndoSequenceDataProvider;

import java.util.ArrayList;
import java.util.Collection;



/**
 * Implements the insertion and removal of sequence elements in an instance of {@link SequenceDataProvider}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.1
 * @see SwingUndoSequenceDataProvider
 */
public abstract class SwingInsertRemoveTokensEdit extends SwingTokenEdit {
	/**
	 * Creates a new instance of this class used to insert a new set of tokens into a sequence.
	 * 
	 * @param provider - the data provider creating this instance 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param beginIndex - the index where the new tokens shall be inserted 
	 *        (The first element in the sequence has the index 0.)
	 * @param tokens - the new tokens for the specified position
	 */
	public SwingInsertRemoveTokensEdit(SwingUndoSequenceDataProvider provider, int sequenceID, int beginIndex, 
			Collection<? extends Object> tokens) {

		super(provider, sequenceID, beginIndex, tokens);
	}
	
	
	/**
	 * Creates a new instance of this class used to remove a set of tokens from a sequence. These tokens
	 * will be stored in the inherited field {@link #tokens} during the execution of this constructor.
	 * 
	 * @param provider - the data provider creating this instance 
	 * @param sequenceID - the identifier the sequence where the token is contained
	 * @param beginIndex - the index of the first element to be removed 
	 *        (The first element in the sequence has the index 0.)
	 * @param tokens - the new tokens for the specified position
	 */
	public SwingInsertRemoveTokensEdit(SwingUndoSequenceDataProvider provider, int sequenceID, int beginIndex, 
			int endIndex) {

		super(provider, sequenceID, beginIndex, null);
		
		int length = beginIndex - endIndex;
		tokens = new ArrayList<Object>(length);
		for (int i = 0; i < length; i++) {
			((ArrayList<Object>)tokens).add(getProvider().getTokenAt(sequenceID, beginIndex + i));
		}
	}
	
	
	protected void insert() {
		getProvider().getUnderlyingProvider().insertTokensAt(sequenceID, beginIndex, tokens);
	}
	
	
	protected void remove() {
		getProvider().getUnderlyingProvider().removeTokensAt(sequenceID, beginIndex, beginIndex + tokens.size());
	}
}
