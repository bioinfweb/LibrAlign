/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben St�ver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.test;

import java.util.ArrayList;
import java.util.Collection;

import info.bioinfweb.libralign.AlignmentSourceDataType;
import info.bioinfweb.libralign.sequenceprovider.implementations.ArrayListSequenceDataProvider;



public class GenericCollectionTest {
  public static void main(String[] args) {
		ArrayListSequenceDataProvider<Character> provider = 
				new ArrayListSequenceDataProvider(AlignmentSourceDataType.OTHER);
		
		Collection collection = new ArrayList<Object>();
		collection.add('a');
		collection.add('b');
		collection.add('c');
		
		provider.addSequence("Seq");
		provider.insertTokensAt(provider.sequenceIDByName("Seq"), 0, collection);
		System.out.println(provider.getSequenceLength(provider.sequenceIDByName("Seq")));
	}	
}
