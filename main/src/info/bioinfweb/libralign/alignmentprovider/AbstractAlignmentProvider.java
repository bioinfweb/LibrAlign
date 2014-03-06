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
package info.bioinfweb.libralign.alignmentprovider;


import info.bioinfweb.libralign.AlignmentDataType;



/**
 * Implements basic functionality of {@link AlignmentProvider}.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0
 */
public abstract class AbstractAlignmentProvider implements AlignmentProvider {
	private AlignmentDataType dataType;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param initialDataType - the initial data type before any call of {@link #setDataType(AlignmentDataType)}
	 */
	public AbstractAlignmentProvider(AlignmentDataType initialDataType) {
		super();
		dataType = initialDataType;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.AlignmentProvider#setDataType(info.bioinfweb.libralign.AlignmentDataType)
	 */
	@Override
	public void setDataType(AlignmentDataType type) {
		dataType = type;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.libralign.alignmentprovider.AlignmentProvider#getDataType()
	 */
	@Override
	public AlignmentDataType getDataType() {
		return dataType;
	}
}
