/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.pherogram;


import java.util.ArrayList;
import java.util.List;

import org.biojava3.core.sequence.compound.DNACompoundSet;
import org.biojava3.core.sequence.compound.NucleotideCompound;



/**
 * Class for internal that provides values for constants of {@link PherogramProvider}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
class PherogramUtils {
  public static List<NucleotideCompound> createTraceCurveNucleotideList() {
  	List<NucleotideCompound> result = new ArrayList<NucleotideCompound>(4);
  	DNACompoundSet set = DNACompoundSet.getDNACompoundSet();
  	result.add(set.getCompoundForString("A"));
  	result.add(set.getCompoundForString("T"));
  	result.add(set.getCompoundForString("C"));
  	result.add(set.getCompoundForString("G"));
  	return result;
  }

  
  public static List<String> createProbabilityLabelsList() {
  	List<String> result = new ArrayList<String>(3);
  	result.add(PherogramProvider.LABEL_SUBSTITUTION_PROBABILITY);
  	result.add(PherogramProvider.LABEL_OVERCALL_PROBABILITY);
  	result.add(PherogramProvider.LABEL_UNDERCALL_PROBABILITY);
  	return result;
  }
}
