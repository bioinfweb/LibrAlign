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


import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.factory.AlignmentModelFactory;
import info.bioinfweb.libralign.model.factory.BioPolymerCharAlignmentModelFactory;



/**
 * Reads the contents of {@link AlignmentModel}s and {@link DataModel}s from a stream of <i>JPhyloIO</i> events.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * @bioinfweb.module info.bioinfweb.libralign.io
 */
public class AlignmentDataReader {
	//TODO Could this class be inherited from EventForwarder?
	
	private static final JPhyloIOReaderWriterFactory FACTORY = new JPhyloIOReaderWriterFactory();
	
	
	private JPhyloIOEventReader eventReader;
	private AlignmentModelEventReader alignmentModelReader;
	private List<DataElementEventReader<?>> dataElementReaders = new ArrayList<>();
	
	
	/**
	 * Creates a new instance of this class.
	 * <p>
	 * A new instance must be created for each stream of <i>JPhyloIO</i> events that shall be processed.
	 * 
	 * @param eventReader the reader providing <i>JPhyloIO</i> events
	 * @param alignmentModelFactory the factory to create alignment model instances during reading
	 * @throws NullPointerException if {@code eventReader} or {@code alignmentModelReader} are {@code null} 
	 */
	public AlignmentDataReader(JPhyloIOEventReader eventReader, AlignmentModelFactory<?> alignmentModelFactory) {
		super();
		if ((eventReader == null) || (alignmentModelFactory == null)) {
			throw new NullPointerException("The specified event reader and alignment model factory must not be null.");
		}
		else {
			this.eventReader = eventReader;
			this.alignmentModelReader = new AlignmentModelEventReader(alignmentModelFactory);
		}
	}


	/**
	 * Creates a new instance of this class.
	 * <p>
	 * The format of the specified files is guessed using 
	 * {@link JPhyloIOReaderWriterFactory#guessReader(File, ReadWriteParameterMap)}.
	 * 
	 * @param file the file to be loaded
	 * @param alignmentModelFactory the factory to create alignment model instances during reading
	 * @throws NullPointerException if {@code file} or {@code alignmentModelReader} are {@code null} 
	 * @throws Exception if an exception is thrown by 
	 *         {@link JPhyloIOReaderWriterFactory#guessReader(File, ReadWriteParameterMap)} while guessing
	 *         the format of {@code file}
	 */
	public AlignmentDataReader(File file, AlignmentModelFactory<?> alignmentModelFactory) throws Exception {
		this(FACTORY.guessReader(file, new ReadWriteParameterMap()), alignmentModelFactory);
	}


	/**
	 * Creates a new instance of this class.
	 * <p>
	 * The format of the specified files is guessed using 
	 * {@link JPhyloIOReaderWriterFactory#guessReader(InputStream, ReadWriteParameterMap)}.
	 * 
	 * @param stream the input stream providing the data to be loaded
	 * @param alignmentModelFactory the factory to create alignment model instances during reading
	 * @throws NullPointerException if {@code stream} or {@code alignmentModelReader} are {@code null} 
	 * @throws Exception if an exception is thrown by 
	 *         {@link JPhyloIOReaderWriterFactory#guessReader(InputStream, ReadWriteParameterMap)} while guessing
	 *         the format of {@code stream}
	 */
	public AlignmentDataReader(InputStream stream, AlignmentModelFactory<?> alignmentModelFactory) throws Exception {
		this(FACTORY.guessReader(stream, new ReadWriteParameterMap()), alignmentModelFactory);
	}


	/**
	 * Creates a new instance of this class.
	 * <p>
	 * The format of the specified files is guessed using 
	 * {@link JPhyloIOReaderWriterFactory#guessReader(File, ReadWriteParameterMap)} and an instance of
	 * {@link BioPolymerCharAlignmentModelFactory} will be used to create new model instances for the loaded data. 
	 * 
	 * @param file the file to be loaded
	 * @throws NullPointerException if {@code file} is {@code null} 
	 * @throws Exception if an exception is thrown by 
	 *         {@link JPhyloIOReaderWriterFactory#guessReader(File, ReadWriteParameterMap)} while guessing
	 *         the format of {@code file}
	 */
	public AlignmentDataReader(File file) throws Exception {
		this(file, new BioPolymerCharAlignmentModelFactory());
	}


