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
package info.bioinfweb.libralign.sequenceprovider.tokenset;


import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;

import java.util.Set;



/**
 * This interface should be implemented by all classes specifying a set of tokens (sequence elements, e.g.
 * nucleotides) that can be aligned in an {@link AlignmentArea} and are provided by an implementation of
 * {@link SequenceDataProvider}.
 * <p>
 * Implementing classes define a set of valid tokens that may be contained in an alignment and define
 * an optional keyboard shortcut a user of LibrAlign can use to insert the according token into an alignment. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the type of sequence elements (tokens) used in the sequences of an {@link AlignmentArea}
 */
public interface TokenSet<T> extends Set<T> {
	/**
	 * Returns the character on the keyboard that can be pressed to insert the specified token into an
	 * {@link AlignmentArea}. 
	 * <p>
	 * Note that special
	 * 
	 * @param token - the token to be inserted
	 * @return a character
	 */
	public char keyCharByToken(T token);
	
	public String representationByToken(T token);
	
	public String descriptionByToken(T token);
}
