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


import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.implementations.ListBasedDocumentDataAdapter;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.libralign.model.AlignmentModel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;



/**
 * Collection of convenience methods for reading and writing contents of {@link AlignmentModel}s to file
 * formats supported by <i>JPhyloIO</i>.
 * 
 * @author Ben St&ouml;ver
 * @since 0.6.0
 * @bioinfweb.module info.bioinfweb.libralign.io
 * 
 * @see AlignmentDataReader
 * @see AlignmentModelDataAdapter
 */
public class IOTools {
	private static final JPhyloIOReaderWriterFactory FACTORY = new JPhyloIOReaderWriterFactory();
	
	
	/**
	 * Creates a document data adapter that allows to write the contents of {@code model} to a file using e.g.
	 * {@link JPhyloIOEventWriter#writeDocument(info.bioinfweb.jphyloio.dataadapters.DocumentDataAdapter, File, ReadWriteParameterMap)}.
	 * <p>
	 * The result is a list based document data adapter that allows to add additional adapter instances for e.g. 
	 * alignments, OTU lists or trees.
	 * <p>
	 * This is a convenience method. Application code may also create instances of {@link ListBasedDocumentDataAdapter}
	 * and {@link AlignmentModelDataAdapter} directly. 
	 * 
	 * @param model the alignment model providing the content to be written
	 * @param alignmentLabel the label of the alignment to be written (Maybe {@code null}.)
	 * @param linkOTUs Specify {@code true} here, if each sequence shall link an OTU or {@code false} otherwise. 
	 *        (If {@code true} is specified, a respective OTU list adapter must be added to the returned document 
	 *        adapter. See {@link #getLinkedOTUID(int)} for details on how OTU IDs are generated.) 
	 * @return the document data adapter providing access to the contents of {@code model}
	 * @see AlignmentModelDataAdapter
	 */
	public static <T> ListBasedDocumentDataAdapter createDocumentAdapter(AlignmentModel<T> model, String alignmentLabel, 
			boolean linkOTUs) {
		
		ListBasedDocumentDataAdapter result = new ListBasedDocumentDataAdapter();
		result.getMatrices().add(new AlignmentModelDataAdapter<T>(
				"", new LinkedLabeledIDEvent(EventContentType.ALIGNMENT, "Alignment0", alignmentLabel, null), model, linkOTUs));
		return result;
	}
	
	
	/**
	 * Creates a document data adapter that allows to write the contents of {@code model} to a file using e.g.
	 * {@link JPhyloIOEventWriter#writeDocument(info.bioinfweb.jphyloio.dataadapters.DocumentDataAdapter, File, ReadWriteParameterMap)}.
	 * <p>
	 * The result is a list based document data adapter that allows to add additional adapter instances for e.g. 
	 * alignments, OTU lists or trees. The contained matrix adapter does not link any OTUs to sequences. Use
	 * {@link #createDocumentAdapter(AlignmentModel, String, boolean)} instead if you model OTUs.
	 * <p>
	 * This is a convenience method. Application code may also create instances of {@link ListBasedDocumentDataAdapter}
	 * and {@link AlignmentModelDataAdapter} directly. 
	 * 
	 * @param model the alignment model providing the content to be written
	 * @param alignmentLabel the label of the alignment to be written (Maybe {@code null}.)
	 * @return the document data adapter providing access to the contents of {@code model}
	 * @see AlignmentModelDataAdapter
	 */
	public static <T> ListBasedDocumentDataAdapter createDocumentAdapter(AlignmentModel<T> model, String alignmentLabel) {
		return createDocumentAdapter(model, alignmentLabel, false);
	}
	
	
	/**
	 * Creates a document data adapter that allows to write the contents of {@code model} to a file using e.g.
	 * {@link JPhyloIOEventWriter#writeDocument(info.bioinfweb.jphyloio.dataadapters.DocumentDataAdapter, File, ReadWriteParameterMap)}.
	 * <p>
	 * The result is a list based document data adapter that allows to add additional adapter instances for e.g. 
	 * alignments, OTU lists or trees. The contained matrix adapter does not specify a name for the alignment or link 
	 * any OTUs to sequences. Use {@link #createDocumentAdapter(AlignmentModel, String, boolean)} instead if you want
	 * to specify.
	 * <p>
	 * This is a convenience method. Application code may also create instances of {@link ListBasedDocumentDataAdapter}
	 * and {@link AlignmentModelDataAdapter} directly. 
	 * 
	 * @param model the alignment model providing the content to be written
	 * @return the document data adapter providing access to the contents of {@code model}
	 * @see AlignmentModelDataAdapter
	 */
	public static <T> ListBasedDocumentDataAdapter createDocumentAdapter(AlignmentModel<T> model) {
		return createDocumentAdapter(model, null, false);
	}
	
	
	/**
	 * Writes the contents of an alignment model to a file in the specified format.
	 * <p>
	 * Note that this method is a convenience method, which cannot be used if more than one alignment should
	 * be written to a single file or if additional data or metadata should be included.
	 * 
	 * @param model the alignment model providing the content to be written
	 * @param writer the writer to write the output to (Note that application code is responsible for
	 *        closing the writer. This method does not close it.)
	 * @param format the format of the output (Any format constant defined in {@link JPhyloIOFormatIDs} can 
	 *        be passed here.)
	 * @throws IOException if the underlying <i>JPhyloIO</i> writer thrown an exception during writing to the 
	 *         writer
	 */
	public static <T> void writeSingleAlignment(AlignmentModel<T> model, String alignmentLabel, Writer writer, String format) throws IOException {
		FACTORY.getWriter(format).writeDocument(createDocumentAdapter(model, alignmentLabel, false), writer, new ReadWriteParameterMap());
	}
	
	
	/**
	 * Writes the contents of an alignment model to a file in the specified format.
	 * <p>
	 * Note that this method is a convenience method, which cannot be used if more than one alignment should
	 * be written to a single file or if additional data or metadata should be included.
	 * 
	 * @param model the alignment model providing the content to be written
	 * @param stream the output stream to write the output to (Note that application code is responsible for
	 *        closing the stream. This method does not close it.)
	 * @param format the format of the output (Any format constant defined in {@link JPhyloIOFormatIDs} can 
	 *        be passed here.)
	 * @throws IOException if the underlying <i>JPhyloIO</i> writer thrown an exception during writing to the 
	 *         stream
	 */
	public static <T> void writeSingleAlignment(AlignmentModel<T> model, String alignmentLabel, OutputStream stream, String format) throws IOException {
		FACTORY.getWriter(format).writeDocument(createDocumentAdapter(model, alignmentLabel, false), stream, new ReadWriteParameterMap());
	}
	
	
	/**
	 * Writes the contents of an alignment model to a file in the specified format.
	 * <p>
	 * Note that this method is a convenience method, which cannot be used if more than one alignment should
	 * be written to a single file or if additional data or metadata should be included.
	 * 
	 * @param model the alignment model providing the content to be written
	 * @param alignmentLabel the label of the alignment to be written (Maybe {@code null}.)
	 * @param file the file to write the output to
	 * @param format the format of the output (Any format constant defined in {@link JPhyloIOFormatIDs} can 
	 *        be passed here.)
	 * @throws IOException if the underlying <i>JPhyloIO</i> writer thrown an exception during writing to the 
	 *         file
	 */
	public static <T> void writeSingleAlignment(AlignmentModel<T> model, String alignmentLabel, File file, String format) throws IOException {
		FACTORY.getWriter(format).writeDocument(createDocumentAdapter(model, alignmentLabel, false), file, new ReadWriteParameterMap());
	}
}
