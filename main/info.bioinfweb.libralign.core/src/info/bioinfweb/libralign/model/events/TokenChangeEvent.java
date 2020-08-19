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
package info.bioinfweb.libralign.model.events;


import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.model.AlignmentModel;

import java.util.Collection;
import java.util.Collections;



/**
 * Event object that indicates that tokens in a sequence provided by an instance of 
 * {@link AlignmentModel} were inserted, removed or replaced.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * 
 * @param <T> the type of sequence elements (tokens) the implementing provider object works with
 */
public class TokenChangeEvent<T> extends TypedAlignmentModelChangeEvent<T> {
	private int startIndex;
	private boolean leftBound;
	private Collection<? extends T> affectedTokens;
	private Collection<? extends T> newTokens;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source the sequence data provider that fires this event
	 * @param sequenceID the ID of the affected sequence
	 * @param type the type of change that happened
	 * @param startIndex the index of the first affected token in the sequence
	 * @param leftBound determines whether newly inserted tokens were added left bound or right bound 
	 *        (The value of this property only has a meaning if the event instance models the insertion of new tokens.)  
	 * @param affectedTokens a list of the affected tokens
	 */
	protected TokenChangeEvent(AlignmentModel<T> source, String sequenceID, ListChangeType type,
			int startIndex, boolean leftBound, Collection<? extends T> affectedTokens, Collection<? extends T> newTokens) {
		
		super(source, sequenceID, type);
		if (affectedTokens == null) {
			throw new IllegalArgumentException("affectedTokens must not be null.");
		}
		else {
			this.startIndex = startIndex;
			this.leftBound = leftBound;
			this.affectedTokens = affectedTokens;
			this.newTokens = newTokens;
		}
	}
	
	
	/**
	 * Creates a new instance of this class that represents an insertion of a list of tokens.
	 * 
	 * @param source the sequence data provider that fires this event
	 * @param sequenceID the ID of the affected sequence
	 * @param startIndex the index where the first token was inserted into the sequence
	 * @param leftBound determines whether newly inserted tokens were added left bound or right bound 
	 * @param newTokens the list of the tokens to be inserted 
	 * @return a new instance with the same token type as {@code source}
	 */
	public static <T> TokenChangeEvent<T> newInsertInstance(AlignmentModel<T> source, String sequenceID,
			int startIndex, boolean leftBound, Collection<? extends T> newTokens) {
		
		return new TokenChangeEvent<T>(source, sequenceID, ListChangeType.INSERTION, startIndex, leftBound, newTokens, null);
	}


	/**
	 * Creates a new instance of this class that represents an insertion of a single token.
	 * 
	 * @param source the sequence data provider that fires this event
	 * @param sequenceID the ID of the affected sequence
	 * @param index the index where the new token was inserted
	 * @param leftBound determines whether newly inserted tokens were added left bound or right bound 
	 * @param newToken the token to be inserted 
	 * @return a new instance with the same token type as {@code source}
	 */
	public static <T> TokenChangeEvent<T> newInsertInstance(AlignmentModel<T> source, String sequenceID,
			int index, boolean leftBound, T newToken) {
		
		return newInsertInstance(source, sequenceID, index, leftBound, Collections.nCopies(1, newToken));
	}


