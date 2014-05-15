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

import org.biojava3.core.sequence.template.Compound;
import org.biojava3.core.sequence.template.CompoundSet;
import org.biojava3.core.sequence.template.Sequence;



/**
 * Adapter class that allows to access the contents of an implementation of {@link SequenceDataProvider}
 * as a set of BioJava {@link Sequence} objects.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type used by the underlying provider
 * @param <C> - the compound class to be used in the returned sequences
 */
public class BioJavaSequenceAdapter<T, C extends Compound> extends AbstractSequenceDataAdapter<T> 
    implements SequenceDataAdapter<SingleBioJavaSequenceAdapter<T, C>, T> {

	private CompoundSet<C> compoundSet;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param provider - the sequence data provider that contains the sequences to be viewed
	 * @param compoundSet - the compound set to be used for the translation
   */
	public BioJavaSequenceAdapter(SequenceDataProvider<T> underlyingProvider, CompoundSet<C> compoundSet) {
		super(underlyingProvider);
		this.compoundSet = compoundSet;
	}

	
	/**
	 * Returns the compound set to be used for the translation.
	 * 
	 * @return the compound set that was specified in the constructor
	 */
	public CompoundSet<C> getCompoundSet() {
		return compoundSet;
	}


	/**
	 * Returns an implementation of {@code Sequence} that acts as a view to the sequence with the specified ID
	 * in the underlying data source.
	 * 
	 * @param sequenceID - the ID of the sequence to be viewed
	 * @return a sequence as an {@link Sequence} object
	 */
	@Override
	public SingleBioJavaSequenceAdapter<T, C> toSequence(int sequenceID) {
		return new SingleBioJavaSequenceAdapter<>(getUnderlyingProvider(), sequenceID, getCompoundSet());
	}

	
	/**
	 * Returns an implementation of {@code Sequence} that acts as a view to a subsequence of the sequence 
	 * with the specified ID in the underlying data source.
	 * 
	 * @param sequenceID - the ID of the sequence to be viewed
	 * @return a subsequence as an {@link Sequence} object
	 */
	@Override
	public SingleBioJavaSequenceAdapter<T, C> toSequence(int sequenceID, int offset, int length) {
		return new SingleBioJavaSequenceAdapter<>(getUnderlyingProvider(), sequenceID, offset, length, 
				getCompoundSet());
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
