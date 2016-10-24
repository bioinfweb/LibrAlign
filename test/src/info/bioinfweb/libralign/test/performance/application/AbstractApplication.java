/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.test.performance.application;


import java.io.File;

import org.biojava3.core.sequence.compound.NucleotideCompound;

import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.BioJava3TokenSet;



public class AbstractApplication {
	private AlignmentArea alignmentArea;

	
	protected AlignmentArea getAlignmentArea() {
		throw new InternalError("not implemented");
//		if (alignmentArea == null) {
//			AlignmentModel<NucleotideCompound> provider = new PackedAlignmentModel<NucleotideCompound>(
//					new BioJava3TokenSet<NucleotideCompound>(CharacterStateType.NUCLEOTIDE,
//							AlignmentAmbiguityNucleotideCompoundSet.getAlignmentAmbiguityNucleotideCompoundSet(), false));
//			
//			
//			AlignmentReader reader = new FASTAReader();
//			try {
//				reader.read(new File("D:\\Users\\BenStoever\\ownCloud\\Dokumente\\Projekte\\LibrAlign\\Testdaten\\Performance\\Alignment_8_134217728.fasta"), provider);
//				// Alignment_8_268435456.fasta is the first file that is too long to be displayed (Alignment_8_134217728.fasta works with
//				// default compound width). The sequence components are just not set visible (without any exception). (The reason should be 
//				// that the component width would be higher than Integer.MAX_VALUE.) Loading of the the data took 200298 ms, which is a 
//				// linear complexity.
//				alignmentArea = new AlignmentArea();
//				alignmentArea.getDataAreas().getTopAreas().add(new SequenceIndexArea(alignmentArea.getContentArea()));
//				alignmentArea.setAlignmentModel(provider, false);
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//				throw new InternalError(e);
//			}
//		}
//		return alignmentArea;
	}
}
