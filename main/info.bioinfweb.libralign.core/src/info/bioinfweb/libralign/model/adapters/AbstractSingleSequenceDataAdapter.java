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
package info.bioinfweb.libralign.model.adapters;


import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * Implements basic functionality of {@link SingleSequenceDataAdapter}. Specifically the sequence ID
 * of the viewed sequence, the index of its first viewed token and the length of the viewed part
 * are stored. Additionally protected setter methods are offered.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T>
 */
public class AbstractSingleSequenceDataAdapter<T> extends AbstractBasicAlignmentModelView<T> 
    implements SingleSequenceDataAdapter<T> {
	
	private String sequenceID;
	private int offset;
	private int length;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param underlyingModel the alignment model that contains the sequence to be viewed
	 * @param sequenceID the ID used in {@code provider} of the sequence to be viewed
	 * @param offset the start index of the subsequence to be viewed (The first token has the index 0.)
	 * @param length the length of the subsequence to be viewed
	 */
	protected AbstractSingleSequenceDataAdapter(AlignmentModel<T> underlyingModel, String sequenceID, 
			int offset,	int length) {
		
		super(underlyingModel);
		this.sequenceID = sequenceID;
		this.offset = offset;
		this.length = length;
	}


	/**
	 * Creates a new instance of this class which views the whole length of a sequence in the underlying data 
	 * source.
	 * 
	 * @param underlyingModel the sequence data provider that contains the sequence to be viewed
	 * @param sequenceID the ID used in {@code provider} of the sequence to be viewed
	 */
	protected AbstractSingleSequenceDataAdapter(AlignmentModel<T> underlyingModel, String sequenceID) {
		super(underlyingModel);
		this.sequenceID = sequenceID;
		this.offset = 0;
		this.length = Integer.MAX_VALUE;
	}


	@Override
	public String getSequenceID() {
		return sequenceID;
	}


	protected void setSequenceID(String sequenceID) {
		this.sequenceID = sequenceID;
	}


	@Override
	public int getOffset() {
		return offset;
	}


	protected void setOffset(int offset) {
		this.offset = offset;
	}


	/**
	 * Returns the length of this character sequence. Note that the return value might change if the length
	 * of the viewed sequence changes.
	 *
	 * @return the number of characters in this sequence
	 */
	@Override
	public int getLength() {
		return Math.min(length, 
				Math.max(0, getUnderlyingModel().getSequenceLength(getSequenceID()) - getOffset()));
	}
	
	
	protected void setLength(int length) {
		this.length = length;
	}
}
