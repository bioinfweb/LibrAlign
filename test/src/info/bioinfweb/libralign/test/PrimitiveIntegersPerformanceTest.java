/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.test;



public class PrimitiveIntegersPerformanceTest {
	public static final int SIZE = 1024 * 1024 * 32;
	
	
	private static void testWithInt() {
  	byte[] byteArray = new byte[SIZE];
  	byte[] shortArray = new byte[SIZE];
  	byte[] intArray = new byte[SIZE];
  	byte[] longArray = new byte[SIZE];
  	
		long start = System.currentTimeMillis();
		for (int repeat = 0; repeat < 100; repeat++) {
			for (int i = 0; i < byteArray.length; i++) {
				byteArray[i] = 23;
			}
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("byte: " + time);
		
		start = System.currentTimeMillis();
		for (int repeat = 0; repeat < 100; repeat++) {
			for (int i = 0; i < shortArray.length; i++) {
				shortArray[i] = 23;
			}
		}
		time = System.currentTimeMillis() - start;
		System.out.println("short: " + (time / 2));
		
		start = System.currentTimeMillis();
		for (int repeat = 0; repeat < 100; repeat++) {
			for (int i = 0; i < intArray.length; i++) {
				intArray[i] = 23;
			}
		}
		time = System.currentTimeMillis() - start;
		System.out.println("int: " + (time / 4));
		
		start = System.currentTimeMillis();
		for (int repeat = 0; repeat < 100; repeat++) {
			for (int i = 0; i < longArray.length; i++) {
				longArray[i] = 23;
			}
		}
		time = System.currentTimeMillis() - start;
		System.out.println("long: " + (time / 8));
	}
	
	
	private static void testWithAccording() {
  	byte[] intArray = new byte[SIZE];
  	byte[] longArray = new byte[SIZE];
  	
		long start = System.currentTimeMillis();
		for (int repeat = 0; repeat < 100; repeat++) {
			int secondIndex = 0;
			for (int i = 0; i < intArray.length; i++) {
				intArray[i] = 23;
				secondIndex++;
			}
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("int: " + (time / 4));
		
		start = System.currentTimeMillis();
		for (long repeat = 0; repeat < 100; repeat++) {
			int secondIndex = 0;
			for (long i = 0; i < longArray.length; i++) {
				longArray[secondIndex] = 23;
				secondIndex++;
			}
		}
		time = System.currentTimeMillis() - start;
		System.out.println("long: " + (time / 8));
	}
	
	
  public static void main(String[] args) {
		testWithAccording();
		// => Storing binary data in double arrays seems to be far more efficient.
	}	
}
