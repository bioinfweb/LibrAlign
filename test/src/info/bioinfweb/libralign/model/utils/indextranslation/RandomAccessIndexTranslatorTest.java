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
package info.bioinfweb.libralign.model.utils.indextranslation;


import static org.junit.Assert.*;

import static info.bioinfweb.libralign.test.LibrAlignTestTools.*;


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.model.tokenset.TokenSet;
import info.bioinfweb.libralign.model.utils.AlignmentModelUtils;

import org.junit.*;



public class RandomAccessIndexTranslatorTest {
	@Test
	public void test_getUnalignedAlignedIndex() {
		TokenSet<Character> tokenSet = CharacterTokenSet.newDNAInstance(false);
		AlignmentModel<Character> model = new PackedAlignmentModel<Character>(tokenSet);
		String id = model.addSequence("A");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("--A-A-AA--AAA-", tokenSet), true);
		
		RandomAccessIndexTranslator<Character> calculator = new RandomAccessIndexTranslator<Character>(model);
		assertIndexRelation(IndexRelation.OUT_OF_RANGE, IndexRelation.OUT_OF_RANGE, 0, calculator.getUnalignedIndex(id, -1));
		assertIndexRelation(IndexRelation.OUT_OF_RANGE, IndexRelation.GAP, 0, calculator.getUnalignedIndex(id, 0));
		assertIndexRelation(IndexRelation.OUT_OF_RANGE, IndexRelation.GAP, 0, calculator.getUnalignedIndex(id, 1));
		assertIndexRelation(0, 0, 0, calculator.getUnalignedIndex(id, 2));
		assertIndexRelation(0, IndexRelation.GAP, 1, calculator.getUnalignedIndex(id, 3));
		assertIndexRelation(1, 1, 1, calculator.getUnalignedIndex(id, 4));
		assertIndexRelation(1, IndexRelation.GAP, 2, calculator.getUnalignedIndex(id, 5));
		assertIndexRelation(2, 2, 2, calculator.getUnalignedIndex(id, 6));
		assertIndexRelation(3, 3, 3, calculator.getUnalignedIndex(id, 7));
		assertIndexRelation(3, IndexRelation.GAP, 4, calculator.getUnalignedIndex(id, 8));
		assertIndexRelation(3, IndexRelation.GAP, 4, calculator.getUnalignedIndex(id, 9));
		assertIndexRelation(4, 4, 4, calculator.getUnalignedIndex(id, 10));
		assertIndexRelation(5, 5, 5, calculator.getUnalignedIndex(id, 11));
		assertIndexRelation(6, 6, 6, calculator.getUnalignedIndex(id, 12));
		assertIndexRelation(6, IndexRelation.GAP, IndexRelation.OUT_OF_RANGE, calculator.getUnalignedIndex(id, 13));
		assertIndexRelation(6, IndexRelation.OUT_OF_RANGE, IndexRelation.OUT_OF_RANGE, calculator.getUnalignedIndex(id, 14));
		
		assertIndexRelation(1, IndexRelation.GAP, 2, calculator.getUnalignedIndex(id, 5));
		assertIndexRelation(3, 3, 3, calculator.getUnalignedIndex(id, 7));
		assertIndexRelation(3, IndexRelation.GAP, 4, calculator.getUnalignedIndex(id, 8));
		assertIndexRelation(0, 0, 0, calculator.getUnalignedIndex(id, 2));
		
		assertEquals(IndexRelation.OUT_OF_RANGE, calculator.getAlignedIndex(id, -1));
		assertEquals(2, calculator.getAlignedIndex(id, 0));
		assertEquals(4, calculator.getAlignedIndex(id, 1));
		assertEquals(6, calculator.getAlignedIndex(id, 2));
		assertEquals(7, calculator.getAlignedIndex(id, 3));
		assertEquals(10, calculator.getAlignedIndex(id, 4));
		assertEquals(11, calculator.getAlignedIndex(id, 5));
		assertEquals(12, calculator.getAlignedIndex(id, 6));
		assertEquals(IndexRelation.OUT_OF_RANGE, calculator.getAlignedIndex(id, 7));
		
		assertEquals(7, calculator.getUnalignedLength(id));
	}	
	
	
	@Test
	public void test_getUnalignedIndex_onlyGaps() {
		TokenSet<Character> tokenSet = CharacterTokenSet.newDNAInstance(false);
		AlignmentModel<Character> model = new PackedAlignmentModel<Character>(tokenSet);
		String id = model.addSequence("A");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("---", tokenSet), true);
		
		RandomAccessIndexTranslator<Character> calculator = new RandomAccessIndexTranslator<Character>(model);
		assertIndexRelation(IndexRelation.OUT_OF_RANGE, IndexRelation.OUT_OF_RANGE, IndexRelation.OUT_OF_RANGE, 
				calculator.getUnalignedIndex(id, -1));
		assertIndexRelation(IndexRelation.OUT_OF_RANGE, IndexRelation.GAP, IndexRelation.OUT_OF_RANGE, calculator.getUnalignedIndex(id, 0));
		assertIndexRelation(IndexRelation.OUT_OF_RANGE, IndexRelation.GAP, IndexRelation.OUT_OF_RANGE, calculator.getUnalignedIndex(id, 1));
		assertIndexRelation(IndexRelation.OUT_OF_RANGE, IndexRelation.GAP, IndexRelation.OUT_OF_RANGE, calculator.getUnalignedIndex(id, 2));
		assertIndexRelation(IndexRelation.OUT_OF_RANGE, IndexRelation.OUT_OF_RANGE, IndexRelation.OUT_OF_RANGE, 
				calculator.getUnalignedIndex(id, 3));

		assertEquals(IndexRelation.OUT_OF_RANGE, calculator.getAlignedIndex(id, -1));
		assertEquals(IndexRelation.OUT_OF_RANGE, calculator.getAlignedIndex(id, 0));
		assertEquals(IndexRelation.OUT_OF_RANGE, calculator.getAlignedIndex(id, 18));
		
		assertEquals(0, calculator.getUnalignedLength(id));
	}	
	
	
	@Test
	public void test_getUnalignedLength() {
		TokenSet<Character> tokenSet = CharacterTokenSet.newDNAInstance(false);
		AlignmentModel<Character> model = new PackedAlignmentModel<Character>(tokenSet);
		RandomAccessIndexTranslator<Character> calculator = new RandomAccessIndexTranslator<Character>(model);
		
		String id = model.addSequence("A");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("---", tokenSet), true);
		assertEquals(0, calculator.getUnalignedLength(id));

		id = model.addSequence("B");
		assertEquals(0, calculator.getUnalignedLength(id));

		id = model.addSequence("C");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("ACGT", tokenSet), true);
		assertEquals(4, calculator.getUnalignedLength(id));

		id = model.addSequence("D");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("-ACGT", tokenSet), true);
		assertEquals(4, calculator.getUnalignedLength(id));

		id = model.addSequence("E");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("ACGT-", tokenSet), true);
		assertEquals(4, calculator.getUnalignedLength(id));

		id = model.addSequence("F");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("AC--G-T-", tokenSet), true);
		assertEquals(4, calculator.getUnalignedLength(id));

		id = model.addSequence("G");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("---ACGT-", tokenSet), true);
		assertEquals(4, calculator.getUnalignedLength(id));
		
		id = model.addSequence("H");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("G", tokenSet), true);
		assertEquals(1, calculator.getUnalignedLength(id));
	}	
}
