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
package info.bioinfweb.libralign.pherogram.provider;


import java.util.Set;

import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.pherogram.PherogramUtils;



/**
 * Interface that should be implemented by all classes providing pherogram data. Data provided by
 * such implementations can be displayed by a {@link PherogramArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public interface PherogramProvider {
	/** A list of the nucleotides A, T, C and G to which trace curves exist in a trace file from Sanger sequencing. */ 
  public static final Set<Character> TRACE_CURVE_NUCLEOTIDES = PherogramUtils.createTraceCurveNucleotideList();
  
	/** Label to be used with {@link #getAnnotation(String, int)} to determine the substitution probability */
	public static final String LABEL_SUBSTITUTION_PROBABILITY = "substitution-probability";
	
	/** Label to be used with {@link #getAnnotation(String, int)} to determine the overcall probability */
	public static final String LABEL_OVERCALL_PROBABILITY = "overcall-probability";
	
	/** Label to be used with {@link #getAnnotation(String, int)} to determine the undercall probability */
	public static final String LABEL_UNDERCALL_PROBABILITY = "undercall-probability";

	/** A list that allows to iterate over all probability labels. */
	public static final Set<String> PROBABILITY_LABELS = PherogramUtils.createProbabilityLabelsList();

  
	/**
	 * Returns the y value of the specified trace curve at the specified position.
	 * 
	 * @param nucleotide the type of trace curve (A, T, C or G)
	 * @param x the x position of the curve (The first position is 0 and the last is 
	 *        {@link #getTraceLength()}{@code - 1}.)
	 * @return the stored trace value at this position normalized between 0 and 1
	 */
	public double getTraceValue(char nucleotide, int x);
	
	/**
	 * Returns the length of the stored trace. (The length depends on the read length and sampling rate.)
	 * 
	 * @return an integer values >= 0
	 */
	public int getTraceLength();
	
	/**
	 * Returns the maximum trace value the specified curve contains. (The normalization is the same as
	 * in {@link #getTraceValue(TraceCurve, int)}.)
	 * 
	 * @param nucleotide - the type of trace curve (A, T, C or G)
	 * @return a floating point value between 0 and 1
	 */
	public double getMaxTraceValue(char nucleotide);
	
	/**
	 * Returns a base from the DNA sequence associated with the stored trace. 
	 * 
	 * @param baseIndex - the index of the base in the pherogram (base call) sequence (This might differ 
	 *        from the index of the base in the associated sequence in the alignment. Indices start with 0.)
	 * @return a DNA base or an ambiguity code
	 */
	public char getBaseCall(int baseIndex);
	
	/**
	 * Returns the position in the trace where the base at the specified index was observed.
	 * 
	 * @param baseIndex - the index of the base in the pherogram (base call) sequence (This might differ 
	 *        from the index of the base in the associated sequence in the alignment. Indices start with 0.)
	 * @return an integer value between 0 and {@link #getTraceLength()}{@code - 1}
	 */
	public int getBaseCallPosition(int baseIndex);
	
	/**
	 * Returns the quality score associated with the specified nucleotide.
	 * 
	 * @param nucleotide - the nucleotide identifying the trace curve for the quality score
	 * @param baseIndex - the index of the base call position to which the quality score belongs (Indices start with 0.)
	 * @return the quality score or -1 if no value is stored at the specified position for the specified nucleotide
	 */
	public int getQuality(char nucleotide, int baseIndex);
	
	/**
	 * Returns the specified annotation (e.g. substitution probability) associated with the base call at 
	 * the specified position.
	 * 
	 * @param label - the name of the attachment to be returned
	 * @param baseIndex - the index of base call to which the returned attachment score belongs (Indices start with 0.)
	 * @return the attached value or -1 if no value is stored at the specified position under the specified label
	 */
	public int getAnnotation(String label, int baseIndex);	
	
	/**
	 * Returns the length of the sequence associated with the stored trace.
	 * 
	 * @return an integer value >= 0
	 */
	public int getSequenceLength();

	/**
	 * Returns a reverse complemented view of this model.
	 * 
	 * @return another instance with a possibly shared data source 
	 */
	public PherogramProvider reverseComplement();
}
