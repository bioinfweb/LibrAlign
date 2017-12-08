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


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.data.DataModelFactory;



/**
 * Implements shared functionality for all classes reading data from <i>JPhyloIO</i> events into 
 * implementations of {@link DataModel}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * @bioinfweb.module info.bioinfweb.libralign.io
 */
public abstract class AbstractDataModelEventReader<M extends DataModel> implements DataModelEventReader<M> {
	//TODO AbstractDataModelEventReader sollte eine Liste von aktuell ladenden Modellen und nicht nur eins haben. Zusätzlich bleibt die Liste der vollständigen Modelle bestehen.
	
	private AlignmentDataReader mainReader;
	private DataModelFactory<M> factory;
	private Map<DataModelKey, M> loadingModels = new HashMap<DataModelKey, M>();
	private MultiValuedMap<DataModelKey, M> completedModels = new ArrayListValuedHashMap<DataModelKey, M>();


	/**
	 * Creates a new instance of this class.
	 * 
	 * @param mainReader the associates main reader instance forwarding to this reader
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


	@Override
	public DataModelFactory<M> getFactory() {
		return factory;
	}


	protected Map<DataModelKey, M> getLoadingModels() {
		return loadingModels;
	}


	@Override
	public MultiValuedMap<DataModelKey, M> getCompletedModels() {
		return completedModels;
	}
}
