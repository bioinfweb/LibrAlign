/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.factory;


import org.biojava3.core.sequence.compound.AminoAcidCompound;
import org.biojava3.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.CompoundSet;

import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.NucleotideCompoundSet;
import info.bioinfweb.jphyloio.events.SingleTokenDefinitionEvent;
import info.bioinfweb.jphyloio.events.TokenSetDefinitionEvent;
import info.bioinfweb.jphyloio.events.TokenSetType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.exception.InvalidCompoundException;
import info.bioinfweb.libralign.model.implementations.ArrayListAlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.BioJavaTokenSet;
import info.bioinfweb.libralign.model.tokenset.DefaultTokenSet;
import info.bioinfweb.libralign.model.tokenset.TokenSet;
import info.bioinfweb.libralign.model.tokenset.continuous.DoubleTokenSet;



/**
 * Default factory implementation for creating alignment models. If a modified behavior is needed application
 * developers can overwrite methods of this class to catch according cases and delegate the remaining to the
 * super implementation.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class DefaultAlignmentModelFactory implements AlignmentModelFactory {
	public static final int DEFAULT_NUCLEOTIDE_CHARACTER_STATE_COUNT = 32;  // Nucleotide, ambiguity codes and special characters make up more than 16.
	public static final int DEFAULT_AMINO_ACID_CHARACTER_STATE_COUNT = 32;  // 23 amino acids and stop are already more than 16.
	
	
	private static class ModifiableNucleotideCompoundSet extends AlignmentAmbiguityNucleotideCompoundSet {
		@Override
		public void addNucleotideCompound(String base, String complement, String... equivalents) {
			super.addNucleotideCompound(base, complement, equivalents);
		}
	};

	
	
	/**
	 * Creates a new instance of an alignment model according to the specified parameters.
	 * <p> 
	 * The following instances are created by this factory depending on the token set type specified in the parameter
	 * map ({@link NewAlignmentModelParameterMap#getCharacterStateSetType()}):
	 * <ul>
	 *   <li>{@link TokenSetType#CONTINUOUS}: {@link ArrayListAlignmentModel}</li>
	 *   <li>{@link TokenSetType#DISCRETE}: {@link PackedAlignmentModel} if the number of tokens is specified (using
	 *       {@link NewAlignmentModelParameterMap#KEY_CHARACTER_STATE_COUNT}) or {@link ArrayListAlignmentModel} if not</li>
	 *   <li>{@link TokenSetType#NUCLEOTIDE}, {@link TokenSetType#DNA}, {@link TokenSetType#RNA}: {@link PackedAlignmentModel}
	 *       supporting up to {@value #DEFAULT_NUCLEOTIDE_CHARACTER_STATE_COUNT} states (as specified by 
	 *       {@link #DEFAULT_NUCLEOTIDE_CHARACTER_STATE_COUNT}) or a specified character state count, if one was provided
	 *       as an entry in {@code parameterMap} for {@link NewAlignmentModelParameterMap#KEY_CHARACTER_STATE_COUNT} which is
	 *       not greater than {@link Integer#MAX_VALUE}.</li>
	 *   <li>{@link TokenSetType#AMINO_ACID}: {@link PackedAlignmentModel} supporting up to 
	 *       {@value #DEFAULT_AMINO_ACID_CHARACTER_STATE_COUNT} states (as specified by 
	 *       {@link #DEFAULT_AMINO_ACID_CHARACTER_STATE_COUNT}) or a specified character state count, if one was provided
	 *       as an entry in {@code parameterMap} for {@link NewAlignmentModelParameterMap#KEY_CHARACTER_STATE_COUNT}.</li>
	 * </ul>
	 * 
	 * @param parameterMap a map with parameters describing the requirements to be met by the returned instance
	 * @return a new instance of an alignment model
	 */
	@Override
	public AlignmentModel<?> createNewModel(NewAlignmentModelParameterMap parameterMap) {
		switch (parameterMap.getCharacterStateSetType()) {
			case CONTINUOUS:
				return new ArrayListAlignmentModel<Double>(new DoubleTokenSet());
				
			case DNA:
			case RNA:
			case NUCLEOTIDE:
				// Create token set:
				ModifiableNucleotideCompoundSet nucleotideCompoundSet = new ModifiableNucleotideCompoundSet();
				for (SingleTokenDefinitionEvent tokenDefinition : parameterMap.getDefinedTokens()) {
					if (nucleotideCompoundSet.getCompoundForString(tokenDefinition.getTokenName()) == null) {
						nucleotideCompoundSet.addNucleotideCompound(tokenDefinition.getTokenName(), tokenDefinition.getTokenName());
					}
				}
				BioJavaTokenSet<NucleotideCompound> nucleotideTokenSet = new BioJavaTokenSet<NucleotideCompound>(nucleotideCompoundSet);  //TODO Use BioJava 4 tokens here/ Should this class really be bound to BioJava?
				
				// Create model:
				return new PackedAlignmentModel<NucleotideCompound>(nucleotideTokenSet,
						Math.max(nucleotideTokenSet.size(), (int)parameterMap.getLong(NewAlignmentModelParameterMap.KEY_CHARACTER_STATE_COUNT, 
						DEFAULT_NUCLEOTIDE_CHARACTER_STATE_COUNT)));
				
			case AMINO_ACID:
				return new PackedAlignmentModel<AminoAcidCompound>(new BioJavaTokenSet<AminoAcidCompound>(new AminoAcidCompoundSet()));
				// BioJava does not support adding new amino acid definitions to single compound sets. Therefore token definitions in the 
				// file and possible token counts are ignored.

			default:
			case DISCRETE:
				// Create token set:
				DefaultTokenSet<String> discreteTokenSet = new DefaultTokenSet<String>();
				for (SingleTokenDefinitionEvent tokenDefinition : parameterMap.getDefinedTokens()) {
					discreteTokenSet.add(tokenDefinition.getTokenName());  //TODO Should the meaning be modeled in the token set?
				}
				
				// Create model:
				long charStateCount = parameterMap.getLong(NewAlignmentModelParameterMap.KEY_CHARACTER_STATE_COUNT, -1);
				if ((charStateCount == -1) || (charStateCount > Integer.MAX_VALUE)) {
					return new ArrayListAlignmentModel<String>(discreteTokenSet);  //TODO Only tokens that have explicitly defined will be loaded that way.
				}
				else {
					return new PackedAlignmentModel<String>(discreteTokenSet, (int)charStateCount);
				}
		}
	}

	
	/**
	 * Creates the token using the token set of the specified alignment model.
	 * <p>
	 * Overwrite this method to support more or different token string representations than the token set of the 
	 * alignment model. 
	 *  
	 * @return the new token
	 * @throws InvalidCompoundException if no token for the specified representation could be found in the token set
	 *         of the specified alignment model
	 */
	@Override
	public <T> T createToken(AlignmentModel<T> alignmentModel, String tokenRepresentation) {
		T result = alignmentModel.getTokenSet().tokenByRepresentation(tokenRepresentation);
		if (result == null) {
			throw new InvalidCompoundException(alignmentModel, tokenRepresentation);
		}
		else {
			return result;
		}
	}	
}
