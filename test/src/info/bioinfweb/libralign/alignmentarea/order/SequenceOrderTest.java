/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea.order;


import static org.junit.Assert.assertEquals;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.model.utils.AlignmentModelUtils;

import org.junit.Test;



/**
 * Tests methods of {@link SequenceOrder}.
 * 
 * @author Ben St&ouml;ver
 */
public class SequenceOrderTest {
	private void addSequence(String name, String sequence, AlignmentModel<Character> model) {
		String id = model.addSequence(name);
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("ATGGGCT", model.getTokenSet()));
	}
	
	
	private SequenceOrder createSequenceOrder() {
		PackedAlignmentModel<Character> model = new PackedAlignmentModel<>(CharacterTokenSet.newNucleotideInstance(false));
		addSequence("Sequence C", "ATGGGCT", model);
		addSequence("Sequence D", "ATG-GCA", model);
		addSequence("Sequence A", "ACGGGCA", model);
		addSequence("Sequence B", "ATG--CT", model);
		
		AlignmentArea alignmentArea = new AlignmentArea();
		alignmentArea.setAlignmentModel(model, false);
		return new SequenceOrder(alignmentArea);
	}
	
	
	@Test
	public void test_moveSequence_backward() {
		SequenceOrder sequenceOrder = createSequenceOrder();
		AlignmentModel provider = sequenceOrder.getOwner().getAlignmentModel();
		assertEquals(0, sequenceOrder.moveSequence(2, -2));
		assertEquals("Sequence A", provider.sequenceNameByID(sequenceOrder.idByIndex(0)));
		assertEquals("Sequence C", provider.sequenceNameByID(sequenceOrder.idByIndex(1)));
		assertEquals("Sequence D", provider.sequenceNameByID(sequenceOrder.idByIndex(2)));
		assertEquals("Sequence B", provider.sequenceNameByID(sequenceOrder.idByIndex(3)));
	}
	
	
	@Test
	public void test_moveSequence_forward() {
		SequenceOrder sequenceOrder = createSequenceOrder();
		AlignmentModel provider = sequenceOrder.getOwner().getAlignmentModel();
		assertEquals(3, sequenceOrder.moveSequence(2, 5));  // Should be equal to moveSequence(2, 1) 
		assertEquals("Sequence C", provider.sequenceNameByID(sequenceOrder.idByIndex(0)));
		assertEquals("Sequence D", provider.sequenceNameByID(sequenceOrder.idByIndex(1)));
		assertEquals("Sequence B", provider.sequenceNameByID(sequenceOrder.idByIndex(2)));
		assertEquals("Sequence A", provider.sequenceNameByID(sequenceOrder.idByIndex(3)));
	}
	
	
	@Test
	public void test_moveSequence_noChange() {
		SequenceOrder sequenceOrder = createSequenceOrder();
		AlignmentModel provider = sequenceOrder.getOwner().getAlignmentModel();
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
		AlignmentModel provider = sequenceOrder.getOwner().getAlignmentModel();

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
