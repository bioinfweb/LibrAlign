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
package info.bioinfweb.libralign.sequenceprovider.implementations;


import info.bioinfweb.commons.collections.PackedObjectArrayList;
import info.bioinfweb.libralign.sequenceprovider.tokenset.TokenSet;

import java.util.List;



/**
 * A sequence data provider using a {@link PackedObjectArrayList} is the underlying data source.
 * This class can be used to efficiently store large editable sequences, because it will usually use only
 * a small fraction of the space {@link ArrayListSequenceDataProvider} will use to store its tokens.
 * <p>
 * Due to the compression method the token set cannot be changed during runtime for instances of this class. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type to be stored
 */
public class PackedSequenceDataProvider<T> extends AbstractListSequenceDataProvider<T> {
	public PackedSequenceDataProvider(TokenSet<T> tokenSet) {
		super(tokenSet);
	}


	@Override
	protected List<T> createNewSequence(int sequenceID, String sequenceName, int initialCapacity) {
		return new PackedObjectArrayList<T>(getTokenSet().size(), initialCapacity);
	}


	/**
	 * This method is not supported by this class.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public void setTokenSet(TokenSet<T> set) {
		throw new UnsupportedOperationException("This class does not support changing the token set during runtume.");
	}
}
