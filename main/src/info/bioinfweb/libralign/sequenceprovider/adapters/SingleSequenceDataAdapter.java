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
package info.bioinfweb.libralign.sequenceprovider.adapters;


import info.bioinfweb.libralign.sequenceprovider.SequenceDataAdapter;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;



/**
 * Adapter classes providing views of (sub-) sequences of single rows of alignment data provided by an 
 * implementation of {@link SequenceDataProvider} should implement this interface.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type used by the underlying sequence data provider
 */
public interface SingleSequenceDataAdapter<T> extends SequenceDataAdapter<T> {
	/**
	 * The ID of the viewed sequence in the underlying sequence data provider.
	 * 
	 * @return the sequence ID
	 */
	public int getSequenceID();

	/**
	 * Returns the index the first character in this sequence corresponds to in the original sequence in
	 * the underlying sequence data provider.
	 * 
	 * @return a value >= 0
	 */
	public int getOffset();
	
	/**
	 * Returns the number of tokens (sequence elements) that make up this view. Depending on the implementation
	 * this might differ from the length of the viewed (sub-) sequence (e.g. if a nucleotide sequence would be
	 * viewed as an amino acid sequence). 
	 * 
	 * @return a value >= 0
	 */
	public int length();
}
