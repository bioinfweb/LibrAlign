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
package info.bioinfweb.libralign.model.tokenset;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.CharacterSymbolMeaning;
import info.bioinfweb.commons.bio.CharacterSymbolType;
import info.bioinfweb.commons.bio.SequenceUtils;

import java.util.Iterator;



/**
 * Provides tool methods useful for implementing {@link TokenSet}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class TokenSetTools {
  /**
	 * Calculates the maximal length of a representation returned by {@link #representationByToken(Object)}
	 * of all tokens contained in the specified set.
	 * 
   * @param tokenSet - the set to be examined
	 * @return Since all representations must be at least one character long the returned string should have
	 *         a length >= 1.
   */
	@SuppressWarnings({"rawtypes", "unchecked"})
  public static int maxRepresentationLength(TokenSet tokenSet) {
  	int result = 0;
  	Iterator iterator = tokenSet.iterator();
  	while (iterator.hasNext()) {
  		result = Math.max(result, tokenSet.representationByToken(iterator.next()).length());
  	}
  	return result;
  }


	/**
	 * Checks if all representation strings returned by {@link #representationByToken(Object)}
	 * have the same length.
	 * 
   * @param tokenSet - the set to be examined
	 * @return {@code true} if all lengths are equal, {@code false} otherwise
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
  public static boolean representationLengthEqual(TokenSet tokenSet) {
  	Iterator iterator = tokenSet.iterator();
  	if (iterator.hasNext()) {
    	int length = tokenSet.representationByToken(iterator.next()).length();
    	while (iterator.hasNext()) {
    		if (length != tokenSet.representationByToken(iterator.next()).length()) {
    			return false;
    		}
    	}
  	}
 		return true;
  }
	
	
	/**
	 * Tests if the specified token represents a gap in the specified token set. 
	 * 
	 * @param tokenSet the token set containing the specified token
	 * @param token the token to be tested
	 * @return {@code true} if the specified token is recognized as a gap, {@code false} otherwise.
	 */
	public static <T> boolean isGapToken(TokenSet<T> tokenSet, T token) {
		return Character.toString(SequenceUtils.GAP_CHAR).equals(tokenSet.representationByToken(token));
	}
	
	
	/**
	 * Tests if the specified token represents missing data in the specified token set. 
	 * 
	 * @param tokenSet the token set containing the specified token
	 * @param token the token to be tested
	 * @return {@code true} if the specified token is recognized as a missing data symbol, {@code false} otherwise.
	 */
	public static <T> boolean isMissingInformationToken(TokenSet<T> tokenSet, T token) {
		return Character.toString(SequenceUtils.MISSING_DATA_CHAR).equals(tokenSet.representationByToken(token));
	}
	
	
	/**
	 * Returns {@link CharacterSymbolType#UNCERTAIN} for all missing information tokens. If {@link TokenSet#getType()} of 
	 * {@code tokenSet} specifies a  nucleotide or amino acid token set, {@link CharacterSymbolType#UNCERTAIN} is returned for 
	 * all tokens which have a IUPAC ambiguity code string representation (as returned by 
	 * {@link TokenSet#representationByToken(Object)}). In all other cases {@link CharacterSymbolType#ATOMIC_STATE} is returned.
	 * 
	 * @param tokenSet the token set containing the specified token
	 * @param token the token to be tested
	 * @return the symbol type
	 */
	public static <T> CharacterSymbolType getSymbolType(TokenSet<T> tokenSet, T token) {
		if (tokenSet.isMissingInformationToken(token)) {
			return CharacterSymbolType.UNCERTAIN;
		}  //TODO Should gaps also be treated as uncertain (like they are in NeXML)?
		else {
			String representation = tokenSet.representationByToken(token);
			if (representation.length() == 1) {
				char c = representation.charAt(0);
				if (tokenSet.getType().isNucleotide() && SequenceUtils.isNucleotideAmbuguityCode(c)) {
					return CharacterSymbolType.UNCERTAIN;
				}
			}
			else if (tokenSet.getType().equals(CharacterStateSetType.AMINO_ACID) && SequenceUtils.isAminoAcidAmbiguityCode(representation)) {
				return CharacterSymbolType.UNCERTAIN;
			}
			return CharacterSymbolType.ATOMIC_STATE;
		}
	}
	
	
	/**
	 * Determines the meaning of the specified token as defined by {@link TokenSet#getMeaning(Object)}.
	 * <p>
	 * This default implementation will return {@link CharacterSymbolMeaning#GAP} for all tokens which are positively tested
	 * by {@link TokenSet#isGapToken(Object)} and {@link CharacterSymbolMeaning#MISSING} for all tokens which are positively 
	 * tested by {@link TokenSet#isMissingInformationToken(Object)}. In all other cases 
	 * {@link CharacterSymbolMeaning#CHARACTER_STATE} is returned.
	 * 
	 * @param tokenSet the token set containing the specified token
	 * @param token the token to be tested
	 * @return the symbol meaning
	 */
	public static <T> CharacterSymbolMeaning getMeaning(TokenSet<T> tokenSet, T token) {
		if (tokenSet.isGapToken(token)) {
			return CharacterSymbolMeaning.GAP;
		}
		else if (tokenSet.isMissingInformationToken(token)) {
			return CharacterSymbolMeaning.MISSING;
		}
		else {
			return CharacterSymbolMeaning.CHARACTER_STATE;
		}
	}
}
