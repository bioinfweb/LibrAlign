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
		int id = model.addSequence("A");
		model.appendTokens(id, AlignmentModelUtils.charSequenceToTokenList("A-AA--AAA", tokenSet));
		
		DegapedIndexCalculator<Character> calculator = new DegapedIndexCalculator(model);
		assertEquals(2, calculator.degapedIndex(id, 4));
		assertEquals(2, calculator.degapedIndex(id, 3));
		assertEquals(1, calculator.degapedIndex(id, 2));
	}
}
