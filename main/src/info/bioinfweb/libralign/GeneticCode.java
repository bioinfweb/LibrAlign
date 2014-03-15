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
package info.bioinfweb.libralign;


import org.biojava3.core.sequence.compound.AminoAcidCompound;
import org.biojava3.core.sequence.compound.CodonCompound;
import org.biojava3.core.sequence.compound.NucleotideCompound;



public interface GeneticCode {
  AminoAcidCompound getAminoAcid(NucleotideCompound first, NucleotideCompound second, NucleotideCompound third);
  
  AminoAcidCompound getAminoAcid(CodonCompound codon);
  
  /**
   * Returns the codon representing the specified amino acid. Depending on the genetic code some nucleotides
   * might be ambiguity codes.
   * 
   * @param aminoAcid - the amino acid to be back translated
   * @return a codon compound consisting of three nucleotide compounds
   */
  AminoAcidCompound getCodon(AminoAcidCompound aminoAcid);
}
