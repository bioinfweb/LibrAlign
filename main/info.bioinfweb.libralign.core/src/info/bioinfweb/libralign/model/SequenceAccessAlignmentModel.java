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
package info.bioinfweb.libralign.model;


import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;



/**
 * Interface for classes implementing {@link AlignmentModel} by using sequence objects. Methods to 
 * replace a whole sequence are specified for the case that the sequence objects (e.g., strings or 
 * <i>BioJava</i> sequences) do not allow to be modified after creation. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <S> the type of the sequence objects (e.g. a BioJava sequence type or {@link String})
 * @param <T> the type of sequence elements (tokens) the implementing provider object works with
 */
public interface SequenceAccessAlignmentModel<S, T> extends AlignmentModel<T>, SequenceAccess<S> {
  /**
   * Adds a new sequence with the specified content to the underlying data source and generates an ID for it.
   * 
   * @param sequenceName the name of the new sequence
	 * @param content the sequence object to be added.
   * @return the unique ID of the new sequence
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for sequences
   */
	public String addSequence(String sequenceName, S content) throws AlignmentSourceNotWritableException;
	
  /**
   * Adds a new sequence with the specified content to the underlying data source assigning the specified ID to it.
   * 
   * @param sequenceName the name of the new sequence
   * @param sequenceID the ID the new sequence should have
	 * @param content the sequence object to be added.
   * @return the unique ID of the new sequence (identical with {@code sequenceID})
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for sequences
	 * @throws IllegalArgumentException if a sequence with the specified ID is already present in this model
   */
  public String addSequence(String sequenceName, String sequenceID, S content) throws AlignmentSourceNotWritableException, IllegalArgumentException;
  
  
  /**
   * Adds a new empty sequence to the underlying data source at the specified index assigning the specified ID to it.
   * 
   * @param sequenceName the name of the new sequence
   * @param sequenceID the ID the new sequence should have
   * @param content the sequence object to be added.
   * @param index the index of where the sequence should be added
   * @return the unique ID of the new sequence (identical with {@code sequenceID})
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for sequences
	 * @throws IllegalArgumentException if a sequence with the specified ID is already present in this model
   */
  public String addSequence(int index, String sequenceName, String sequenceID, S content) throws AlignmentSourceNotWritableException, IllegalArgumentException;
  
  
  /**
   * Adds a new empty sequence to the underlying data source at the specified index and generates an ID for it.
   * 
   * @param sequenceName the name of the new sequence
   * @param content the sequence object to be added.
   * @param index the index of where the sequence should be added
   * @return the unique ID of the new sequence
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for sequences
   */
  public String addSequence(int index, String sequenceName, S content) throws AlignmentSourceNotWritableException;
}
