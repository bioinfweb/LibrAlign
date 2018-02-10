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


import java.util.ArrayList;
import java.util.Collection;

import info.bioinfweb.libralign.model.implementations.ArrayListAlignmentModel;



public class GenericCollectionTest {
  public static void main(String[] args) {
		ArrayListAlignmentModel<Character> provider = 
				new ArrayListAlignmentModel(null);  //TODO When implementation of main classes is finished, specifying null might lead to an exception.
		
		Collection collection = new ArrayList<Object>();
		collection.add('a');
		collection.add('b');
		collection.add('c');
		
		String id = provider.addSequence("Seq");
		provider.insertTokensAt(id, 0, collection);
		System.out.println(provider.getSequenceLength(id));
	}	
}
