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
package info.bioinfweb.libralign.sequenceprovider.adapters;


import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;



/**
 * Adapter class that allows to access the contents of an implementation of {@link SequenceDataProvider}
 * as a set of {@link CharSequence}s or {@link String}s.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type used by the underlying provider
 */
public class CharSequenceAdapter<T> extends AbstractSequenceDataAdapter<T> 
    implements SequenceDataAdapter<CharSequence, T> {
	
	private boolean cutLongRepresentations;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param provider - the sequence data provider that contains the sequences to be viewed
	 * @param cutLongRepresentations - Specify {@code true} here if the first character of string 
	 *        representations (of tokens in the underlying sequence data provider) that are longer than 
	 *        one character shall be used for translations or {@code false} if an 
	 *        {@link InvalidUnderlyingTokenException} shall be thrown if such a token occurs.
   */
	public CharSequenceAdapter(SequenceDataProvider<T> underlyingProvider, boolean cutLongRepresentations) {
		super(underlyingProvider);
		this.cutLongRepresentations = cutLongRepresentations;
	}
	
	
	/**
	 * Returns {@code true} if the first character of string representations (of tokens in the underlying 
	 * sequence data provider) that are longer than one character shall be used for translations or 
	 * {@code false} if an {@link InvalidUnderlyingTokenException} shall be thrown if such a token occurs.
	 * 
	 * @return the value that was specified for {@code cutLongRepresentations} in the constructor
	 */
	public boolean isCutLongRepresentations() {
		return cutLongRepresentations;
	}


	/**
	 * Returns an implementation of {@code SingleCharSequenceAdapter<T>} that acts as a view to the sequence 
	 * with the specified ID.
	 * 
	 * @param sequenceID - the ID of the sequence to be viewed
	 * @return a sequence as a {@link CharSequence}
	 */
	@Override
	public CharSequence getSequence(int sequenceID) {
		return new SingleCharSequenceAdapter<T>(getUnderlyingProvider(), sequenceID, isCutLongRepresentations());
	}


	/**
	 * Returns an implementation of {@code SingleCharSequenceAdapter<T>} that acts as a view to a subsequence
	 * of the sequence with the specified ID.
	 * 
	 * @param sequenceID - the ID of the sequence to be viewed
	 * @param offset - the start index of the subsequence to be viewed (The first token has the index 0.)
	 * @param length - the length of the subsequence to be viewed
	 * @return a sequence as a {@link CharSequence}
	 */
	@Override
	public CharSequence getSubSequence(int sequenceID, int offset, int length) {
		return new SingleCharSequenceAdapter<T>(getUnderlyingProvider(), sequenceID, offset, length, 
				isCutLongRepresentations());
	}


	/**
	 * Returns {@code false} since this class always returns views of the underlying data source.
	 * 
	 * @return {@code false}
	 */
	@Override
	public boolean returnsCopies() {
		return false;
	}
}