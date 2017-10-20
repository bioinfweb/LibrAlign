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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.semanticweb.owlapi.io.XMLUtils;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.NoCharDefsNoSetsMatrixDataAdapter;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.SequenceTokensEvent;
import info.bioinfweb.jphyloio.events.TokenSetDefinitionEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;



/**
 * An <i>JPhyloIO</i> data adapter that allows writing the contents of an implementation of {@link AlignmentModel}
 * to an implementation of {@link JPhyloIOEventWriter}.
 * <p>
 * Whether the sequence start events link an associated OTU or not can be defined by the constructor parameter
 * {@code linkOTUs}. If OTUs shall be linked, it will be assumed that such an OTU will have an ID consisting of the
 * concatenation of {@link ReadWriteConstants#DEFAULT_OTU_ID_PREFIX} and the sequence ID used in the underlying 
 * {@link AlignmentModel}. If a more complex OTU linking is necessary, the method {@link #getLinkedOTUID(int)} can
 * be overwritten accordingly.
 * <p>
 * This implementation does not write any metadata associated with sequences or the alignment as a whole. If such data
 * shall be written the methods {@link #writeMetadata(JPhyloIOEventReceiver)} or 
 * {@link #writeSequenceMetadata(JPhyloIOEventReceiver, String)} should be overwritten accordingly. If character 
 * definitions or sets shall be written, the according methods need to be overwritten as well.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <T> the type of sequence elements (tokens) used by the underlying alignment model object
 */
public class AlignmentModelDataAdapter<T> extends NoCharDefsNoSetsMatrixDataAdapter implements MatrixDataAdapter {
	//TODO Can this class be simplified, now that String IDs are also used in LibrAlign? (It can probably remain unchanged, since an ID prefix is still necessary, if multiple alignments are combined in a single file.)
	
