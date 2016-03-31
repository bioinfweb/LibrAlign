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
package info.bioinfweb.libralign.model.tokenset;


import java.util.Iterator;



/**
 * Provides static methods useful for implementing {@link TokenSet}.
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
		return Character.toString(AbstractTokenSet.DEFAULT_GAP_REPRESENTATION).equals(tokenSet.representationByToken(token));
	}
	
	
	/**
	 * Tests if the specified token represents an unknown position in the specified token set. 
	 * 
	 * @param tokenSet the token set containing the specified token
	 * @param token the token to be tested
	 * @return {@code true} if the specified token is recognized as an unknown position symbol, {@code false} otherwise.
	 */
	public static <T> boolean isUnknownPositionToken(TokenSet<T> tokenSet, T token) {
		return Character.toString(AbstractTokenSet.DEFAULT_MISSING_INFORMATION_REPRESENTATION).equals(tokenSet.representationByToken(token));
	}
}
