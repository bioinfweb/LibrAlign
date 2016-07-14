/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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


import info.bioinfweb.libralign.pherogram.provider.PherogramProvider;

import java.util.TreeSet;
import java.util.Set;



/**
 * provides values for constants of {@link PherogramProvider}  and general tool methods to deal with pherogram data. 
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
	public static int getFirstTracePosition(PherogramProvider provider, int baseCallIndex) {
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
	
	
  public static Set<Character> createTraceCurveNucleotideList() {
  	Set<Character> result = new TreeSet<Character>();
  	result.add('A');
  	result.add('T');
  	result.add('C');
  	result.add('G');
  	return result;
  }

  
  public static Set<String> createProbabilityLabelsList() {
  	Set<String> result = new TreeSet<String>();
  	result.add(PherogramProvider.LABEL_SUBSTITUTION_PROBABILITY);
  	result.add(PherogramProvider.LABEL_OVERCALL_PROBABILITY);
  	result.add(PherogramProvider.LABEL_UNDERCALL_PROBABILITY);
  	return result;
  }
}
