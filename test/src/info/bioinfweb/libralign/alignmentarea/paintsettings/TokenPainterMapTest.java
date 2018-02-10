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
package info.bioinfweb.libralign.alignmentarea.paintsettings;


import static org.junit.Assert.*;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.AminoAcidTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.NucleotideTokenPainter;
import info.bioinfweb.libralign.alignmentarea.tokenpainter.SingleColorTokenPainter;

import org.junit.Test;



public class TokenPainterMapTest {
	@Test
	public void test_getPainter() {
		TokenPainterMap map = new TokenPainterMap();
		assertEquals(NucleotideTokenPainter.class, map.getPainter(CharacterStateSetType.DNA).getClass());
		assertEquals(NucleotideTokenPainter.class, map.getPainter(CharacterStateSetType.RNA).getClass());
		assertEquals(NucleotideTokenPainter.class, map.getPainter(CharacterStateSetType.NUCLEOTIDE).getClass());
		assertEquals(AminoAcidTokenPainter.class, map.getPainter(CharacterStateSetType.AMINO_ACID).getClass());
		assertEquals(SingleColorTokenPainter.class, map.getPainter(CharacterStateSetType.DISCRETE).getClass());
		assertEquals(SingleColorTokenPainter.class, map.getPainter(CharacterStateSetType.CONTINUOUS).getClass());
		assertEquals(SingleColorTokenPainter.class, map.getPainter(CharacterStateSetType.UNKNOWN).getClass());
		assertEquals(SingleColorTokenPainter.class, map.getPainter(null).getClass());
	}
}
