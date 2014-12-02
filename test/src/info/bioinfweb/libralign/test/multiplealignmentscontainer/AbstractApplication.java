/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben St�ver
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
package info.bioinfweb.libralign.test.multiplealignmentscontainer;


import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.commons.bio.biojava3.alignment.SimpleAlignment;
import info.bioinfweb.commons.bio.biojava3.alignment.template.Alignment;
import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.dataarea.implementations.ConsensusSequenceArea;
import info.bioinfweb.libralign.dataarea.implementations.SequenceIndexArea;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSet;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetArea;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.libralign.pherogram.provider.BioJavaPherogramProvider;
import info.bioinfweb.libralign.sequenceprovider.implementations.BioJavaSequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.tokenset.BioJavaTokenSet;

import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;



/**
 * This class provides implementations that are used by both the Swing and the SWT demo applications.
 * 
 * @author Ben St&ouml;ver
 */
public class AbstractApplication {
	private CharSetArea createCharSetArea(AlignmentContentArea owner) {
		List<CharSet> model = new ArrayList<CharSet>(3);
		
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
		
		return new CharSetArea(owner, model);
	}
	
	
	protected MultipleAlignmentsContainer createAlignmentsContainer() {
		try {
			MultipleAlignmentsContainer result = new MultipleAlignmentsContainer();
			
			// Create models:
			BioJavaPherogramProvider pherogramProvider = new BioJavaPherogramProvider(ChromatogramFactory.create(
	      	new File("data\\pherograms\\Test_qualityScore.scf")));
			
			Alignment<DNASequence, NucleotideCompound> alignment = 
					new SimpleAlignment<DNASequence, NucleotideCompound>();
			alignment.add("Sequence 1", new DNASequence("ATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAG"));
			alignment.add("Sequence 2", new DNASequence("AT-GTTG"));
			alignment.add("Sequence 3", new DNASequence("AT-GTAG"));
			
			StringBuffer seqBuffer = new StringBuffer(pherogramProvider.getSequenceLength());
			for (int i = 0; i < pherogramProvider.getSequenceLength(); i++) {
				seqBuffer.append(pherogramProvider.getBaseCall(i).getUpperedBase());
			}
			alignment.add("Sequence 4", new DNASequence("-----" + seqBuffer.substring(0, 38) + 
					seqBuffer.substring(39, 49) + "-A" + seqBuffer.substring(49)));  // One A is deleted for shift change specified below. 
					//seqBuffer.substring(39, 49) + "--" + seqBuffer.substring(49)));  // One A is deleted for shift change specified below. 

			alignment.add("Sequence 5", new DNASequence("ATCGTAGATCGTAGATGGTAGATCGTAGATCGT---TCGTAGATCGTAG"));
			
			BioJavaSequenceDataProvider<DNASequence, NucleotideCompound> sequenceProvider = 
					new BioJavaSequenceDataProvider<DNASequence, NucleotideCompound>(
							new BioJavaTokenSet<NucleotideCompound>(
									AlignmentAmbiguityNucleotideCompoundSet.getAlignmentAmbiguityNucleotideCompoundSet(), false),
							alignment);
			
			
			// Create alignment areas:
			
			// Index:
			AlignmentArea area = new AlignmentArea(result);
			area.setAllowVerticalScrolling(false);
			area.getContentArea().getDataAreas().getTopAreas().add(new SequenceIndexArea(area.getContentArea()));
			result.add(area);
			
			// Char sets:    //TODO CharSetArea now needs a sequenceProvider parameter, because in could be in another alignment.
//			area = new AlignmentArea(result);
//			area.setAllowVerticalScrolling(true);
//			area.getContentArea().getDataAreas().getTopAreas().add(createCharSetArea(area.getContentArea()));
//			result.add(area);
      
			// Alignment with pherograms:
			area = new AlignmentArea(result);
			area.setAllowVerticalScrolling(true);
      			
			area.getContentArea().setSequenceProvider(sequenceProvider, false);
			
			PherogramArea pherogramArea = new PherogramArea(area.getContentArea(), pherogramProvider);
			pherogramArea.setFirstSeqPos(34 + 5);
			pherogramArea.setLeftCutPosition(34);
			pherogramArea.setRightCutPosition(820);
			pherogramArea.getAlignmentModel().setShiftChange(38, -1);
			pherogramArea.getAlignmentModel().setShiftChange(49, 2);
			area.getContentArea().getDataAreas().getSequenceAreas(sequenceProvider.sequenceIDByName("Sequence 4")).add(pherogramArea);
			
			result.add(area);
			
			// Consensus sequence:
      area = new AlignmentArea(result);  //TODO ConsensusSequenceArea now needs a sequenceProvider parameter, because in could be in another alignment.
			area.setAllowVerticalScrolling(false);
			area.getContentArea().getDataAreas().getBottomAreas().add(new ConsensusSequenceArea(area.getContentArea(), sequenceProvider));
      result.add(area);
			
			return result;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
