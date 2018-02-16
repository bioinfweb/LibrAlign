/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.utils.indextranslation;


import info.bioinfweb.libralign.model.AlignmentModel;

import java.util.Set;



/**
 * Interface to be implemented by classes that can translate between alignment column indices and unalinged indices if the single
 * sequences within an alignment. Since different strategies may be used to do that, this interface provides an 
 * implementation-independent way to acces such translators.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <T> the token type used in the underlying alignment model
 */
public interface IndexTranslator<T> {
	/**
	 * Returns the alignment model this instance works on.
	 * 
	 * @return the associated alignment model
	 */
	public AlignmentModel<T> getModel();
	
	
	/**
	 * Returns the set of gap tokens used by this instance.  
	 * 
	 * @return a set of tokens to be considered as gaps
	 */
	public Set<T> getGapTokens();
	
	/**
	 * Returns the index of the sequence token at the specified alignment column. 
	 * 
	 * @param sequenceID the ID of the sequence which contains the token to be located
	 * @param alignedIndex the column index in the alignment
	 * @return an index relation object describing the position of respective sequence tokens
	 *         (If the specified alignment columns contains a gap, the {@link IndexRelation#getCorresponding()}
	 *         will return {@link IndexRelation#GAP}, and {@link IndexRelation#getBefore()} and 
	 *         {IndexRelation#getAfter()} the positions of the neighboring tokens, if present. If the specified
	 *         column contains a token, {@link IndexRelation#getCorresponding()} will return the index of that
	 *         token in the unaligned sequence.) 
	 */
	public IndexRelation getUnalignedIndex(String sequenceID, int alignedIndex);
	
	/**
	 * Returns the column in the alignment, the specified token is contained in
	 * 
	 * @param sequenceID the ID of the sequence which contains the token to be located
	 * @param unalignedIndex the index of the token in the unaligned sequence
	 * @return the column index in the alignment
	 */
	public int getAlignedIndex(String sequenceID, int unalignedIndex);
	
}