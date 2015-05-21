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
package info.bioinfweb.libralign.pherogram.model;


import info.bioinfweb.libralign.model.AlignmentModel;

import org.biojava3.core.sequence.compound.NucleotideCompound;



public class ReverseComplementPherogramModel implements PherogramModel {
	private PherogramModel source;

	
	public ReverseComplementPherogramModel(PherogramModel source) {
		super();
		this.source = source;
	}


	@Override
	public double getTraceValue(NucleotideCompound nucleotide, int x) {
		return source.getTraceValue((NucleotideCompound)nucleotide.getComplement(), source.getTraceLength() - x - 1);
	}


	@Override
	public int getTraceLength() {
		return source.getTraceLength();
	}


	@Override
	public double getMaxTraceValue(NucleotideCompound nucleotide) {
		return source.getMaxTraceValue(nucleotide);
	}

	
	private int convertBaseIndex(int baseIndex) {
		return source.getSequenceLength() - baseIndex - 1;
	}
	

	@Override
	public NucleotideCompound getBaseCall(int baseIndex) {
		return (NucleotideCompound)source.getBaseCall(convertBaseIndex(baseIndex)).getComplement();
	}


	@Override
	public int getBaseCallPosition(int baseIndex) {
		return source.getTraceLength() - source.getBaseCallPosition(convertBaseIndex(baseIndex)) - 1;
	}


	@Override
	public int getQuality(NucleotideCompound nucleotide, int baseIndex) {
		return source.getQuality((NucleotideCompound)nucleotide.getComplement(), convertBaseIndex(baseIndex));
	}


	@Override
	public int getAnnotation(String label, int baseIndex) {
		return source.getAnnotation(label, convertBaseIndex(baseIndex));  //TODO if a quality label would be specified here, it would have to be complemented as well
	}


	@Override
	public int getSequenceLength() {
		return source.getSequenceLength();
	}
	
	
	/**
	 * Returns the source instance.
	 * 
	 * @return the instance specified in the constructor
	 */
	@Override
	public PherogramModel reverseComplement() {
		return source;
	}
}
