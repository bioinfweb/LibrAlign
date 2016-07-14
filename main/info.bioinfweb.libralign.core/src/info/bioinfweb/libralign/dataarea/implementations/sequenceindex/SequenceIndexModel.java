/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
package info.bioinfweb.libralign.dataarea.implementations.sequenceindex;


import info.bioinfweb.commons.collections.PackedIntegerArrayList;
import info.bioinfweb.libralign.model.data.DataModel;



public class SequenceIndexModel implements DataModel {
	public static final int MIN_BITS_PER_VALUE = 16;  // Below 16 not more than 16 KB could be saved by reducing the bit rate.
	
	
	private int sequenceID;
	private PackedIntegerArrayList unalignedIndices;
	
	
	public SequenceIndexModel(int sequenceID, int initialCapacity) {
		super();
		this.sequenceID = sequenceID;
		ensureCapacity(initialCapacity);
	}


	public void ensureCapacity(int newCapacity) {
		int bitsPerValue = Math.max(MIN_BITS_PER_VALUE, PackedIntegerArrayList.calculateBitsPerValue(newCapacity));
		if ((unalignedIndices == null) || (unalignedIndices.getBitsPerValue() < bitsPerValue)) {
			unalignedIndices = new PackedIntegerArrayList(bitsPerValue, 0, newCapacity);
		}
		unalignedIndices.ensureCapacity(newCapacity);
	}
	
	
	public int get(int column) {
		return (int)unalignedIndices.get(column);
	}
	
	
	public void set(int column, int value) {
		unalignedIndices.set(column, value);
	}
	
	
	public void add(int value) {
		unalignedIndices.add(value);
	}
	
	//TODO Add necessary methods to fill the model. (Calculation probably happens somewhere else?)
}
