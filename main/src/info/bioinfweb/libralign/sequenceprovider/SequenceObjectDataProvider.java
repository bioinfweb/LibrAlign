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
package info.bioinfweb.libralign.sequenceprovider;


import info.bioinfweb.libralign.sequenceprovider.exception.AlignmentSourceNotWritableException;

import org.biojava.bio.seq.Sequence;



/**
 * Interface for classes implementing {@link SequenceDataProvider} by sequence objects. Methods to replace a 
 * whole sequence are specified for the case that the sequence objects (e.g. Strings or BioJava sequences)
 * do not allow to be modified after creation. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <S> - the type of the sequence objects (e.g. {@link Sequence} or {@link String})
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public interface SequenceObjectDataProvider<S, T> extends SequenceDataProvider<T> {
  /**
   * Adds a the specified sequence to the underlying data source and generates an ID for it.
   * 
   * @param sequenceName - the name of the new sequence
	 * @param content - the sequence object to be added.
   * @return the unique ID of the new sequence
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for sequences
   */
	public int addSequence(String sequenceName, S content);
	
	/**
	 * Replaces the sequence object with the specified ID.
	 * 
	 * @param sequenceID - the ID of the sequence to be replaced
	 * @param content - the new sequence object
	 */
	public void setSequenceContent(int sequenceID, S content);
	
	/**
	 * Returns the sequence object with the specified ID.
	 * 
	 * @param sequenceID - the ID of the sequence to be returned
	 * @return the sequence object from the underlying data source
	 */
	public S getSequence(int sequenceID);
}
