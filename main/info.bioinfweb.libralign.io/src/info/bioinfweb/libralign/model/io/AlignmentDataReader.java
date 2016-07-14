/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.factory.AlignmentModelFactory;



/**
 * Reads the contents of {@link AlignmentModel}s and {@link DataModel}s from a stream of <i>JPhyloIO</i> events.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * @bioinfweb.module info.bioinfweb.libralign.io
 */
public class AlignmentDataReader {
	//TODO Could this class be inherited from EventForwarder?
	
	private JPhyloIOEventReader eventReader;
	private AlignmentModelEventReader alignmentModelReader;
	private List<DataModelEventReader<?>> dataModelReaders = new ArrayList<DataModelEventReader<?>>();
	private Stack<JPhyloIOEvent> parentEvents;
	private List<JPhyloIOEvent> unmodifiableParentEvents;
	
	
	/**
	 * Creates a new instance of this class.
	 * <p>
	 * A new instance must be created for each stream of <i>JPhyloIO</i> events that shall be processed.
	 * 
	 * @param eventReader the reader providing <i>JPhyloIO</i> events
	 * @param alignmentModelFactory the factory to create alignment model instances during reading
	 * @throws NullPointerException if {@code eventReader} or {@code alignmentModelReader} are {@code null} 
	 */
	public AlignmentDataReader(JPhyloIOEventReader eventReader, AlignmentModelFactory alignmentModelFactory) {
		super();
		if ((eventReader == null) || (alignmentModelFactory == null)) {
			throw new NullPointerException("The specified event reader and alignment model factory must not be null.");
		}
		else {
			this.eventReader = eventReader;
			this.alignmentModelReader = new AlignmentModelEventReader(alignmentModelFactory);
			parentEvents = new Stack<JPhyloIOEvent>();
			unmodifiableParentEvents = Collections.unmodifiableList(parentEvents);
		}
	}


	/**
	 * Returns the alignment event reader used create to and read alignment model instances
	 * 
	 * @return the alignment model reader using the factory that was passed to the constructor
	 */
	public AlignmentModelEventReader getAlignmentModelReader() {
		return alignmentModelReader;
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
	
	
	private boolean processNextEvent() throws Exception {
		boolean result = eventReader.hasNextEvent();
		if (result) {
			JPhyloIOEvent event = eventReader.next();
			
			if (event.getType().getTopologyType().equals(EventTopologyType.END)) {
				parentEvents.pop();  // Throws an exception, if more end than start events are encountered.
			}
			
			alignmentModelReader.processEvent(eventReader, unmodifiableParentEvents, event);
			for (DataModelEventReader<?> dataModelReader : dataModelReaders) {
				dataModelReader.processEvent(eventReader, unmodifiableParentEvents, event);
			}
			
			if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
				parentEvents.add(event);
			}
		}
		return result;
	}
	
	
	/**
	 * Processes all events from the underlying <i>JPhyloIO</i> event stream. The resulting models
	 * can be accessed by {@link AlignmentModelEventReader#getCompletedModels()} or by the according 
	 * data model readers, if specified before calling this method.
	 * 
	 * @throws Exception if an exception was thrown by {@link JPhyloIOEventReader#next()}.
	 */
	public void readAll() throws Exception {
		while (processNextEvent());
	}
	
	
	public void readUnitlNextAlignmentModel() throws Exception {
		throw new InternalError("not implemented");
		//TODO implement (Callback class should be passed to alignment and data model readers that informs about newly completed model instances)
	}
}
