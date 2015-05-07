/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben StÃ¶ver
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
package info.bioinfweb.libralign.alignmentmodel;


import info.bioinfweb.libralign.alignmentmodel.tokenset.TokenSet;



/**
 * Interface to be implemented by all classes that provide a part of an alignment (a set of subsequence 
 * columns) based on a single token set.
 * <p>
 * Different implementations of this interface using different token sets can be concatenated to a single 
 * alignment by {@link DefaultConcatenatedAlignmentModel}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T>
 */
public interface AlignmentPartModel<T> extends AlignmentModel<T> {
	/**
	 * Returns the owning model concatenating all part models.
	 * 
	 * @return the concatenated alignment model
	 */
	public ConcatenatedAlignmentModel getOwner();	
	
	/**
	 * Returns the token set which is supported by the implementation.
	 * 
	 * @return a token set containing all valid tokens
	 */
	public TokenSet<T> getTokenSet();
	
	/**
	 * Replaces the current token set with the specified one.
	 * 
	 * @param set - the new token set to be used
	 */
	public void setTokenSet(TokenSet<T> set);
}
