/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.test.tests;



public class BitShiftTest {
  public static void main(String[] args) {
//		System.out.println(Integer.MAX_VALUE);
//		System.out.println(Integer.MIN_VALUE);
//		System.out.println(Integer.MIN_VALUE >> 1);
//		System.out.println(Integer.MIN_VALUE >>> 1);
//		System.out.println(Integer.MIN_VALUE << 1);
//		int full = 0b11111111111111111111111111111111;  // -1
//		System.out.println(full);
//		full = 0b01111111111111111111111111111111;  // MAX_VALUE
//		System.out.println(full);
//		full = 0b10000000000000000000000000000000;  // MIN_VALUE
//		System.out.println(full);
//		System.out.println();
	
  	// Reading
		int value = 0b00000001000000100000001100000100;
		int mask = 0b00000000000000000000000011111111; 
		System.out.println(value + " " + mask);
		System.out.println((value >> 24) & mask);
		System.out.println((value >> 16) & mask);
		System.out.println((value >> 8) & mask);
		System.out.println(value & mask);
		
		// Writing:
		//V  0010
		//N  0001
		//  ~1110
		//  
	}	
}
