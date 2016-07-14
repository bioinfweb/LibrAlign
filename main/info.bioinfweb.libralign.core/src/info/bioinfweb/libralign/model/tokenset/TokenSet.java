/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
package info.bioinfweb.libralign.model.tokenset;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.CharacterSymbolMeaning;
import info.bioinfweb.commons.bio.CharacterSymbolType;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.adapters.CharSequenceAdapter;
import info.bioinfweb.libralign.model.adapters.StringAdapter;

import java.util.Set;

import javax.swing.KeyStroke;



/**
 * This interface should be implemented by all classes specifying a set of tokens (sequence elements, e.g.
 * nucleotides) that can be aligned in an {@link AlignmentArea} and are provided by an implementation of
 * {@link AlignmentModel}.
 * <p>
 * Implementing classes define a set of valid tokens that may be contained in an alignment and define
 * an optional keyboard shortcut a user of LibrAlign can use to insert the according token into an alignment.
 * <p>
 * Note that implementations may also represent a set of continuous values. In this case the implementation
 * should return {@code true} for {@link #isContinuous()}, {@code null} for {@link #iterator()},
 * {@link Integer#MAX_VALUE} for {@link #size()} and {@code false} for {@link #isEmpty()}. The optional add
 * and remove operations may throw {@link UnsupportedOperationException}s.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> the type of sequence elements (tokens) used in the sequences of an {@link AlignmentArea}
 */
public interface TokenSet<T> extends Set<T>, Cloneable {
	/**
	 * Returns the token from this set that is associated with the specified key stroke. This key can be 
	 * pressed to insert the specified token into an {@link AlignmentArea}. 
	 * <p>
	 * Note that the key character is only used by {@link AlignmentArea} if it does not collide with a default
	 * keyboard action. (Usually the key character can be any lower or upper case letter or digit.)
	 * 
	 * @param key - the key stroke (key code and shift mask) the user can press to insert the returned token 
	 *        into an {@link AlignmentArea}
	 * @return the associated token (sequence element) or {@code null} if none is defined
	 */
	public T tokenByKeyStroke(KeyStroke key);
	
	/**
	 * Returns the representation string of the specified token that shall be displayed in an 
	 * {@link AlignmentArea} or written into an alignment file.
	 * <p>
	 * Note that some {@link CharSequenceAdapter}s and {@link StringAdapter}s only use the first character
	 * of the returned string to represent a token.
	 * 
	 * @param token - the token associated with the representation
	 * @return a string which is at least one character long an empty string if no token in this set has the
	 *         specified representation
	 */
	public String representationByToken(T token);
	
	/**
	 * Returns the token associated with the specified string representation used to display the token in
	 * an {@link AlignmentArea} or an alignment file.
	 * 
	 * @param representation the string representation of the token
	 * @return the according token or {@code null} if none is defined
	 */
	public T tokenByRepresentation(String representation);
	
	/**
	 * Calculates the maximal length of a representation returned by {@link #representationByToken(Object)}
	 * of all tokens contained in this set.
	 * 
	 * @return a value >= 1 (since all representations must be at least one character long)
	 */
	public int maxRepresentationLength();
	
	/**
	 * Checks if all representation strings returned by {@link #representationByToken(Object)}
	 * have the same length.
	 * 
	 * @return {@code true} if all lengths are equal, {@code false} otherwise
	 */
  public boolean representationLengthEqual();

  /**
	 * An description of the specified token (e.g. the full name of the compound like "Adenine"). 
	 * 
	 * @param token - the token that shall be described
	 * @return a string containing no line breaks
	 */
	public String descriptionByToken(T token);
	
	/**
	 * Determines the type of tokens that are defined by this instance.
	 * 
	 * @return the token set type
	 */
	public CharacterStateSetType getType();
	
	/**
	 * Tests if the specified token represents a gap. This method is e.g. used by data areas to determine the positions 
	 * of gaps.
	 * <p>
	 * Note that it's legal to have multiple tokens to be considered as gaps, but one of them must be defined as the default
	 * gap tokens. When alignments are written to files (e.g. using <i>JPhyloIO</i>), some formats may accept only one of these 
	 * tokens as a gap and the others may be represented as additional character states.
	 * 
	 * @param token the token to be tested
	 * @return {@code true} if the specified token is recognized as a gap, {@code false} otherwise.
	 */
	public boolean isGapToken(T token);
	
	/**
	 * Returns the default object that represents a gap in this token set.
	 * 
	 * @return the according gap object (Implementations may create a new instance with every call of this method
	 *         or always return the same instance. In general gap objects should not have any mutable object 
	 *         specific properties to avoid problems if the same instance if located at multiple positions in an
	 *         alignment.)
	 */
	public T getGapToken();
	
	/**
	 * Determines whether {@link #tokenByKeyStroke(KeyStroke)} shall return the gap token, if the space bar is
	 * pressed, even if that key stroke is not associated with that token.
	 * 
	 * @return {@code true} if the gap token shall always be returned, {@code false} otherwise.
	 */
	public boolean isSpaceForGap();
	
	/**
	 * Allows to specify whether {@link #tokenByKeyStroke(KeyStroke)} shall return the gap token, if the space 
	 * bar is pressed, even if that key stroke is not associated with that token.
	 * 
	 * @param spaceForGap Specify {@code true} here, if the gap token shall always be returned, {@code false} 
	 *        otherwise.
	 */
	public void setSpaceForGap(boolean spaceForGap);
	
	/**
	 * Tests if the specified token represents an unknown position (usually '?'). This method is e.g. used by data areas to 
	 * determine the positions of gaps.
	 * <p>
	 * Note that it's legal to have multiple tokens to be considered as unknown symbols, but one of them must be defined as 
	 * the default. When alignments are written to files, some formats may accept only one   
	 * 
	 * @param token the token to be tested
	 * @return {@code true} if the specified token is recognized as an unknown position, {@code false} otherwise.
	 */
	public boolean isMissingInformationToken(T token);
	
	/**
	 * Returns the default object that represents an unknown position in this token set.
	 * 
	 * @return the according object (Implementations may create a new instance with every call of this method
	 *         or always return the same instance. In general objects should not have any mutable object 
	 *         specific properties to avoid problems if the same instance if located at multiple positions in an
	 *         alignment.)
	 */
	public T getMissingInformationToken();
	
	/**
	 * Returns the meaning of the specified symbol. (Note that <i>LibrAlign</i> models will usually not contain match tokens 
	 * directly).
	 * 
	 * @param token the token to determine the meaning from
	 * @return the meaning of the specified token
	 */
	public CharacterSymbolMeaning getMeaning(T token);
	
	/**
	 * Returns the type of the specified token (symbol). Symbols can either represent an atomic token or a polymorphic state
	 * or a set of tokens, if the the position is uncertain. 
	 * 
	 * @param token the symbol to be tested
	 * @return the type of the specified symbol
	 */
	public CharacterSymbolType getSymbolType(T token);
	
	/**
	 * Returns a deep copy of this instance. Implementing this method is important when creating custom
	 * implementations of this interface, because other classes in LibrAlign use this method internally. 
	 * 
	 * @return an identical token set
	 */
	public TokenSet<T> clone();
}
