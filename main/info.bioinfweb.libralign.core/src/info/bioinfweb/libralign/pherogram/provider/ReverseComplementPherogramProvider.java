/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.pherogram.provider;


import info.bioinfweb.commons.bio.SequenceUtils;



/**
 * Pherogram model that allows to view another model instance reverse complemented.
 * 
 * @author Ben St&ouml;ver
 */
public class ReverseComplementPherogramProvider implements PherogramProvider {
	private PherogramProvider source;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source the model to be viewed reverse complemented
	 */
	public ReverseComplementPherogramProvider(PherogramProvider source) {
		super();
		this.source = source;
	}


	@Override
	public double getTraceValue(char nucleotide, int x) {
		return source.getTraceValue(SequenceUtils.complement(nucleotide), source.getTraceLength() - x - 1);
	}


	@Override
	public int getTraceLength() {
		return source.getTraceLength();
	}


	@Override
	public double getMaxTraceValue(char nucleotide) {
		return source.getMaxTraceValue(nucleotide);
	}

	
	private int convertBaseIndex(int baseIndex) {
		return source.getSequenceLength() - baseIndex - 1;
	}
	

	@Override
	public char getBaseCall(int baseIndex) {
		return SequenceUtils.complement(source.getBaseCall(convertBaseIndex(baseIndex)));
	}


	@Override
	public int getBaseCallPosition(int baseIndex) {
		return source.getTraceLength() - source.getBaseCallPosition(convertBaseIndex(baseIndex)) - 1;
	}


	@Override
	public int getQuality(char nucleotide, int baseIndex) {
		return source.getQuality(SequenceUtils.complement(nucleotide), convertBaseIndex(baseIndex));
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
	public PherogramProvider reverseComplement() {
		return source;
	}
}
