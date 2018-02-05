/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.adapters;


import java.util.Iterator;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.order.SequenceOrder;
import info.bioinfweb.libralign.model.AlignmentModel;

import org.biojava3.core.sequence.MultipleSequenceAlignment;
import org.biojava3.core.sequence.template.Compound;
import org.biojava3.core.sequence.template.CompoundSet;
import org.biojava3.core.sequence.template.LightweightProfile;
import org.biojava3.core.sequence.template.Sequence;



/**
 * Adapter class that allows to access the contents of an implementation of {@link AlignmentModel}
 * as a set of BioJava {@link Sequence} objects.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 * @bioinfweb.module info.bioinfweb.libralign.biojava3
 *
 * @param <T> - the token type used by the underlying provider
 * @param <C> - the compound class to be used in the returned sequences
 */
public class BioJava3SequenceAdapter<T, C extends Compound> extends AbstractBasicAlignmentModelView<T> 
    implements SequenceDataAdapter<SingleBioJava3SequenceAdapter<T, C>, T> {

	private CompoundSet<C> compoundSet;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param provider - the sequence data provider that contains the sequences to be viewed
	 * @param compoundSet - the compound set to be used for the translation
   */
	public BioJava3SequenceAdapter(AlignmentModel<T> underlyingProvider, CompoundSet<C> compoundSet) {
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
	public SingleBioJava3SequenceAdapter<T, C> getSequence(String sequenceID) {
		return new SingleBioJava3SequenceAdapter<T, C>(getUnderlyingModel(), sequenceID, getCompoundSet());
	}

	
	/**
	 * Returns an implementation of {@code Sequence} that acts as a view to a subsequence of the sequence 
	 * with the specified ID in the underlying data source.
	 * 
	 * @param sequenceID - the ID of the sequence to be viewed
	 * @return a subsequence as an {@link Sequence} object
	 */
	@Override
	public SingleBioJava3SequenceAdapter<T, C> getSubSequence(String sequenceID, int offset, int length) {
		return new SingleBioJava3SequenceAdapter<T, C>(getUnderlyingModel(), sequenceID, offset, length, 
				getCompoundSet());
	}
	
	
	/**
	 * Returns a BioJava alignment that contains the views of all sequences contained in the underlying data
	 * source in the order they are stored there.
	 * 
	 * @return an instance of {@link MultipleSequenceAlignment} (Note that the implementation type of 
	 *         {@link LightweightProfile} might change in future releases of LibrAlign.)
	 */
	public LightweightProfile<Sequence<C>, C> toLightweightProfile() {
		MultipleSequenceAlignment<Sequence<C>, C> result = new MultipleSequenceAlignment<Sequence<C>, C>();
		Iterator<String> iterator = getUnderlyingModel().sequenceIDIterator();
		while (iterator.hasNext()) {
			result.addAlignedSequence(getSequence(iterator.next()));
		}
		return result;
	}

	
	/**
	 * Returns a BioJava alignment that contains the views of all sequences contained in the underlying data
	 * source in the order specified in the {@code order} object. This method can be used to obtain an BioJava
	 * alignment with the same order as an {@link AlignmentArea} area displaying these sequences.
	 * 
	 * @param order - the object specifying the order of the sequences in the returned alignment
	 * @return an instance of {@link MultipleSequenceAlignment} (Note that the implementation type of 
	 *         {@link LightweightProfile} might change in future releases of LibrAlign.)
	 */
	public LightweightProfile<Sequence<C>, C> toLightweightProfile(SequenceOrder order) {
		MultipleSequenceAlignment<Sequence<C>, C> result = new MultipleSequenceAlignment<Sequence<C>, C>();
		for (int i = 0; i < getUnderlyingModel().getSequenceCount(); i++) {
			result.addAlignedSequence(getSequence(order.idByIndex(i)));
		}
		return result;
	}
	
	
	// There is currently no toProfile() method, since Profile expects a collection of aligned sequences, which
	// need information on the alignment steps that lead to this alignment.
	
	
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
