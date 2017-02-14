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
package info.bioinfweb.libralign.test.tests.performance;



public class DataTypeTest {
	public static void main(String[] args) {
		System.out.println("Max. human chromosome: " + (247 * 1000 * 1000));
		System.out.println("Max. indices:         " + Integer.MAX_VALUE);
		System.out.println("Max. tokens for 10px:  " + (Integer.MAX_VALUE / 10));
		System.out.println("Max. tokens for 100px:  " + (Integer.MAX_VALUE / 100));
		// => int border for indices may be no problem, but for painting it might not be enough.
	}
}
