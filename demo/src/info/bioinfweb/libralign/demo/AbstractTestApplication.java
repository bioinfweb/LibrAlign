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
package info.bioinfweb.libralign.demo;


import java.io.File;

import info.bioinfweb.commons.bio.biojava3.alignment.SimpleAlignment;
import info.bioinfweb.commons.bio.biojava3.alignment.template.Alignment;
import info.bioinfweb.commons.bio.biojava3.core.sequence.compound.AlignmentAmbiguityNucleotideCompoundSet;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.implementations.ConsensusSequenceArea;
import info.bioinfweb.libralign.dataarea.implementations.SequenceIndexArea;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.pherogram.BioJavaPherogramProvider;
import info.bioinfweb.libralign.selection.SelectionType;
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
public class AbstractTestApplication {
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
			for (int i = 1; i <= pherogramProvider.getSequenceLength(); i++) {
				seqBuffer.append(pherogramProvider.getBaseCall(i).getUpperedBase());
			}
			alignment.add("Sequence 4", new DNASequence("-----" + seqBuffer.substring(0, 38) + 
					seqBuffer.substring(39, 49) + "-A" + seqBuffer.substring(49)));  // One A is deleted for shift change specified below. 
					//seqBuffer.substring(39, 49) + "--" + seqBuffer.substring(49)));  // One A is deleted for shift change specified below. 

			alignment.add("Sequence 5", new DNASequence("ATCGTAGATCGTAGATGGTAGATCGTAGATCGT---TCGTAGATCGTAG"));
			
			BioJavaSequenceDataProvider<DNASequence, NucleotideCompound> sequenceProvider = 
					new BioJavaSequenceDataProvider<DNASequence, NucleotideCompound>(
							new BioJavaTokenSet<NucleotideCompound>(
									AlignmentAmbiguityNucleotideCompoundSet.getAlignmentAmbiguityNucleotideCompoundSet()),
							alignment);
			
			AlignmentArea result = new AlignmentArea();
			result.setSequenceProvider(sequenceProvider, false);
			SequenceIndexArea sequenceIndexArea = new SequenceIndexArea(result);
			//sequenceIndexArea.setFirstIndex(5);
			//sequenceIndexArea.setHeight(25);
			result.getDataAreas().getTopAreas().add(sequenceIndexArea);
			
			PherogramArea pherogramArea = new PherogramArea(result, pherogramProvider);
			pherogramArea.setFirstSeqPos(34 + 5);
			pherogramArea.setLeftCutPosition(34);
			pherogramArea.setRightCutPosition(820);
			pherogramArea.getAlignmentModel().setShiftChange(38, -1);
			pherogramArea.getAlignmentModel().setShiftChange(49, 2);
			result.getDataAreas().getSequenceAreas(sequenceProvider.sequenceIDByName("Sequence 4")).add(pherogramArea);
			
			result.getDataAreas().getBottomAreas().add(new ConsensusSequenceArea(result));
			
			//result.getSelection().setType(SelectionType.ROW_ONLY);
			
			return result;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
