/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.model.implementations;


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.tokenset.TokenSet;

import java.util.ArrayList;
import java.util.List;



/**
 * An abstract implementation of {@link AlignmentModel} using a map of array lists to store 
 * alignment data. The constructors offer different options to specify the initial capacity a new 
 * {@link ArrayList} instance.
 * 
 * @author Ben St&ouml;ever
 * @since 0.1.0
 *
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public class ArrayListAlignmentModel<T> extends AbstractListAlignmentModel<T> {
	/**
	 * Creates a new instance of this class using {@link #DEFAULT_INITIAL_CAPACITY} as the initial capacity
	 * which is only used if it is lower than {@link #getMaxSequenceLength()} to create new sequence array 
	 * lists.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 */
	public ArrayListAlignmentModel(TokenSet<T> tokenSet) {
		super(tokenSet);
	}


	/**
	 * Creates a new instance of this class.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 * @param initialCapacity - the initial capacity newly generated array lists will have
	 * @param useMaxLength - Specify {@code true} here if {@code initialCapacity} shall only be used if it 
	 *        is lower than {@link #getMaxSequenceLength()} to create new sequence array lists.
	 */
	public ArrayListAlignmentModel(TokenSet<T> tokenSet, int initialCapacity, 
			boolean useMaxLength) {
		
		super(tokenSet, initialCapacity, useMaxLength);
	}


	@Override
	protected List<T> createNewSequence(int sequenceID, String sequenceName,
			int initialCapacity) {

		return new ArrayList<T>(initialCapacity);
	}
}