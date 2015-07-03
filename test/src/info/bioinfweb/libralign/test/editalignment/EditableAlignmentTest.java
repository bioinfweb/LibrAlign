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
package info.bioinfweb.libralign.test.editalignment;


import info.bioinfweb.commons.bio.CharacterStateType;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.NucleotideTokenPainter;
import info.bioinfweb.libralign.dataarea.implementations.SequenceIndexArea;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.ArrayListAlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.BioJava3TokenSet;
import info.bioinfweb.libralign.model.tokenset.TokenSet;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;
import info.bioinfweb.libralign.pherogram.provider.BioJavaPherogramProvider;

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
			
			TokenSet<NucleotideCompound> tokenSet = new BioJava3TokenSet<NucleotideCompound>(
					CharacterStateType.NUCLEOTIDE, new DNACompoundSet(), true);
			AlignmentModel<NucleotideCompound> provider = new ArrayListAlignmentModel<NucleotideCompound>(tokenSet);
			//AlignmentModel<NucleotideCompound> provider = new PackedAlignmentModel<NucleotideCompound>(tokenSet);
			
			// Add index area:
			alignmentArea.getDataAreas().getTopAreas().add(new SequenceIndexArea(contentArea));
			
			// Test sequence:
			provider.addSequence("A");
			int id = provider.sequenceIDByName("A");
			provider.insertTokenAt(id, 0, tokenSet.tokenByRepresentation("A"));
			provider.insertTokenAt(id, 1, tokenSet.tokenByRepresentation("C"));
			provider.insertTokenAt(id, 2, tokenSet.tokenByRepresentation("G"));
			provider.insertTokenAt(id, 3, tokenSet.tokenByRepresentation("-"));
			provider.insertTokenAt(id, 4, tokenSet.tokenByRepresentation("T"));
			
			// Another test sequence:
			provider.addSequence("B");
			id = provider.sequenceIDByName("B");
			provider.insertTokenAt(id, 0, tokenSet.tokenByRepresentation("A"));
			provider.insertTokenAt(id, 1, tokenSet.tokenByRepresentation("C"));
			provider.insertTokenAt(id, 2, tokenSet.tokenByRepresentation("G"));
			provider.insertTokenAt(id, 3, tokenSet.tokenByRepresentation("G"));
			provider.insertTokenAt(id, 4, tokenSet.tokenByRepresentation("T"));
			
			// Test sequence with pherogram:
			try {
				provider.addSequence("C");
				id = provider.sequenceIDByName("C");
				BioJavaPherogramProvider pherogramProvider = new BioJavaPherogramProvider(ChromatogramFactory.create(
		      	new File("data/pherograms/Test_qualityScore.scf")));
	
				// Copy base call sequence into alignment:
				for (int i = 0; i < pherogramProvider.getSequenceLength(); i++) {
					provider.insertTokenAt(id, i, tokenSet.tokenByRepresentation(Character.toString(pherogramProvider.getBaseCall(i))));
				}
				
				// Add data area:
				PherogramArea pherogramArea = new PherogramArea(alignmentArea.getContentArea(), new PherogramAreaModel(pherogramProvider));
				//pherogramArea.setFirstSeqPos(1);
				//pherogramArea.setLeftCutPosition(1);
				alignmentArea.getDataAreas().getSequenceAreas(id).add(pherogramArea);
			}
			catch (UnsupportedChromatogramFormatException | IOException e) {
				e.printStackTrace();
			}
			
			alignmentArea.setAlignmentModel(provider, false);
			alignmentArea.getPaintSettings().getTokenPainterList().set(0, new NucleotideTokenPainter());
		}
		
		return alignmentArea;
	}
	
	
	private String newSequenceName() {
		int index = 1;
		while (getAlignmentArea().getAlignmentModel().sequenceIDByName(
				"Sequence" + index) != AlignmentModel.NO_SEQUENCE_FOUND) {
			index++;
		}
		return "Sequence" + index;
	}
	
	
	protected void addPherogramSequence() {
		try {
			AlignmentModel provider = getAlignmentArea().getAlignmentModel();
			String name = newSequenceName();
			provider.addSequence(name);
			int id = provider.sequenceIDByName(name);
			BioJavaPherogramProvider pherogramProvider = new BioJavaPherogramProvider(ChromatogramFactory.create(
	      	new File("data/pherograms/Test_qualityScore.scf")));

			// Copy base call sequence into alignment:
			for (int i = 0; i < pherogramProvider.getSequenceLength(); i++) {
				provider.insertTokenAt(id, i, provider.getTokenSet().tokenByRepresentation(Character.toString(pherogramProvider.getBaseCall(i))));
			}
			
			// Add data area:
			PherogramArea pherogramArea = new PherogramArea(alignmentArea.getContentArea(), new PherogramAreaModel(pherogramProvider));
			alignmentArea.getDataAreas().getSequenceAreas(id).add(pherogramArea);
		}
		catch (UnsupportedChromatogramFormatException | IOException e) {
			e.printStackTrace();
		}
	}
}
