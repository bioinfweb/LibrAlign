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
import info.bioinfweb.libralign.model.data.DataModel;



/**
 * Stores information about a data model that has been read from a data source (e.g. a file). 
 * <p>
 * This information includes the new model as well as information about alignments and 
 * sequences its data is possibly associated with.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * @bioinfweb.module info.bioinfweb.libralign.io
 *
 * @param <M> the type of data model that was read
 */
public class DataModelReadInfo<M extends DataModel> {
	private M dataModel;
	private AlignmentModel<?> alignmentModel;
	private String sequenceID;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param dataModel the data model that was read
	 * @param alignmentModel the alignment model the read data is associated with (Can be {@code null}.)
	 * @param sequenceID the ID of the sequence in the alignment model the read data is associated with 
	 *        (Can be {@link AlignmentModel#NO_SEQUENCE_FOUND}.)
	 * @throws IllegalArgumentException if {@code null} was specified as the alignment model, but still 
	 *         a sequence ID (which is not {@code null}) was specified.
	 */
	public DataModelReadInfo(M dataModel, AlignmentModel<?> alignmentModel,	String sequenceID) {
		super();
		if ((alignmentModel == null) && (sequenceID != null)) {
			throw new IllegalArgumentException("A sequence ID can only be specified, if an alignment model was specified as well.");
		}
		else {
			this.dataModel = dataModel;
			this.alignmentModel = alignmentModel;
			this.sequenceID = sequenceID;
		}
	}


	/**
	 * Creates a new instance of this class with no sequence ID.
	 * 
	 * @param dataModel the data model that was read
	 * @param alignmentModel the alignment model the read data is associated with (Can be {@code null}.)
	 * @throws IllegalArgumentException if {@code null} was specified as the alignment model, but still 
	 *         a sequence ID (which is not {@code null}) was specified.
	 */
	public DataModelReadInfo(M dataModel, AlignmentModel<?> alignmentModel) {
		this(dataModel, alignmentModel, null);
	}
	
	
	/**
	 * Creates a new instance of this class with associated aligmment model.
	 * 
	 * @param dataModel the data model that was read
	 * @throws IllegalArgumentException if {@code null} was specified as the alignment model, but still 
	 *         a sequence ID (which is not {@code null}) was specified.
	 */
	public DataModelReadInfo(M dataModel) {
		this(dataModel, null, null);
	}
	
	
	public M getDataModel() {
		return dataModel;
	}


	public AlignmentModel<?> getAlignmentModel() {
		return alignmentModel;
	}


	public String getSequenceID() {
		return sequenceID;
	}
}
