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
package info.bioinfweb.libralign.model.utils;


import static org.junit.Assert.*;


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.model.tokenset.TokenSet;

import org.junit.*;



public class DegapedIndexCalculatorTest {
	@Test
	public void test_degapedIndex() {
		TokenSet<Character> tokenSet = CharacterTokenSet.newDNAInstance();
		AlignmentModel<Character> model = new PackedAlignmentModel(tokenSet);
		String id = model.addSequence("A");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("A-AA--AAA", tokenSet));
		
		DegapedIndexCalculator<Character> calculator = new DegapedIndexCalculator(model);
		assertEquals(2, calculator.degapedIndex(id, 4));
		assertEquals(2, calculator.degapedIndex(id, 3));
		assertEquals(1, calculator.degapedIndex(id, 2));
	}
}
