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
package info.bioinfweb.libralign.demo;


import java.awt.Color;
import java.io.File;

import info.bioinfweb.commons.bio.CharacterStateType;
import info.bioinfweb.commons.bio.biojava3.alignment.SimpleAlignment;
import info.bioinfweb.commons.bio.biojava3.alignment.template.Alignment;
import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionType;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.NucleotideTokenPainter;
import info.bioinfweb.libralign.dataarea.implementations.ConsensusSequenceArea;
import info.bioinfweb.libralign.dataarea.implementations.SequenceIndexArea;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSet;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetArea;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetDataModel;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.model.implementations.BioJava3AlignmentModel;
import info.bioinfweb.libralign.model.tokenset.BioJava3TokenSet;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;
import info.bioinfweb.libralign.pherogram.provider.BioJavaPherogramProvider;

import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;



/**
 * This class provides implementations that are used by both the Swing and the SWT demo applications.
 * 
 * @author Ben St&ouml;ver
 */
public class AbstractTestApplication {
	private CharSetArea createCharSetArea(AlignmentContentArea owner) {
		CharSetDataModel model = new CharSetDataModel();
		
		CharSet charSet = new CharSet("A", Color.RED);
		charSet.add(2, 5);
		charSet.add(8, 20);
		model.add(charSet);
		
		charSet = new CharSet("B", Color.GREEN.darker());
		charSet.add(1, 20);
		model.add(charSet);
		
		charSet = new CharSet("C", Color.BLUE);
		charSet.add(7, 12);
		charSet.add(17, 25);
		model.add(charSet);
		
		return new CharSetArea(owner, owner.getOwner(), model);
	}
	
	
	protected AlignmentArea createAlignmentArea() {
		try {
			BioJavaPherogramProvider pherogramProvider = new BioJavaPherogramProvider(ChromatogramFactory.create(
	      	new File("data\\Test_qualityScore.scf")));
			
			Alignment<DNASequence, NucleotideCompound> alignment = 
					new SimpleAlignment<DNASequence, NucleotideCompound>();
			alignment.add("Sequence 1", new DNASequence("ATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAG"));
			alignment.add("Sequence 2", new DNASequence("AT-GTTG"));
			alignment.add("Sequence 3", new DNASequence("AT-GTAG"));
			
			StringBuffer seqBuffer = new StringBuffer(pherogramProvider.getSequenceLength());
			for (int i = 0; i <= pherogramProvider.getSequenceLength() - 1; i++) {
				seqBuffer.append(pherogramProvider.getBaseCall(i));
			}
			alignment.add("Sequence 4", new DNASequence("-----" + seqBuffer.substring(0, 38) + 
					seqBuffer.substring(39, 49) + "-A" + seqBuffer.substring(49)));  // One A is deleted for shift change specified below. 
					//seqBuffer.substring(39, 49) + "--" + seqBuffer.substring(49)));  // One A is deleted for shift change specified below. 

			alignment.add("Sequence 5", new DNASequence("ATCGTAGATCGTAGATGGTAGATCGTAGATCGT---TCGTAGATCGTAG"));
			
			BioJava3AlignmentModel<DNASequence, NucleotideCompound> sequenceProvider = 
					new BioJava3AlignmentModel<DNASequence, NucleotideCompound>(
							new BioJava3TokenSet<NucleotideCompound>(CharacterStateType.NUCLEOTIDE,
									AlignmentAmbiguityNucleotideCompoundSet.getAlignmentAmbiguityNucleotideCompoundSet(), false),
							alignment);
			
			AlignmentArea result = new AlignmentArea();
			result.setAlignmentModel(sequenceProvider, false);
			SequenceIndexArea sequenceIndexArea = new SequenceIndexArea(result.getContentArea());
			//sequenceIndexArea.setFirstIndex(5);
			//sequenceIndexArea.setHeight(25);
			result.getDataAreas().getTopAreas().add(sequenceIndexArea);
			
			result.getDataAreas().getTopAreas().add(createCharSetArea(result.getContentArea()));
			
			PherogramArea pherogramArea = new PherogramArea(result.getContentArea(), new PherogramAreaModel(pherogramProvider));
			PherogramAreaModel model = pherogramArea.getModel();
			model.setFirstSeqPos(5);
			model.setLeftCutPosition(34);
			model.setRightCutPosition(820);
			model.setShiftChange(38, -1);
			model.setShiftChange(49, 2);
			result.getDataAreas().getSequenceAreas(sequenceProvider.sequenceIDByName("Sequence 4")).add(pherogramArea);
			
			result.getDataAreas().getBottomAreas().add(new ConsensusSequenceArea(result.getContentArea()));
			
			result.getPaintSettings().getTokenPainterList().set(0, new NucleotideTokenPainter());
			result.getSelection().setType(SelectionType.COLUMN_ONLY);
			
			return result;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
