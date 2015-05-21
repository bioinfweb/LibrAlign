/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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


import info.bioinfweb.libralign.pherogram.model.PherogramModel;

import java.util.ArrayList;
import java.util.List;

import org.biojava3.core.sequence.compound.DNACompoundSet;
import org.biojava3.core.sequence.compound.NucleotideCompound;



/**
 * provides values for constants of {@link PherogramModel}  and general tool methods to deal with pherogram data. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class PherogramUtils {
	/**
	 * Returns the index of the first trace value in the area of a trace curve belonging the base call with the specified index.
	 * 
	 * @param provider - the provider of the pherogram data
	 * @param baseCallIndex - the index of the base call
	 * @return a valid trace value index
	 */
	public static int getFirstTracePosition(PherogramModel provider, int baseCallIndex) {
		if (baseCallIndex <= 0) {  // BioJava indices start with 1
			return 0;
		}
		else if (baseCallIndex >= provider.getSequenceLength()) {
			return provider.getTraceLength() - 1;
		}
		else {
			return (provider.getBaseCallPosition(baseCallIndex - 1) + 
					provider.getBaseCallPosition(baseCallIndex)) / 2; 
		}
	}
	
	
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
  	result.add(PherogramModel.LABEL_SUBSTITUTION_PROBABILITY);
  	result.add(PherogramModel.LABEL_OVERCALL_PROBABILITY);
  	result.add(PherogramModel.LABEL_UNDERCALL_PROBABILITY);
  	return result;
  }
}
