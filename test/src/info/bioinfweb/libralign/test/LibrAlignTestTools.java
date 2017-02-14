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
package info.bioinfweb.libralign.test;


import static org.junit.Assert.*;

import info.bioinfweb.libralign.model.utils.indextranslation.IndexRelation;



public class LibrAlignTestTools {
	public static void assertIndexRelation(int expectedBefore, int expectedCorresponding, int expectedAfter, IndexRelation actual) {
		assertEquals(expectedBefore, actual.getBefore());
		assertEquals(expectedCorresponding, actual.getCorresponding());
		assertEquals(expectedAfter, actual.getAfter());
	}
}
