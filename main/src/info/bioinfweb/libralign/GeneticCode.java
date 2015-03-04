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


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.biojava3.core.sequence.compound.AminoAcidCompound;
import org.biojava3.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava3.core.sequence.compound.CodonCompound;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.compound.RNACompoundSet;
import org.biojava3.core.sequence.io.IUPACParser;
import org.biojava3.core.sequence.io.IUPACParser.IUPACTable;
import org.biojava3.core.sequence.transcription.RNAToAminoAcidTranslator;
import org.biojava3.core.sequence.transcription.Table;
import org.biojava3.core.sequence.transcription.TranscriptionEngine;



public class GeneticCode {
	private Map<String, String> codonToAminoAcidMap = createDefaultCodonToAminoAcidMap();
	private Map<String, String> aminoAcidToCodonMap = createDefaultAminoAcidToCodonMap();


	public static void main(String[] args) {
		List<Table.Codon> codons = IUPACParser.getInstance().getTable(1).getCodons(RNACompoundSet.getRNACompoundSet(), AminoAcidCompoundSet.getAminoAcidCompoundSet());
		Iterator<Table.Codon> iterator = codons.iterator();
		while (iterator.hasNext()) {
			Table.Codon codon = iterator.next();
			System.out.println(codon.getAminoAcid().getShortName() + ": " + codon.getTriplet());
		}
	}
	
	
	private static Map<String, String> createDefaultCodonToAminoAcidMap() {
		Map<String, String> result = new TreeMap<String, String>();
		// Phenylalanine:
	  result.put("UUU", "F");	
	  result.put("UUC", "F");
	  
	  result.put("UUY", "F");
	  
	  // Leucine:
	  result.put("UUA", "L");	
	  result.put("UUG", "L");
	  result.put("CUU", "L");	
	  result.put("CUC", "L");	
	  result.put("CUA", "L");
	  result.put("CUG", "L");	
	  
	  result.put("UUR", "L");
	  result.put("CUN", "L");
	  result.put("CUX", "L");
	  
	  // Serine:
	  result.put("UCU", "S");	
	  result.put("UCC", "S");	
	  result.put("UCA", "S");	
	  result.put("UCG", "S");

	  result.put("UCN", "S");
	  result.put("UCX", "S");
	  
	  // Tyrosine:
	  result.put("UAU", "Y");	
	  result.put("UAC", "Y");
	  
	  result.put("UAY", "Y");
	  
	  // Cysteine:
	  result.put("UGU", "C");	
	  result.put("UGC", "C");
	  
	  // Tryptophan:
	  result.put("UGG", "W");
	  
	  // Proline:
	  result.put("CCU", "P");	
	  result.put("CCC", "P");	
	  result.put("CCA", "P");
	  result.put("CCG", "P");
	  
	  // Histidine:
	  result.put("CAU", "H");	
	  result.put("CAC", "H");
	  
	  // 
	  result.put("", "");	
	  result.put("", "");	
	  result.put("", "");
	  result.put("", "");	
	  result.put("", "");	
	  result.put("", "");
	  result.put("", "");	
	  result.put("", "");	
	  result.put("", "");
	  
	  // STOP:
	  result.put("UAA", "*");	
	  result.put("UAG", "*");	
	  result.put("UGA", "*");
	  
		return result;
	}
	
	
	private static Map<String, String> createDefaultAminoAcidToCodonMap() {
		Map<String, String> result = new TreeMap<String, String>();
		
		return result;
	}
	
	
  public AminoAcidCompound getAminoAcid(NucleotideCompound first, NucleotideCompound second, NucleotideCompound third) {
  	return null;
  }
  
  
  public AminoAcidCompound getAminoAcid(CodonCompound codon) {
  	return getAminoAcid(codon.getOne(), codon.getTwo(), codon.getThree());
  }

  
  /**
   * Returns the codon representing the specified amino acid. Depending on the genetic code some nucleotides
   * might be ambiguity codes.
   * 
   * @param aminoAcid - the amino acid to be back translated
   * @return a codon compound consisting of three nucleotide compounds
   */
  public AminoAcidCompound getCodon(AminoAcidCompound aminoAcid) {
  	return null;
  }
}
