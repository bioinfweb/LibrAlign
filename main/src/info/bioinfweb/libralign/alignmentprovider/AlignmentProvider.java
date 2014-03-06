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

import info.bioinfweb.libralign.AlignmentDataViewMode;



/**
 * Interface that allows LibrAlign GUI elements to access different types of alignment data.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public interface AlignmentProvider {
	/**
	 * Returns the nucleotide to be displayed at the specified position if the view mode of the reading object
	 * is {@link AlignmentDataViewMode#NUCLEOTIDE}, {@link AlignmentDataViewMode#DNA} or 
	 * {@link AlignmentDataViewMode#RNA}, .
	 * 
	 * @param sequenceName - the name of the row in the alignment
	 * @param elementIndex - the index of the element contained in the specified sequence 
	 *        (Depending on the return value of {@link #getRowElementWidth(String)} and 
	 *        {@link #getReadingFrameOffset(String)} this might not be identical with the 
	 *        column index. The first column has the index 0.)
	 * @return a string to be displayed in the GUI alignment view
	 */
	public NucleotideCompound getNucleotideAt(String sequenceName, int elementIndex);
	
	public CodonCompound getCodonAt(String sequenceName, int elementIndex);
	
	public AminoAcidCompound getAminoAcidAt(String sequenceName, int elementIndex);
	
	/**
	 * Returns the string to be displayed at the specified position if the view mode of the reading object
	 * is {@link AlignmentDataViewMode#NONE}.
	 * <p>
	 * (Implementing classes should make sure that the returned string fits into the available
	 * space defined by {@link #getRowElementWidth(String)}.)
	 * 
	 * @param sequenceName - the name of the row in the alignment
	 * @param elementIndex - the index of the element contained in the specified sequence 
	 *        (Depending on the return value of {@link #getRowElementWidth(String)} and 
	 *        {@link #getReadingFrameOffset(String)} this might not be identical with the 
	 *        column index. The first column has the index 0.)
	 * @return a string to be displayed in the GUI alignment view
	 */
	public String getTextAt(String sequenceName, int elementIndex);
	
	/**
	 * Tests if the implementation of this data provider supports providing data of the specified
	 * type.
	 * 
	 * @param type - the alignment data type to be read from the implementation
	 * @return {@code true} if the specified data type is supported, {@code false} otherwise
	 */
	public boolean supportsDataType(AlignmentDataViewMode type);
}