	/**
	 * Creates a new instance of this class.
	 * <p>
	 * The format of the specified files is guessed using 
	 * {@link JPhyloIOReaderWriterFactory#guessReader(InputStream, ReadWriteParameterMap)} and an instance of
	 * {@link BioPolymerCharAlignmentModelFactory} will be used to create new model instances for the loaded data. 
	 * 
	 * @param stream the input stream providing the data to be loaded
	 * @throws NullPointerException if {@code stream} is {@code null} 
	 * @throws Exception if an exception is thrown by 
	 *         {@link JPhyloIOReaderWriterFactory#guessReader(InputStream, ReadWriteParameterMap)} while guessing
	 *         the format of {@code stream}
	 */
	public AlignmentDataReader(InputStream stream) throws Exception {
		this(stream, new BioPolymerCharAlignmentModelFactory());
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
	 * Adds the specified data element reader to this instance, if it is not already present. All <i>JPhyloIO</i> 
	 * events processed from now on will be forwarded to that reader.
	 * <p>
	 * It is recommended to add all readers before the first method that processed events (e.g. 
	 * {@link #readAll()}) is called. If new readers are added in between, its up to the application developer to 
	 * make sure that these readers still work properly without knowledge of the events that were already consumed.
	 * 
	 * @param reader the reader to be added
	 * @return {@code true} if the new reader was added, {@code false} if the specified reader was already present
	 * @throws IllegalArgumentException if the specified reader does not reference this instance by 
	 *         {@link DataElementEventReader#getMainReader()}
	 */
	public boolean addDataElementReader(DataElementEventReader<?> reader) {
		if (!dataElementReaders.contains(reader)) {
			if (this.equals(reader.getMainReader())) {
				return dataElementReaders.add(reader);
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
	 * Removes the specified data element reader from this instance.
	 * 
	 * @param reader the reader to be removed
	 * @return {@code true} if the specified reader was removed, {@code false} if that reader was not contained in 
	 *         this instance
	 */
	public boolean removeDataElementReader(DataElementEventReader<?> reader) {
		return dataElementReaders.remove(reader);
	}
	
	
	protected JPhyloIOEvent processNextEvent() throws Exception {
		if (eventReader.hasNextEvent()) {
			JPhyloIOEvent event = eventReader.next();
			System.out.println(event.getType());
			if (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT) && (event.asLiteralMetadataContentEvent().getObjectValue() instanceof List)) {
				System.out.println("ShiftList");
			}
			
			alignmentModelReader.processEvent(eventReader, event);
			for (DataElementEventReader<?> dataModelReader : dataElementReaders) {
				dataModelReader.processEvent(eventReader, event);
			}
			return event;
		}
		else {
			return null;
		}
	}
	
	
	/**
	 * Processes all events from the underlying <i>JPhyloIO</i> event stream. The resulting models
	 * can be accessed by {@link AlignmentModelEventReader#getCompletedModels()} or by the according 
	 * data model readers, if specified before calling this method.
	 * 
	 * @throws Exception if an exception was thrown by {@link JPhyloIOEventReader#next()}.
	 */
	public void readAll() throws Exception {
		while (processNextEvent() != null);
	}
	
	
//	public void readUntilAlignmentEnd() throws Exception {
//		JPhyloIOEvent event;
//		do {
//			event = processNextEvent();
//		}	while ((event != null) && ());
//	}
	
	
	/**
	 * This method can be called by application code after {@link #readAll()} to automatically associate all read data models with their respective
	 * alignment models. After calling this method the {@link DataElementEventReader#getCompletedElements()} map of {@link DataElementEventReader}
	 * will only contain data models that have no reference to a specific alignment model and all others will have been attached to the
	 * respective data models.
	 * <p>
	 * Calling this method will not affect any read data elements that do not implement {@link DataModel}.
	 */
	public void associateDataModels() {
		for (AlignmentModel<?> alignmentModel : getAlignmentModelReader().getCompletedModels()) {
			for (DataElementEventReader<?> dataElementReader : dataElementReaders) {
				if (DataModel.class.isAssignableFrom(dataElementReader.getElementClass())) {  // Ignore data elements that are not data models.
					@SuppressWarnings("unchecked")
					DataElementEventReader<DataModel<?>> dataModelReader = (DataElementEventReader<DataModel<?>>)dataElementReader;
					
					// Add data models for alignment:
					alignmentModel.getDataModels().getAlignmentList().addAll(
							dataModelReader.getCompletedElements().remove(new DataElementKey(alignmentModel.getID())));
					
					// Add data models for sequences:
					Iterator<String> sequenceIDIterator = alignmentModel.sequenceIDIterator();
					while (sequenceIDIterator.hasNext()) {
						String sequenceID = sequenceIDIterator.next();
						alignmentModel.getDataModels().getSequenceList(sequenceID).addAll(
								dataModelReader.getCompletedElements().remove(new DataElementKey(alignmentModel.getID(), sequenceID)));
					}
				}
			}
		}
	}
}