	/**
	 * Creates a new instance of this class that represents a deletion of a list of tokens.
	 * 
	 * @param source the sequence data provider that fires this event
	 * @param sequenceID the ID of the affected sequence
	 * @param startIndex the index where the first token has been deleted from the sequence
	 * @param removedTokens the tokens that have been deleted
	 * @return a new instance with the same token type as {@code source}
	 */
	public static <T> TokenChangeEvent<T> newRemoveInstance(AlignmentModel<T> source, String sequenceID,
			int startIndex, Collection<? extends T> removedTokens) {
		
		return new TokenChangeEvent<T>(source, sequenceID, ListChangeType.DELETION, startIndex, true, removedTokens, null);
	}

	
	/**
	 * Creates a new instance of this class that represents a deletion of a single token.
	 * 
	 * @param source the sequence data provider that fires this event
	 * @param sequenceID the ID of the affected sequence
	 * @param index the index where the token has been deleted from the sequence
	 * @param removedToken the token that has been deleted
	 * @return a new instance with the same token type as {@code source}
	 */
	public static <T> TokenChangeEvent<T> newRemoveInstance(AlignmentModel<T> source, String sequenceID,
			int index, T removedToken) {
		
		return newRemoveInstance(source, sequenceID, index, Collections.nCopies(1, removedToken));
	}

	
	/**
	 * Creates a new instance of this class that represents a replacement of a list of tokens.
	 * 
	 * @param source the sequence data provider that fires this event
	 * @param sequenceID the ID of the affected sequence
	 * @param startIndex the index where the first token was replaced in the sequence
	 * @param replacedTokens the list of tokens that have been replaced 
	 * @return a new instance with the same token type as {@code source}
	 */
	public static <T> TokenChangeEvent<T> newReplaceInstance(AlignmentModel<T> source, String sequenceID,
			int startIndex, Collection<? extends T> replacedTokens) {
		
		return new TokenChangeEvent<T>(source, sequenceID, ListChangeType.REPLACEMENT, startIndex, true, replacedTokens, null);
	}
	
	
	public static <T> TokenChangeEvent<T> newReplaceInstance(AlignmentModel<T> source, String sequenceID,
			int startIndex, Collection<? extends T> replacedTokens, Collection<? extends T> newToken) {
	
		return new TokenChangeEvent<T>(source, sequenceID, ListChangeType.REPLACEMENT, startIndex, true, replacedTokens, newToken);
	}


	/**
	 * Creates a new instance of this class that represents a replacement of a single token.
	 * 
	 * @param source the sequence data provider that fires this event
	 * @param sequenceID the ID of the affected sequence
	 * @param index the index where the token shall be inserted into the sequence
	 * @param replacedToken the list of tokens that have been replaced 
	 * @return a new instance with the same token type as {@code source}
	 */
	public static <T> TokenChangeEvent<T> newReplaceInstance(AlignmentModel<T> source, String sequenceID,
			int index, T replacedToken, T newTokens) {
		
		return newReplaceInstance(source, sequenceID, index, Collections.nCopies(1, replacedToken),Collections.nCopies(1, newTokens));
	}


	/**
	 * Returns the (former) index of the first affected token.
	 * 
	 * @return a value greater or equal to zero (The first token of sequence has the index 0.)
	 */
	public int getStartIndex() {
		return startIndex;
	}


	/**
	 * Determines whether inserted tokens were added left bound or right bound.
	 * <p>
	 * This property only has a defined meaning if this event models the insertion of tokens. It is relevant for data areas that
	 * need to adjust to sequence changes. 
	 * 
	 * @return {@code true} if the insertion was left bound, {@code false} otherwise
	 */
	public boolean isLeftBound() {
		return leftBound;
	}


	/**
	 * Returns a collection of tokens that have been affected by this operation. For insertions this would
	 * be the new tokens and for replacements and deletions this would be the removed tokens.
	 * 
	 * @return a collection of sequence elements
	 */
	public Collection<? extends T> getAffectedTokens() {
		return affectedTokens;
	}
	
	
	/**
	 * Returns the first affected token. Convenience method if only one element is affected.
	 * 
	 * @return the fist affected element or {@code null} if {@link #getAffectedElements()} return an empty list
	 */
	public Object getAffectedToken() {
		if (!getAffectedTokens().isEmpty()) {
			return getAffectedTokens().iterator().next();
		}
		else {
			return null;
		}
	}
	
	
	public Collection<? extends T> getNewTokens() {
		return newTokens;
	}


	@Override
	public TokenChangeEvent<T> cloneWithNewSource(AlignmentModel<T> source) {
		return (TokenChangeEvent<T>)super.cloneWithNewSource(source);
	}


	@Override
	public TokenChangeEvent<T> clone() {
		return (TokenChangeEvent<T>)super.clone();
	}
}
