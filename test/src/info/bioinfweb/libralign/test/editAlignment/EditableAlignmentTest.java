/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.test.editAlignment;


import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.AlignmentContentArea;
import info.bioinfweb.libralign.dataarea.implementations.SequenceIndexArea;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.pherogram.BioJavaPherogramProvider;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.implementations.PackedSequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.tokenset.BioJavaTokenSet;
import info.bioinfweb.libralign.sequenceprovider.tokenset.TokenSet;

import java.io.File;
import java.io.IOException;

import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.chromatogram.UnsupportedChromatogramFormatException;
import org.biojava3.core.sequence.compound.DNACompoundSet;
import org.biojava3.core.sequence.compound.NucleotideCompound;



public class EditableAlignmentTest {
	private AlignmentArea alignmentArea = null;
	
	
	protected  AlignmentArea getAlignmentArea() {
		if (alignmentArea == null) {
			alignmentArea = new AlignmentArea();
			AlignmentContentArea contentArea = alignmentArea.getContentArea();
			
			TokenSet<NucleotideCompound> tokenSet = new BioJavaTokenSet<NucleotideCompound>(new DNACompoundSet(), true);
			SequenceDataProvider<NucleotideCompound> provider = new PackedSequenceDataProvider<NucleotideCompound>(tokenSet);
			
			// Add index area:
			contentArea.getDataAreas().getTopAreas().add(new SequenceIndexArea(contentArea));
			
			// Test sequence:
			provider.addSequence("A");
			int id = provider.sequenceIDByName("A");
			provider.insertTokenAt(id, 0, tokenSet.tokenByKeyChar('A'));
			provider.insertTokenAt(id, 1, tokenSet.tokenByKeyChar('C'));
			provider.insertTokenAt(id, 2, tokenSet.tokenByKeyChar('G'));
			provider.insertTokenAt(id, 3, tokenSet.tokenByKeyChar('-'));
			provider.insertTokenAt(id, 4, tokenSet.tokenByKeyChar('T'));
			
			// Another test sequence:
			provider.addSequence("B");
			id = provider.sequenceIDByName("B");
			provider.insertTokenAt(id, 0, tokenSet.tokenByKeyChar('A'));
			provider.insertTokenAt(id, 1, tokenSet.tokenByKeyChar('C'));
			provider.insertTokenAt(id, 2, tokenSet.tokenByKeyChar('G'));
			provider.insertTokenAt(id, 3, tokenSet.tokenByKeyChar('G'));
			provider.insertTokenAt(id, 4, tokenSet.tokenByKeyChar('T'));
			
			// Test sequence with pherogram:
			try {
				provider.addSequence("C");
				id = provider.sequenceIDByName("C");
				BioJavaPherogramProvider pherogramProvider = new BioJavaPherogramProvider(ChromatogramFactory.create(
		      	new File("data/pherograms/Test_qualityScore.scf")));
	
				// Copy base call sequence into alignment:
				for (int i = 0; i < pherogramProvider.getSequenceLength(); i++) {
					provider.insertTokenAt(id, i, tokenSet.tokenByKeyChar(pherogramProvider.getBaseCall(i).getUpperedBase().charAt(0)));
				}
				
				// Add data area:
				PherogramArea pherogramArea = new PherogramArea(alignmentArea.getContentArea(), pherogramProvider);
				alignmentArea.getContentArea().getDataAreas().getSequenceAreas(id).add(pherogramArea);
			}
			catch (UnsupportedChromatogramFormatException | IOException e) {
				e.printStackTrace();
			}
			
			contentArea.setSequenceProvider(provider, false);
		}
		
		return alignmentArea;
	}
	
	
	
}
