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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;



/**
 * Reads the contents of {@link AlignmentModel}s and {@link DataModel}s from a stream of <i>JPhyloIO</i> events.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class AlignmentDataReader {
	private JPhyloIOEventReader eventReader;
	private List<DataModelEventReader<?>> dataModelReaders = new ArrayList<DataModelEventReader<?>>();
	private AlignmentModel<?> currentAlignmentModel = null;
	private List<AlignmentModel<?>> alignmentModels = new ArrayList<AlignmentModel<?>>();
	
	
	/**
	 * Creates a new instance of this class.
	 * <p>
	 * A new instance must be created for each stream of <i>JPhyloIO</i> events that shall be processed.
	 * 
	 * @param eventReader the reader providing <i>JPhyloIO</i> events
	 * @param dataModelReaders the data model readers to process events (an empty collection may be specified here)
	 * @throws NullPointerException if {@code eventReader} or {@code dataModelReaders} is {@code null} 
	 */
	public AlignmentDataReader(JPhyloIOEventReader eventReader) {
		super();
		if (eventReader == null) {
			throw new NullPointerException("The specified event reader must not be null.");
		}
		else {
			this.eventReader = eventReader;
		}
	}


	/**
	 * Adds the specified data model reader to this instance, if it is not already present. All <i>JPhyloIO</i> 
	 * events processed from now on will be forwarded to that reader.
	 * <p>
	 * It is recommended to add all readers before the first method that processed events (e.g. 
	 * {@link #readUnitlNextAlignmentModel()}) is called. If new readers are added in between, its up to the 
	 * application developer to make sure that these reader still work properly without knowledge of the events
	 * that were already consumed.
	 * 
	 * @param reader the reader to be added
	 * @return {@code true} if the new reader was added, {@code false} if the specified reader was already present
	 * @throws IllegalArgumentException if the specified reader does not reference this instance by 
	 *         {@link DataModelEventReader#getMainReader()}
	 */
	public boolean addDataModelReader(DataModelEventReader<?> reader) {
		if (!dataModelReaders.contains(reader)) {
			if (this.equals(reader.getMainReader())) {
				return dataModelReaders.add(reader);
			}
			else {
				throw new IllegalArgumentException("The specified reader does not reference this instance as its main reader.");
			}
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Removes the specified data model reader from this instance.
	 * 
	 * @param reader the reader to be removed
	 * @return {@code true} if the specified reader was removed, {@code false} if that reader was not contained in 
	 *         this instance
	 */
	public boolean removeDataModelReader(DataModelEventReader<?> reader) {
		return dataModelReaders.remove(reader);
	}
	
	
	/**
	 * Returns the alignment model that is currently read. Note that this model might not yet contain all data,
	 * because this data is currently read from the underlying <i>JPhyloIO</i> event stream. As soon as a model
	 * is read completely (an alignment end event if reached) it is added to {@link #getAlignmentModels()} and
	 * this method will return {@code null} until the next alignment is reached in the stream.
	 * 
	 * @return the current alignment model or {@code null} if the current stream position is not between an 
	 *         alignment start and end event
	 * @see AlignmentDataReader#getAlignmentModels()
	 */
	public AlignmentModel<?> getCurrentAlignmentModel() {
		return currentAlignmentModel;
	}
	
	
	/**
	 * Checks whether an alignment model that is currently filled with data from the stream is present.
	 * That would be the case every time the reader is positioned between an alignment start and end event.
	 * 
	 * @return {@code true} if an alignment model is currently present, {@code false} otherwise.
	 */
	public boolean hasCurrentAlignmentModel() {
		return getCurrentAlignmentModel() != null;
	}


	/**
	 * Returns the list of alignment models that have been completely read from the underlying <i>JPhyloIO</i> 
	 * event stream until now.
	 * 
	 * @return the list of models (may be empty, but is never {@code null})
	 * @see #getCurrentAlignmentModel()
	 */
	public List<AlignmentModel<?>> getAlignmentModels() {
		return alignmentModels;
	}
	
	
	private void processEventForAlignmentModel(JPhyloIOEvent event) {
		//TODO implement (possibly in separate reader class)
	}
	
	
	private boolean processNextEvent() throws Exception {
		if (eventReader.hasNextEvent()) {
			JPhyloIOEvent event = eventReader.next();
			processEventForAlignmentModel(event);
			for (DataModelEventReader<?> dataModelReader : dataModelReaders) {
				dataModelReader.processEvent(eventReader, event);
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Processes all events from the underlying <i>JPhyloIO</i> event stream. The resulting models
	 * can be accessed by {@link #getAlignmentModels()} or by the according data model readers, if 
	 * specified before calling this method.
	 * 
	 * @throws Exception if an exception was thrown by {@link JPhyloIOEventReader#next()}.
	 */
	public void readAll() throws Exception {
		while (processNextEvent());
	}
	
	
	public void readUnitlNextAlignmentModel() throws Exception {
		//TODO implement (Callback class should be passed to alignment and data model readers that informs about newly completed model instances)
	}
}
