/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.libralign.model.utils;


import static org.junit.Assert.*;


import org.junit.Test;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.adapters.CharSequenceAdapter;
import info.bioinfweb.libralign.model.implementations.ArrayListAlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;



/**
 * Contains test cases for {@link AlignmentModelUtils}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class AlignmentModelUtilsTest {
	@Test
	public void test_reverseComplement() {
		AlignmentModel<Character> model = new PackedAlignmentModel<Character>(CharacterTokenSet.newNucleotideInstance(false));
		String sequenceID = model.addSequence("A");
		model.appendTokens(sequenceID, AlignmentModelUtils.charSequenceToTokenList("ACGGT-ACT", model.getTokenSet()), true);
		
		AlignmentModelUtils.reverseComplement(model, sequenceID, 2, 8);
		
		CharSequenceAdapter<Character> adapter = new CharSequenceAdapter<Character>(model, true);
		assertEquals("ACGT-ACCT", adapter.getSequence(sequenceID).toString());
	}
	

/**
* Compares two sequences with each other.
* 
* @return false if sequences are not equal, true if sequences are equal.
*/	
	public void test_sequencesEqual() {
		
		AlignmentModel<Character> alignmentModel1 = new ArrayListAlignmentModel<Character>(CharacterTokenSet.newNucleotideInstance(false));
		AlignmentModel<Character> alignmentModel2 = new ArrayListAlignmentModel<Character>(CharacterTokenSet.newNucleotideInstance(false));
		

		String sequenceID1 = alignmentModel1.addSequence("seq1");
		alignmentModel1.appendToken(sequenceID1, 'A', true);
		alignmentModel1.appendToken(sequenceID1, 'T', true);
		alignmentModel1.appendToken(sequenceID1, 'G', true);
		alignmentModel1.appendToken(sequenceID1, 'C', true);
		
		String sequenceID2 = alignmentModel1.addSequence("seq2");
		alignmentModel1.appendToken(sequenceID2, 'A', true);
		alignmentModel1.appendToken(sequenceID2, 'T', true);
		alignmentModel1.appendToken(sequenceID2, 'G', true);
		alignmentModel1.appendToken(sequenceID2, 'C', true);
		
		String sequenceID3 = alignmentModel2.addSequence("seq3");
		alignmentModel2.appendToken(sequenceID3, 'A', true);
		alignmentModel2.appendToken(sequenceID3, 'T', true);
		alignmentModel2.appendToken(sequenceID3, 'G', true);
		alignmentModel2.appendToken(sequenceID3, 'G', true);
		
		
	
		assertTrue(AlignmentModelUtils.sequencesEqual(alignmentModel1, sequenceID1, alignmentModel1, sequenceID2));
		assertFalse(AlignmentModelUtils.sequencesEqual(alignmentModel1, sequenceID1, alignmentModel2, sequenceID3));
		assertFalse(AlignmentModelUtils.sequencesEqual(alignmentModel1, sequenceID2, alignmentModel2, sequenceID3));

	}
}
