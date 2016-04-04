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
package info.bioinfweb.libralign.model.factory.biojava;


import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.CompoundSet;

import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.factory.AlignmentModelFactory;
import info.bioinfweb.libralign.model.factory.NewAlignmentModelParameterMap;
import info.bioinfweb.libralign.model.factory.TokenDefinition;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.BioJava3TokenSet;



/**
 * Alignment model factory for models representing nucleotides as BioJava {@link NucleotideCompound} objects.
 * 
 * @author Ben St&ouml;ver
 * @bioinfweb.module info.bioinfweb.libralign.biojava3
 */
public class BioJava3NucleotideAlignmentModelFactory implements AlignmentModelFactory<NucleotideCompound> {
	public static final int DEFAULT_NUCLEOTIDE_CHARACTER_STATE_COUNT = 32;  // Nucleotide, ambiguity codes and special characters make up more than 16.
	
	
	private static class ModifiableNucleotideCompoundSet extends AlignmentAmbiguityNucleotideCompoundSet {
		@Override
		public void addNucleotideCompound(String base, String complement, String... equivalents) {
			super.addNucleotideCompound(base, complement, equivalents);
		}
	};

	
	@Override
	public AlignmentModel<NucleotideCompound> createNewModel(NewAlignmentModelParameterMap parameterMap) {
		CharacterStateSetType type = parameterMap.getCharacterStateSetType();
		if (!type.isNucleotide()) {
			if (type.equals(CharacterStateSetType.UNKNOWN)) {
				type = CharacterStateSetType.NUCLEOTIDE;
			}
			else {
				throw new IllegalArgumentException("An instance of " + getClass().getCanonicalName() + 
						" cannot be created with the token type " + type + ".");
			}
		}
		ModifiableNucleotideCompoundSet nucleotideCompoundSet = new ModifiableNucleotideCompoundSet();
		for (TokenDefinition tokenDefinition : parameterMap.getDefinedTokens()) {
			if (nucleotideCompoundSet.getCompoundForString(tokenDefinition.getRepresentation()) == null) {
				nucleotideCompoundSet.addNucleotideCompound(tokenDefinition.getRepresentation(), tokenDefinition.getRepresentation());
			}
		}
		BioJava3TokenSet<NucleotideCompound> nucleotideTokenSet = new BioJava3TokenSet<NucleotideCompound>(type, nucleotideCompoundSet, true);  //TODO Use BioJava 4 tokens here/ Should this class really be bound to BioJava?
		
		// Create model:
		return new PackedAlignmentModel<NucleotideCompound>(nucleotideTokenSet,
				Math.max(nucleotideTokenSet.size(), (int)parameterMap.getLong(NewAlignmentModelParameterMap.KEY_CHARACTER_STATE_COUNT, 
				DEFAULT_NUCLEOTIDE_CHARACTER_STATE_COUNT)));
	}
	

	@Override
	public NucleotideCompound createToken(AlignmentModel<NucleotideCompound> alignmentModel, String tokenRepresentation) {
		// TODO Should the functionality of creating new tokens be moved to TokenSet (similar like in CompoundSet)?
		CompoundSet<NucleotideCompound> compoundSet = ((BioJava3TokenSet<NucleotideCompound>)alignmentModel.getTokenSet()).getCompoundSet();
		NucleotideCompound result = compoundSet.getCompoundForString(tokenRepresentation);
		if (result == null) {
			((ModifiableNucleotideCompoundSet)compoundSet).addNucleotideCompound(tokenRepresentation, tokenRepresentation);  // Tokens that are already present should not be actual nucleotides that have a complement.
			result = compoundSet.getCompoundForString(tokenRepresentation);
		}
		return result;
	}
}
