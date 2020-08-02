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

	
	@Test
	public void test_sequencesEqual() {
		AlignmentModel<Character> alignmentModel1 = new ArrayListAlignmentModel<Character>(CharacterTokenSet.newNucleotideInstance(false));
		AlignmentModel<Character> alignmentModel2 = new ArrayListAlignmentModel<Character>(CharacterTokenSet.newNucleotideInstance(false));
		
		String sequenceID1 = alignmentModel1.addSequence("seq1");
		alignmentModel1.appendTokens(sequenceID1, AlignmentModelUtils.charSequenceToTokenList("ATGC", alignmentModel1.getTokenSet()), true);		
		String sequenceID2 = alignmentModel1.addSequence("seq2");
		alignmentModel1.appendTokens(sequenceID2, AlignmentModelUtils.charSequenceToTokenList("ATGC", alignmentModel1.getTokenSet()), true);
		String sequenceID3 = alignmentModel1.addSequence("seq3");
		alignmentModel1.appendTokens(sequenceID3, AlignmentModelUtils.charSequenceToTokenList("ATGCT-C", alignmentModel1.getTokenSet()), true);
		String sequenceID4 = alignmentModel1.addSequence("seq4");
		alignmentModel1.appendTokens(sequenceID4, AlignmentModelUtils.charSequenceToTokenList("TTGC", alignmentModel1.getTokenSet()), true);
			
		String sequenceID5 = alignmentModel2.addSequence("seq5");
		alignmentModel2.appendTokens(sequenceID5, AlignmentModelUtils.charSequenceToTokenList("ATGC", alignmentModel2.getTokenSet()), true);		
		String sequenceID6 = alignmentModel2.addSequence("seq6");
		alignmentModel2.appendTokens(sequenceID6, AlignmentModelUtils.charSequenceToTokenList("AAGC", alignmentModel2.getTokenSet()), true);		
		String sequenceID7 = alignmentModel2.addSequence("seq7");
		alignmentModel2.appendTokens(sequenceID7, AlignmentModelUtils.charSequenceToTokenList("AACC", alignmentModel2.getTokenSet()), true);
		String sequenceID8 = alignmentModel2.addSequence("seq8");
		alignmentModel2.appendTokens(sequenceID8, AlignmentModelUtils.charSequenceToTokenList("AACG", alignmentModel2.getTokenSet()), true);
		String sequenceID9 = alignmentModel2.addSequence("seq9");
		alignmentModel2.appendTokens(sequenceID9, AlignmentModelUtils.charSequenceToTokenList("TAAGTGTACG", alignmentModel2.getTokenSet()), true);
			
		assertTrue(AlignmentModelUtils.sequencesEqual(alignmentModel1, sequenceID1, alignmentModel1, sequenceID2));	//same AM, tokens equal
		assertFalse(AlignmentModelUtils.sequencesEqual(alignmentModel1, sequenceID1, alignmentModel1, sequenceID3));//same AM, different length
		assertFalse(AlignmentModelUtils.sequencesEqual(alignmentModel1, sequenceID1, alignmentModel1, sequenceID4));//same AM, first position changed
		assertFalse(AlignmentModelUtils.sequencesEqual(alignmentModel1, sequenceID3, alignmentModel1, sequenceID4));//same AM, different length + first Token changed
		
		assertTrue(AlignmentModelUtils.sequencesEqual(alignmentModel1, sequenceID1, alignmentModel2, sequenceID5));	//different AM, tokens equal
		assertFalse(AlignmentModelUtils.sequencesEqual(alignmentModel1, sequenceID1, alignmentModel2, sequenceID9));//completely different (AM, length, tokens)
		assertFalse(AlignmentModelUtils.sequencesEqual(alignmentModel2, sequenceID1, alignmentModel2, sequenceID6));//same AM, second position changed
		assertFalse(AlignmentModelUtils.sequencesEqual(alignmentModel2, sequenceID1, alignmentModel2, sequenceID7));//same AM, third position changed
		assertFalse(AlignmentModelUtils.sequencesEqual(alignmentModel2, sequenceID1, alignmentModel2, sequenceID8));//same AM, last position changed
	}
}
