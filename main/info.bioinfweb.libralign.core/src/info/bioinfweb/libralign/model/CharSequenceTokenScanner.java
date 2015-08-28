/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model;


import java.util.List;

import info.bioinfweb.libralign.model.exception.InvalidTokenRepresentationException;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * A scanner that can subsequently parse token from a character sequence consisting of concatenated token
 * representations.
 * <p>
 * If token representations are allowed to be longer than one character, each representation must be separated 
 * by one ore more whitespace characters. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> the type of token to be parsed
 */
public class CharSequenceTokenScanner<T> {
	private CharSequence sequence;
	private TokenSet<T> tokenSet; 
	private boolean allowWhitespace;
	private T defaultToken;
	private boolean longTokens;
	private int position = 0;
	private T nextToken;
	boolean defaultTokenUsed = false;
	boolean defaultTokenUsedNext = false;
	boolean defaultTokenUsedNow = false;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param sequence the sequence to parse tokens from
	 * @param tokenSet the token set containing the tokens to be parsed
	 * @param allowWhitespace Specify {@code true} here to allow whitespace between token representations or {@code false}
	 *        if not. Note that specifying false will lead to a {@link IllegalArgumentException} if the specified token set
	 *        contains representations longer than one character.
	 * @param defaultToken a default token to be returned if invalid token representations are found (If {@code null}
	 *        is specified here, {@link #next()} will throw an exception if an invalid representation is found.)
	 * @throws IllegalArgumentException if {@code allowWhitespace} was set to {@code false} although {@code tokenSet}
	 *         contains token representations that are longer than one character
	 */
	public CharSequenceTokenScanner(CharSequence sequence, TokenSet<T> tokenSet, boolean allowWhitespace, T defaultToken) {
		super();
		this.sequence = sequence;
		this.tokenSet = tokenSet;
		this.allowWhitespace = allowWhitespace;
		this.defaultToken = defaultToken;
		
		longTokens = tokenSet.maxRepresentationLength() > 1;
		if (longTokens && !allowWhitespace) {
			throw new IllegalArgumentException("Whitespaces can only be disllowed if the the specified token set does not "
					+ "contains representations longer than one character.");
		}
		nextToken = readNextToken();
	}

	
	private String readNextRepresentation() {
		if (position >= sequence.length()) {
			return null;
		}
		else {
			if (longTokens) {
				StringBuilder result = new StringBuilder();
				while ((position < sequence.length()) && !Character.isWhitespace(sequence.charAt(position))) {
					result.append(sequence.charAt(position));
					position++;
				}
				
				while ((position < sequence.length()) && Character.isWhitespace(sequence.charAt(position))) {  // Skip trailing whitespace.
					position++;
				}
				
				if (result.length() > 0) {
					return result.toString();
				}
				else {
					return null;
				}
			}
			else {
				String result = Character.toString(sequence.charAt(position));
				position++;
				return result;
			}
		}
	}
	

	private T readNextToken() {
		defaultTokenUsedNext = false;
		String representation = readNextRepresentation();
		if (representation == null) {
			return null;
		}
		else {
			T result = tokenSet.tokenByRepresentation(representation);
			if (result == null) {
				if (allowWhitespace && (representation.length() == 1) && Character.isWhitespace(representation.charAt(0))) {
					return readNextToken();
				}
				else {
					if (defaultToken == null) {
						throw new InvalidTokenRepresentationException(tokenSet, representation);
					}
					else {
						defaultTokenUsedNext = true;
						return defaultToken;
					}
				}
			}
			else {
				return result;
			}
		}
	}
	
	
	/**
	 * Indicates whether an additional token can be parsed with the next call of {@link #next()}.
	 * 
	 * @return {@code true} if additional tokens can be parsed or {@code false} if the end of the underlying
	 *         character sequence has been reached
	 */
	public boolean hasNext() {
		return (nextToken != null);
	}
	
	
	/**
	 * Returns the next token from parsed from the underlying character sequence.
	 * <p>
	 * Token representations not found in the underlying token set are replaced by a default token, if one
	 * was specified. Use {@link #isDefaultTokenUsedNow()} to determine whether that was the case in the
	 * last call of the method.
	 * 
	 * @return the next token or {@code null} if the end of the underlying character sequence was reached.
	 * @throws InvalidTokenRepresentationException if no according token could be found for the current
	 *         string representation and no default token was defined 
	 */
	public T next() throws InvalidTokenRepresentationException {
		T result = nextToken;
		defaultTokenUsedNow = defaultTokenUsedNext;  // Must happen before readNextToken().
		defaultTokenUsed = defaultTokenUsed || defaultTokenUsedNow;
		nextToken = readNextToken();
		return result;
	}


	/**
	 * Indicates whether a string representation not found in the token set was replaced by the default token in the last 
	 * call of {@link #next()}.
	 * 
	 * @return {@code true} if an according replacement happened, {@code false} if no replacement needed to be done until now.
	 * @see #isDefaultTokenUsed()
	 */
	public boolean isDefaultTokenUsedNow() {
		return defaultTokenUsedNow;
	}


	/**
	 * Indicates whether a string representation not found in the token set was replaced by the default token in any call
	 * of {@link #next()} until now.
	 * 
	 * @return {@code true} if an according replacement happened, {@code false} if no replacement needed to be done until now.
	 * @see #isDefaultTokenUsedNow()
	 */
	public boolean isDefaultTokenUsed() {
		return defaultTokenUsed;
	}
}
