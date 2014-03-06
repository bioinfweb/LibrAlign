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


import org.biojava3.core.sequence.compound.AminoAcidCompound;
import org.biojava3.core.sequence.compound.CodonCompound;
import org.biojava3.core.sequence.compound.NucleotideCompound;

import info.bioinfweb.libralign.AlignmentSourceDataType;



/**
 * Interface that allows LibrAlign GUI elements to access different types of alignment data.
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
	 * @param elementIndex - the index of the element contained in the specified sequence 
	 * @return the token to be displayed in the GUI alignment view
	 */
	public Object getTokenAt(String sequenceName, int elementIndex);

  /**
   * Returns the data type of the underlying sequences.
   */
  public AlignmentSourceDataType getDataType();
}
