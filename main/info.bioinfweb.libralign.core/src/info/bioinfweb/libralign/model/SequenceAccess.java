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
package info.bioinfweb.libralign.model;


import info.bioinfweb.libralign.model.adapters.SequenceDataAdapter;



/**
 * Implementations of {@link AlignmentModel} or {@link SequenceDataAdapter} that provide access to
 * a sequence object representing a row in the alignment should implement this interface.
 * <p>
 * This interface can be considered as the overlap between {@link SequenceAccessAlignmentModel} and 
 * {@link SequenceDataAdapter}, but could also be implemented by classes not implementing one of these
 * interfaces.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <S> the class of the sequence objects representing a row in the alignment
 */
public interface SequenceAccess<S> {
	/**
	 * Returns the sequence object with the specified ID.
	 * <p>
	 * If the implementing class also implements {@link SequenceDataAdapter} the returned object can be a view 
	 * or copy of a sequence in the underlying data source depending in the return value of 
	 * {@link SequenceDataAdapter#returnsCopies()}. If {@link SequenceAccessAlignmentModel} is implemented
	 * the object used to store the sequence data is returned.
	 * 
	 * @param sequenceID - the ID of the sequence to be returned
	 * @return the sequence object as described above
	 */
	public S getSequence(String sequenceID);
}
