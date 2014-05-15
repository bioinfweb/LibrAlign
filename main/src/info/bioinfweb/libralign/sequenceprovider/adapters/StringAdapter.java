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


import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;



/**
 * Returns the sequences contained in the underlying data source as strings. Note that the returned string 
 * sequences are copies of the data, whereas {@link CharSequenceAdapter} returns views. Therefore 
 * {@link CharSequenceAdapter} should be used instead if possible for large sequences to save memory.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type of the underlying data source
 */
public class StringAdapter<T> extends CharSequenceAdapter<T> {
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param provider - the sequence data provider that contains the sequences to be viewed
	 * @param cutLongRepresentations - Specify {@code true} here if the first character of string 
	 *        representations (of tokens in the underlying sequence data provider) that are longer than 
	 *        one character shall be used for translations or {@code false} if an 
	 *        {@link InvalidUnderlyingTokenException} shall be thrown if such a token occurs.
   */
	public StringAdapter(SequenceDataProvider<T> underlyingProvider, boolean cutLongRepresentations) {
		super(underlyingProvider, cutLongRepresentations);
	}

	
	/**
	 * Returns a string copy of the sequence with the specified ID in the underlying data source.
	 * 
	 * @param sequenceID - the ID of the sequence to be viewed
	 * @return a string object
	 */
	@Override
	public String toSequence(int sequenceID) {
		return super.toSequence(sequenceID).toString();
	}


	/**
	 * Returns a string copy of a subsequence of the sequence with the specified ID in the underlying 
	 * data source.
	 * 
	 * @param sequenceID - the ID of the sequence to be viewed
	 * @param offset - the start index of the subsequence to be viewed (The first token has the index 0.)
	 * @param length - the length of the subsequence to be viewed
	 * @return a string object
	 */
	@Override
	public String toSequence(int sequenceID, int offset, int length) {
		return super.toSequence(sequenceID, offset, length).toString(); 
	}


	/**
	 * Returns {@code true} since this class always returns copies of the data in the underlying data source.
	 * 
	 * @return {@code true}
	 */
	@Override
	public boolean returnsCopies() {
		return true;
	}
}
