/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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


import info.bioinfweb.commons.bio.biojava3.alignment.SimpleAlignment;
import info.bioinfweb.commons.bio.biojava3.alignment.template.Alignment;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.AlignmentSourceDataType;
import info.bioinfweb.libralign.dataarea.ConsensusSequenceArea;
import info.bioinfweb.libralign.sequenceprovider.implementations.BioJavaSequenceDataProvider;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;



/**
 * This class provides implementations that are used by both the Swing and the SWT demo applications.
 * 
 * @author Ben St&ouml;ver
 */
public class AbstractTestApplication {
	protected AlignmentArea createAlignmentArea() {
		Alignment<DNASequence, NucleotideCompound> alignment = 
				new SimpleAlignment<DNASequence, NucleotideCompound>();
		alignment.add("Sequence 1", new DNASequence("ATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAG"));
		alignment.add("Sequence 2", new DNASequence("AT-GTTG"));
		alignment.add("Sequence 3", new DNASequence("AT-GTAG"));
		
		BioJavaSequenceDataProvider<DNASequence, NucleotideCompound> sequenceProvider = 
				new BioJavaSequenceDataProvider<DNASequence, NucleotideCompound>(
						alignment, AlignmentSourceDataType.NUCLEOTIDE);
		
		AlignmentArea result = new AlignmentArea();
		result.setSequenceProvider(sequenceProvider, false);
		result.getDataAreas().getBottomAreas().add(new ConsensusSequenceArea(result));
		return result;
	}
}
