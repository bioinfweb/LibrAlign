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
package info.bioinfweb.libralign;



/**
 * Enumerates the possible view modes of aligned data.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public enum AlignmentDataViewMode {
  /** Allows both U and T in one sequence. */
  NUCLEOTIDE,
  
  /** U would be translated to R. */
  DNA, 
  
  /** T would be translated to U. */
  RNA,
  
  /** One sequence element contains three nucleotides. */
  CODON,
  
  /** Three column wide amino acids are displayed only where a protein coding region is marked. */
  MIXED_AMINO_ACID,
  
  /** A sequence only of amino acids each displayed one column wide. */
  ALL_AMINO_ACID,
  
  /** Simply an alignment of tokens represented by a string, which do not necessarily have a biological meaning. */
  NONE;
  
  //TODO Different treatments of gaps in nucleotide data when translating them to amino acids might influence the types of necessary view modes.
}