	private static final int MAX_TOKENS_PER_EVENT = 64;
	
	
	private String idPrefix;
	private LinkedLabeledIDEvent startEvent;
	private AlignmentModel<T> model;
	private boolean linkOTUs;
	private TokenSetAdapter<T> tokenSetAdapter;
	

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param idPrefix the ID prefix to be used for all generated <i>JPhyloIO</i> IDs of sequences and token sets.
	 *        (Must be a valid <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>
	 *        or an empty string. See {@link #getIDPrefix()} for further details.)
	 * @param startEvent the <i>JPhyloIO</i> alignment start event to be used
	 * @param model the <i>LibrAlign</i> alignment model which shall be the source for this adapter
	 * @param linkOTUs Specify {@code true} here, if each sequence shall link an OTU or {@code false} otherwise.
	 *        (See {@link #getLinkedOTUID(int)} for details on how OTU IDs are generated.) 
	 */
	public AlignmentModelDataAdapter(String idPrefix, LinkedLabeledIDEvent startEvent, AlignmentModel<T> model, boolean linkOTUs) {
		super();
		if (!("".equals(idPrefix) || XMLUtils.isNCName(idPrefix))) {
			throw new IllegalArgumentException("The ID prefix  (\"" + idPrefix + "\") is not an empty string or a valid NCName.");
		}
		else if (startEvent == null) {
			throw new NullPointerException("startEvent must not be null.");
		}
		else if (model == null) {
			throw new NullPointerException("model must not be null.");
		}
		else {
			this.idPrefix = idPrefix;
			this.startEvent = startEvent;
			this.model = model;
			this.linkOTUs = linkOTUs;
			tokenSetAdapter = new TokenSetAdapter<T>(idPrefix + "ts_", model.getTokenSet(), 0, model.getMaxSequenceLength());  // For unaligned data, the token set must still reach the end of the longest sequence.
		}
	}


	/**
	 * Returns the ID prefix used by this instance.
	 * <p>
	 * The prefix is added to <i>LibrAlign</i> sequence IDs to create <i>JPhyloIO</i> sequences IDs from them. It may be
	 * an empty string, otherwise it needs to be a valid 
	 * <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>. If multiple instance of this
	 * class are combined to write a file containing multiple alignments (e.g. possible in <i>NeXML</i> or <i>Nexus</i>)
	 * the prefixes of each instance need to be different in order to avoid ID conflicts.
	 * 
	 * @return the ID prefix of this instance as it was specified in the constructor
	 */
	public String getIDPrefix() {
		return idPrefix;
	}


	public boolean isLinkOTUs() {
		return linkOTUs;
	}


	@Override
	public LinkedLabeledIDEvent getStartEvent(ReadWriteParameterMap parameters) {
		return startEvent;
	}


	@Override
	public long getSequenceCount(ReadWriteParameterMap parameters) {
		return model.getSequenceCount();
	}


	/**
	 * Returns the number of columns in the underlying {@link AlignmentModel} instance if all sequences have an equal length.
	 * If at least two of these sequences differ in their length, -1 is returned. 
	 * 
	 * @return the column count or -1
	 * @see info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter#getColumnCount(info.bioinfweb.jphyloio.ReadWriteParameterMap)
	 */
	@Override
	public long getColumnCount(ReadWriteParameterMap parameters) {
		Iterator<String> iterator = model.sequenceIDIterator();
		if (iterator.hasNext()) {
			long lastLength = model.getSequenceLength(iterator.next());
			while (iterator.hasNext()) {
				if (lastLength != model.getSequenceLength(iterator.next())) {
					return -1;
				}
			}
			return lastLength;
		}
		return -1;
	}


	@Override
	public boolean containsLongTokens(ReadWriteParameterMap parameters) {
		return model.getTokenSet().maxRepresentationLength() > 1;
	}


	@Override
	public ObjectListDataAdapter<TokenSetDefinitionEvent> getTokenSets(ReadWriteParameterMap parameters) {
		if (model instanceof ConcatenatedAlignmentModel) {
			throw new InternalError("Support for concatenated alignment models not implemented.");  //TODO Implement when ConcatenatedAlignmentModel is available
		}
		else {
			return tokenSetAdapter;
		}
	}
	
	
	/**
	 * Returns the <i>JPhyloIO</i> sequence ID to be used for the specified sequence ID used in the underlying model.
	 * <p>
	 * This default implementation returns the concatenation of {@link #getIDPrefix()} and {@code sequenceID}. It can be 
	 * overwritten to use a different pattern.
	 * 
	 * @param modelSequenceID the ID of the sequence used in the underlying {@link AlignmentModel} instance
	 * @return the <i>JPhyloIO</i> sequence ID
	 */
	protected String jPhyloIOByModelSequenceID(String modelSequenceID) {
		return idPrefix + modelSequenceID;
	}


	/**
	 * Returns the <i>LibrAlign</i> model sequence ID associated with the specified <i>JPhyloIO</i> sequence ID.
	 * <p>
	 * This default implementation extracts the integer ID that was used to create such an ID using 
	 * {@link #jPhyloIOByModelSequenceID(int)}. If {@link #jPhyloIOByModelSequenceID(int)} was overwritten this method
	 * must be overwritten accordingly.
	 * <p>
	 * Note that this method does not test, if the extracted ID actually exists in the underlying <i>LibrAlign</i>
	 * alignment model.
	 * 
	 * @param jPhyloIOsequenceID the sequence ID used by <i>JPhyloIO</i>
	 * @return the sequence ID used by the underlying alignment model
	 * @throws IllegalArgumentException if no integer ID can be extracted from the specified ID
	 */
	protected String modelByJPhyloIOSequenceID(String jPhyloIOsequenceID) throws IllegalArgumentException {
		try {
			return jPhyloIOsequenceID.substring(idPrefix.length());
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("The specified JPhyloIO sequence ID \"" + jPhyloIOsequenceID + 
					"\" is invalid an cannot be converted to a model ID.", e);
		}
	}


	/**
	 * Returns the ID of the <i>JPhyloIO</i> OTU that shall be linked with the specified sequence.
	 * <p>
	 * This default implementation returns the concatenation of {@link ReadWriteConstants#DEFAULT_OTU_ID_PREFIX} and 
	 * {@code sequenceID} if {@link #isLinkOTUs()} returns {@code true} or {@code null} if {@link #isLinkOTUs()} returns
	 * {@code false}.
	 * <p>
	 * Note that the ID prefix for sequences and token sets, which can be specified in the constructor will not be used 
	 * for IDs creates by this method.
	 * <p>
	 * This method can be overwritten to use a different pattern of OTU linking.
	 * 
	 * @param sequenceID the ID of the sequence used in the underlying {@link AlignmentModel} instance
	 * @return the linked OTU or {@code null} if no OTU shall be linked.
	 */
	protected String getLinkedOTUID(String sequenceID) {
		if (isLinkOTUs()) {
			return ReadWriteConstants.DEFAULT_OTU_ID_PREFIX + sequenceID;
		}
		else {
			return null;
		}
	}


	@Override
	public Iterator<String> getSequenceIDIterator(ReadWriteParameterMap parameters) {
		final Iterator<String> iterator = model.sequenceIDIterator();
		return new Iterator<String>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			
			@Override
			public String next() {
				return jPhyloIOByModelSequenceID(iterator.next());
			}


			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}
	
	
	@Override
	public LinkedLabeledIDEvent getSequenceStartEvent(ReadWriteParameterMap parameters, String sequenceID) {
		String modelID = modelByJPhyloIOSequenceID(sequenceID);
		if (model.containsSequence(modelID)) {
			return new LinkedLabeledIDEvent(EventContentType.SEQUENCE, sequenceID, model.sequenceNameByID(modelID), 
					getLinkedOTUID(modelID));
		}
		else {
			throw new IllegalArgumentException("No according sequence to the ID \"" + sequenceID + 
					"\" could be found in the underlying model.");
		}
	}


	@Override
	public long getSequenceLength(ReadWriteParameterMap parameters, String sequenceID) throws IllegalArgumentException {
		return model.getSequenceLength(modelByJPhyloIOSequenceID(sequenceID));
	}
	
	
	/**
	 * Can be used to write metadata associated with a sequence.
	 * <p>
	 * This default implementation is empty, but can be overwritten by inherited classes.
	 * 
	 * @param receiver the receiver to write the events to
	 * @param sequenceID the ID of the sequence carrying the metadata
	 * @throws IOException if an I/O error occurs while {@code receiver} is trying to write the data
	 * @throws IllegalArgumentException if not sequence with the specified ID exists
	 */
	protected void writeSequenceMetadata(JPhyloIOEventReceiver receiver, String sequenceID) 
			throws IOException,	IllegalArgumentException {}  //TODO Automatically write content of linked data models here (if there are such in future versions of AlignmentModel)?


	@Override
	public void writeSequencePartContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String sequenceID, 
			long startColumn, long endColumn) throws IOException, IllegalArgumentException {
		
		String modelID = modelByJPhyloIOSequenceID(sequenceID);
		int sequenceLength = model.getSequenceLength(modelID); 
		if (Math2.isBetween(startColumn, 0, sequenceLength) && Math2.isBetween(endColumn, startColumn, sequenceLength)) {
			if (startColumn == 0) {
				writeSequenceMetadata(receiver, sequenceID);
			}
			
			ArrayList<String> tokens = new ArrayList<String>(MAX_TOKENS_PER_EVENT);
			for (int column = (int)startColumn; column < (int)endColumn; column++) {
				if (tokens.size() >= MAX_TOKENS_PER_EVENT) {
					receiver.add(new SequenceTokensEvent(tokens));
					tokens = new ArrayList<String>(MAX_TOKENS_PER_EVENT);
				}
				tokens.add(model.getTokenSet().representationByToken(model.getTokenAt(modelID, column)));
			}
			if (!tokens.isEmpty()) {
				receiver.add(new SequenceTokensEvent(tokens));
			}
		}
		else {
			throw new IndexOutOfBoundsException("The specified start (" + startColumn + ") or end column (" + endColumn + 
					") was invalid.");  //TODO Allow endColumn to be behind the end of the sequence?  
		}
	}
}
