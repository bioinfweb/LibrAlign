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


import info.bioinfweb.commons.Math2;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProviderAdapter;



/**
 * An adapter class allowing to view a single sequence provided by an implementation of 
 * {@link SequenceDataProvider} as a {@link CharSequence}.
 * <p>
 * Note that the underlying sequence data provider is directly used to that all changes in that provider
 * are also reflected by the instance of this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type that is used by the underlying sequence data provider
 */
public class SingleCharSequenceAdapter<T> implements CharSequence, SequenceDataProviderAdapter<T> {
	private SequenceDataProvider<T> provider;
	private int sequenceID;
	private int offset;
	private int length;
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
	public SingleCharSequenceAdapter(SequenceDataProvider<T> provider, int sequenceID, int offset, int length,
			boolean cutLongRepresentations) {
		
		super();
		this.provider = provider;
		this.sequenceID = sequenceID;
		this.offset = offset;
		this.length = length;
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
	public SingleCharSequenceAdapter(SequenceDataProvider<T> provider, int sequenceID, 
			boolean cutLongRepresentations) {
		
		super();
		this.sequenceID = sequenceID;
		this.provider = provider;
		this.offset = 0;
		this.length = Integer.MAX_VALUE;  // The real length will always be returned by length() since it will be smaller.
		this.cutLongRepresentations = cutLongRepresentations;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.sequenceprovider.SequenceDataProviderAdapter#getUnderlyingProvider()
	 */
	@Override
	public SequenceDataProvider<T> getUnderlyingProvider() {
		return provider;
	}


	/**
	 * The ID of the viewed sequence in the underlying sequence data provider.
	 * 
	 * @return the sequence ID
	 */
	public int getSequenceID() {
		return sequenceID;
	}


	/**
	 * Returns the index the first character in this sequence corresponds to in the original sequence in
	 * the underlying sequence data provider.
	 * 
	 * @return a value >= 0
	 */
	public int getOffset() {
		return offset;
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
	 * @throws InvalidStringRepresentationException if the underlying token has a string representation that is 
	 *         not exactly one character long and {@link #isCutLongRepresentations()} was set to {@code false}
	 * @throws IndexOutOfBoundsException if {@code index} was {@code < 0} or {@code >= }{@link #length()} 
	 */
	@Override
	public char charAt(int index) {
		if (Math2.isBetween(index, 0, length() - 1)) {
			T token = provider.getTokenAt(sequenceID, offset + index); 
			String representation = provider.getTokenSet().representationByToken(token);
			if ((representation.length() == 1) || ((representation.length() > 0) && isCutLongRepresentations())) {
				return representation.charAt(0);
			}
			else {
				throw new InvalidStringRepresentationException(this, token, representation);
			}
		}
		else {
			throw new IndexOutOfBoundsException("Invalid index " + index + ".");
		}
	}

	
	@Override
	public int length() {
		return Math.min(length, Math.max(0, provider.getSequenceLength(sequenceID) - offset));
	}

	
	@Override
	public CharSequence subSequence(int start, int end) {
		if (Math2.isBetween(start, 0, end) && Math2.isBetween(end, start, length())) {
			return new SingleCharSequenceAdapter<T>(provider, sequenceID, start, end - start, 
					isCutLongRepresentations());
		}
		else {
			throw new IndexOutOfBoundsException("Invalid sequence bounds (" + start + ", " + end + ").");
		}
	}


	@Override
	public String toString() {
		return new StringBuffer(this).toString();  //TODO Check if the constructor of StringBuffer does not use the method recursively.
	}
}
