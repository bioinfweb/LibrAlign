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
package info.bioinfweb.libralign.model;


import static org.junit.Assert.*;
import info.bioinfweb.libralign.model.adapters.CharSequenceAdapter;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;

import org.junit.* ;



/**
 * Contains test cases for {@link AlignmentModelUtils}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class AlignmentModelUtilsTest {
	@Test
	public void test_reverseComplement() {
		AlignmentModel<Character> model = new PackedAlignmentModel<Character>(CharacterTokenSet.newNucleotideInstance());
		model.addSequence("A");
		int sequenceID = model.sequenceIDByName("A");
		model.appendTokens(sequenceID, AlignmentModelUtils.charSequenceToTokenList("ACGGT-ACT", model.getTokenSet()));
		
		AlignmentModelUtils.reverseComplement(model, sequenceID, 2, 8);
		
		CharSequenceAdapter<Character> adapter = new CharSequenceAdapter<Character>(model, true);
		assertEquals("ACGT-ACCT", adapter.getSequence(sequenceID).toString());
	}
}
