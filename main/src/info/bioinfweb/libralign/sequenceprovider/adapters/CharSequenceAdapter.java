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
 * Adapter class that allows to access the contents of an implementation of {@link SequenceDataProvider}
 * as a set of {@link CharSequence}s or {@link String}s.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type used by the underlying provider
 */
public class CharSequenceAdapter<T> extends AbstractSequenceDataAdapter<T> {
	private boolean cutLongRepresentations;
	
	
	public CharSequenceAdapter(SequenceDataProvider<T> underlyingProvider, boolean cutLongRepresentations) {
		super(underlyingProvider);
		this.cutLongRepresentations = cutLongRepresentations;
	}
	
	
	public boolean isCutLongRepresentations() {
		return cutLongRepresentations;
	}


	/**
	 * Returns an implementation of {@link CharSequence} that acts as a view to the sequence with the
	 * specified ID.
	 * <p>
	 * This method should be favored over {@link #toString(int)} of no copy of the sequence is needed to
	 * save resources.  
	 * 
	 * @param sequenceID - the ID of the sequence to be viewed
	 * @return a sequence as a {@link CharSequence}
	 */
	public SingleCharSequenceAdapter<T> asCharSequence(int sequenceID) {
		return new SingleCharSequenceAdapter<T>(getUnderlyingProvider(), sequenceID, isCutLongRepresentations());
	}


	/**
	 * Returns the sequence with the specified ID as a string. Note that the string is a copy of the sequence
	 * stored in the underlying data source and uses additional memory. Therefore {@link #asCharSequence(int)} #
	 * should be used instead if possible.
	 * 
	 * @param sequenceID - the ID of the sequence to be viewed
	 * @return a copy of the specified sequence as a string
	 * 
	 * @throws InvalidStringRepresentationException if one token in the underlying data source has a string 
	 *         representation that is not exactly one character long and {@link #isCutLongRepresentations()} 
	 *         was set to {@code false}
	 */
	public String toString(int sequenceID) {
		return asCharSequence(sequenceID).toString();
	}
}
