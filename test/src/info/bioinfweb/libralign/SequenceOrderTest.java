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
package info.bioinfweb.libralign;


import static org.junit.Assert.* ;

import info.bioinfweb.commons.bio.biojava3.alignment.SimpleAlignment;
import info.bioinfweb.commons.bio.biojava3.alignment.template.Alignment;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.implementations.BioJavaSequenceDataProvider;


import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.junit.* ;



/**
 * Tests methods of {@link SequenceOrder}.
 * 
 * @author Ben St&ouml;ver
 */
public class SequenceOrderTest {
	private SequenceOrder createSequenceOrder() {
		Alignment<DNASequence, NucleotideCompound> alignment = new SimpleAlignment<DNASequence, NucleotideCompound>();
		alignment.add("Sequence C", new DNASequence("ATGGGCT"));
		alignment.add("Sequence D", new DNASequence("ATG-GCA"));
		alignment.add("Sequence A", new DNASequence("ACGGGCA"));
		alignment.add("Sequence B", new DNASequence("ATG--CT"));
		
		AlignmentArea alignmentArea = new AlignmentArea();
		alignmentArea.setSequenceProvider(new BioJavaSequenceDataProvider<DNASequence, NucleotideCompound>(
				alignment, AlignmentSourceDataType.NUCLEOTIDE), false);
		return new SequenceOrder(alignmentArea);
	}
	
	
	@Test
	public void test_moveSequence_backward() {
		SequenceOrder sequenceOrder = createSequenceOrder();
		SequenceDataProvider provider = sequenceOrder.getOwner().getSequenceProvider();
		assertEquals(0, sequenceOrder.moveSequence(2, -2));
		assertEquals("Sequence A", provider.sequenceNameByID(sequenceOrder.idByIndex(0)));
		assertEquals("Sequence C", provider.sequenceNameByID(sequenceOrder.idByIndex(1)));
		assertEquals("Sequence D", provider.sequenceNameByID(sequenceOrder.idByIndex(2)));
		assertEquals("Sequence B", provider.sequenceNameByID(sequenceOrder.idByIndex(3)));
	}
	
	
	@Test
	public void test_moveSequence_forward() {
		SequenceOrder sequenceOrder = createSequenceOrder();
		SequenceDataProvider provider = sequenceOrder.getOwner().getSequenceProvider();
		assertEquals(3, sequenceOrder.moveSequence(2, 5));  // Should be equal to moveSequence(2, 1) 
		assertEquals("Sequence C", provider.sequenceNameByID(sequenceOrder.idByIndex(0)));
		assertEquals("Sequence D", provider.sequenceNameByID(sequenceOrder.idByIndex(1)));
		assertEquals("Sequence B", provider.sequenceNameByID(sequenceOrder.idByIndex(2)));
		assertEquals("Sequence A", provider.sequenceNameByID(sequenceOrder.idByIndex(3)));
	}
	
	
	@Test
	public void test_moveSequence_noChange() {
		SequenceOrder sequenceOrder = createSequenceOrder();
		SequenceDataProvider provider = sequenceOrder.getOwner().getSequenceProvider();
		assertEquals(0, sequenceOrder.moveSequence(0, -5));
		assertEquals("Sequence C", provider.sequenceNameByID(sequenceOrder.idByIndex(0)));
		assertEquals("Sequence D", provider.sequenceNameByID(sequenceOrder.idByIndex(1)));
		assertEquals("Sequence A", provider.sequenceNameByID(sequenceOrder.idByIndex(2)));
		assertEquals("Sequence B", provider.sequenceNameByID(sequenceOrder.idByIndex(3)));

		assertEquals(3, sequenceOrder.moveSequence(3, 5));
		assertEquals("Sequence C", provider.sequenceNameByID(sequenceOrder.idByIndex(0)));
		assertEquals("Sequence D", provider.sequenceNameByID(sequenceOrder.idByIndex(1)));
		assertEquals("Sequence A", provider.sequenceNameByID(sequenceOrder.idByIndex(2)));
		assertEquals("Sequence B", provider.sequenceNameByID(sequenceOrder.idByIndex(3)));

		assertEquals(1, sequenceOrder.moveSequence(1, 0));
		assertEquals("Sequence C", provider.sequenceNameByID(sequenceOrder.idByIndex(0)));
		assertEquals("Sequence D", provider.sequenceNameByID(sequenceOrder.idByIndex(1)));
		assertEquals("Sequence A", provider.sequenceNameByID(sequenceOrder.idByIndex(2)));
		assertEquals("Sequence B", provider.sequenceNameByID(sequenceOrder.idByIndex(3)));
	}
	
	
	@Test
	public void test_setSequenceOrder() {
		SequenceOrder sequenceOrder = createSequenceOrder();
		SequenceDataProvider provider = sequenceOrder.getOwner().getSequenceProvider();

		sequenceOrder.setAlphabeticalSequenceOrder(true);
		assertEquals("Sequence A", provider.sequenceNameByID(sequenceOrder.idByIndex(0)));
		assertEquals("Sequence B", provider.sequenceNameByID(sequenceOrder.idByIndex(1)));
		assertEquals("Sequence C", provider.sequenceNameByID(sequenceOrder.idByIndex(2)));
		assertEquals("Sequence D", provider.sequenceNameByID(sequenceOrder.idByIndex(3)));
		
		sequenceOrder.setSourceSequenceOrder();
		assertEquals("Sequence C", provider.sequenceNameByID(sequenceOrder.idByIndex(0)));
		assertEquals("Sequence D", provider.sequenceNameByID(sequenceOrder.idByIndex(1)));
		assertEquals("Sequence A", provider.sequenceNameByID(sequenceOrder.idByIndex(2)));
		assertEquals("Sequence B", provider.sequenceNameByID(sequenceOrder.idByIndex(3)));

		sequenceOrder.setAlphabeticalSequenceOrder(false);
		assertEquals("Sequence D", provider.sequenceNameByID(sequenceOrder.idByIndex(0)));
		assertEquals("Sequence C", provider.sequenceNameByID(sequenceOrder.idByIndex(1)));
		assertEquals("Sequence B", provider.sequenceNameByID(sequenceOrder.idByIndex(2)));
		assertEquals("Sequence A", provider.sequenceNameByID(sequenceOrder.idByIndex(3)));
  }
}
