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
package info.bioinfweb.libralign.model.utils;


import static org.junit.Assert.*;


import info.bioinfweb.commons.bio.CharacterStateType;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.model.tokenset.TokenSet;
import info.bioinfweb.libralign.model.tokenset.continuous.DoubleTokenSet;

import org.junit.*;



public class CharSequenceTokenScannerTest {
	@Test
	public void test_singleCharacter() {
		TokenSet<Character> tokenSet = new CharacterTokenSet(CharacterStateType.DNA, "ATCG-");
		CharSequenceTokenScanner<Character> scanner = new CharSequenceTokenScanner("ATfC G-TAT", tokenSet, true, '?');
		
		assertFalse(scanner.isDefaultTokenUsedNow());
		assertFalse(scanner.isDefaultTokenUsed());
		
		assertTrue(scanner.hasNext());
		assertEquals(new Character('A'), scanner.next());
		assertTrue(scanner.hasNext());
		assertEquals(new Character('T'), scanner.next());
		assertFalse(scanner.isDefaultTokenUsedNow());
		assertFalse(scanner.isDefaultTokenUsed());
		
		assertTrue(scanner.hasNext());
		assertEquals(new Character('?'), scanner.next());
		assertTrue(scanner.isDefaultTokenUsedNow());
		assertTrue(scanner.isDefaultTokenUsed());
		
		assertTrue(scanner.hasNext());
		assertEquals(new Character('C'), scanner.next());
		assertFalse(scanner.isDefaultTokenUsedNow());
		assertTrue(scanner.isDefaultTokenUsed());
		
		assertTrue(scanner.hasNext());
		assertEquals(new Character('G'), scanner.next());
		assertTrue(scanner.hasNext());
		assertEquals(new Character('-'), scanner.next());
		assertTrue(scanner.hasNext());
		assertEquals(new Character('T'), scanner.next());
		assertTrue(scanner.hasNext());
		assertEquals(new Character('A'), scanner.next());
		assertTrue(scanner.hasNext());
		assertEquals(new Character('T'), scanner.next());
		
		assertFalse(scanner.hasNext());
		assertNull(scanner.next());
	}


	@Test
	public void test_multipleCharacters() {
		DoubleTokenSet tokenSet = new DoubleTokenSet();
		CharSequenceTokenScanner<Double> scanner = new CharSequenceTokenScanner(
				"18 20.2\n7E-3 \tABC -5  22", tokenSet, true, Double.NaN);
		
		assertFalse(scanner.isDefaultTokenUsedNow());
		assertFalse(scanner.isDefaultTokenUsed());
		
		assertTrue(scanner.hasNext());
		assertEquals(new Double(18.0), scanner.next());
		assertTrue(scanner.hasNext());
		assertEquals(new Double(20.2), scanner.next());

		assertTrue(scanner.hasNext());
		assertEquals(new Double(7E-3), scanner.next());
		assertFalse(scanner.isDefaultTokenUsedNow());
		assertFalse(scanner.isDefaultTokenUsed());
		
		assertTrue(scanner.hasNext());
		assertEquals(new Double(Double.NaN), scanner.next());
		assertTrue(scanner.isDefaultTokenUsedNow());
		assertTrue(scanner.isDefaultTokenUsed());

		assertTrue(scanner.hasNext());
		assertEquals(new Double(-5.0), scanner.next());
		assertTrue(scanner.hasNext());
		assertEquals(new Double(22.0), scanner.next());
		
		assertFalse(scanner.isDefaultTokenUsedNow());
		assertTrue(scanner.isDefaultTokenUsed());
		assertFalse(scanner.hasNext());
		assertNull(scanner.next());
	}
}
