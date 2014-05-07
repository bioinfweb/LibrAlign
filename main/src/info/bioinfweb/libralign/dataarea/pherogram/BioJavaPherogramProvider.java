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
package info.bioinfweb.libralign.dataarea.pherogram;


import info.bioinfweb.commons.bio.biojava3.core.sequence.BioJava1SymbolTranslator;

import java.util.EnumMap;
import java.util.Map;

import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.AtomicSymbol;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.IntegerAlphabet;
import org.biojava3.core.sequence.compound.NucleotideCompound;



/**
 * Adapter class that provides the contents of any implementation of {@link Chromatogram} from BioJava 1
 * as a {@link PherogramProvider}.
 * <p>
 * You can use this class together with {@link ChromatogramFactory} to load ABI or SCF filed into LibrAlign.
 * 
 * @author Ben St&ouml;ver
 */
public class BioJavaPherogramProvider implements PherogramProvider {
	private Chromatogram chromatogram;
	private Map<TraceCurve, AtomicSymbol> traceCurveMap = createTraceCurveMap();
	private double maxTraceValue = 0;  // Must be double in order to avoid a integer division in normalizeTraceValue().
	private DefaultPherogramAlignmentModel alignmentModel = new DefaultPherogramAlignmentModel();

	
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

	
	private static Map<TraceCurve, AtomicSymbol> createTraceCurveMap() {
		Map<TraceCurve, AtomicSymbol> result = new EnumMap<TraceCurve, AtomicSymbol>(TraceCurve.class);
		result.put(TraceCurve.ADENINE, DNATools.a());
		result.put(TraceCurve.THYMINE, DNATools.t());
		result.put(TraceCurve.CYTOSINE, DNATools.c());
		result.put(TraceCurve.GUIANINE, DNATools.g());
		return result;
	}
	
	
	private AtomicSymbol symbolByTraceCurve(TraceCurve traceCurve) {
		return traceCurveMap.get(traceCurve);
	}

	
	private double normalizeTraceValue(int value) {
		return value / maxTraceValue;
	}
	

	@Override
	public double getTraceValue(TraceCurve traceCurve, int x) {
		try {
			return normalizeTraceValue(chromatogram.getTrace(symbolByTraceCurve(traceCurve))[x]);
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
	public double getMaxTraceValue(TraceCurve traceCurve) {
		try {
			return normalizeTraceValue(chromatogram.getMax(symbolByTraceCurve(traceCurve)));
		}
		catch (IllegalSymbolException e) {
			throw new InternalError("An unexpected internal error occurred. No trace data for the symbol " + 
		      e.getSymbol() + " was found.");
		}
	}


	@Override
	public NucleotideCompound getBaseCall(int baseIndex) {
		try {
			return BioJava1SymbolTranslator.symbolToNucleotideCompound(				
					chromatogram.getBaseCalls().symbolAt(Chromatogram.DNA, baseIndex));
		}
		catch (IllegalSymbolException e) {
			throw new InternalError("An unexpected internal error occurred. No trace data for the symbol " + 
		      e.getSymbol() + " was found.");
		}
	}


	@Override
	public int getBaseCallPosition(int baseIndex) {
		return ((IntegerAlphabet.IntegerSymbol)chromatogram.getBaseCalls().symbolAt(
				Chromatogram.OFFSETS, baseIndex)).intValue();
	}


	@Override
	public int getSequenceLength() {
		return chromatogram.getSequenceLength();
	}


	@Override
	public PherogramAlignmentModel getAlignmentModel() {
		return alignmentModel;
	}
}
