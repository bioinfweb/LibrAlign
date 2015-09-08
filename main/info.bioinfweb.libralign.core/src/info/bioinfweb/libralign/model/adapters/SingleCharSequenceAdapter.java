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
package info.bioinfweb.libralign.model.adapters;


import info.bioinfweb.commons.Math2;
import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * An adapter class allowing to view a single sequence provided by an implementation of 
 * {@link AlignmentModel} as a {@link CharSequence}.
 * <p>
 * Note that the underlying sequence data provider is directly used to that all changes in that provider
 * are also reflected by the instance of this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type that is used by the underlying sequence data provider
 */
public class SingleCharSequenceAdapter<T> extends AbstractSingleSequenceDataAdapter<T> 
    implements CharSequence, SingleSequenceDataAdapter<T> {
	
	private boolean cutLongRepresentations;
	

	/**
	 * Creates a new instance of this class specifying a subsequence to be viewed.
	 * 
	 * @param provider - the sequence data provider that contains the sequence to be viewed
	 * @param sequenceID - the ID used in {@code provider} of the sequence to be viewed
	 * @param offset - the start index of the subsequence to be viewed (The first token has the index 0.)
	 * @param length - the length of the subsequence to be viewed
	 * @param cutLongRepresentations - Specify {@code true} here if tokens that have a string representation 
	 *        that is not exactly one character long shall be exported with the first character of their
	 *        representation or {@code false} if an exception shall be thrown if such a token is found.
	 */
	public SingleCharSequenceAdapter(AlignmentModel<T> provider, int sequenceID, int offset, int length,
			boolean cutLongRepresentations) {
		
		super(provider, sequenceID, offset, length);
		this.cutLongRepresentations = cutLongRepresentations;
	}


	/**
	 * Creates a new instance of this class specifying a whole sequence to be viewed.
	 * 
	 * @param provider - the sequence data provider that contains the sequence to be viewed
	 * @param sequenceID - the ID used in {@code provider} of the sequence to be viewed
	 * @param cutLongRepresentations - Specify {@code true} here if tokens that have a string representation 
	 *        that is not exactly one character long shall be exported with the first character of their
	 *        representation or {@code false} if an exception shall be thrown if such a token is found.
	 */
	public SingleCharSequenceAdapter(AlignmentModel<T> provider, int sequenceID, 
			boolean cutLongRepresentations) {
		
		this(provider, sequenceID, 0, Integer.MAX_VALUE, cutLongRepresentations);  // The real length will always be returned by length() since it will be smaller.
	}


	/**
	 * The return values indicates whether string representations of tokens that are not exactly one character
	 * long are allowed.
	 * 
	 * @return {@code true} if tokens that have a string representation that is not exactly one character long 
	 *         shall be exported with the first character of their representation or {@code false} if an exception 
	 *         shall be thrown if such a token is found
	 */
	public boolean isCutLongRepresentations() {
		return cutLongRepresentations;
	}


	/**
	 * Returns the character representation of the token in the underlying data source at the specified position.
	 * 
	 * @see java.lang.CharSequence#charAt(int)
	 * @throws InvalidUnderlyingTokenException if the underlying token has a string representation that is 
	 *         not exactly one character long and {@link #isCutLongRepresentations()} was set to {@code false}
	 * @throws IndexOutOfBoundsException if {@code index} was {@code < 0} or {@code >= }{@link #length()} 
	 */
	@Override
	public char charAt(int index) throws InvalidUnderlyingTokenException, IndexOutOfBoundsException {
		if (Math2.isBetween(index, 0, length() - 1)) {
			T token = getUnderlyingModel().getTokenAt(getSequenceID(), getOffset() + index); 
			String representation = getUnderlyingModel().getTokenSet().representationByToken(token);
			if ((representation.length() == 1) || ((representation.length() > 0) && isCutLongRepresentations())) {
				return representation.charAt(0);
			}
			else {
				throw new InvalidUnderlyingTokenException(this, token, representation);
			}
		}
		else {
			throw new IndexOutOfBoundsException("Invalid index " + index + ".");
		}
	}

	
	/**
	 * This method delegates to {@link #getLength()}.
	 * 
	 * @return the number of characters in this sequence
	 */
	@Override
	public int length() {
		return getLength();
	}


	@Override
	public CharSequence subSequence(int start, int end) {
		if (Math2.isBetween(start, 0, end) && Math2.isBetween(end, start, length())) {
			return new SingleCharSequenceAdapter<T>(getUnderlyingModel(), getSequenceID(), start, end - start, 
					isCutLongRepresentations());
		}
		else {
			throw new IndexOutOfBoundsException("Invalid sequence bounds (" + start + ", " + end + ").");
		}
	}


	/**
	 * Returns a copy of this character sequence as a string.
	 * 
	 * @return the string representation of this object
	 * 
	 * @throws InvalidUnderlyingTokenException if one token in the underlying data source has a string 
	 *         representation that is not exactly one character long and {@link #isCutLongRepresentations()} 
	 *         was set to {@code false}
	 */
	@Override
	public String toString() {
		return new StringBuffer(this).toString();  //TODO Check if the constructor of StringBuffer does not use the method recursively.
	}
}
