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
package info.bioinfweb.libralign.pherogram.provider;


import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.AtomicSymbol;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.IntegerAlphabet;



/**
 * Adapter class that provides the contents of any implementation of {@link Chromatogram} from BioJava 1
 * as a {@link PherogramProvider}.
 * <p>
 * You can use this class together with {@link ChromatogramFactory} to load ABI or SCF files into LibrAlign.
 * Note that base call indices in LibrAlign start with 0 whereas the BioJava indices in the underlying
 * {@link Chromatogram} instance start with 1. (This class converts the indices accordingly.)
 * <p>
 * Trace indices (x-values) start with 0 in LibrAlign and in BioJava.
 * 
 * @author Ben St&ouml;ver
 */
public class BioJavaPherogramProvider implements PherogramProvider {
	public static final String QUALITY_LABEL_PREFIX = "quality-";
	
	
	private Chromatogram chromatogram;
	private Map<Character, AtomicSymbol> traceCurveMap = createTraceCurveMap();
	private double maxTraceValue = 0;  // Must be double in order to avoid an integer division in normalizeTraceValue().

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param chromatogram - the BioJava pherogram instance
	 */
	public BioJavaPherogramProvider(Chromatogram chromatogram) {
		super();
		this.chromatogram = chromatogram;
		maxTraceValue = chromatogram.getMax();
	}

	
	private static Map<Character, AtomicSymbol> createTraceCurveMap() {
		Map<Character, AtomicSymbol> result = new TreeMap<Character, AtomicSymbol>();
		result.put('A', DNATools.a());
		result.put('T', DNATools.t());
		result.put('C', DNATools.c());
		result.put('G', DNATools.g());
		return result;
	}
	
	
	private AtomicSymbol symbolByNucleotide(char nucleotide) {
		return traceCurveMap.get(Character.toUpperCase(nucleotide));
	}

	
	private double normalizeTraceValue(int value) {
		return value / maxTraceValue;
	}
	

	@Override
	public double getTraceValue(char nucleotide, int x) {
		try {
			return normalizeTraceValue(chromatogram.getTrace(symbolByNucleotide(nucleotide))[x]);
		}
		catch (IllegalSymbolException e) {
			throw new InternalError("An unexpected internal error occurred. No trace data for the symbol " + 
		      e.getSymbol() + " was found.");
		}
	}


	@Override
	public int getTraceLength() {
		return chromatogram.getTraceLength();
	}


	@Override
	public double getMaxTraceValue(char nucleotide) {
		try {
			return normalizeTraceValue(chromatogram.getMax(symbolByNucleotide(nucleotide)));
		}
		catch (IllegalSymbolException e) {
			throw new InternalError("An unexpected internal error occurred. No trace data for the symbol " + 
		      e.getSymbol() + " was found.");
		}
	}


	@Override
	public char getBaseCall(int baseIndex) {
		try {
			return Character.toUpperCase(DNATools.dnaToken(chromatogram.getBaseCalls().symbolAt(Chromatogram.DNA, baseIndex + 1)));
		}
		catch (IllegalSymbolException e) {
			throw new InternalError("An unexpected internal error occurred. No trace data for the symbol " + 
		      e.getSymbol() + " was found.");
		}
	}


	@Override
	public int getBaseCallPosition(int baseIndex) {
		return ((IntegerAlphabet.IntegerSymbol)chromatogram.getBaseCalls().symbolAt(
				Chromatogram.OFFSETS, baseIndex + 1)).intValue();
	}


	@Override
	public int getQuality(char nucleotide, int baseIndex) {
		return getAnnotation(QUALITY_LABEL_PREFIX + Character.toLowerCase(nucleotide), baseIndex);
	}


	@Override
	public int getAnnotation(String label, int baseIndex) {
		try {
			return ((IntegerAlphabet.IntegerSymbol)chromatogram.getBaseCalls().symbolAt(
					label, baseIndex + 1)).intValue();
		}
		catch (NoSuchElementException e) {
			return -1;
		}
	}


	@Override
	public int getSequenceLength() {
		return chromatogram.getSequenceLength();
	}
	
	
	/**
   * Returns a new instance of this class which uses the reverse complemented version of the underlying BioJava 1 
   * chromatogram.
   * 
   * @return a new instance of this class
   */
  @Override
  public PherogramProvider reverseComplement() {
  	return new ReverseComplementPherogramProvider(this);  // Reverse complementing BioJava model loses quality scores.
	}
}
