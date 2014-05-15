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
	

	/**
	 * Creates a new instance of this class specifying a subsequence to be viewed.
	 * 
	 * @param provider - the sequence data provider that contains the sequence to be viewed
	 * @param sequenceID - the ID used in {@code provider} of the sequence to be viewed
	 * @param offset - the start index of the subsequence to be viewed (The first token has the index 0.)
	 * @param length - the length of the subsequence to be viewed
	 */
	public SingleCharSequenceAdapter(SequenceDataProvider<T> provider, int sequenceID, int offset, int length) {
		super();
		this.provider = provider;
		this.sequenceID = sequenceID;
		this.offset = offset;
		this.length = length;
	}


	/**
	 * Creates a new instance of this class specifying a whole sequence to be viewed.
	 * 
	 * @param provider - the sequence data provider that contains the sequence to be viewed
	 * @param sequenceID - the ID used in {@code provider} of the sequence to be viewed
	 */
	public SingleCharSequenceAdapter(SequenceDataProvider<T> provider, int sequenceID) {
		super();
		this.sequenceID = sequenceID;
		this.provider = provider;
		this.offset = 0;
		this.length = Integer.MAX_VALUE;  // The real length will always be returned by length() since it will be smaller.
	}


	@Override
	public SequenceDataProvider<T> getUnderlyingProvider() {
		return provider;
	}


	public int getSequenceID() {
		return sequenceID;
	}


	public int getOffset() {
		return offset;
	}


	public int getLength() {
		return length;
	}


	@Override
	public char charAt(int index) {
		if (Math2.isBetween(index, 0, length() - 1)) {
			return provider.getTokenSet().charRepresentationByToken(provider.getTokenAt(sequenceID, offset + index));
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
			return new SingleCharSequenceAdapter<T>(provider, sequenceID, start, end - start);
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
