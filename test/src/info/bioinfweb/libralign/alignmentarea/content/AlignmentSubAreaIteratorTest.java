/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea.content;


import static org.junit.Assert.* ;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.dataarea.implementations.ConsensusSequenceArea;
import info.bioinfweb.libralign.dataarea.implementations.sequenceindex.SequenceIndexArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.model.utils.AlignmentModelUtils;

import org.junit.* ;



public class AlignmentSubAreaIteratorTest {
	@Test
	public void test_next() {
		AlignmentArea area = new AlignmentArea();
		
		AlignmentModel<Character> model = new PackedAlignmentModel<Character>(CharacterTokenSet.newDNAInstance(false));
		String id1 = model.addSequence("Sequence 1");
		model.appendTokens(id1, AlignmentModelUtils.charSequenceToTokenList("ATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAG", 
				model.getTokenSet()));
		String id2 = model.addSequence("Sequence 2");
		model.appendTokens(id2, AlignmentModelUtils.charSequenceToTokenList("AT-GTTG",	model.getTokenSet()));
		String id3 = model.addSequence("Sequence 3");
		model.appendTokens(id3, AlignmentModelUtils.charSequenceToTokenList("AT-GTAG",	model.getTokenSet()));
		area.setAlignmentModel(model, true);
		
		SequenceIndexArea topDataArea = new SequenceIndexArea(area.getContentArea());
		area.getDataAreas().getTopAreas().add(topDataArea);
		ConsensusSequenceArea sequenceDataArea = new ConsensusSequenceArea(area.getContentArea());
		area.getDataAreas().getSequenceAreas(id2).add(sequenceDataArea);
		ConsensusSequenceArea bottomDataArea = new ConsensusSequenceArea(area.getContentArea());
		area.getDataAreas().getBottomAreas().add(bottomDataArea);
		
		AlignmentSubAreaIterator iterator = new AlignmentSubAreaIterator(area);
		assertTrue(iterator.hasNext());
		assertEquals(topDataArea, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(area.getContentArea().getSequenceAreaByID(id1), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(area.getContentArea().getSequenceAreaByID(id2), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(sequenceDataArea, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(area.getContentArea().getSequenceAreaByID(id3), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(bottomDataArea, iterator.next());
		assertFalse(iterator.hasNext());
	}
}
