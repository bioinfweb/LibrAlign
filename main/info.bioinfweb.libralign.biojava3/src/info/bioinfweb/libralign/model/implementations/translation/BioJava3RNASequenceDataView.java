/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
package info.bioinfweb.libralign.model.implementations.translation;


import info.bioinfweb.libralign.model.AlignmentModel;

import org.biojava3.core.sequence.compound.DNACompoundSet;
import org.biojava3.core.sequence.compound.NucleotideCompound;




/**
 * Shows the underlying nucleotide data source as a RNA sequence, i.e. replaces all uracil tokens 
 * by thymine tokens.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 * @bioinfweb.module info.bioinfweb.libralign.biojava3
 */
public class BioJava3RNASequenceDataView extends SameTokenTypeAlignmentModelView<NucleotideCompound> {
	private static final NucleotideCompound URACIL = 
			DNACompoundSet.getDNACompoundSet().getCompoundForString("U");
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param underlyingProvider - the underlying provider to be viewed
	 */
	public BioJava3RNASequenceDataView(AlignmentModel<NucleotideCompound> underlyingProvider) {
		super(underlyingProvider);
		getTokenSet().add(URACIL);  // Just to be sure.
	}

	
	@Override
	public NucleotideCompound translateToken(NucleotideCompound token) {
		if ((token != null) && token.getUpperedBase() == "T") {
			return URACIL;
		}
		else {
			return token;
		}
	}
}
