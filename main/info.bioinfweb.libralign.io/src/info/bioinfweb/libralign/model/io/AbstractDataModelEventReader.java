/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben StÃ¶ver
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
package info.bioinfweb.libralign.model.io;


import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.data.DataModelFactory;



/**
 * Implements shared functionality for all classes reading data from JPhyloIO events into implementations
 * of {@link DataModel}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public abstract class AbstractDataModelEventReader<M extends DataModel> implements DataModelEventReader<M> {
	private AlignmentDataReader mainReader;
	private DataModelFactory<M> factory;
	private DataModelReadInfo<M> currentInfo = null;
	private List<DataModelReadInfo<M>> models = new ArrayList<DataModelReadInfo<M>>();


	/**
	 * Creates a new instance of this class.
	 * 
	 * @param factory the factory to be used to create new data model instance during reading
	 */
	public AbstractDataModelEventReader(AlignmentDataReader mainReader, DataModelFactory<M> factory) {
		super();
		this.mainReader = mainReader;
		this.factory = factory;
	}
	
	
	public AlignmentDataReader getMainReader() {
		return mainReader;
	}


	protected void createNewInfo(AlignmentModel<?> alignmentModel) {
		createNewInfo(alignmentModel, AlignmentModel.NO_SEQUENCE_FOUND);
	}
	
	
	protected void createNewInfo(AlignmentModel<?> alignmentModel, int sequenceID) {
		publishCurrentInfo();
		currentInfo = new DataModelReadInfo<M>(factory.createNewModel(), alignmentModel, sequenceID);
	}
	
	
	/**
	 * Adds the current info object to the model list, if one is present.
	 */
	protected void publishCurrentInfo() {
		if (isReadingInstance()) {
			models.add(currentInfo);
			currentInfo = null;
		}
	}


	protected DataModelReadInfo<M> getCurrentInfo() {
		return currentInfo;
	}
	
	
	/**
	 * Determines whether this object contains an unpublished data model that is currently read.
	 * It will be added to {@link #getModels()} as soon as it is completely read. 
	 * 
	 * @return {@code true} if an unpublished object is present, {@code false} otherwise.
	 */
	public boolean isReadingInstance() {
		return currentInfo != null;
	}


	@Override
	public DataModelFactory<M> getFactory() {
		return factory;
	}


	@Override
	public List<DataModelReadInfo<M>> getModels() {
		return models;
	}
}
