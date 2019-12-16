/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import info.bioinfweb.libralign.model.data.DataModel;



/**
 * Implements shared functionality for all classes reading data from <i>JPhyloIO</i> events into 
 * implementations of {@link DataModel}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * @bioinfweb.module info.bioinfweb.libralign.io
 */
public abstract class AbstractDataElementEventReader<E> implements DataElementEventReader<E> {
	private AlignmentDataReader mainReader;
	private Class<E> elementClass;
	private Map<DataElementKey, E> loadingModels = new HashMap<>();
	private MultiValuedMap<DataElementKey, E> completedModels = new ArrayListValuedHashMap<>();


	/**
	 * Creates a new instance of this class.
	 * 
	 * @param mainReader the associates main reader instance forwarding to this reader
	 */
	public AbstractDataElementEventReader(AlignmentDataReader mainReader, Class<E> elementClass) {
		super();
		this.mainReader = mainReader;
		this.elementClass = elementClass;
	}
	
	
	public AlignmentDataReader getMainReader() {
		return mainReader;
	}


	protected Map<DataElementKey, E> getLoadingModels() {
		return loadingModels;
	}

	
	public E getFirstCompletedModel(DataElementKey key) {
		Collection<E> collection = completedModels.get(key);
		if (!collection.isEmpty()) {
			return collection.iterator().next();
		}
		else {
			return null;
		}
	}
	

	@Override
	public MultiValuedMap<DataElementKey, E> getCompletedElements() {
		return completedModels;
	}


	@Override
	public Class<E> getElementClass() {
		return elementClass;
	}
}
