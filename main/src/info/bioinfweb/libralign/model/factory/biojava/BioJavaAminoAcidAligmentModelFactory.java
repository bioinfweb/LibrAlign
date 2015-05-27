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


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.exception.InvalidTokenException;
import info.bioinfweb.libralign.model.factory.AlignmentModelFactory;
import info.bioinfweb.libralign.model.factory.NewAlignmentModelParameterMap;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.BioJavaTokenSet;

import org.biojava3.core.sequence.compound.AminoAcidCompound;
import org.biojava3.core.sequence.compound.AminoAcidCompoundSet;



public class BioJavaAminoAcidAligmentModelFactory implements AlignmentModelFactory<AminoAcidCompound> {
	@Override
	public AlignmentModel<AminoAcidCompound> createNewModel(NewAlignmentModelParameterMap parameterMap) {
		return new PackedAlignmentModel<AminoAcidCompound>(new BioJavaTokenSet<AminoAcidCompound>(new AminoAcidCompoundSet(), true));
		// BioJava does not support adding new amino acid definitions to single compound sets. Therefore token definitions in the 
		// file and possible token counts are ignored.
	}

	
	/**
	 * Creates the token using the token set of the specified alignment model.
	 *  
	 * @return the new token
	 * @throws InvalidTokenException if no token for the specified representation could be found in the token set
	 *         of the specified alignment model
	 */
	@Override
	public AminoAcidCompound createToken(AlignmentModel<AminoAcidCompound> alignmentModel, String tokenRepresentation) {
		AminoAcidCompound result = alignmentModel.getTokenSet().tokenByRepresentation(tokenRepresentation);  // Creating new tokens specific for a single set is not possible with BioJava amino acids. 
		if (result == null) {
			throw new InvalidTokenException(alignmentModel, tokenRepresentation);
		}
		else {
			return result;
		}
	}
}
