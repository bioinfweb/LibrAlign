/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.alignmentprovider;


import java.util.Iterator;

import org.biojava3.core.sequence.compound.AminoAcidCompound;
import org.biojava3.core.sequence.compound.CodonCompound;
import org.biojava3.core.sequence.compound.NucleotideCompound;

import info.bioinfweb.libralign.AlignmentSourceDataType;
import info.bioinfweb.libralign.exception.AlignmentSourceNotWritableException;



/**
 * Interface that allows LibrAlign GUI elements to access different types of alignment data sources.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public interface AlignmentDataProvider {
	/**
	 * Returns the token to be displayed at the specified position. Depending on the return value of
	 * {@link #getDataType()} the returned object should be an instance of the following classes:
	 * <table>
	 *   <tr>
	 *     <th>Data type</th>
	 *     <th>Returned instance</th>
	 *   </tr>
	 *   <tr>
	 *     <td>{@link AlignmentSourceDataType#NUCLEOTIDE}</td>
	 *     <td>{@link NucleotideCompound}</td>
	 *   </tr>
	 *   <tr>
	 *     <td>{@link AlignmentSourceDataType#CODON}</td>
	 *     <td>{@link CodonCompound}</td>
	 *   </tr>
	 *   <tr>
	 *     <td>{@link AlignmentSourceDataType#AMINO_ACID}</td>
	 *     <td>{@link AminoAcidCompound}</td>
	 *   </tr>
	 *   <tr>
	 *     <td>{@link AlignmentSourceDataType#OTHER}</td>
	 *     <td>{@link Object}</td>
	 *   </tr>
	 * </table>
	 * 
	 * @param sequenceName - the name of the row in the alignment
	 * @param elementIndex - the index of the element contained in the specified sequence (The first element has the index 0.)
	 * @return the token to be displayed in the GUI alignment view
	 */
	public Object getTokenAt(String sequenceName, int elementIndex);
	
	/**
	 * Replaces the token at the specified position by the passed token.
	 * 
	 * @param sequenceName - the name of the row in the alignment
	 * @param elementIndex - the index of the element to be replaced (The first element has the index 0.)
	 * @param token - the new token for the specified position
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable 
	 */
	public void setTokenAt(String sequenceName, int elementIndex, Object token) throws AlignmentSourceNotWritableException;

	/**
	 * Inserts a token at the specified position.
	 * 
	 * @param sequenceName - the name of the row in the alignment
	 * @param elementIndex - the new index the inserted element will have ({@code 0 <= elementIndex < sequenceLength}) 
	 * @param token - the token to be inserted
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable 
	 */
	public void insertTokenAt(String sequenceName, int elementIndex, Object token) throws AlignmentSourceNotWritableException;

	/**
	 * Removes the token at the specified position from the underlying data source.
	 * 
	 * @param sequenceName - the name of the row in the alignment
	 * @param elementIndex - the index of the element to be removed (The first element has the index 0.)
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable 
	 */
	public void removeTokenAt(String sequenceName, int elementIndex) throws AlignmentSourceNotWritableException;
	
	/**
	 * Returns the number of sequences in the underlying data source.
	 */
	public int getSequenceCount();
	
	/**
	 * Returns an iterator over the names of all sequences contained in the underlying data source.
	 */
	public Iterator<String> sequenceNameIterator();
	
	/**
	 * Returns the length of the specified sequence.
	 * 
	 * @param sequenceName - the name of the row in the alignment
	 * @return the length of the sequence or {@code -1} if no sequence with the specified name exists
	 */
	public int getSequenceLength(String sequenceName);
	
	/**
	 * Returns the length of the longest sequence in the alignment which is equivalent to the length of
	 * the alignment. 
	 * <p>
	 * Note that this value represents the number of compounds of the specified data type. If e.g. a
	 * DNA data source is viewed as amino acid data this method would still return the number of 
	 * nucleotides in the longest sequence. 
	 * 
	 * @return the number of columns in the alignment
	 */
	public int getMaxSequenceLength();

	/**
	 * Allows to check whether the underlying data source can be modified.
	 * 
	 * @return {@code true} if the underlying data source is not writable, {@code false} otherwise.
	 */
	public boolean isReadOnly();
	
  /**
   * Returns the data type of the underlying sequences.
   */
  public AlignmentSourceDataType getDataType();
}
