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


import org.apache.commons.collections4.MultiValuedMap;

import info.bioinfweb.jphyloio.push.JPhyloIOEventListener;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.data.DataModelFactory;



/**
 * Interface to be implemented by all readers that process <i>JPhyloIO</i> events to read data to be stored in
 * implementations of {@link DataModel}.
 * <p>
 * All implementations should accept an instance of {@link DataModel} as a constructor parameter that
 * will than be returned by {@link #getModels()}. Implementing classes should not allow to change the model
 * during runtime, since changing the model between two calls of 
 * {@link #processEvent(info.bioinfweb.jphyloio.JPhyloIOEventReader, info.bioinfweb.jphyloio.events.JPhyloIOEvent)}
 * should be avoided.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * @bioinfweb.module info.bioinfweb.libralign.io
 *
 * @param <M> the type of data model to read by this reader
 */
public interface DataModelEventReader<M extends DataModel> extends JPhyloIOEventListener {
	/**
	 * Returns the instance of the main reader (for reading alignment and data models) which
	 * uses this data model reader.
	 * 
	 * @return the parent alignment and data reader
	 */
	public AlignmentDataReader getMainReader();
	
	/**
	 * Returns the model objects that have been read from the underlying <i>JPhyloIO</i> event stream until now.
	 * The list may be modified by application classes (e.g. to remove consumed objects), but this may change 
	 * the future behavior of some implementations.
	 * 
	 * @return the associated model
	 */
	public MultiValuedMap<DataModelKey, M> getCompletedModels();
	
	/**
	 * Returns the factory used to create new data models in this instance.
	 * 
	 * @return the factory (never {@code null})
	 */
	public DataModelFactory<M> getFactory();
}
