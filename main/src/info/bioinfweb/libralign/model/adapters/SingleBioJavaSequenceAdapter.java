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


import java.util.Iterator;
import java.util.List;

import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.libralign.model.AlignmentModel;

import org.biojava3.core.sequence.AccessionID;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.Compound;
import org.biojava3.core.sequence.template.CompoundSet;
import org.biojava3.core.sequence.template.Sequence;
import org.biojava3.core.sequence.template.SequenceMixin;
import org.biojava3.core.sequence.template.SequenceView;



/**
 * Abstract base class for all implementations of {@link SingleSequenceDataAdapter} that allow
 * to view one sequence of a {@link AlignmentModel} as a BioJava {@link Sequence} object. 
 * <p>
 * Note that the underlying sequence data provider is directly used to that all changes in that provider
 * are also reflected by the instance of this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type of the underlying data source
 * @param <C> - the compound type of the provided BioJava sequences
 */
public class SingleBioJavaSequenceAdapter<T, C extends Compound> 
    extends AbstractSingleSequenceDataAdapter<T> implements Sequence<C> {
	
	public static final NucleotideCompound UNKNOWN_NUCLEOTIDE_COMPOUND = 
			AlignmentAmbiguityNucleotideCompoundSet.getAlignmentAmbiguityNucleotideCompoundSet().getCompoundForString(
					"" + AlignmentAmbiguityNucleotideCompoundSet.UNKNOWN_CHARACTER);
  //TODO Add constants for codons and amino acids
	
	
	private CompoundSet<C> compoundSet;
	private boolean allowUnkownCompounds;
	private C unknownCompound;
	
	
	/**
	 * Creates a new instance of this class that does not allow unknown compounds.
	 * 
	 * @param provider - the sequence data provider that contains the sequence to be viewed
	 * @param sequenceID - the ID used in {@code provider} of the sequence to be viewed
	 * @param offset - the start index of the subsequence to be viewed (The first token has the index 0.)
	 * @param length - the length of the subsequence to be viewed
	 * @param compoundSet - the compound set to be used for the translation
	 */
	public SingleBioJavaSequenceAdapter(AlignmentModel<T> underlyingProvider, int sequenceID, int offset,
			int length, CompoundSet<C> compoundSet) {
		
		super(underlyingProvider, sequenceID, offset, length);
		this.compoundSet = compoundSet;
		this.allowUnkownCompounds = false;
	}

	
	/**
	 * Creates a new instance of this class allowing unknown compounds.
	 * 
	 * @param provider - the sequence data provider that contains the sequence to be viewed
	 * @param sequenceID - the ID used in {@code provider} of the sequence to be viewed
	 * @param offset - the start index of the subsequence to be viewed (The first token has the index 0.)
	 * @param length - the length of the subsequence to be viewed
	 * @param compoundSet - the compound set to be used for the translation
	 * @param unknownCompound - the compound that will be inserted into the BioJava sequence if no according 
	 *        compound to the token in the underlying data source can be found in {@code compoundSet}
	 */
	public SingleBioJavaSequenceAdapter(AlignmentModel<T> underlyingProvider, int sequenceID, int offset,
			int length, CompoundSet<C> compoundSet, C unknownCompound) {
		
		super(underlyingProvider, sequenceID, offset, length);
		this.compoundSet = compoundSet;
		this.allowUnkownCompounds = true;
		this.unknownCompound = unknownCompound;
	}

	
	/**
	 * Creates a new instance of this class that does not allow unknown compounds.
	 * 
	 * @param provider - the sequence data provider that contains the sequence to be viewed
	 * @param sequenceID - the ID used in {@code provider} of the sequence to be viewed
	 * @param compoundSet - the compound set to be used for the translation
	 */
	public SingleBioJavaSequenceAdapter(AlignmentModel<T> underlyingProvider, int sequenceID, 
			CompoundSet<C> compoundSet) {
		
		super(underlyingProvider, sequenceID);
		this.compoundSet = compoundSet;
		this.allowUnkownCompounds = false;
	}


	/**
	 * Creates a new instance of this class allowing unknown compounds.
	 * 
	 * @param provider - the sequence data provider that contains the sequence to be viewed
	 * @param sequenceID - the ID used in {@code provider} of the sequence to be viewed
	 * @param compoundSet - the compound set to be used for the translation
	 * @param unknownCompound - the compound that will be inserted into the BioJava sequence if no according 
	 *        compound to the token in the underlying data source can be found in {@code compoundSet}
	 */
	public SingleBioJavaSequenceAdapter(AlignmentModel<T> underlyingProvider, int sequenceID, 
			CompoundSet<C> compoundSet, C unknownCompound) {
		
		super(underlyingProvider, sequenceID);
		this.compoundSet = compoundSet;
		this.allowUnkownCompounds = true;
		this.unknownCompound = unknownCompound;
	}


	public boolean isAllowUnkownCompounds() {
		return allowUnkownCompounds;
	}


	/**
	 * Returns the compound that will be inserted into the BioJava sequence if no according compound to the token 
	 * in the underlying data source can be found in the specified compound set. 
	 * 
	 * @return the unknown compound that was specified in the constructor
	 */
	public C getUnknownCompound() {
		return unknownCompound;
	}


	/**
	 * 
	 * 
	 * @param index
	 * @return
	 * 
	 * @throws InvalidUnderlyingTokenException
	 */
	@Override
	public C getCompoundAt(int index) {
		T token = getUnderlyingProvider().getTokenAt(getSequenceID(), index);
		String representation = getUnderlyingProvider().getTokenSet().representationByToken(token);
		C result = getCompoundSet().getCompoundForString(representation);
		if (result != null) {
			return result;
		}
		else if (isAllowUnkownCompounds()) {
			return getUnknownCompound();
		}
		else {
			throw new InvalidUnderlyingTokenException(this, token, representation);
		}
	}


	@Override
	public CompoundSet<C> getCompoundSet() {
		return compoundSet;
	}


	@Override
	public Iterator<C> iterator() {
		return SequenceMixin.createIterator(this);
	}

	
	/**
	 * This method always returns null since sequences stored in instances of {@link AlignmentModel}
	 * do not store an associated instance of {@link AccessionID}.
	 * 
	 * @see org.biojava3.core.sequence.template.Accessioned#getAccession()
	 */
	@Override
	public AccessionID getAccession() {
		return null;
	}
	

	@Override
	public int countCompounds(C... compounds) {
		return SequenceMixin.countCompounds(this, compounds);
	}

	
	@Override
	public List<C> getAsList() {
		return SequenceMixin.toList(this);
	}
	

	@Override
	public int getIndexOf(C compound) {
		return SequenceMixin.indexOf(this, compound);
	}
	

	@Override
	public SequenceView<C> getInverse() {
		return SequenceMixin.inverse(this);
	}
	

	@Override
	public int getLastIndexOf(C compound) {
		return SequenceMixin.lastIndexOf(this, compound);
	}

	
	@Override
	public String getSequenceAsString() {
		return SequenceMixin.toString(this);
	}

	
	@Override
	public SequenceView<C> getSubSequence(Integer start, Integer end) {
		return SequenceMixin.createSubSequence(this, start, end);
	}
}
