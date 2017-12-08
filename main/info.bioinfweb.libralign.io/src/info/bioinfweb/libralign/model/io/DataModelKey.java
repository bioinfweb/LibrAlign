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
package info.bioinfweb.libralign.model.io;


import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * Stores information to access a data model that has been read from a data source (e.g. a file). 
 * This information includes the alignment model and the sequence its data is possibly associated with.
 * 
 * @author Ben St&ouml;ver
 * @since 0.6.0
 * @bioinfweb.module info.bioinfweb.libralign.io
 */
public class DataModelKey {
	private AlignmentModel<?> alignmentModel;
	private String sequenceID;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param alignmentModel the alignment model the read data is associated with (Can be {@code null}.)
	 * @param sequenceID the ID of the sequence in the alignment model the read data is associated with 
	 *        (Can be {@code null}.)
	 * @throws IllegalArgumentException if {@code null} was specified as the alignment model, but still 
	 *         a sequence ID (which is not {@code null}) was specified.
	 */
	public DataModelKey(AlignmentModel<?> alignmentModel,	String sequenceID) {
		super();
		if ((alignmentModel == null) && (sequenceID != null)) {
			throw new IllegalArgumentException("A sequence ID can only be specified, if an alignment model was specified as well.");
		}
		else {
			this.alignmentModel = alignmentModel;
			this.sequenceID = sequenceID;
		}
	}


	/**
	 * Creates a new instance of this class with no sequence ID.
	 * 
	 * @param alignmentModel the alignment model the read data is associated with (Can be {@code null}.)
	 * @throws IllegalArgumentException if {@code null} was specified as the alignment model, but still 
	 *         a sequence ID (which is not {@code null}) was specified.
	 */
	public DataModelKey(AlignmentModel<?> alignmentModel) {
		this(alignmentModel, null);
	}
	
	
	public AlignmentModel<?> getAlignmentModel() {
		return alignmentModel;
	}


	public String getSequenceID() {
		return sequenceID;
	}
}
