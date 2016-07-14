/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
package info.bioinfweb.libralign.model.adapters;


import info.bioinfweb.libralign.model.BasicAlignmentModelView;
import info.bioinfweb.libralign.model.SequenceAccess;
import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * Classes implementing this interface provide access to the data provided by an implementation of
 * {@link AlignmentModel} in a different way (e.g. as a set of {@link CharSequence}s or
 * BioJava sequences).
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <S> - the type of sequence object that is returned by the implementation of this interface
 * @param <T> - the token type used by the underlying provider
 */
public interface SequenceDataAdapter<S, T> extends BasicAlignmentModelView<T>, SequenceAccess<S> {
	/**
	 * Returns a view or copy of a subsequence in the underlying data source.
	 * 
	 * @param sequenceID - the ID of the sequence in the underlying {@link AlignmentModel} to be viewed
	 * @param offset - the start index of the subsequence to be viewed (The first token has the index 0.)
	 * @param length - the length of the subsequence to be viewed
	 * @return a copy or view depending on the return value of {@link #returnsCopies()}
	 */
	public S getSubSequence(int sequenceID, int offset, int length);
	
	/**
	 * Specifies if the instances returned by {@link #getSequence(int)} are views of the underlying data source
	 * or independent copies of the sequences.
	 * 
	 * @return {@code true} if copies are returned, {@code false} if views are returned
	 */
	public boolean returnsCopies();
}
